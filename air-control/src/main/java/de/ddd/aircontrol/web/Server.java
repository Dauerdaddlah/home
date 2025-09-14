package de.ddd.aircontrol.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;

import de.ddd.aircontrol.Env;
import de.ddd.aircontrol.pi.PiPin;
import de.ddd.aircontrol.pi.PinMode;
import de.ddd.aircontrol.sensor.Sensor;
import de.ddd.aircontrol.sensor.SensorData;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.sensor.SimSensor;
import de.ddd.aircontrol.ventilation.Level;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.staticfiles.Location;

public class Server
{
	private final ThreadLocal<Context> ctx;
	
	private final Javalin app;
	
	private final Gson gson;
	
	private final ServerEventQueue eventQueue;
	
	public Server(int port, ServerEventQueue eventQueue)
	{
		this.eventQueue = eventQueue;
		this.ctx = new ThreadLocal<>();
		gson = new Gson();
		
		app = Javalin.create(this::configure);
		
		app.get("/v1/sensor", new EnvHandler(this::reqGetAllSensors));
		app.get("/v1/sensor/{sensor}", new EnvHandler(this::reqGetSensor));
		app.put("/v1/sensor/{sensor}", new EnvHandler(this::reqSetSensor));
		
		app.get("/v1/setting", new EnvHandler(this::reqGetAllSettings));
		app.get("/v1/setting/{setting}", new EnvHandler(this::reqGetSetting));
		
		app.get("/v1/pi", new EnvHandler(this::reqGetPi));
		app.get("/v1/pi/{bcm}", new EnvHandler(this::reqGetPiPin));
		app.put("/v1/pi/{bcm}", new EnvHandler(this::reqSetPiPin));
		
		app.get("/v1/ventilation", new EnvHandler(this::reqGetVentilation));
		app.put("/v1/ventilation", new EnvHandler(this::reqSetVentilation));
		
		// controller
		
		app.get("/v1/state", new EnvHandler(this::reqGetState));
		app.put("/v1/state", new EnvHandler(this::reqSetState));
		
		
		app.start(port);
	}
	
	private void configure(JavalinConfig config)
	{
		config.staticFiles.add("./public", Location.EXTERNAL);
		config.bundledPlugins.enableRouteOverview("/v1/api");
		config.bundledPlugins.enableDevLogging(cfg ->
			{
				cfg.skipStaticFiles = false;
			});
	}
	
	private Context ctx()
	{
		return ctx.get();
	}
	
	private <T> T getData(EventFunction<T> action)
	{
		CompletableFuture<T> f = new CompletableFuture<>();
		
		eventQueue.accessEnv(env ->
			{
				try
				{
					f.complete(action.apply(env));
				}
				catch(Exception e)
				{
					f.completeExceptionally(e);
				}
			});
		
		try
		{
			return f.get(10, TimeUnit.SECONDS);
		}
		catch(Exception e)
		{
			Thread.interrupted();
			throw new RuntimeException(e);
		}
	}
	
//	public static void main(String[] args)
//	{
//		new Server(8080);
//	}
	
	public List<HttpSensor> reqGetAllSensors(Env env) throws Exception
	{
		List<HttpSensor> ret = new ArrayList<>();
		
		env.sensors().getLastResults().forEach((name, res) ->
				ret.add(new HttpSensor(name, String.valueOf(res.temperature()), String.valueOf(res.humidity()))));
		
		return ret;
	}
	
	public HttpSensor reqGetSensor(Env env) throws Exception
	{
		String name = ctx().pathParam("sensor");
		var data = env.sensor(name);
		
		return new HttpSensor(name, String.valueOf(data.lastResult().temperature()), String.valueOf(data.lastResult().humidity()));
	}
	
