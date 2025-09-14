package de.ddd.aircontrol.ventilation;

import java.util.EnumMap;

import de.ddd.aircontrol.pi.Pi;
import de.ddd.aircontrol.pi.PinMode;

public class Ventilation
{
	private final EnumMap<Level, Configuration> configurations;
	private final EnumMap<Level, Configuration> bridgeConfigurations;
	private final int bridgeModeGpioPin;
	private final boolean bridgeModeInvert;
	
	private final Pi pi;
	private volatile Level level;
	private volatile Level bridgeLevel;
	
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
		
		for(Level l : Level.values())
		{
			Configuration config = configurations.get(l);
			
			if(config == null)
			{
				throw new IllegalArgumentException("missing configuration for Level " + l);
			}
			
			for(int pin : config.activeGpioPins)
			{
				pi.getPin(pin).setPinMode(PinMode.DIGITAL_OUT);
			}
			
			for(int pin : config.inactiveGpioPins)
			{
				pi.getPin(pin).setPinMode(PinMode.DIGITAL_OUT);
			}
			
			Configuration bridgeConfig = bridgeConfigurations.get(l);
			
			if(bridgeConfig == null)
			{
				throw new IllegalArgumentException("missing bridgeconfiguration for Level " + l);
			}
			
			for(int pin : bridgeConfig.activeGpioPins)
			{
				pi.getPin(pin).setPinMode(PinMode.DIGITAL_OUT);
			}
			
			for(int pin : bridgeConfig.inactiveGpioPins)
			{
				pi.getPin(pin).setPinMode(PinMode.DIGITAL_OUT);
			}
		}
		
		this.level = readLevel();
		this.bridgeLevel = readBridgeLevel();
		
		if(bridgeModeGpioPin != -1)
		{
			pi.getPin(bridgeModeGpioPin).setPinMode(PinMode.DIGITAL_IN);
		}
	}
	
	public void setLevel(Level level)
	{
		if(this.level == level)
		{
			return;
		}
		
		Configuration config = configurations.get(level);
		
		configure(config);
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	public Level readLevel()
	{
		return readLevel(configurations);
	}
	
	public void setBridgeLevel(Level bridgeLevel)
	{
		if(this.bridgeLevel == bridgeLevel)
		{
			return;
		}
		
		Configuration config = bridgeConfigurations.get(bridgeLevel);
		
		configure(config);
	}
	
	public Level getBridgeLevel()
	{
		return bridgeLevel;
	}
	
	private Level readBridgeLevel()
	{
		return readLevel(bridgeConfigurations);
	}
	
	private void configure(Configuration config)
	{
		for(int active : config.activeGpioPins)
		{
			pi.getPin(active).setDigitalValue(true);
		}
		
		for(int inactive : config.inactiveGpioPins)
		{
			pi.getPin(inactive).setDigitalValue(false);
		}
	}
	
	private Level readLevel(EnumMap<Level, Configuration> configurations)
	{
		lvls:for(Level lvl : configurations.keySet())
		{
			Configuration config = configurations.get(lvl);
			
			for(int active : config.activeGpioPins)
			{
				if(!pi.getPin(active).getDigitalValue())
				{
					continue lvls;
				}
			}
			
			for(int inactive : config.inactiveGpioPins)
			{
				if(pi.getPin(inactive).getDigitalValue())
				{
					continue lvls;
				}
			}
			
			return lvl;
		}
		
		return Level.DEFAULT;
	}
	
	public VentilationMode getVentilationMode()
	{
		if(bridgeModeGpioPin == -1)
		{
			return VentilationMode.UNKNOWN;
		}
		
		boolean state = pi.getPin(bridgeModeGpioPin).getDigitalValue();
		
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
