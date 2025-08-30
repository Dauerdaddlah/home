package de.ddd.aircontrol.sensor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sensors
{
	private final Map<String, Sensor> sensors;
	private final Map<String, SensorResult> lastPull;
	private final Map<String, SensorResult> lastResult;
	
	public Sensors()
	{
		sensors = new ConcurrentHashMap<>();
		lastPull = new ConcurrentHashMap<>();
		lastResult = new ConcurrentHashMap<>();
	}
	
	public void addSensor(String name, Sensor sensor)
	{
		sensors.put(name, sensor);
	}
	
	public Map<String, SensorResult> pullSensors()
	{
		lastPull.clear();
		
		for(String key : sensors.keySet())
		{
			try
			{
				SensorResult result = sensors.get(key).measure();
				lastResult.put(key, result);
				lastPull.put(key, result);
			}
			catch(Exception e)
			{
			}
		}
		
		return getLastResults();
	}
	
	public Map<String, SensorResult> getLastResults()
	{
		return Collections.unmodifiableMap(lastPull);
	}
}
