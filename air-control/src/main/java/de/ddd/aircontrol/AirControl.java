package de.ddd.aircontrol;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ddd.aircontrol.cleaning.Cleanings;
import de.ddd.aircontrol.cleaning.VentilatorCleaning;
import de.ddd.aircontrol.control.Controller;
import de.ddd.aircontrol.control.ControllerSimple;
import de.ddd.aircontrol.db.DB;
import de.ddd.aircontrol.db.Repository;
import de.ddd.aircontrol.event.Event;
import de.ddd.aircontrol.event.EventAction;
import de.ddd.aircontrol.event.EventQueue;
import de.ddd.aircontrol.pi.Model;
import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.pi.RaspberryPi;
import de.ddd.aircontrol.pi.SimPi;
import de.ddd.aircontrol.sensor.HumidSensor;
import de.ddd.aircontrol.sensor.Sensors;
import de.ddd.aircontrol.sensor.SimSensor;
import de.ddd.aircontrol.settings.Settings;
import de.ddd.aircontrol.settings.SettingsProperties;
import de.ddd.aircontrol.ventilation.Level;
import de.ddd.aircontrol.ventilation.Ventilation;
import de.ddd.aircontrol.ventilation.Ventilation.Configuration;
import de.ddd.aircontrol.web.Server;
import de.ddd.aircontrol.web.ServerEventQueue;

public class AirControl implements ServerEventQueue
{
	private static final Logger log = LoggerFactory.getLogger(AirControl.class);
	
	public static void main(String[] args)
	{
		log.info("Start AirVentilation");
		
		log.info("create aircontrol");
		AirControl ctrl = new AirControl();
		
		ctrl.startLoop();
	}
	
	public static final String SETTING_PREFIX = "de.ddd.aircontrol.";
	
	public static final String SETTING_WEBSERVER_PORT = SETTING_PREFIX + "webserver.port";
	
	public static final String SETTING_PI_PREFIX = SETTING_PREFIX + "pi.";
	
	public static final String SETTING_PI_TYPE = SETTING_PI_PREFIX + "type";
	public static final String SETTING_PI_TYPE_HARDWARE = "hardware";
	public static final String SETTING_PI_TYPE_SIMULATION = "sim";
	
	public static final String SETTING_PI_MODEL = SETTING_PI_PREFIX + "model";
	
	public static final String SETTING_DB_PATH = SETTING_PREFIX + "db.path";
	
	public static final String SETTING_DATA_PREFIX = SETTING_PREFIX + "data.";
	public static final String SETTING_DATA_LOG = SETTING_DATA_PREFIX + "log";
	public static final String SETTING_DATA_MAXSIZE = SETTING_DATA_PREFIX + "maxsize";
	public static final String SETTING_DATA_COUNT = SETTING_DATA_PREFIX + "count";
	
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
	
	public static final String SETTING_CONTROLLER_SENSOR = SETTING_CONTROLLER_PREFIX + "sensor";
	public static final String SETTING_CONTROLLER_START1 = SETTING_CONTROLLER_PREFIX + "start1";
	public static final String SETTING_CONTROLLER_START2 = SETTING_CONTROLLER_PREFIX + "start2";
	public static final String SETTING_CONTROLLER_START3 = SETTING_CONTROLLER_PREFIX + "start3";
	public static final String SETTING_CONTROLLER_END1 = SETTING_CONTROLLER_PREFIX + "end1";
	public static final String SETTING_CONTROLLER_END2 = SETTING_CONTROLLER_PREFIX + "end2";
	public static final String SETTING_CONTROLLER_END3 = SETTING_CONTROLLER_PREFIX + "end3";
	
	public static final String SETTING_CHECK_INTERVAL = SETTING_PREFIX + "check.interval";
	
	public static final String SETTING_CLEANING_PREFIX = SETTING_PREFIX + "cleaning.";
	public static final String SETTING_CLEANING_NAME_POSTFIX = ".name";
	public static final String SETTING_CLEANING_INTERVAL_POSTFIX = ".interval";
	public static final String SETTING_CLEANING_REPLACEINTERVAL_POSTFIX = ".replaceInterval";
	
	private final EventQueue events;
	
	private final Settings settings;
	private final Pi pi;
	private final Sensors sensors;
	private final Ventilation ventilation;
	private final Controller controller;
//	private final Server server;
	private final Repository repo;
	private final Cleanings cleanings;
	private final Env env;
	
