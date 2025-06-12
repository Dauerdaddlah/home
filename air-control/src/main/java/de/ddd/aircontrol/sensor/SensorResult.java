package de.ddd.aircontrol.sensor;

public record SensorResult(
		/** the humidity measured in percent. This is guaranteed to be between 0 and 100 inclusive or NaN if not found */
		double humidity,
		/** the temperature result in celsius. NaN if not found at all */
		double temperature
	)
{
	public SensorResult
	{
		if(!Double.isNaN(humidity) && (humidity < 0 || humidity > 100))
		{
			throw new IllegalArgumentException("humidity must be between 0 and 100 or NaN");
		}
	}
	
	public boolean hasTemperature()
	{
		return !Double.isNaN(temperature);
	}
	
	public boolean hasHumidity()
	{
		return !Double.isNaN(humidity);
	}
}
