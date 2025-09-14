package de.ddd.aircontrol;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DataConverter
{
	public static void main(String[] args) throws Exception
	{
		Path p = Paths.get("20250901.data");
		
		List<String> lines = new ArrayList<>();
		
		for(String line : Files.readAllLines(p))
		{
			int i = line.indexOf(" - ");
			
			String time = line.substring(0, i);
			
			int j = line.indexOf(':');
			
			String type = line.substring(i + " - ".length(), j);
			
			String json = line.substring(j + 1);
			
			json = "{\"type\":\"" + type + "\", \"time\":\"" + time + "\", " + json.substring(1);
			
			lines.add(json);
		}
		
		Files.write(Paths.get("20250901_clean.data"), lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
