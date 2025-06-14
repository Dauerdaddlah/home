package de.ddd.aircontrol.gui;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class GuiResources
{
	// generated
	public static final String CUBE_128 = "cube_128.png";
	public static final String CUBE_16 = "cube_16.png";
	public static final String CUBE_24 = "cube_24.png";
	public static final String CUBE_256 = "cube_256.png";
	public static final String CUBE_32 = "cube_32.png";
	public static final String CUBE_512 = "cube_512.png";
	public static final String CUBE_64 = "cube_64.png";
	public static final String FAN_128 = "fan_128.png";
	public static final String FAN_16 = "fan_16.png";
	public static final String FAN_24 = "fan_24.png";
	public static final String FAN_256 = "fan_256.png";
	public static final String FAN_32 = "fan_32.png";
	public static final String FAN_512 = "fan_512.png";
	public static final String FAN_64 = "fan_64.png";
	public static final String FINGER_128 = "finger_128.png";
	public static final String FINGER_16 = "finger_16.png";
	public static final String FINGER_24 = "finger_24.png";
	public static final String FINGER_256 = "finger_256.png";
	public static final String FINGER_32 = "finger_32.png";
	public static final String FINGER_512 = "finger_512.png";
	public static final String FINGER_64 = "finger_64.png";
	public static final String HOME_128 = "home_128.png";
	public static final String HOME_16 = "home_16.png";
	public static final String HOME_24 = "home_24.png";
	public static final String HOME_256 = "home_256.png";
	public static final String HOME_32 = "home_32.png";
	public static final String HOME_512 = "home_512.png";
	public static final String HOME_64 = "home_64.png";
	public static final String HUMIDITY_512 = "humidity_512.png";
	public static final String SETTING_128 = "setting_128.png";
	public static final String SETTING_16 = "setting_16.png";
	public static final String SETTING_24 = "setting_24.png";
	public static final String SETTING_256 = "setting_256.png";
	public static final String SETTING_32 = "setting_32.png";
	public static final String SETTING_512 = "setting_512.png";
	public static final String SETTING_64 = "setting_64.png";
	public static final String SWITCH_OFF_128 = "switch-off_128.png";
	public static final String SWITCH_OFF_16 = "switch-off_16.png";
	public static final String SWITCH_OFF_24 = "switch-off_24.png";
	public static final String SWITCH_OFF_256 = "switch-off_256.png";
	public static final String SWITCH_OFF_32 = "switch-off_32.png";
	public static final String SWITCH_OFF_512 = "switch-off_512.png";
	public static final String SWITCH_OFF_64 = "switch-off_64.png";
	public static final String SWITCH_ON_128 = "switch-on_128.png";
	public static final String SWITCH_ON_16 = "switch-on_16.png";
	public static final String SWITCH_ON_24 = "switch-on_24.png";
	public static final String SWITCH_ON_256 = "switch-on_256.png";
	public static final String SWITCH_ON_32 = "switch-on_32.png";
	public static final String SWITCH_ON_512 = "switch-on_512.png";
	public static final String SWITCH_ON_64 = "switch-on_64.png";
	public static final String SWITCH_UNKNOWN_128 = "switch-unknown_128.png";
	public static final String SWITCH_UNKNOWN_16 = "switch-unknown_16.png";
	public static final String SWITCH_UNKNOWN_24 = "switch-unknown_24.png";
	public static final String SWITCH_UNKNOWN_256 = "switch-unknown_256.png";
	public static final String SWITCH_UNKNOWN_32 = "switch-unknown_32.png";
	public static final String SWITCH_UNKNOWN_512 = "switch-unknown_512.png";
	public static final String SWITCH_UNKNOWN_64 = "switch-unknown_64.png";
	public static final String THERMOMETER_128 = "thermometer_128.png";
	public static final String THERMOMETER_16 = "thermometer_16.png";
	public static final String THERMOMETER_24 = "thermometer_24.png";
	public static final String THERMOMETER_256 = "thermometer_256.png";
	public static final String THERMOMETER_32 = "thermometer_32.png";
	public static final String THERMOMETER_512 = "thermometer_512.png";
	public static final String THERMOMETER_64 = "thermometer_64.png";
	public static final String TURN_ON_128 = "turn-on_128.png";
	public static final String TURN_ON_16 = "turn-on_16.png";
	public static final String TURN_ON_24 = "turn-on_24.png";
	public static final String TURN_ON_256 = "turn-on_256.png";
	public static final String TURN_ON_32 = "turn-on_32.png";
	public static final String TURN_ON_512 = "turn-on_512.png";
	public static final String TURN_ON_64 = "turn-on_64.png";
	public static final String VENTILATOR_512 = "ventilator_512.png";
	public static final String WATERDROPS_128 = "waterdrops_128.png";
	public static final String WATERDROPS_16 = "waterdrops_16.png";
	public static final String WATERDROPS_24 = "waterdrops_24.png";
	public static final String WATERDROPS_256 = "waterdrops_256.png";
	public static final String WATERDROPS_32 = "waterdrops_32.png";
	public static final String WATERDROPS_512 = "waterdrops_512.png";
	public static final String WATERDROPS_64 = "waterdrops_64.png";
	// generated
	
	public static URL getResource(String name)
	{
		return GuiResources.class.getResource(name);
	}
	
	public static void main(String[] args) throws Exception
	{
		Path resourceRoot = Paths.get("src", "main", "resources", GuiResources.class.getPackage().getName().replaceAll("[.]", "/"));
		Path source = Paths.get("src", "main", "java", GuiResources.class.getName().replaceAll("[.]", "/") + ".java");
		
		List<String> resources =
			Files.list(resourceRoot)
				.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
				.map(p ->
					{
						String fileName = p.getFileName().toString();
						String constName = fileName.substring(0, fileName.length() - ".png".length())
								.toUpperCase()
								.replaceAll("[-.]", "_");
								
						return "	public static final String " + constName + " = \"" + fileName + "\";";
					})
				.sorted()
				.toList();
		
		List<String> sourceLines = new ArrayList<>(Files.readAllLines(source));
		
		int start = sourceLines.indexOf("	// generated");
		int end = sourceLines.lastIndexOf("	// generated");
		
		if(start == -1 || start == end)
		{
			throw new RuntimeException("missing generated comments in file");
		}
		
		// remove old generated lines
		for(int i = end - 1; i > start; i--)
		{
			sourceLines.remove(i);
		}
		
		sourceLines.addAll(start + 1, resources);
		
		Files.write(source, sourceLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
