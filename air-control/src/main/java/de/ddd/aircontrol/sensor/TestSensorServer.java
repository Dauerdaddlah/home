package de.ddd.aircontrol.sensor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class TestSensorServer
{
	public static void main(String[] args)
	{
		var s = new TestSensorServer();
		s.start(12345);
	}
	
	private final Javalin app;
	private final Gson g;
	
	public TestSensorServer()
	{
		g = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.create();
		
		app = Javalin.create(this::config);
		
		app.get("/", this::generateSimData);
	}
	
	public void generateSimData(Context ctx) throws Exception
	{
		double humidity = Math.random() * 100;
		double temp = Math.random() * 40;
		
		SensorResult res = new SensorResult(humidity, temp);
		
		ctx.status(HttpStatus.OK);
		ctx.contentType(ContentType.JSON);
		ctx.result(g.toJson(res));
	}
	
	public void start(int port)
	{
		app.start(port);
	}
	
	private void config(JavalinConfig config)
	{
	}
}
