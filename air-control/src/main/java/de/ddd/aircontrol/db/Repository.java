package de.ddd.aircontrol.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

public class Repository
{
	private static final Logger log = LoggerFactory.getLogger(Repository.class);
	
	private final DB db;
	
	public Repository(DB db)
	{
		this.db = db;
	}

	public void create(SensorData sd)
	{
		String stmt = """
				INSERT INTO SensorData(SensorDataName, SensorDataDt, SensorDataData)
				VALUES(?, ?, ?)
				""";
		List<Object> args = List.of(sd.getName(), sd.getLdt(), sd.getData());
		try
		{
			db.execUpdate(stmt, args);
		}
		catch (SQLException e)
		{
			log.error("Error on inserting sensor data", e);
		}
	}
	
	public void create(Cleaning c)
	{
		String stmt = """
				INSERT INTO Cleaning(CleaningNumber, CleaningDt, CleaningReplaced)
				VALUES(?, ?, ?)
				""";
		List<Object> args = List.of(c.getNumber(), c.getLdt(), c.isReplaced());
		try
		{
			db.execUpdate(stmt, args);
		}
		catch (SQLException e)
		{
			log.error("Error on inserting cleaning data", e);
		}
	}

	public DB getDb()
	{
		return db;
	}

	public List<SensorData> getLastSensorResults()
	{
		String sql = """
				select *
				from sensordata s,
				(select max(SensorDataId) as id, sensordataname as name
				from SensorData
				group by sensordataname) m
				where s.sensordataid = m.id and s.SensorDataName = m.name
				""";
		
		try(ResultSet res = db.execQuery(sql, List.of());)
		{
			List<SensorData> ret = new ArrayList<>();
			
			while(res.next())
			{
				SensorData sd = new SensorData();
				sd.setId(res.getInt("SensorDataId"));
				sd.setLdt(res.getTimestamp("SonserDataDt").toLocalDateTime());
				sd.setName(res.getString("SensorDataName"));
				sd.setData(JsonParser.parseString(res.getString("SensorDataData")).getAsJsonObject());
				ret.add(sd);
			}
			
			return ret;
		}
		catch (SQLException e)
		{
			log.error("Error while reading sensordata");
			return List.of();
		}
	}
	
	public List<CleaningData> getLastCleaningData()
	{
		String sql = """
				WITH RankedCleanings AS (
				    SELECT 
				        CleaningNumber,
				        CleaningDt,
				        CleaningReplaced,
				        MAX(CASE WHEN CleaningReplaced = 1 THEN CleaningDt END) 
				            OVER (PARTITION BY CleaningNumber) as LastReplacedDt
				    FROM Cleaning
				)
				SELECT 
				    CleaningNumber,
				    MAX(CleaningDt) AS LastCleaningDt,
				    LastReplacedDt,
				    COUNT(*) FILTER (
				        WHERE CleaningDt > LastReplacedDt OR LastReplacedDt IS NULL
				    ) AS CountSinceLastReplaced
				FROM RankedCleanings
				GROUP BY CleaningNumber;
				""";
		
		try(ResultSet res = db.execQuery(sql, List.of()))
		{
			List<CleaningData> ret = new ArrayList<>();
			
			while(res.next())
			{
				int number = res.getInt("CleaningNumber");
				LocalDateTime lastCleaning = res.getTimestamp("LastCleaningDt").toLocalDateTime();
				LocalDateTime lastReplaced = res.getObject("LastReplacedDt") == null ? null : res.getTimestamp("LastReplacedDt").toLocalDateTime();
				int sinceLastReplaced = res.getInt("CountSinceLastReplaced");
				
				CleaningData cd = new CleaningData(number, lastCleaning, lastReplaced, sinceLastReplaced);
				ret.add(cd);
			}
			
			return ret;
		}
		catch(SQLException e)
		{
			log.error("Error while reading cleaning-data", e);
			return List.of();
		}
	}
	
	public static final record CleaningData(
			int number,
			LocalDateTime lastCleaned,
			/** the last time, this cleaning had a replacement, maybe null */
			LocalDateTime lastReplaced,
			/** the number of cleanings since the last replacement */
			int sinceLastReplaced)
	{
	}
}
