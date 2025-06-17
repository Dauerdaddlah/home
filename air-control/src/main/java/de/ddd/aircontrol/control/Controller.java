package de.ddd.aircontrol.control;

import java.util.Map;

import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.ventilation.Level;

/**
 * A controller is a unit, that checks the data given in an Environment and
 * calculates the new Level, the ventilation should use
 */
public interface Controller
{
	/**
	 * check which level should be active next
	 * 
	 * @param currentLevel the ventilation level that is currently used
	 * @param sensorResults all results of all sensors
	 * @return the next level, the ventilation shall have
	 */
	public Level check(Level currentLevel, Map<String, SensorResult> sensorResults);
}
