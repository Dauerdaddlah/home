package de.ddd.aircontrol.ventilation;

import java.util.EnumMap;

import de.ddd.aircontrol.pi.Pi;

public class Ventilation
{
	private final EnumMap<Level, Configuration> configurations;
	private final EnumMap<Level, Configuration> bridgeConfigurations;
	private final int bridgeModeGpioPin;
	private final boolean bridgeModeInvert;
	
	public Ventilation(Pi pi,
			EnumMap<Level, Configuration> configurations,
			EnumMap<Level, Configuration> bridgeConfigurations,
			int bridgeModeGpioPin,
			boolean bridgeModeInvert)
	{
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
			
			Configuration bridgeConfig = configurations.get(l);
			
			if(bridgeConfig == null)
			{
				throw new IllegalArgumentException("missing bridgeconfiguration for Level " + l);
			}
		}
	}
	
	public void setLevel(Level level, Pi pi)
	{
		Configuration config = configurations.get(level);
		
		configure(config, pi);
	}
	
	public Level getLevel(Pi pi)
	{
		return getLevel(pi, configurations);
	}
	
	public void setBridgeLevel(Level bridgeLevel, Pi pi)
	{
		Configuration config = bridgeConfigurations.get(bridgeLevel);
		
		configure(config, pi);
	}
	
	public Level getBridgeLevel(Pi pi)
	{
		return getLevel(pi, bridgeConfigurations);
	}
	
	private void configure(Configuration config, Pi pi)
	{
		for(int active : config.activeGpioPins)
		{
			pi.setDigitalValue(active, true);
		}
		
		for(int inactive : config.inactiveGpioPins)
		{
			pi.setDigitalValue(inactive, false);
		}
	}
	
	private Level getLevel(Pi pi, EnumMap<Level, Configuration> configurations)
	{
		lvls:for(Level lvl : configurations.keySet())
		{
			Configuration config = configurations.get(lvl);
			
			for(int active : config.activeGpioPins)
			{
				if(!pi.getDigitalValue(active))
				{
					continue lvls;
				}
			}
			
			for(int inactive : config.inactiveGpioPins)
			{
				if(pi.getDigitalValue(inactive))
				{
					continue lvls;
				}
			}
			
			return lvl;
		}
		
		return Level.DEFAULT;
	}
	
	public VentilationMode getVentilationMode(Pi pi)
	{
		if(bridgeModeGpioPin == -1)
		{
			return VentilationMode.UNKNOWN;
		}
		
		boolean state = pi.getDigitalValue(bridgeModeGpioPin);
		
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