	public AirControl()
	{
		events = new EventQueue();
		
		log.debug("load settings");
		settings = new SettingsProperties(Paths.get("config", "aircontrol.properties"));
		
		log.debug("create pi");
		
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
		
		log.debug("create db");
		try
		{
			DB db = new DB(Paths.get(settings.getString(SETTING_DB_PATH, "aircontrol.db")));
			repo = new Repository(db);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		cleanings = new Cleanings(repo);
		
		for(int i = 1; true; i++)
		{
			String name = settings.getString(SETTING_CLEANING_PREFIX + i + SETTING_CLEANING_NAME_POSTFIX, "");
			String interval = settings.getString(SETTING_CLEANING_PREFIX + i + SETTING_CLEANING_INTERVAL_POSTFIX, "");
			int replaceInterval = settings.getInt(SETTING_CLEANING_PREFIX + i + SETTING_CLEANING_REPLACEINTERVAL_POSTFIX, -1);
			
			if(name.isEmpty() || interval.isEmpty())
			{
				break;
			}
			
			String[] split = interval.split("[.]{2}");
			
			int intervalMin = Integer.parseInt(split[0]);
			int intervalMax = intervalMin;
			
			if(split.length > 1)
			{
				intervalMax = Integer.parseInt(split[1]);
			}
			
			VentilatorCleaning c = new VentilatorCleaning(i, name, intervalMin, intervalMax, replaceInterval);
			cleanings.addCleaning(c);
		}
		cleanings.loadLast();
		
		log.debug("load sensors");
		sensors = new Sensors(repo);
		for(String name : settings.getString(SETTING_SENSOR_NAMES, "bath").split("[,]"))
		{
			name = name.trim();
			
			log.trace("load sensor {}", name);
			
			switch(settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_TYPE_SUFFIX, SETTING_SENSOR_TYPE_HTTP))
			{
				case SETTING_SENSOR_TYPE_HTTP ->
				{
					sensors.addSensor(name,
						new HumidSensor(
							settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_URL_SUFFIX, "http://localhost/" + name),
							Duration.ofMillis(
								settings.getInt(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_TIMEOUT_SUFFIX, 1000)),
							settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_KEY_HUMIDITY_SUFFIX, "humidity"),
							settings.getString(SETTING_SENSOR_PREFIX + name + SETTING_SENSOR_KEY_TEMPERATUR_SUFFIX, "temperature")));
				}
				case SETTING_SENSOR_TYPE_SIM ->
				{
					sensors.addSensor(name, new SimSensor());
				}
				default ->
				{
					throw new RuntimeException("unknown sensor type for sensor " + name);
				}
			}
		}
		sensors.loadLast();
		
		log.debug("create ventilation");
		ventilation = new Ventilation(pi, getConfigurations(settings, "normal"), getConfigurations(settings, "bridge"),
				settings.getInt(SETTING_VENTILATION_BRIDGE_MODE, -1),
				settings.getBoolean(SETTING_VENTILATION_BRIDGE_MODE_REVERSE, false));
		
		switch(settings.getString(SETTING_CONTROLLER_TYPE, SETTING_CONTROLLER_TYPE_SIMPLE))
		{
			case SETTING_CONTROLLER_TYPE_SIMPLE ->
			{
				controller = new ControllerSimple(
						settings.getString(SETTING_CONTROLLER_SENSOR, "bath"),
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
		
		int port = settings.getInt(SETTING_WEBSERVER_PORT, 12345);
		if(port > 0)
		{
			new Server(port, this);
		}
		
		env = new Env(sensors, pi, cleanings, settings, ventilation, controller);
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
		
		addEvent(this::checkAllPeriodically);
		
		while(true)
		{
			try
			{
				log.info("wait for next event");
				Event e = events.getNextEvent();
				log.debug("next event {}", e);
				e.action().performAction(events, e);
				log.debug("event finished");
			}
			catch (Exception exc)
			{
				// clear interrupt state just in case
				Thread.interrupted();
				log.error("Error within loop", exc);
			}
		}
	}
	
	public void addEvent(Event e)
	{
		events.addEvent(e);
	}
	
	public void addEvent(EventAction a)
	{
		events.addEvent(new Event(System.currentTimeMillis(), a));
	}
	
	public void addEvent(EnvAction a)
	{
		events.addEvent(new Event(new EA(a)));
	}
	
	private void checkAllPeriodically(EventQueue queue, Event e) throws Exception
	{
		log.info("start periodic check");
		try
		{
			checkAll(queue, e);
		}
		finally
		{
			// TODO
			long delta = settings.getLong(SETTING_CHECK_INTERVAL, 60 * 1000);
			log.debug("add next check in {}", delta);
			queue.addEvent(new Event(System.currentTimeMillis() + delta, this::checkAllPeriodically));
		}
	}
	
	public void checkAll(EventQueue queue, Event e) throws Exception
	{
		log.info("check all");
		sensors.pullSensors();
		
		Level nextLevel = env.controllerManual().check(env);
		
		ventilation.setLevel(nextLevel);
	}
	
	@Override
	public void accessEnv(EnvAction action)
	{
		addEvent(action);
	}
	
	private class EA implements EventAction
	{
		private final EnvAction ea;
		
		public EA(EnvAction ea)
		{
			this.ea = ea;
		}
		
		@Override
		public void performAction(EventQueue queue, Event event) throws Exception
		{
			ea.performAction(env);
		}
	}
}
