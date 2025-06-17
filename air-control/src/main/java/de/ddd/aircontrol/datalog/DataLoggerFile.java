package de.ddd.aircontrol.datalog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataLoggerFile implements DataLogger
{
	private static final Logger log = LoggerFactory.getLogger(DataLoggerFile.class);
	
	private final Path file;
	private final Gson gson;
	private long size;
	private final long maxSize;
	private final int maxCount;
	
	public DataLoggerFile(Path file, long maxSize, int maxCount)
	{
		this.file = file;
		this.gson = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.create();
		this.maxSize = maxSize;
		this.maxCount = maxCount;
		
		try
		{
			rollover();
			size = Files.size(file);
		}
		catch (IOException e)
		{
			size = 0;
		}
	}
	
	@Override
	public synchronized void log(String type, Object value)
	{
		String val = gson.toJson(value);
		String s = LocalDateTime.now() + " - " + type + ":" + val;
		
		try
		{
			Files.write(file,
					List.of(s),
					StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
			
			size += s.length();
			
			if(size > maxSize)
			{
				rollover();
				size = 0;
			}
		}
		catch (IOException e)
		{
			log.error("Error on logging data", e);
		}
		
	}

	private void rollover()
	{
		for(int i = maxCount - 2; i > 0; i--)
		{
			Path src = file.getParent().resolve(file.getFileName().toString() + "-" + i);
			Path dest = file.getParent().resolve(file.getFileName().toString() + "-" + (i + 1));
			
			if(Files.exists(src))
			{
				try
				{
					Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
				}
				catch (IOException e)
				{
					log.error("error in rollover from {} to {}", src, dest, e);
				}
			}
		}
		
		Path dest = file.getParent().resolve(file.getFileName().toString() + "-1");
		
		try
		{
			Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			log.error("error in rollover from {} to {}", file, dest, e);
		}
		
		try
		{
			Files.writeString(file, "",
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch(IOException e)
		{
			log.error("error while creating/truncating file {}", file);
		}
	}
}
