package de.ddd.aircontrol.pi;

public interface PiPinDigitalOutput extends PiPinDigital
{
	public void setDigitalValue(boolean value);
	
	@Override
	public default PinMode getPinMode()
	{
		return PinMode.DIGITAL_OUT;
	}
}
