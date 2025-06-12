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

	@SuppressWarnings("unchecked")
	@Override
	public <P extends PiPin> P configure(int gpioPin, PinMode mode) throws IllegalArgumentException
	{
		SimPin p = pins[gpioPin];
		
		if(p == null)
		{
			p = new SimPin(gpioPin, mode);
			pins[gpioPin] = p;
		}
		
		if(p.getPinMode() != mode)
		{
			throw new IllegalArgumentException();
		}
		
		return (P)p;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P extends PiPin> P getPin(int gpioPin) throws IllegalArgumentException
	{
		return (P)pins[gpioPin];
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
