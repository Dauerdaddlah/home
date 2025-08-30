package de.ddd.aircontrol.control;

import de.ddd.aircontrol.sensor.Sensors;
import de.ddd.aircontrol.ventilation.Level;
import de.ddd.aircontrol.ventilation.Ventilation;

/**
 * A controller is a unit, that checks the data given in an Environment and
 * calculates the new Level, the ventilation should use
 */
public interface Controller
{
	/**
	 * check which level should be active next
	 * @param ventilation 
	 * 
	 * @param currentLevel the ventilation level that is currently used
	 * @param sensorResults all results of all sensors
	 * @return the next level, the ventilation shall have
	 */
	public Level check(Ventilation ventilation, Sensors sensors);
}
