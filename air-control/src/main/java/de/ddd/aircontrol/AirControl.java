package de.ddd.aircontrol;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class AirControl
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
	
	private final Settings settings;
	private final Gui gui;
	private final Pi pi;
	private final DataLogger dataLogger;
	private final Ventilation ventilation;
	
	private final Map<String, Sensor> sensors;
	
	public AirControl()
	{
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
		
		log.debug("create data logger");
		dataLogger = new DataLoggerFile(Paths.get(settings.getString(SETTING_DATA_LOG, "./log/data.log")));
		
		log.debug("load sensors");
		sensors = new HashMap<>();
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
		ventilation = new Ventilation(pi, getConfigurations(settings, "normal"), getConfigurations(settings, "bridge"),
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
			throw new RuntimeException("could not create GUI");
		}
		
		this.gui = refGui.get();
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
		while(true)
		{
			
		}
	}
}
