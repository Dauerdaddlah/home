package de.ddd.aircontrol.event;

public record Event(long due, EventAction action) implements Comparable<Event>
{
	@Override
	public int compareTo(Event o)
	{
		return Long.compare(due, o.due);
	}
}