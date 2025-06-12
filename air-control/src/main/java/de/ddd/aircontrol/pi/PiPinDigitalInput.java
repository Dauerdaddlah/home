package de.ddd.aircontrol.pi;

public interface PiPinDigitalInput extends PiPinDigital
{
	public boolean getDigitalValue();
	
	@Override
	public default PinMode getPinMode()
	{
		return PinMode.DIGITAL_IN;
	}
}
