package de.ddd.aircontrol.pi;

public class SimPi implements Pi
{
//	private final Model model;
	
	private final SimPin[] pins;
	
	public SimPi(Model model)
	{
//		this.model = model;
		pins = new SimPin[model.getNumPins()];
	}
	
	@Override
	public boolean getDigitalValue(int gpioPin)
	{
		return getPin(gpioPin, PinMode.DIGITAL_IN).getDigitalValue();
	}
	
	@Override
	public void setDigitalValue(int gpioPin, boolean value)
	{
		getPin(gpioPin, PinMode.DIGITAL_OUT).getDigitalValue();
	}
	
	@Override
	public int getAnalogValue(int gpioPin)
	{
		return getPin(gpioPin, PinMode.ANALOG_IN).getAnalogValue();
	}
	
	@Override
	public void setAnalogValue(int gpioPin, int value)
	{
		getPin(gpioPin, PinMode.ANALOG_OUT).setAnalogValue(value);
	}

	@Override
	public void configure(int gpioPin, PinMode mode) throws IllegalArgumentException
	{
		SimPin p = getPin(gpioPin, mode);
		
		if(p.getPinMode() != mode)
		{
			throw new IllegalArgumentException();
		}
	}
	
	private SimPin getPin(int gpioPin, PinMode mode)
	{
		if(pins[gpioPin] == null)
		{
			pins[gpioPin] = new SimPin(gpioPin, mode);
		}
		
		return pins[gpioPin];
	}

	private static class SimPin implements PiPinDigitalInput, PiPinDigitalOutput, PiPinAnalogOutput, PiPinAnalogInput
	{
//		private final int gpioPin;
		private final PinMode mode;
		
		private int value;
		
		public SimPin(int gpioPin, PinMode mode)
		{
//			this.gpioPin = gpioPin;
			this.mode = mode;
		}
		
		@Override
		public int getAnalogValue()
		{
			return value;
		}
		
		@Override
		public boolean getDigitalValue()
		{
			return value > 0;
		}
		
		@Override
		public void setAnalogValue(int value)
		{
			this.value = value;
		}
		
		@Override
		public void setDigitalValue(boolean value)
		{
			this.value = value ? 1 : 0;
		}
		
		@Override
		public PinMode getPinMode()
		{
			return mode;
		}
	}
}
