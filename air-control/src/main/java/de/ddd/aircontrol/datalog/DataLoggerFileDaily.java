package de.ddd.aircontrol.datalog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DataLoggerFileDaily implements DataLogger
{
	private static final Logger log = LoggerFactory.getLogger(DataLoggerFile.class);
	
	/** formatter used for each log containing detailed time */
	private final DateTimeFormatter fLdt = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");
	/** formatter used for files only being precise to a day-level */
	private final DateTimeFormatter fLd = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	/** the folder in which to place the log-files */
	private final Path folder;
	/** gson used to transofrm log-data for persisting */
	private final Gson gson;
	
	/**
	 * 
	 * @param folder
	 * @throws IOException
	 */
	public DataLoggerFileDaily(Path folder) throws IOException
	{
		this.folder = folder;
		this.gson = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.create();
		
		Files.createDirectories(folder);
	}
	
	@Override
	public synchronized void log(String type, Object value)
	{
		LocalDateTime ldt = LocalDateTime.now();
		
		String time = ldt.format(fLdt);
		
		Path file = folder.resolve(ldt.toLocalDate().format(fLd) + ".data");
		
		JsonElement data = gson.toJsonTree(value);
		
		JsonObject o = new JsonObject();
		o.add("type", new JsonPrimitive(type));
		o.add("time", new JsonPrimitive(time));
		o.add("data", data);
		
		try
		{
			String s = o.toString() + "\n";
			Files.write(file,
					s.getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
		}
		catch (IOException e)
		{
			log.error("Error on logging data", e);
		}
	}
}
