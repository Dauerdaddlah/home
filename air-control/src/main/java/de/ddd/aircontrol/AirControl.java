package de.ddd.aircontrol;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ddd.aircontrol.control.Controller;
import de.ddd.aircontrol.control.ControllerSimple;
import de.ddd.aircontrol.datalog.DataLogger;
import de.ddd.aircontrol.datalog.DataLoggerFile;
import de.ddd.aircontrol.gui.Gui;
import de.ddd.aircontrol.pi.Model;
import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.pi.RaspberryPi;
import de.ddd.aircontrol.pi.SimPi;
import de.ddd.aircontrol.sensor.HumidSensor;
import de.ddd.aircontrol.sensor.Sensor;
import de.ddd.aircontrol.sensor.SimSensor;
import de.ddd.aircontrol.settings.Settings;
import de.ddd.aircontrol.settings.SettingsProperties;
import de.ddd.aircontrol.ventilation.Level;
import de.ddd.aircontrol.ventilation.Ventilation;
import de.ddd.aircontrol.ventilation.Ventilation.Configuration;

public class AirControl implements Executor
{
	private static final Logger log = LoggerFactory.getLogger(AirControl.class);
	
	public static void main(String[] args)
	{
		log.info("Start AirVentilation");
		
		log.info("create aircontrol");
		instance = new AirControl();
		
		log.info("start loop");
		instance.startLoop();
	}
	
	private static volatile AirControl instance;
	
	public static AirControl getInstance()
	{
		return instance;
	}
	
	public static final String SETTING_PREFIX = "de.ddd.aircontrol.";
	
	public static final String SETTING_PI_PREFIX = SETTING_PREFIX + "pi.";
	
	public static final String SETTING_PI_TYPE = SETTING_PI_PREFIX + "type";
	public static final String SETTING_PI_TYPE_HARDWARE = "hardware";
	public static final String SETTING_PI_TYPE_SIMULATION = "sim";
	
	public static final String SETTING_PI_MODEL = SETTING_PI_PREFIX + "model";
	
	public static final String SETTING_DATA_LOG = SETTING_PREFIX + "data.log";
	
	public static final String SETTING_SENSOR_PREFIX = SETTING_PREFIX + "sensor.";
	
	public static final String SETTING_SENSOR_NAMES = SETTING_SENSOR_PREFIX + "names";
	
	public static final String SETTING_SENSOR_TYPE_SUFFIX = ".type";
	public static final String SETTING_SENSOR_TYPE_HTTP = "http";
	public static final String SETTING_SENSOR_TYPE_SIM = "sim";
	
	public static final String SETTING_SENSOR_URL_SUFFIX = ".url";
	public static final String SETTING_SENSOR_TIMEOUT_SUFFIX = ".timeout";
	public static final String SETTING_SENSOR_KEY_HUMIDITY_SUFFIX = ".key.humidity";
	public static final String SETTING_SENSOR_KEY_TEMPERATUR_SUFFIX = ".key.temperature";
	
	public static final String SETTING_VENTILATION_PREFIX = SETTING_PREFIX + "ventilation.";
	
	public static final String SETTING_VENTILATION_BRIDGE_MODE = SETTING_VENTILATION_PREFIX + "bridge.gpiopin";
	public static final String SETTING_VENTILATION_BRIDGE_MODE_REVERSE = SETTING_VENTILATION_PREFIX + "bridge.reversed";
	public static final String SETTING_VENTILATION_CONFIG_PIN_SUFFIX = ".gpiopins";
	
	public static final String SETTING_CONTROLLER_PREFIX = SETTING_PREFIX + "controller.";
	public static final String SETTING_CONTROLLER_TYPE = SETTING_CONTROLLER_PREFIX + "type";
	public static final String SETTING_CONTROLLER_TYPE_SIMPLE = "simple";
	
	public static final String SETTING_CONTROLLER_START1 = SETTING_CONTROLLER_PREFIX + "start1";
	public static final String SETTING_CONTROLLER_START2 = SETTING_CONTROLLER_PREFIX + "start2";
	public static final String SETTING_CONTROLLER_START3 = SETTING_CONTROLLER_PREFIX + "start3";
	public static final String SETTING_CONTROLLER_END1 = SETTING_CONTROLLER_PREFIX + "end1";
	public static final String SETTING_CONTROLLER_END2 = SETTING_CONTROLLER_PREFIX + "end2";
	public static final String SETTING_CONTROLLER_END3 = SETTING_CONTROLLER_PREFIX + "end3";
	
	private final Gui gui;
	
	private final PriorityBlockingQueue<Event> actions;
	
