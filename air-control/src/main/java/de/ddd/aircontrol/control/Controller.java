package de.ddd.aircontrol.control;

import de.ddd.aircontrol.Env;
import de.ddd.aircontrol.ventilation.Level;

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
	 * @param env the environment to perform on
	 * @return the next level, the ventilation shall have
	 */
	public Level check(Env env);
}
