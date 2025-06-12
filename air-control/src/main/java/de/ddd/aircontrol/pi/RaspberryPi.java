package de.ddd.aircontrol.pi;

import java.util.HashMap;
import java.util.Map;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.analog.AnalogInput;
import com.pi4j.io.gpio.analog.AnalogOutput;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;

public class RaspberryPi implements Pi
{
	private final Model model;
	private final Context pi4j;
	
	private final Map<Integer, PiPin> pinCache;
	
	public RaspberryPi(Model model)
	{
		this.model = model;
		pi4j = Pi4J.newAutoContext();
		pinCache = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <P extends PiPin> P configure(int gpioPin, PinMode mode) throws IllegalArgumentException
	{
		if(model.toPin(gpioPin) == -1)
		{
			throw new IllegalArgumentException("invalid gpio-pin " + gpioPin);
		}
		
		PiPin pin = pinCache.get(gpioPin);
		
		if(pin == null)
		{
			pin = new Pin(gpioPin, mode);
			pinCache.put(gpioPin, pin);
		}
		
		if(pin.getPinMode() != mode)
		{
			throw new IllegalArgumentException("gpiopin " + gpioPin + " cannot be configured as " + mode
					+ " it is already a " + pin.getPinMode());
		}
		
		return (P)pin;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P extends PiPin> P getPin(int gpioPin) throws IllegalArgumentException
	{
		return (P)pinCache.get(gpioPin);
	}
	
	private class Pin implements PiPinAnalogInput, PiPinAnalogOutput, PiPinDigitalInput, PiPinDigitalOutput
	{
		private final PinMode mode;
		private final AnalogInput analogIn;
		private final AnalogOutput analogOut;
		private final DigitalInput digitalIn;
		private final DigitalOutput digitalOut;
		
		public Pin(int gpioPin, PinMode mode)
		{
			this.mode = mode;
			
			switch(mode)
			{
				case ANALOG_IN ->
				{
					analogIn = pi4j.analogInput().create(gpioPin);
					analogOut = null;
					digitalIn = null;
					digitalOut = null;
				}
				case ANALOG_OUT ->
				{
					analogIn = null;
					analogOut = pi4j.analogOutput().create(gpioPin);
					digitalIn = null;
					digitalOut = null;
				}
				case DIGITAL_IN ->
				{
					analogIn = null;
					analogOut = null;
					digitalIn = pi4j.digitalInput().create(gpioPin);
					digitalOut = null;
				}
				case DIGITAL_OUT ->
				{
					analogIn = null;
					analogOut = null;
					digitalIn = null;
					digitalOut = pi4j.digitalOutput().create(gpioPin);
				}
				default ->
				{
					// should not be able to happen
					throw new RuntimeException();
				}
			}
		}
		
		@Override
		public PinMode getPinMode()
		{
			return mode;
		}

		@Override
		public void setDigitalValue(boolean value)
		{
			if(digitalOut == null)
			{
				throw new UnsupportedOperationException("cannot set digital value, pin is in mode " + mode);
			}
			digitalOut.setState(value);
		}

		@Override
		public boolean getDigitalValue()
		{
			if(digitalIn != null)
			{
				return digitalIn.isHigh();
			}
			
			if(digitalOut != null)
			{
				return digitalOut.isHigh();
			}
			
			throw new UnsupportedOperationException("cannot get digital value, pin is in mode " + mode);
		}

		@Override
		public void setAnalogValue(int value)
		{
			if(analogOut == null)
			{
				throw new UnsupportedOperationException("cannot set analog value, pin is in mode " + mode);
			}
			analogOut.setValue(value);
		}

		@Override
		public int getAnalogValue()
		{
			if(analogIn != null)
			{
				return analogIn.getValue().intValue();
			}
			
			if(analogOut != null)
			{
				return analogOut.getValue().intValue();
			}
			
			throw new UnsupportedOperationException("cannot get analog value, pin is in mode " + mode);
		}
	}
}
