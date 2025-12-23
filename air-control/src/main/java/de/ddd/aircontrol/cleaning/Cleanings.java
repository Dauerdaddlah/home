package de.ddd.aircontrol.cleaning;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ddd.aircontrol.db.Cleaning;
import de.ddd.aircontrol.db.Repository;

public class Cleanings
{
	private static final Logger log = LoggerFactory.getLogger(Cleanings.class);
	
	private final List<VentilatorCleaning> cleanings;
	private final Repository repo;
	
	public Cleanings(Repository repo)
	{
		cleanings = new ArrayList<>();
		this.repo = repo;
	}
	
	public void addCleaning(VentilatorCleaning cleaning)
	{
		cleanings.add(cleaning);
	}
	
	public void cleaningDone(LocalDateTime ldt, int number, boolean replaced)
	{
		Cleaning c = new Cleaning();
		c.setLdt(ldt);
		c.setNumber(number);
		c.setReplaced(replaced);
		
		repo.create(c);
	}

	public void loadLast()
	{
		for(var cd : repo.getLastCleaningData())
		{
			VentilatorCleaning vc = cleanings.get(cd.number() - 1);
			
			if(vc == null)
			{
				log.error("found unknown cleaning {} in db", cd.number());
				continue;
			}
			
			vc.setLastCleaning(cd.lastCleaned());
			vc.setLastReplacement(cd.lastReplaced());
			vc.setCleaningsWithoutReplacement(cd.sinceLastReplaced());
		}
		
	}

	public VentilatorCleaning getCleaning(int number)
	{
		return cleanings.get(number - 1);
	}
	
	public List<VentilatorCleaning> getCleanings()
	{
		return cleanings;
	}
}
