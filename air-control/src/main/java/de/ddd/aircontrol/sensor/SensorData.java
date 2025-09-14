package de.ddd.aircontrol.sensor;

public record SensorData(
		Sensor sensor,
		SensorResult lastPull,
		SensorResult lastResult
	)
{
}
