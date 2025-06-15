package de.ddd.aircontrol.ventilation;

import java.util.EnumMap;

import de.ddd.aircontrol.Environment;
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
	
	public void setLevel(Level level, Environment env)
	{
		Configuration config = configurations.get(level);
		
		configure(config, env);
	}
	
	public Level getLevel(Environment env)
	{
		return getLevel(env, configurations);
	}
	
	public void setBridgeLevel(Level bridgeLevel, Environment env)
	{
		Configuration config = bridgeConfigurations.get(bridgeLevel);
		
		configure(config, env);
	}
	
	public Level getBridgeLevel(Environment env)
	{
		return getLevel(env, bridgeConfigurations);
	}
	
	private void configure(Configuration config, Environment env)
	{
		for(int active : config.activeGpioPins)
		{
			env.getPi().setDigitalValue(active, true);
		}
		
		for(int inactive : config.inactiveGpioPins)
		{
			env.getPi().setDigitalValue(inactive, false);
		}
	}
	
	private Level getLevel(Environment env, EnumMap<Level, Configuration> configurations)
	{
		lvls:for(Level lvl : configurations.keySet())
		{
			Configuration config = configurations.get(lvl);
			
			for(int active : config.activeGpioPins)
			{
				if(!env.getPi().getDigitalValue(active))
				{
					continue lvls;
				}
			}
			
			for(int inactive : config.inactiveGpioPins)
			{
				if(env.getPi().getDigitalValue(inactive))
				{
					continue lvls;
				}
			}
			
			return lvl;
		}
		
		return Level.DEFAULT;
	}
	
	public VentilationMode getVentilationMode(Environment env)
	{
		if(bridgeModeGpioPin == -1)
		{
			return VentilationMode.UNKNOWN;
		}
		
		boolean state = env.getPi().getDigitalValue(bridgeModeGpioPin);
		
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
