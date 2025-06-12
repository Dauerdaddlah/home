package de.ddd.aircontrol.pi;

public interface PiPinAnalogInput extends PiPinAnalog
{
	public int getAnalogValue();
	
	@Override
	public default PinMode getPinMode()
	{
		return PinMode.ANALOG_IN;
	}
}
