package de.ddd.aircontrol.pi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimPi implements Pi
{
	private final Model model;
	
	private final List<SimPin> pins;
	
	public SimPi(Model model)
	{
		this.model = model;
		
		List<SimPin> pins = new ArrayList<>();
		
		for(int i = 0; i < model.getNumPins(); i++)
		{
			pins.add(new SimPin(i + 1, PinMode.UNKNOWN));
		}
		
		this.pins = Collections.unmodifiableList(pins);
	}
	
	@Override
	public int getNumPins()
	{
		return model.getNumPins();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<PiPin> getAllPins()
	{
		return (List)pins;
	}
	
	@Override
	public PiPin getPin(int gpioPin)
	{
		return pins.get(gpioPin);
	}

	private static class SimPin implements PiPin
	{
//		private final int gpioPin;
		private PinMode mode;
		
		private int value;
		
		public SimPin(int gpioPin, PinMode mode)
		{
//			this.gpioPin = gpioPin;
			this.mode = mode;
		}
		
		@Override
		public void setPinMode(PinMode mode) throws IllegalStateException
		{
			if(this.mode != mode && this.mode != PinMode.UNKNOWN)
			{
				throw new IllegalArgumentException("pinmode already set to " + mode);
			}
			
			this.mode = mode;
		}
		
		// TODO add mode checks
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
