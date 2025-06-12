package de.ddd.aircontrol.ventilation;

public enum VentilationMode
{
	/** normal mode active, no user input happened */
	NORMAL,
	/** bridge mode active, user pressed button to activate it */
	BRIDGE,
	/** mode unknown and may not be readable */
	UNKNOWN;
}
