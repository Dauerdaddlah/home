package de.ddd.aircontrol.event;

public interface EventQueue
{
	public default void addAction(EventAction action)
	{
		addEvent(new Event(System.currentTimeMillis(), action));
	}
	
	public void addEvent(Event e);
}
