package de.ddd.aircontrol.ventilation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.ddd.aircontrol.pi.Model;
import de.ddd.aircontrol.pi.SimPi;
import de.ddd.aircontrol.ventilation.Ventilation.Configuration;

class VentilationTest
{
//	private EnumMap<Level, Configuration> configs;
//	EnumMap<Level, Configuration> bridgeConfigs;
//	private Ventilation v;
//	private SimPi pi;
//	
//	@BeforeEach
//	public void before()
//	{
//		configs = new EnumMap<>(Level.class);
//		configs.put(Level.DEFAULT, new Configuration(new int[] { 1 }, new int[] { 2 }));
//		configs.put(Level.OFF, new Configuration(new int[] { 3 }, new int[] { 4 }));
//		configs.put(Level.ONE, new Configuration(new int[] { 5 }, new int[] { 6 }));
//		configs.put(Level.TWO, new Configuration(new int[] { 7 }, new int[] { 8 }));
//		configs.put(Level.THREE, new Configuration(new int[] { 9 }, new int[] { 10 }));
//		
//		bridgeConfigs = new EnumMap<>(Level.class);
//		bridgeConfigs.put(Level.DEFAULT, new Configuration(new int[] { 11 }, new int[] { 12 }));
//		bridgeConfigs.put(Level.OFF, new Configuration(new int[] { 13 }, new int[] { 14 }));
//		bridgeConfigs.put(Level.ONE, new Configuration(new int[] { 15 }, new int[] { 16 }));
//		bridgeConfigs.put(Level.TWO, new Configuration(new int[] { 17 }, new int[] { 18 }));
//		bridgeConfigs.put(Level.THREE, new Configuration(new int[] { 19 }, new int[] { 20 }));
//		
//		pi = new SimPi(Model.PI_3_B);
//		
//		v = new Ventilation(pi, configs, bridgeConfigs, 21, false);
//	}
//
//	@ParameterizedTest
//	@CsvSource({
//		"-1,false,false,UNKNOWN",
//		"21,false,false,NORMAL",
//		"21,false,true,BRIDGE",
//		"21,true,false,BRIDGE",
//		"21,true,true,NORMAL"
//	})
//	public void test_getBridgeMode(int pin, boolean revert, boolean digitalValue, VentilationMode mode)
//	{
//		v = new Ventilation(pi, configs, bridgeConfigs, pin, revert);
//		if(pin != -1)
//		{
//			pi.setDigitalValue(pin, digitalValue);
//		}
//		assertEquals(mode, v.getVentilationMode(pi));
//	}
//	
//	@ParameterizedTest
//	@CsvSource({
//		"0,DEFAULT",
//		"1,DEFAULT",
//		"3,OFF",
//		"5,ONE",
//		"7,TWO",
//		"9,THREE"
//	})
//	public void test_getLevel(int pin, Level level)
//	{
//		pi.setDigitalValue(pin, true);
//		assertEquals(level, v.getLevel(pi));
//	}
//	
//	@ParameterizedTest
//	@CsvSource({
//		"10,DEFAULT",
//		"11,DEFAULT",
//		"13,OFF",
//		"15,ONE",
//		"17,TWO",
//		"19,THREE"
//	})
//	public void test_getBridgeLevel(int pin, Level level)
//	{
//		pi.setDigitalValue(pin, true);
//		assertEquals(level, v.getBridgeLevel(pi));
//	}
//	
//	@ParameterizedTest
//	@CsvSource({
//		"DEFAULT,1,2",
//		"OFF,3,4",
//		"ONE,5,6",
//		"TWO,7,8",
//		"THREE,9,10"
//	})
//	public void test_setLevel(Level level, int pinActive, int pinInactive)
//	{
//		pi.setDigitalValue(pinActive, false);
//		pi.setDigitalValue(pinInactive, true);
//		
//		v.setLevel(level, pi);
//		
//		assertTrue(pi.getDigitalValue(pinActive));
//		assertFalse(pi.getDigitalValue(pinInactive));
//	}
//	
//	@ParameterizedTest
//	@CsvSource({
//		"DEFAULT,11,12",
//		"OFF,13,14",
//		"ONE,15,16",
//		"TWO,17,18",
//		"THREE,19,20"
//	})
//	public void test_setBridgeLevel(Level level, int pinActive, int pinInactive)
//	{
//		pi.setDigitalValue(pinActive, false);
//		pi.setDigitalValue(pinInactive, true);
//		
//		v.setBridgeLevel(level, pi);
//		
//		assertTrue(pi.getDigitalValue(pinActive));
//		assertFalse(pi.getDigitalValue(pinInactive));
//	}
}
