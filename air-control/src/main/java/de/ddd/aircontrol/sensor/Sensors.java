package de.ddd.aircontrol.sensor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.ddd.aircontrol.db.Repository;

public class Sensors
{
	private final Repository repo;
	private final Gson gson;
	
	private final Map<String, Sensor> sensors;
	private final Map<String, SensorResult> lastPull;
	private final Map<String, SensorResult> lastResult;
	
	public Sensors(Repository repo)
	{
		this.repo = repo;
		
		this.gson = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.create();
		
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
				
				var sd = new de.ddd.aircontrol.db.SensorData();
				sd.setName(key);
				sd.setLdt(LocalDateTime.now());
				sd.setData(gson.toJsonTree(result).getAsJsonObject());
				repo.create(sd);
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
	
	public Set<String> getSensorNames()
	{
		return sensors.keySet();
	}
	
	public SensorData getData(String sensorName)
	{
		return new SensorData(sensorName, sensors.get(sensorName), lastPull.get(sensorName), lastResult.get(sensorName));
	}

	public void loadLast()
	{
		for(var sd : repo.getLastSensorResults())
		{
			SensorResult res = gson.fromJson(sd.getData(), SensorResult.class);
			lastPull.put(sd.getName(), res);
			lastResult.put(sd.getName(), res);
		}
	}
}
