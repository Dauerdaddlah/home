package de.ddd.aircontrol.db;

import java.time.LocalDateTime;

import com.google.gson.JsonObject;

public class SensorData
{
	private int id;
	private String name;
	private LocalDateTime ldt;
	private JsonObject data;
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public LocalDateTime getLdt()
	{
		return ldt;
	}
	
	public void setLdt(LocalDateTime ldt)
	{
		this.ldt = ldt;
	}
	
	public JsonObject getData()
	{
		return data;
	}
	
	public void setData(JsonObject data)
	{
		this.data = data;
	}
}
