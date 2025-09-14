package de.ddd.aircontrol.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.Test;

public class EventQueueTest
{
	@Test
	public void test_getNextEvent()
	{
		EventQueue q = new EventQueue();
		Event e1 = new Event(0, null);
		Event e2 = new Event(1, null);
		
		q.addEvent(e1);
		q.addEvent(e2);
		
		assertEquals(e1, q.getNextEvent());
	}
	
	@Test
	public void test_getNextEvent2()
	{
		EventQueue q = new EventQueue();
		Event e1 = new Event(0, null);
		Event e2 = new Event(1, null);
		
		q.addEvent(e2);
		q.addEvent(e1);
		
		assertEquals(e1, q.getNextEvent());
	}
	
	@Test
	public void test_getNextEvent_async()
	{
		EventQueue q = new EventQueue();
		Event e = new Event(System.currentTimeMillis(), null);
		
		Semaphore s = new Semaphore(0);
		Thread t = new Thread(() ->
			{
				try
				{
					s.release();
					
					Thread.sleep(1000);
					q.addEvent(e);
				}
				catch(Exception exc)
				{
					exc.printStackTrace();
				}
			});
		
		t.start();
		
		try
		{
			s.acquire();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
		
		q.addEvent(new Event(System.currentTimeMillis() + 10000, null));
		
		assertEquals(e, q.getNextEvent());
	}
}
