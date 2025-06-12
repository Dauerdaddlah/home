package de.ddd.aircontrol.sensor;

public interface Sensor
{
	/**
	 * Get the current sensor-data from this sensor.
	 * This might take a while and might fail, but never blocks infinitely
	 * 
	 * @return
	 * @throws Exception
	 */
	public SensorResult measure() throws Exception;
}