	public HttpSensor reqSetSensor(Env env) throws Exception
	{
		String name = ctx().pathParam("sensor");
		Sensor s = env.sensors().getSensor(name);
		
		if(s instanceof SimSensor sim)
		{
			HttpSensor dest = gson.fromJson(ctx().body(), HttpSensor.class);
			
			if(dest.sensor() != null && !dest.sensor().equals(name))
			{
				throw new BadRequestResponse("path and sensor name do not match");
			}
			
			double h = Double.NaN;
			double t = Double.NaN;
			
			if(dest.temperature() != null)
			{
				t = Double.parseDouble(dest.temperature());
			}
			
			if(dest.humidity() != null)
			{
				h = Double.parseDouble(dest.humidity());
			}
			
			if(!Double.isNaN(h) || !Double.isNaN(t))
			{
				sim.setResult(new SensorResult(h, t));
			}
		}
		else
		{
			throw new NotFoundResponse();
		}
		
		return reqGetSensor(env);
	}
	
	public List<HttpSetting> reqGetAllSettings(Env env)
	{
		List<HttpSetting> ret = new ArrayList<>();
		
		for(String key : env.settings().keySet())
		{
			ret.add(new HttpSetting(key, env.settings().getString(key, "")));
		}
		
		return ret;
	}
	
	public HttpSetting reqGetSetting(Env env)
	{
		String key = ctx().pathParam("setting");
		String val = env.settings().getString(key, "");
		
		return new HttpSetting(key, val);
	}
	
	public HttpPi reqGetPi(Env env)
	{
		List<HttpPiPin> pins = new ArrayList<>();
		
		for(int i = 0; i < env.pi().getNumPins(); i++)
		{
			PiPin pin = env.pi().getPin(i);
			
			Integer value = switch(pin.getPinMode())
			{
				case ANALOG_IN, ANALOG_OUT ->
					pin.getAnalogValue();
				case DIGITAL_IN, DIGITAL_OUT ->
					pin.getDigitalValue() ? 1 : 0;
				default -> null;
			};
			
			pins.add(new HttpPiPin(i, pin.getPinMode().name().toLowerCase(), value));
		}
		
		return new HttpPi(pins);
	}
	
	public HttpPiPin reqGetPiPin(Env env)
	{
		int gpioPin = Integer.valueOf(ctx().pathParam("bcm"));
		
		PiPin pin = env.pi().getPin(gpioPin);
		
		Integer value = switch(pin.getPinMode())
				{
					case ANALOG_IN, ANALOG_OUT ->
						pin.getAnalogValue();
					case DIGITAL_IN, DIGITAL_OUT ->
						pin.getDigitalValue() ? 1 : 0;
					default -> null;
				};
		
		return new HttpPiPin(gpioPin, pin.getPinMode().name().toLowerCase(), value);
	}
	
	public HttpPiPin reqSetPiPin(Env env)
	{
		HttpPiPin httpPin = gson.fromJson(ctx().body(), HttpPiPin.class);
		
		int gpioPin = Integer.valueOf(ctx().pathParam("bcm"));
		
		if(httpPin.bcm() != null && httpPin.bcm().intValue() != gpioPin)
		{
			throw new BadRequestResponse();
		}
		
		PinMode destMode = null;
		if(httpPin.mode() != null)
		{
			destMode = PinMode.valueOf(httpPin.mode().toUpperCase());
		}
		
		PiPin pin = env.pi().getPin(gpioPin);
		
		if(destMode != null && destMode != pin.getPinMode())
		{
			pin.setPinMode(destMode);
		}
		
		if(httpPin.value() != null)
		{
			switch(pin.getPinMode())
			{
				case ANALOG_IN, ANALOG_OUT ->
					pin.setAnalogValue(httpPin.value());
					
				case DIGITAL_IN, DIGITAL_OUT ->
					pin.setDigitalValue(httpPin.value().intValue() != 0);
					
				default ->
					throw new BadRequestResponse();
			}
		}
		
		Integer value = switch(pin.getPinMode())
				{
					case ANALOG_IN, ANALOG_OUT ->
						pin.getAnalogValue();
					case DIGITAL_IN, DIGITAL_OUT ->
						pin.getDigitalValue() ? 1 : 0;
					default -> null;
				};
		
		return new HttpPiPin(gpioPin, pin.getPinMode().name().toLowerCase(), value);
	}
	
