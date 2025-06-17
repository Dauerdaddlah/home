package de.ddd.aircontrol.control;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.ventilation.Level;

class ControllerSimpleTest
{
	private ControllerSimple c;
	
	
	@BeforeEach
	public void before()
	{
		c = new ControllerSimple(40, 50, 60, 30, 40, 50);
	}
	
	@Test
	public void test_doNothingOnNan()
	{
		SensorResult res = new SensorResult(Double.NaN, Double.NaN);
		var results = Map.of(Environment.SENSOR_BATH, res);
		
		for(Level lvl : Level.values())
		{
			assertEquals(lvl, c.check(lvl, results));
		}
	}

	@ParameterizedTest
	@CsvSource({
		"DEFAULT,39,NaN,OFF",
		"OFF,39,NaN,OFF",
		"DEFAULT,40,NaN,ONE",
		"OFF,40,NaN,ONE",
		"DEFAULT,41,NaN,ONE",
		"OFF,41,NaN,ONE",
		"DEFAULT,49,NaN,ONE",
		"OFF,49,NaN,ONE"
	})
	public void test_start1(Level lastLevel, double h, double t, Level res)
	{
		Level nextLevel = c.check(lastLevel, Map.of(Environment.SENSOR_BATH, new SensorResult(h, t)));
		assertEquals(res, nextLevel);
	}
	
	@ParameterizedTest
	@CsvSource({
		"DEFAULT,50,NaN,TWO",
		"OFF,50,NaN,TWO",
		"ONE,50,NaN,TWO",
		"DEFAULT,51,NaN,TWO",
		"OFF,51,NaN,TWO",
		"ONE,51,NaN,TWO",
		"DEFAULT,59,NaN,TWO",
		"OFF,59,NaN,TWO",
		"ONE,59,NaN,TWO"
	})
	public void test_start2(Level lastLevel, double h, double t, Level res)
	{
		Level nextLevel = c.check(lastLevel, Map.of(Environment.SENSOR_BATH, new SensorResult(h, t)));
		assertEquals(res, nextLevel);
	}
	
	@ParameterizedTest
	@CsvSource({
		"DEFAULT,60,NaN,THREE",
		"OFF,60,NaN,THREE",
		"ONE,60,NaN,THREE",
		"TWO,60,NaN,THREE",
		"DEFAULT,61,NaN,THREE",
		"OFF,61,NaN,THREE",
		"ONE,61,NaN,THREE",
		"TWO,61,NaN,THREE",
		"DEFAULT,69,NaN,THREE",
		"OFF,69,NaN,THREE",
		"ONE,69,NaN,THREE",
		"DEFAULT,100,NaN,THREE",
		"OFF,100,NaN,THREE",
		"ONE,100,NaN,THREE",
		"TWO,100,NaN,THREE"
	})
	public void test_start3(Level lastLevel, double h, double t, Level res)
	{
		Level nextLevel = c.check(lastLevel, Map.of(Environment.SENSOR_BATH, new SensorResult(h, t)));
		assertEquals(res, nextLevel);
	}
	
	@ParameterizedTest
	@CsvSource({
		"ONE,30,NaN,ONE",
		"ONE,29,NaN,OFF",
		"ONE,0,NaN,OFF",
		"TWO,30,NaN,ONE",
		"TWO,29,NaN,OFF",
		"TWO,0,NaN,OFF",
		"THREE,30,NaN,ONE",
		"THREE,29,NaN,OFF",
		"THREE,0,NaN,OFF"
	})
	public void test_end1(Level lastLevel, double h, double t, Level res)
	{
		Level nextLevel = c.check(lastLevel, Map.of(Environment.SENSOR_BATH, new SensorResult(h, t)));
		assertEquals(res, nextLevel);
	}
	
	@ParameterizedTest
	@CsvSource({
		"TWO,40,NaN,TWO",
		"TWO,39,NaN,ONE",
		"TWO,30,NaN,ONE",
		"THREE,40,NaN,TWO",
		"THREE,39,NaN,ONE",
		"THREE,30,NaN,ONE"
	})
	public void test_end2(Level lastLevel, double h, double t, Level res)
	{
		Level nextLevel = c.check(lastLevel, Map.of(Environment.SENSOR_BATH, new SensorResult(h, t)));
		assertEquals(res, nextLevel);
	}
	
	@ParameterizedTest
	@CsvSource({
		"THREE,50,NaN,THREE",
		"THREE,49,NaN,TWO",
		"THREE,40,NaN,TWO"
	})
	public void test_end3(Level lastLevel, double h, double t, Level res)
	{
		Level nextLevel = c.check(lastLevel, Map.of(Environment.SENSOR_BATH, new SensorResult(h, t)));
		assertEquals(res, nextLevel);
	}
}