	public AirControl()
	{
		actions = new PriorityBlockingQueue<>();
		
		log.debug("load settings");
		Settings settings = new SettingsProperties(Paths.get("config", "aircontrol.properties"));
		
		log.debug("create pi");
		final Pi pi;
		switch(settings.getString(SETTING_PI_TYPE, SETTING_PI_TYPE_HARDWARE))
		{
			case SETTING_PI_TYPE_HARDWARE ->
			{
				pi = new RaspberryPi(Model.valueOf(settings.getString(SETTING_PI_MODEL, Model.PI_3_B.name())));
			}
			case SETTING_PI_TYPE_SIMULATION ->
			{
				pi = new SimPi(Model.valueOf(settings.getString(SETTING_PI_MODEL, Model.PI_3_B.name())));
			}
			default ->
			{
				throw new RuntimeException("unknown pi type configured");
			}
		}
		
		log.debug("create data logger");
		DataLogger dataLogger = new DataLoggerFile(Paths.get(settings.getString(SETTING_DATA_LOG, "./log/data.log")));
		
		log.debug("load sensors");
		Map<String, Sensor> sensors = new HashMap<>();
		for(String name : settings.getString(SETTING_SENSOR_NAMES, "bath").split("[,]"))
		{
			name = name.trim();
			
			log.trace("load sensor {}", name);
			
			switch(settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_TYPE_SUFFIX, SETTING_SENSOR_TYPE_HTTP))
			{
				case SETTING_SENSOR_TYPE_HTTP ->
				{
					sensors.put(name,
						new HumidSensor(
							settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_URL_SUFFIX, "http://localhost/" + name),
							Duration.ofMillis(
								settings.getInt(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_TIMEOUT_SUFFIX, 1000)),
							settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_KEY_HUMIDITY_SUFFIX, "humidity"),
							settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_KEY_TEMPERATUR_SUFFIX, "temperature")));
				}
				case SETTING_SENSOR_TYPE_SIM ->
				{
					sensors.put(name, new SimSensor());
				}
				default ->
				{
					throw new RuntimeException("unknown sensor type for sensor " + name);
				}
			}
		}
		
		log.debug("create ventilation");
		Ventilation ventilation = new Ventilation(pi, getConfigurations(settings, "normal"), getConfigurations(settings, "bridge"),
				settings.getInt(SETTING_VENTILATION_BRIDGE_MODE, -1),
				settings.getBoolean(SETTING_VENTILATION_BRIDGE_MODE_REVERSE, false));
		
		log.debug("startup GUI");
		AtomicReference<Gui> refGui = new AtomicReference<>();
		try
		{
			SwingUtilities.invokeAndWait(() ->
				{
					refGui.set(new Gui());
				});
		}
		catch (InvocationTargetException | InterruptedException e)
		{
			throw new RuntimeException("could not create GUI", e);
		}
		
		this.gui = refGui.get();
		
		
		Controller controller;
		switch(settings.getString(SETTING_CONTROLLER_TYPE, SETTING_CONTROLLER_TYPE_SIMPLE))
		{
			case SETTING_CONTROLLER_TYPE_SIMPLE ->
			{
				controller = new ControllerSimple(
						settings.getInt(SETTING_CONTROLLER_START1, 50),
						settings.getInt(SETTING_CONTROLLER_START2, 60),
						settings.getInt(SETTING_CONTROLLER_START3, 70),
						settings.getInt(SETTING_CONTROLLER_END1, 40),
						settings.getInt(SETTING_CONTROLLER_END2, 50),
						settings.getInt(SETTING_CONTROLLER_END3, 60));
			}
			default ->
			{
				throw new RuntimeException("unknwon controller type configuration");
			}
		}
		
		Environment env = new Environment(ventilation, sensors, settings, pi, dataLogger, this, controller);
		Environment.setDefault(env);
	}

	private EnumMap<Level, Configuration> getConfigurations(Settings settings, String type)
	{
		EnumMap<Level, Configuration> configs = new EnumMap<>(Level.class);
		
		for(Level lvl : Level.values())
		{
			List<Integer> activeGpioPins = new ArrayList<>();
			List<Integer> inactiveGpioPins = new ArrayList<>();
			
			for(String sPin : settings.getString(SETTING_VENTILATION_PREFIX + type + "." + lvl + SETTING_VENTILATION_CONFIG_PIN_SUFFIX, "").split("[,]"))
			{
				sPin = sPin.trim();
				
				if(sPin.isEmpty())
				{
					continue;
				}
				
				if(sPin.charAt(0) == '-')
				{
					inactiveGpioPins.add(Integer.parseInt(sPin.substring(1)));
				}
				else
				{
					if(sPin.charAt(0) == '+')
					{
						sPin = sPin.substring(1);
					}
					
					activeGpioPins.add(Integer.parseInt(sPin));
				}
			}
			
			Configuration config = new Configuration(
					activeGpioPins.stream().mapToInt(Integer::intValue).toArray(),
					inactiveGpioPins.stream().mapToInt(Integer::intValue).toArray());
			
			configs.put(lvl, config);
		}
		
		return configs;
	}
	
	void startLoop()
	{
		log.info("start loop");
		
		addEvent(new Event(System.currentTimeMillis(), this::checkSensors));
		
		while(true)
		{
			try
			{
				log.info("next event");
				Event e = getNextEvent();
				e.action.run();
				
				SwingUtilities.invokeLater(() ->
					gui.updateState(Environment.getDefault()));
			}
			catch (Exception exc)
			{
				// clear interrupt state just in case
				Thread.interrupted();
				log.error("Error within loop", exc);
			}
		}
	}
	
	private synchronized Event getNextEvent() throws InterruptedException
	{
		while(true)
		{
			Event e = actions.peek();
			
			if(e == null)
			{
				this.wait();
			}
			else
			{
				long delta = e.due() - System.currentTimeMillis();
				if(delta <= 0)
				{
					return actions.take();
				}
				else
				{
					this.wait(delta);
				}
			}
		}
	}

	private void checkSensors()
	{
		try
		{
			Environment env = Environment.getDefault();
			
			env.pullSensors();
			
			if(!env.isHandMode())
			{
				Level lvl = env.getController().check(env);
				
				if(lvl != env.getLastLevel())
				{
					env.setLastLevel(lvl);
					env.getVentilation().setLevel(lvl, env);
				}
			}
		}
		finally
		{
			addEvent(new Event(System.currentTimeMillis() + (60 * 1000), this::checkSensors));
		}
	}
	
	@Override
	public void execute(Runnable command)
	{
		addEvent(new Event(System.currentTimeMillis(), command));
	}
	
	private synchronized void addEvent(Event e)
	{
		actions.add(e);
		this.notify();
	}
	
	private static record Event(long due, Runnable action) implements Comparable<Event>
	{
		@Override
		public int compareTo(Event o)
		{
			return Long.compare(due, o.due);
		}
	}
}
