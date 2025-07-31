package de.ddd.aircontrol.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;

public class Server
{
	private final Javalin app;
	
	private volatile VentilationState state = new VentilationState(2, 0);
	
	public Server(int port)
	{
		Gson gson = new Gson();
		
		app = Javalin.create(this::configure);
		app.get("/status", ctx -> ctx.result(gson.toJson(state)));
		app.post("/set-state", ctx ->
		{
			VentilationState s =  gson.fromJson(ctx.body(), VentilationState.class);
			state = s;
			
			System.err.println(s);
		});
		app.start(port);
	}
	
	private void configure(JavalinConfig config)
	{
		config.staticFiles.add("./public", Location.EXTERNAL);
	}
	
	public static void main(String[] args)
	{
		new Server(12345);
	}
	
	private static record VentilationState(
			int state,
			int interval)
	{
		
	}
}
