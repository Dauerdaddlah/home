package de.ddd.aircontrol.sensor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sensors
{
	private final Map<String, Sensor> sensors;
	private final Map<String, SensorResult> lastPull;
	private final Map<String, SensorResult> lastResult;
	
	public Sensors()
	{
		sensors = new HashMap<>();
		lastPull = new HashMap<>();
		lastResult = new HashMap<>();
	}
	
	public void addSensor(String name, Sensor sensor)
	{
		sensors.put(name, sensor);
	}
	
	public Sensor getSensor(String name)
	{
		return sensors.get(name);
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
	
	public SensorData getData(String sensorName)
	{
		return new SensorData(sensors.get(sensorName), lastPull.get(sensorName), lastResult.get(sensorName));
	}
}
