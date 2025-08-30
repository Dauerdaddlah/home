package de.ddd.aircontrol.event;

public record Event(long due, EventAction action) implements Comparable<Event>
{
	public Event(EventAction action)
	{
		this(System.currentTimeMillis(), action);
	}
	
	
	
	@Override
	public int compareTo(Event o)
	{
		return Long.compare(due, o.due);
	}
}