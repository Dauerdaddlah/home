package de.ddd.aircontrol.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DB
{
	private static final Logger log = LoggerFactory.getLogger(DB.class);
	
	private final Connection conn;
	
	/** flag if a commit is needed for this db */
	private boolean dirty;
	
	public DB(Path file) throws IOException
	{
		if(!Files.exists(file) && file.getParent() != null)
		{
			Files.createDirectories(file.getParent());
		}
		
		String url = "jdbc:sqlite:" + file.toString();

		try
		{
			conn = DriverManager.getConnection(url);
			try (Statement stmt = conn.createStatement())
			{
	            // Aktiviert den WAL-Modus für besseres Concurrency & SD-Schonung
	            stmt.execute("PRAGMA journal_mode = WAL;");
	            // Optimiert die Schreib-Synchronisation
	            stmt.execute("PRAGMA synchronous = NORMAL;");
	            // Setzt einen Timeout, falls die DB mal belegt ist (5 Sekunden)
	            stmt.execute("PRAGMA busy_timeout = 5000;");
	            // Erhöht die Cache-Größe (z.B. 2000 Pages), um RAM statt Disk zu nutzen
	            stmt.execute("PRAGMA cache_size = -2000;"); 
	        }
			
			createTables();
			
			// switch auto-commit off and only operate with transaction
			conn.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			log.error("Could not open db at {}", file, e);
			throw new IOException(e);
        }
		
	}
	
	private void createTables() throws SQLException
	{
		execUpdate("""
				CREATE TABLE IF NOT EXISTS SensorData (
					SensorDataId INTEGER PRIMARY KEY AUTOINCREMENT,
					SensorDataName TEXT NOT NULL,
					SensorDataDt DATETIME NOT NULL,
					SensorDataData JSON NOT NULL
				)
				""",
				List.of());
		
		execUpdate("""
				CREATE TABLE IF NOT EXISTS Cleaning (
					CleaningId INTEGER PRIMARY KEY AUTOINCREMENT,
					CleaningNumber INTEGER NOT NULL,
					CleaningDt DATETIME NOT NULL,
					CleaningReplaced INTEGER NOT NULL
				)
				""",
				List.of());
	}
	
	public int execUpdate(String sql, List<Object> params) throws SQLException
	{
		try(PreparedStatement stmt = conn.prepareStatement(sql))
		{
			for(int i = 0; i < params.size(); i++)
			{
				Object o = params.get(i);
				if(o instanceof Number n)
				{
					stmt.setInt(i + 1, n.intValue());
				}
				else if(o instanceof Timestamp ts)
				{
					stmt.setTimestamp(i + 1, ts);
				}
				else if(o instanceof LocalDateTime ldt)
				{
					stmt.setTimestamp(i + 1, Timestamp.valueOf(ldt));
				}
				else if(o instanceof Boolean b)
				{
					stmt.setBoolean(i + 1, b.booleanValue());
				}
				else
				{
					stmt.setString(i + 1, String.valueOf(o));
				}
			}
			
			synchronized (this)
			{
				var res =  stmt.executeUpdate();
				dirty = true;
				return res;
			}
			
		}
	}
	
	public ResultSet execQuery(String sql, List<Object> params) throws SQLException
	{
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		try
		{
			for(int i = 0; i < params.size(); i++)
			{
				Object o = params.get(i);
				if(o instanceof Number n)
				{
					stmt.setInt(i + 1, n.intValue());
				}
				else if(o instanceof Timestamp ts)
				{
					stmt.setTimestamp(i + 1, ts);
				}
				else if(o instanceof LocalDateTime ldt)
				{
					stmt.setTimestamp(i + 1, Timestamp.valueOf(ldt));
				}
				else if(o instanceof Boolean b)
				{
					stmt.setBoolean(i + 1, b.booleanValue());
				}
				else
				{
					stmt.setString(i + 1, String.valueOf(o));
				}
			}
			
			return new DBResultSet(stmt, stmt.executeQuery());
		}
		catch(SQLException e)
		{
			stmt.close();
			throw e;
		}
	}
	
	public synchronized void commit() throws SQLException
	{
		if(dirty)
		{
			conn.commit();
			dirty = false;
		}
	}
	
	public synchronized void rollback() throws SQLException
	{
		if(dirty)
		{
			conn.rollback();
			dirty = false;
		}
	}
}
