package de.ddd.aircontrol.sensor;

public record SensorData(
		String name,
		Sensor sensor,
		SensorResult lastPull,
		SensorResult lastResult
	)
{
}
