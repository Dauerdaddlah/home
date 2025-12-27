package de.ddd.aircontrol.gson;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>
{
	@Override
	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if(json.isJsonNull())
		{
			return null;
		}
		
		try
		{
			return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		catch(Exception e)
		{
			return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		}
	}

	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context)
	{
		if(src == null)
		{
			return JsonNull.INSTANCE;
		}
		
		return new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(src));
	}

}
