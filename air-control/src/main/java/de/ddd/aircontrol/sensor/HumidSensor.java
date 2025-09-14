package de.ddd.aircontrol.sensor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class HumidSensor implements Sensor
{
	private static final Logger log = LoggerFactory.getLogger(HumidSensor.class);
	
	private final String url;
	private final Duration timeout;
	private final String[] keyHumidity;
	private final String[] keyTemperature;
	
	public HumidSensor(String url, Duration timeout, String keyHumidity, String keyTemperature)
	{
		this.url = url;
		this.timeout = timeout;
		this.keyHumidity = keyHumidity.split("[.]");
		this.keyTemperature = keyTemperature.split("[.]");
	}
	
	@Override
	public SensorResult measure() throws Exception
	{
		log.debug("request data from humiditySensor");
		HttpClient clt = HttpClient.newBuilder()
				.connectTimeout(timeout)
				.build();
		HttpRequest req = HttpRequest.newBuilder(URI.create(url))
				.GET()
				.timeout(timeout)
				.build();
		
		HttpResponse<String> res =  clt.send(req, BodyHandlers.ofString());
		
		String s = res.body();
		
		log.trace("received {}", s);
		
		JsonElement el =  JsonParser.parseString(s);
		
		double humid = parseValue(el, keyHumidity);
		double temperature = parseValue(el, keyTemperature);
		
		return new SensorResult(humid, temperature);
	}
	
	private double parseValue(JsonElement el, String[] key)
	{
		for(String k : key)
		{
			if(el == null || !el.isJsonObject())
			{
				return Double.NaN;
			}
			
			el = el.getAsJsonObject().get(k);
		}
		
		try
		{
			return el.getAsDouble();
		}
		catch(RuntimeException e)
		{
			return Double.NaN;
		}
	}
}
