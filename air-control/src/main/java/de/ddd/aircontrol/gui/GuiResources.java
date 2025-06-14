package de.ddd.aircontrol.gui;

import java.net.URL;

public class GuiResources
{
	public static URL getResource(String name)
	{
		return GuiResources.class.getResource(name);
	}
}
