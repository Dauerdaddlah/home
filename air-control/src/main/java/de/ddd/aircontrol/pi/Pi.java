package de.ddd.aircontrol.pi;

import java.util.List;

public interface Pi
{
	public List<PiPin> getAllPins();
	public int getNumPins();
	public PiPin getPin(int gpioPin);
}
