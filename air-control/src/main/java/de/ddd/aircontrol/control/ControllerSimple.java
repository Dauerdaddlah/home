package de.ddd.aircontrol.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.sensor.Sensors;
import de.ddd.aircontrol.ventilation.Level;
import de.ddd.aircontrol.ventilation.Ventilation;

public class ControllerSimple implements Controller
{
	private static final Logger log = LoggerFactory.getLogger(ControllerSimple.class);
	
	private volatile int start1;
	private volatile int start2;
	private volatile int start3;
	private volatile int end1;
	private volatile int end2;
	private volatile int end3;
	
	public ControllerSimple(int start1, int start2, int start3, int end1, int end2, int end3)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.start1 = start1;
		this.start2 = start2;
		this.start3 = start3;
		this.end1 = end1;
		this.end2 = end2;
		this.end3 = end3;
	}
	
	@Override
	public Level check(Ventilation ventilation, Sensors sensors)
	{
		log.info("check new level for ventilation");
		
		var sensorResults = sensors.getLastResults();
		SensorResult res = sensorResults.get(Environment.SENSOR_BATH);
		
		Level lastLevel = ventilation.getLevel();
		
		if(res == null || !res.hasHumidity())
		{
			log.debug("no bath data to process, abort");
			return lastLevel;
		}
		
		int h = (int)res.humidity();
		
		final Level lvl;
		
		log.trace("current level {}, humidity {}", lastLevel, h);
		
		switch(lastLevel)
		{
			default ->
			{
				if(h >= start3)
				{
					lvl = Level.THREE;
				}
				else if(h >= start2)
				{
					lvl = Level.TWO;
				}
				else if(h >= start1)
				{
					lvl = Level.ONE;
				}
				else
				{
					lvl = Level.OFF;
				}
			}
			case ONE ->
			{
				if(h >= start3)
				{
					lvl = Level.THREE;
				}
				else if(h >= start2)
				{
					lvl = Level.TWO;
				}
				else if(h < end1)
				{
					lvl = Level.OFF;
				}
				else
				{
					lvl = Level.ONE;
				}
			}
			case TWO ->
			{
				if(h >= start3)
				{
					lvl = Level.THREE;
				}
				else if(h < end1)
				{
					lvl = Level.OFF;
				}
				else if(h < end2)
				{
					lvl = Level.ONE;
				}
				else
				{
					lvl = Level.TWO;
				}
			}
			case THREE ->
			{
				if(h < end1)
				{
					lvl = Level.OFF;
				}
				else if(h < end2)
				{
					lvl = Level.ONE;
				}
				else if(h < end3)
				{
					lvl = Level.TWO;
				}
				else
				{
					lvl = Level.THREE;
				}
			}
		}
		
		log.debug("switch ventilation to level {}", lvl);
		return lvl;
	}
	
	public int getStart1()
	{
		return start1;
	}
	
	public synchronized void setStart1(int start1)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.start1 = start1;
	}
	
	public int getStart2()
	{
		return start2;
	}
	
	public synchronized void setStart2(int start2)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.start2 = start2;
	}
	
	public int getStart3()
	{
		return start3;
	}
	
	public synchronized void setStart3(int start3)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.start3 = start3;
	}
	
	public int getEnd1()
	{
		return end1;
	}
	
	public synchronized void setEnd1(int end1)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.end1 = end1;
	}
	
	public int getEnd2()
	{
		return end2;
	}
	
	public synchronized void setEnd2(int end2)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.end2 = end2;
	}
	
	public int getEnd3()
	{
		return end3;
	}
	
	public synchronized void setEnd3(int end3)
	{
		ensureIntegrity(start1, start2, start3, end1, end2, end3);
		
		this.end3 = end3;
	}
	
	private void ensureIntegrity(int start1, int start2, int start3, int end1, int end2, int end3)
	{
		if(start1 < 0 | start1 > 100 || start2 < 0 || start2 > 100 || start3 < 0 || start3 > 100
			|| end1 < 0 || end1 > 100 || end2 < 0 || end2 > 100 || end3 < 0 || end3 > 100)
		{
			throw new IllegalArgumentException("All values must be between 0 and 100 inclusive");
		}
		
		if(start1 > start2 || start2 > start3 || start1 < end1 || start2 < end2 || start3 < end3)
		{
			throw new IllegalArgumentException("configuration must meet following criteria: start1 <= start2 <= start3 and end <= start");
		}
	}
}
