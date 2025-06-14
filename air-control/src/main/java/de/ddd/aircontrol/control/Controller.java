package de.ddd.aircontrol.control;

import de.ddd.aircontrol.Environment;

/**
 * A controller is a unit, that checks the data given in an Environment and
 * calculates the new Level, the ventilation should use
 */
public interface Controller
{
	public void check(Environment environment);
}
