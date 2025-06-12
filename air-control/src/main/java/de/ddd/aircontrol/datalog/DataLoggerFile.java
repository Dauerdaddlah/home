package de.ddd.aircontrol.datalog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.google.gson.Gson;

public class DataLoggerFile implements DataLogger
{
	private final Path file;
	private final Gson gson;
	
	public DataLoggerFile(Path file)
	{
		this.file = file;
		this.gson = new Gson();
	}
	
	@Override
	public void log(String type, Object value)
	{
		String val = gson.toJson(value);
		
		try
		{
			Files.write(file,
					List.of(System.currentTimeMillis() + " - " + type + ":" + val),
					StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
