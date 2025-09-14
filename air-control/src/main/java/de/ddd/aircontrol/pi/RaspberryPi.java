package de.ddd.aircontrol.pi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.analog.AnalogInput;
import com.pi4j.io.gpio.analog.AnalogOutput;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;

public class RaspberryPi implements Pi
{
	private static final Logger log = LoggerFactory.getLogger(RaspberryPi.class);
	
	private final Model model;
	private final Context pi4j;
	
	private final List<PiPin> pins;
	
	public RaspberryPi(Model model)
	{
		log.debug("create Raspberry pi with model {}", model);
		
		this.model = model;
		pi4j = Pi4J.newAutoContext();
		
		List<PiPin> pins = new ArrayList<>();
		
		for(int i = 0; i < model.getNumPins(); i++)
		{
			pins.add(new Pin(i));
		}
		
		this.pins = Collections.unmodifiableList(pins);
	}
	
	@Override
	public List<PiPin> getAllPins()
	{
		return pins;
	}
	
	@Override
	public PiPin getPin(int gpioPin)
	{
		return pins.get(gpioPin);
	}
	
	@Override
	public int getNumPins()
	{
		return model.getNumPins();
	}

	private class Pin implements PiPin
	{
		private final int pin;
		private PinMode mode;
		private AnalogInput analogIn;
		private AnalogOutput analogOut;
		private DigitalInput digitalIn;
		private DigitalOutput digitalOut;
		
		public Pin(int gpioPin)
		{
			this.pin = gpioPin;
			analogIn = null;
			analogOut = null;
			digitalIn = null;
			digitalOut = null;
			mode = PinMode.UNKNOWN;
		}
		
		@Override
		public PinMode getPinMode()
		{
			return mode;
		}
		
		@Override
		public void setPinMode(PinMode mode) throws IllegalStateException
		{
			if(this.mode == mode)
			{
				return;
			}
			
			if(this.mode != PinMode.UNKNOWN)
			{
				throw new IllegalStateException("cannot set pin " + pin + " to mode " + mode + " it is already set to " + this.mode);
			}
			
			log.trace("set Pin {} to mode {}", pin, mode);
			
			switch(mode)
			{
				case ANALOG_IN ->
					this.analogIn = pi4j.analogInput().create(pin);
					
				case ANALOG_OUT ->
					this.analogOut = pi4j.analogOutput().create(pin);
					
				case DIGITAL_IN ->
					this.digitalIn = pi4j.digitalInput().create(pin);
					
				case DIGITAL_OUT ->
					this.digitalOut = pi4j.digitalOutput().create(pin);
					
				default ->
					throw new IllegalArgumentException("cannot set mode to " + mode + " by hand");
			}
			
			this.mode = mode;
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
