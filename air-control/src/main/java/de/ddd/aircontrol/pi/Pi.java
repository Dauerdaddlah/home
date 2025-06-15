package de.ddd.aircontrol.pi;

public interface Pi
{
	public int getAnalogValue(int gpioPin);
	public void setAnalogValue(int gpioPin, int value);
	
	public boolean getDigitalValue(int gpioPin);
	public void setDigitalValue(int gpioPin, boolean value);
	
	public void configure(int gpioPin, PinMode mode) throws IllegalArgumentException;
}
