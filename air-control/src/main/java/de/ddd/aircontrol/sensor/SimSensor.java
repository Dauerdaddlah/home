package de.ddd.aircontrol.sensor;

public class SimSensor implements Sensor
{
	private volatile SensorResult result = new SensorResult(Double.NaN, Double.NaN);
	private long waitTime = 0;
	
	public void setWaitTime(long waitTime)
	{
		this.waitTime = waitTime;
	}
	
	public long getWaitTime()
	{
		return waitTime;
	}
	
	public void setResult(SensorResult result)
	{
		this.result = result;
	}
	
	@Override
	public SensorResult measure() throws Exception
	{
		if(waitTime > 0)
		{
			synchronized (this)
			{
				this.wait(waitTime);
			}
		}
		return result;
	}
}
