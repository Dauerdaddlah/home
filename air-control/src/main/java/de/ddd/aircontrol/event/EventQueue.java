package de.ddd.aircontrol.event;

import java.util.PriorityQueue;

public class EventQueue
{
	private final PriorityQueue<Event> events;
	
	public EventQueue()
	{
		events = new PriorityQueue<>();
	}
	
	
	public synchronized void addEvent(Event e)
	{
		events.add(e);
		this.notifyAll();
	}
	
	public synchronized Event getNextEvent()
	{
		while(true)
		{
			try
			{
				Event e = events.peek();
				
				if(e == null)
				{
					this.wait();
					continue;
				}
				
				long now = System.currentTimeMillis();
				if(e.due() > now)
				{
					this.wait(e.due() - now);
				}
				
				return events.poll();
			}
			catch(InterruptedException e)
			{
				Thread.interrupted();
			}
		}
	}
}
