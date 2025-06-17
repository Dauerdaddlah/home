package de.ddd.aircontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ddd.aircontrol.control.Controller;
import de.ddd.aircontrol.datalog.DataLogger;
import de.ddd.aircontrol.event.EventAction;
import de.ddd.aircontrol.event.EventQueue;
import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.sensor.Sensor;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.settings.Settings;
import de.ddd.aircontrol.ventilation.Level;
import de.ddd.aircontrol.ventilation.Ventilation;

public class Environment
{
	private static final Logger log = LoggerFactory.getLogger(Environment.class);
	
	public static final String SENSOR_BATH = "bath";
//	public static final String KEY_VENTILATION = "ventilation";
//	public static final String KEY_SENSOR = "sensor.";
//	public static final String KEY_SETTINGS = "settings";
//	public static final String KEY_PI = "pi";
//	public static final String KEY_DATALOGGER = "datalogger";
//	public static final String KEY_CONTROLLER = "controller";
//	
	private final Map<String, Object> values;
	private final Ventilation ventilation;
	private final Map<String, Sensor> sensors;
	private final Settings settings;
	private volatile Pi pi;
	private final DataLogger logger;
	private final Controller controller;
	private final EventAction updateAction;
	private final EventQueue eventQueue;
	
	private final Map<String, SensorResult> lastResults;
	private volatile boolean handMode;
	private volatile Level lastLevel;
	private volatile Level lastBridgeLevel;
	private volatile boolean simulation;
	
	public Environment(Ventilation ventilation, Map<String, Sensor> sensors, Settings settings, Pi pi,
			DataLogger logger, Controller controller, EventAction updateAction, EventQueue eventQueue)
	{
		super();
		
		values = new ConcurrentHashMap<>();
		
		this.ventilation = ventilation;
		this.sensors = sensors;
		this.settings = settings;
		this.pi = pi;
		this.logger = logger;
		this.controller = controller;
		this.updateAction = updateAction;
		this.eventQueue = eventQueue;
		
		lastResults = new HashMap<>();
		handMode = false;
		lastLevel = Level.DEFAULT;
		lastBridgeLevel = Level.DEFAULT;
		simulation = false;
	}
	
	public void pullSensors()
	{
		log.info("pull sensors");
		
		for(String key : sensors.keySet())
		{
			Sensor sensor = sensors.get(key);
			
			SensorResult result;
			try
			{
				result = sensor.measure();
				logger.log(key, result);
				
				lastResults.put(key, result);
			}
			catch (Exception e)
			{
				log.error("error on measuring sensor {}", key, e);
				lastResults.put(key, new SensorResult());
			}
		}
	}
	
	public Ventilation getVentilation()
	{
		return ventilation;
	}
	
	public DataLogger getLogger()
	{
		return logger;
	}
	
	public Pi getPi()
	{
		return pi;
	}
	
	public void setPi(Pi pi)
	{
		this.pi = pi;
	}
	
	public Map<String, Sensor> getSensors()
	{
		return sensors;
	}
	
	public Sensor getSensor(String key)
	{
		return sensors.get(key);
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public Map<String, SensorResult> getLastResults()
	{
		return lastResults;
	}
	
	public SensorResult getLastResult(String key)
	{
		return lastResults.get(key);
	}
	
	public boolean isHandMode()
	{
		return handMode;
	}
	
	public void setHandMode(boolean handMode)
	{
		this.handMode = handMode;
	}
	
	public Level getLastLevel()
	{
		return lastLevel;
	}
	
	public void setLastLevel(Level lastLevel)
	{
		this.lastLevel = lastLevel;
	}
	
	public Level getLastBridgeLevel()
	{
		return lastBridgeLevel;
	}
	
	public void setLastBridgeLevel(Level lastBridgeLevel)
	{
		this.lastBridgeLevel = lastBridgeLevel;
	}
	
	public boolean isSimulation()
	{
		return simulation;
	}
	
	public void setSimulation(boolean simulation)
	{
		this.simulation = simulation;
	}
	
	public Controller getController()
	{
		return controller;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key)
	{
		return (T)values.get(key);
	}
	
	public void putValue(String key, Object value)
	{
		values.put(key, value);
	}
	
	public EventAction getUpdateAction()
	{
		return updateAction;
	}
	
	public EventQueue getEventQueue()
	{
		return eventQueue;
	}
}
