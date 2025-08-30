package de.ddd.aircontrol.event;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class EventQueue
{
	private final BlockingDeque<Event> events;
	
	public EventQueue()
	{
		events = new LinkedBlockingDeque<>();
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