	private int levelToIn(Level lvl)
	{
		return switch(lvl)
				{
					case DEFAULT -> 0;
					case OFF -> 1;
					default -> 2 + lvl.ordinal() - Level.ONE.ordinal();
				};
	}
	
	private Level intToLevel(int lvl)
	{
		return switch(lvl)
				{
					case 0 -> Level.DEFAULT;
					case 1 -> Level.OFF;
					default -> Level.values()[lvl - 2 + Level.ONE.ordinal()];
				};
	}
	
	public HttpVentilation reqGetVentilation(Env env)
	{
		return new HttpVentilation(
				levelToIn(env.ventilation().getLevel()),
				levelToIn(env.ventilation().getBridgeLevel()),
				env.ventilation().getVentilationMode().name().toLowerCase());
	}
	
	public HttpVentilation reqSetVentilation(Env env)
	{
		HttpVentilation v = gson.fromJson(ctx().body(), HttpVentilation.class);
		
		if(v.level() != null)
		{
			env.ventilation().setLevel(intToLevel(v.level()));
		}
		
		if(v.bridgeLevel() != null)
		{
			env.ventilation().setBridgeLevel(intToLevel(v.bridgeLevel()));
		}
		
		return new HttpVentilation(
				levelToIn(env.ventilation().getLevel()),
				levelToIn(env.ventilation().getBridgeLevel()),
				env.ventilation().getVentilationMode().name().toLowerCase());
	}
	
	public SystemState reqGetState(Env env)
	{
		SensorData s = env.sensor("bath");
		
		SensorResult res = s == null ? new SensorResult() : s.lastResult();
		
		long until = env.controllerManual().getUntil();
		long interval = until - System.currentTimeMillis();
		
		return new SystemState(
				levelToIn(env.ventilation().getLevel()),
				res.hasTemperature() ? String.format("%.1f", res.temperature()) : "--",
				res.hasHumidity() ? String.format("%.1f", res.humidity()) : "--",
				interval > 0 ? (int)interval : null);
	}
	
	public SystemState reqSetState(Env env)
	{
		SystemState set = gson.fromJson(ctx().body(), SystemState.class);
		
		if(set.level() != null)
		{
			Level dest = intToLevel(set.level());
			
			if(set.interval() != null)
			{
				env.controllerManual().setDestLevelFor(dest, set.interval());
			}
			else
			{
				env.controllerManual().setDestLevel(dest);
			}
		}
		
		return reqGetState(env);
	}
	
	private class EnvHandler implements Handler
	{
		private final EventFunction<?> action;
		
		public EnvHandler(EventFunction<?> action)
		{
			this.action = action;
		}
		
		@Override
		public void handle(@NotNull Context ctx) throws Exception
		{
			Server.this.ctx.set(ctx);
			try
			{
				Object res = getData(action);
				
				if(res != null)
				{
					ctx.result(gson.toJson(res));
				}
				
				ctx.status(HttpStatus.OK);
			}
			finally
			{
				Server.this.ctx.set(null);
			}
		}
	}
	
	public static record HttpSensor(String sensor, String temperature, String humidity)
	{
	}
	
	public static record HttpSetting(String key, String value)
	{
	}
	
	public static record HttpVentilation(
			Integer level,
			Integer bridgeLevel,
			String mode
//			Map<Integer, Ventilation.Configuration> configurations,
//			Map<Integer, Ventilation.Configuration> bridgeConfigurations
		)
	{
	}
	
	public static record HttpPi(List<HttpPiPin> pins)
	{
	}
	
	public static record HttpPiPin(Integer bcm, String mode, Integer value)
	{
	}
	
	public static record SystemState(
			Integer level,
			String temperature,
			String humidity,
			/** remaining time in ms */
			Integer interval
			)
	{
	}
	
	private static interface EventFunction<T>
	{
		public T apply(Env env) throws Exception;
	}
}
