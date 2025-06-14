package de.ddd.aircontrol;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ddd.aircontrol.datalog.DataLogger;
import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.sensor.Sensor;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.settings.Settings;
import de.ddd.aircontrol.ventilation.Ventilation;

public class Environment
{
	private static final Logger log = LoggerFactory.getLogger(Environment.class);
	
	public static final String SENSOR_BATH = "bath";
	
	private final Ventilation ventilation;
	private final Map<String, Sensor> sensors;
	private final Settings settings;
	private final Pi pi;
	private final DataLogger logger;
	
	private final Map<String, SensorResult> lastResults;
	
	public Environment(Ventilation ventilation, Map<String, Sensor> sensors, Settings settings, Pi pi,
			DataLogger logger)
	{
		super();
		this.ventilation = ventilation;
		this.sensors = sensors;
		this.settings = settings;
		this.pi = pi;
		this.logger = logger;
		
		lastResults = new HashMap<>();
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
}
