package de.ddd.aircontrol.web;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.google.gson.Gson;

import de.ddd.aircontrol.AirControl;
import de.ddd.aircontrol.event.Event;
import de.ddd.aircontrol.event.EventAction;
import de.ddd.aircontrol.event.EventQueue;
import de.ddd.aircontrol.ventilation.Level;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.http.staticfiles.Location;

public class Server
{
	private final Javalin app;
	
	private final ThreadLocal<EventQueue> tQueue = new ThreadLocal<>();
	private final ThreadLocal<Event> tEvent = new ThreadLocal<>();
	
	public Server(int port)
	{
		Gson gson = new Gson();
		
		app = Javalin.create(this::configure);
		app.get("/status", ctx -> 
		{
			VentilationState state = getData(() ->
				{
					Level l = AirControl.getInstance().getVentilation().getLevel();
					
					long until = AirControl.getInstance().getControllerManual().getUntil();
					
					int interval = 0;
					if(until > System.currentTimeMillis())
					{
						interval = (int)(until - System.currentTimeMillis());
					}
					
					return new VentilationState(l.ordinal(), interval);
				});
			
			ctx.contentType(ContentType.JSON);
			ctx.result(gson.toJson(state));
		});
		app.post("/set-state", ctx ->
		{
			final VentilationState s =  gson.fromJson(ctx.body(), VentilationState.class);
			
			getData(() ->
				{
					var c = AirControl.getInstance().getControllerManual();
					var l = Level.values()[s.state];
					
					if(s.interval() > 0)
					{
						c.setDestLevelFor(l, s.interval());
						// add event for faster reaction
						AirControl.getInstance().addEvent(new Event(c.getUntil(), AirControl.getInstance()::checkAll));
					}
					else
					{
						c.setDestLevel(l);
					}
					
					return null;
				});
		});
		app.start(port);
	}
	
	private void configure(JavalinConfig config)
	{
		config.staticFiles.add("./public", Location.EXTERNAL);
	}
	
	
	
	private <T> T getData(Callable<T> action)
	{
		CompletableFuture<T> f = new CompletableFuture<>();
		
		AirControl.getInstance().addEvent((queue, event) ->
			{
				tQueue.set(queue);
				tEvent.set(event);
				
				try
				{
					f.complete(action.call());
				}
				catch(Exception e)
				{
					f.completeExceptionally(e);
				}
				
				tQueue.remove();
				tEvent.remove();
			});
		
		try
		{
			return f.get(10, TimeUnit.SECONDS);
		}
		catch(Exception e)
		{
			Thread.interrupted();
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args)
	{
		new Server(12345);
	}
	
	private static record VentilationState(
			/** current state as Level#ordinal */
			int state,
			/** remaining time in ms */
			int interval)
	{
		
	}
}
