package de.ddd.aircontrol.settings;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsProperties implements Settings
{
	private static final Logger log = LoggerFactory.getLogger(SettingsProperties.class);
	
	private final Path file;
	private final Properties propertiesFile;
	private final Properties properties;
	
	public SettingsProperties(Path file)
	{
		this.file = file;
		
		propertiesFile = new Properties(System.getProperties());
		properties = new Properties(propertiesFile);
		
		reload();
	}
	
	public void reload()
	{
		propertiesFile.clear();
		
		try(Reader r = Files.newBufferedReader(file))
		{
			propertiesFile.load(r);
		}
		catch(IOException e)
		{
			log.warn("could not load properties", e);
		}
	}
	
	
	@Override
	public boolean getBoolean(String key, boolean def)
	{
		return Boolean.valueOf(getString(key, String.valueOf(def)));
	}
	
	public void setBoolean(String key, boolean val)
	{
		setString(key, String.valueOf(val));
	}
	
	@Override
	public int getInt(String key, int def)
	{
		return Integer.parseInt(getString(key, String.valueOf(def)));
	}
	
	public void setInt(String key, int val)
	{
		setString(key, String.valueOf(val));
	}
	
	@Override
	public long getLong(String key, long def)
	{
		return Long.parseLong(getString(key, String.valueOf(def)));
	}
	
	public void setLong(String key, long val)
	{
		setString(key, String.valueOf(val));
	}
	
	@Override
	public String getString(String key, String def)
	{
		return properties.getProperty(key, def);
	}
	
	public void setString(String key, String val)
	{
		properties.setProperty(key, val);
	}
	
	/**
	 * @return the properties containing user-set values
	 */
	public Properties getProperties()
	{
		return properties;
	}
	
	/**
	 * @return the properties containing value load from file
	 */
	public Properties getPropertiesFile()
	{
		return propertiesFile;
	}
	
	@Override
	public Set<String> keySet()
	{
		return propertiesFile.stringPropertyNames();
	}
}
