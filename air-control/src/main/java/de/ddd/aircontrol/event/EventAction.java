package de.ddd.aircontrol.event;

public interface EventAction
{
	public void performAction(EventQueue queue, Event event) throws Exception;
}
