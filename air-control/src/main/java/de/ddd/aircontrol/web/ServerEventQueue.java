package de.ddd.aircontrol.web;

import de.ddd.aircontrol.EnvAction;
import de.ddd.aircontrol.event.Event;
import de.ddd.aircontrol.event.EventQueue;

public interface ServerEventQueue
{
	public void addEvent(Event event);
	public void accessEnv(EnvAction action);
	public void checkAll(EventQueue queue, Event event) throws Exception;
}
