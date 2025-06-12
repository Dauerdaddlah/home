package de.ddd.aircontrol.pi;

public interface Pi
{
	public <P extends PiPin> P configure(int gpioPin, PinMode mode) throws IllegalArgumentException;
	
	public <P extends PiPin> P getPin(int gpioPin) throws IllegalArgumentException;
}
