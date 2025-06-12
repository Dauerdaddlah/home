package de.ddd.aircontrol.ventilation;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.pi.PiPinDigitalInput;
import de.ddd.aircontrol.pi.PiPinDigitalOutput;
import de.ddd.aircontrol.pi.PinMode;

public class Ventilation
{
	private final Pi pi;
	private final EnumMap<Level, Configuration> configurations;
	private final EnumMap<Level, Configuration> bridgeConfigurations;
	private final int bridgeModeGpioPin;
	private final boolean bridgeModeInvert;
	
	private Level level;
	private Level bridgeLevel;
	
	public Ventilation(Pi pi,
			EnumMap<Level, Configuration> configurations,
			EnumMap<Level, Configuration> bridgeConfigurations,
			int bridgeModeGpioPin,
			boolean bridgeModeInvert)
	{
		this.pi = pi;
		this.configurations = configurations;
		this.bridgeConfigurations = bridgeConfigurations;
		this.bridgeModeGpioPin = bridgeModeGpioPin;
		this.bridgeModeInvert = bridgeModeInvert;
		
		Set<Integer> pins = new HashSet<>();
		
		for(Level l : Level.values())
		{
			Configuration config = configurations.get(l);
			
			if(config == null)
			{
				throw new IllegalArgumentException("missing configuration for Level " + level);
			}
			
			for(int gpioPin : config.activeGpioPins)
			{
				if(pins.add(gpioPin))
				{
					pi.<PiPinDigitalOutput>configure(gpioPin, PinMode.DIGITAL_OUT).setDigitalValue(false);
				}
			}
			
			for(int gpioPin : config.inactiveGpioPins)
			{
				if(pins.add(gpioPin))
				{
					pi.<PiPinDigitalOutput>configure(gpioPin, PinMode.DIGITAL_OUT).setDigitalValue(false);
				}
			}
			
			Configuration bridgeConfig = configurations.get(l);
			
			if(bridgeConfig == null)
			{
				throw new IllegalArgumentException("missing bridgeconfiguration for Level " + level);
			}
			
			for(int gpioPin : bridgeConfig.activeGpioPins)
			{
				if(pins.add(gpioPin))
				{
					pi.<PiPinDigitalOutput>configure(gpioPin, PinMode.DIGITAL_OUT).setDigitalValue(false);
				}
			}
			
			for(int gpioPin : bridgeConfig.inactiveGpioPins)
			{
				if(pins.add(gpioPin))
				{
					pi.<PiPinDigitalOutput>configure(gpioPin, PinMode.DIGITAL_OUT).setDigitalValue(false);
				}
			}
		}
		
		if(bridgeModeGpioPin != -1)
		{
			pi.configure(bridgeModeGpioPin, PinMode.DIGITAL_IN);
		}
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	public void setLevel(Level level)
	{
		if(this.level != level)
		{
			this.level = level;
			
			Configuration config = configurations.get(level);
			
			for(int active : config.activeGpioPins)
			{
				pi.<PiPinDigitalOutput>getPin(active).setDigitalValue(true);
			}
			
			for(int inactive : config.inactiveGpioPins)
			{
				pi.<PiPinDigitalOutput>getPin(inactive).setDigitalValue(false);
			}
		}
	}
	
	public Level getBridgeLevel()
	{
		return bridgeLevel;
	}
	
	public void setBridgeLevel(Level bridgeLevel)
	{
		if(this.bridgeLevel != bridgeLevel)
		{
			this.bridgeLevel = bridgeLevel;
			
			Configuration config = bridgeConfigurations.get(bridgeLevel);
			
			for(int active : config.activeGpioPins)
			{
				pi.<PiPinDigitalOutput>getPin(active).setDigitalValue(true);
			}
			
			for(int inactive : config.inactiveGpioPins)
			{
				pi.<PiPinDigitalOutput>getPin(inactive).setDigitalValue(false);
			}
		}
	}
	
	public VentilationMode getVentilationMode()
	{
		if(bridgeModeGpioPin == -1)
		{
			return VentilationMode.UNKNOWN;
		}
		
		boolean state = pi.<PiPinDigitalInput>getPin(bridgeModeGpioPin).getDigitalValue();
		
		if(bridgeModeInvert)
		{
			state = !state;
		}
		
		return state ? VentilationMode.BRIDGE : VentilationMode.NORMAL;
	}
	
	public static record Configuration(
			int[] activeGpioPins,
			int[] inactiveGpioPins
		)
	{
		
	}
}
