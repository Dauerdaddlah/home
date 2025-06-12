package de.ddd.aircontrol.pi;

public interface PiPinAnalogOutput extends PiPinAnalog
{
	public void setAnalogValue(int value);
	
	@Override
	public default PinMode getPinMode()
	{
		return PinMode.ANALOG_OUT;
	}
}
