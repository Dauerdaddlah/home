package de.ddd.aircontrol.settings;

public interface Settings
{
	public String getString(String key, String def);
	
	public int getInt(String key, int def);
	
	public long getLong(String key, long def);
	
	public boolean getBoolean(String key, boolean def);
}
