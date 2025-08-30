package de.ddd.aircontrol.control;

import java.util.Objects;

import de.ddd.aircontrol.sensor.Sensors;
import de.ddd.aircontrol.ventilation.Level;
import de.ddd.aircontrol.ventilation.Ventilation;

public class ControllerManual implements Controller
{
	private Level destLevel = null;
	/** time in ms when the manual set should be reverted */
	private long until = 0;
	
	private final Controller next;
	
	public ControllerManual(Controller next)
	{
		this.next = Objects.requireNonNull(next);
	}
	
	@Override
	public Level check(Ventilation ventilation, Sensors sensors)
	{
		if(destLevel != null)
		{
			if(until == 0)
			{
				// level explicitely set for always
				return destLevel;
			}
			else if(until > System.currentTimeMillis())
			{
				// time run out -> revert state
				destLevel = null;
			}
			else
			{
				// timed set still active
				return destLevel;
			}
		}
		
		return next.check(ventilation, sensors);
	}
	
	public void setDestLevelFor(Level destLevel, long time)
	{
		setDestLevelUntil(destLevel, System.currentTimeMillis() + time);
	}
	
	public void setDestLevelUntil(Level destLevel, long until)
	{
		this.destLevel = destLevel;
		this.until = until;
	}
	
	public void setDestLevel(Level destLevel)
	{
		setDestLevelUntil(destLevel, 0);
	}
	
	public Level getDestLevel()
	{
		return destLevel;
	}
	
	public long getUntil()
	{
		return until;
	}
}
