package de.ddd.aircontrol.db;

import java.time.LocalDateTime;

public class Cleaning
{
	private int id;
	private int number;
	private LocalDateTime ldt;
	private boolean replaced;
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public void setNumber(int number)
	{
		this.number = number;
	}
	
	public LocalDateTime getLdt()
	{
		return ldt;
	}
	
	public void setLdt(LocalDateTime ldt)
	{
		this.ldt = ldt;
	}
	
	public boolean isReplaced()
	{
		return replaced;
	}
	
	public void setReplaced(boolean replaced)
	{
		this.replaced = replaced;
	}
}
