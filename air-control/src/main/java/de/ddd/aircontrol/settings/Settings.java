package de.ddd.aircontrol.settings;

import java.util.Set;

public interface Settings
{
	public Set<String> keySet();
	
	public String getString(String key, String def);
	
	public int getInt(String key, int def);
	
	public long getLong(String key, long def);
	
	public boolean getBoolean(String key, boolean def);
}
