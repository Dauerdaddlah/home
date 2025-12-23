package de.ddd.aircontrol.cleaning;

import java.time.LocalDateTime;

public class VentilatorCleaning
{
	private final int number;
	private final String name;
	/** minimum cleaning interval in months */
	private final int intervalMin;
	/** maximum cleaning interval in months */
	private final int intervalMax;
	/** maximum interval for replacement in cleaning-intervals */
	private final int replacementInterval;
	
	private LocalDateTime lastCleaning;
	private LocalDateTime lastReplacement;
	private int cleaningsWithoutReplacement;
	
	public VentilatorCleaning(int number, String name, int intervalMin, int intervalMax, int replacementInterval)
	{
		this.number = number;
		this.name = name;
		this.intervalMin = intervalMin;
		this.intervalMax = intervalMax;
		this.replacementInterval = replacementInterval;
	}
	
	public int getIntervalMax()
	{
		return intervalMax;
	}
	
	public int getIntervalMin()
	{
		return intervalMin;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public int getReplacementInterval()
	{
		return replacementInterval;
	}
	
	public LocalDateTime getLastCleaning()
	{
		return lastCleaning;
	}
	
	public void setLastCleaning(LocalDateTime lastCleaning)
	{
		this.lastCleaning = lastCleaning;
	}
	
	public LocalDateTime getLastReplacement()
	{
		return lastReplacement;
	}
	
	public void setLastReplacement(LocalDateTime lastReplacement)
	{
		this.lastReplacement = lastReplacement;
	}
	
	public int getCleaningsWithoutReplacement()
	{
		return cleaningsWithoutReplacement;
	}
	
	public void setCleaningsWithoutReplacement(int cleaningsWithoutReplacement)
	{
		this.cleaningsWithoutReplacement = cleaningsWithoutReplacement;
	}
}
