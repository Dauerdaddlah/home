package de.ddd.aircontrol.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;

public class DBResultSet implements ResultSet
{
	private final Statement stmt;
	private final ResultSet res;
	
	public DBResultSet(Statement stmt, ResultSet res)
	{
		this.stmt = stmt;
		this.res = res;
	}
	
	@Override
	public boolean next() throws SQLException
	{
		boolean next = res.next();
		
		if(!next)
		{
			close();
		}
		
		return next;
	}
	
	@Override
	public void close() throws SQLException
	{
		res.close();
		stmt.close();
	}
	
	private Timestamp toTimestamp(String string)
	{
		LocalDateTime ldt = LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		return Timestamp.valueOf(ldt);
	}
	
	// DELEGATE METHODS

	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		return res.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return res.isWrapperFor(iface);
	}

	public boolean wasNull() throws SQLException
	{
		return res.wasNull();
	}

	public String getString(int columnIndex) throws SQLException
	{
		return res.getString(columnIndex);
	}

	public boolean getBoolean(int columnIndex) throws SQLException
	{
		return res.getBoolean(columnIndex);
	}

	public byte getByte(int columnIndex) throws SQLException
	{
		return res.getByte(columnIndex);
	}

	public short getShort(int columnIndex) throws SQLException
	{
		return res.getShort(columnIndex);
	}

	public int getInt(int columnIndex) throws SQLException
	{
		return res.getInt(columnIndex);
	}

	public long getLong(int columnIndex) throws SQLException
	{
		return res.getLong(columnIndex);
	}

	public float getFloat(int columnIndex) throws SQLException
	{
		return res.getFloat(columnIndex);
	}

	public double getDouble(int columnIndex) throws SQLException
	{
		return res.getDouble(columnIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
	{
		return res.getBigDecimal(columnIndex, scale);
	}

	public byte[] getBytes(int columnIndex) throws SQLException
	{
		return res.getBytes(columnIndex);
	}

	public Date getDate(int columnIndex) throws SQLException
	{
		return res.getDate(columnIndex);
	}

	public Time getTime(int columnIndex) throws SQLException
	{
		return res.getTime(columnIndex);
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException
	{
		return toTimestamp(res.getString(columnIndex));
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException
	{
		return res.getAsciiStream(columnIndex);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException
	{
		return res.getUnicodeStream(columnIndex);
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException
	{
		return res.getBinaryStream(columnIndex);
	}

	public String getString(String columnLabel) throws SQLException
	{
		return res.getString(columnLabel);
	}

	public boolean getBoolean(String columnLabel) throws SQLException
	{
		return res.getBoolean(columnLabel);
	}

	public byte getByte(String columnLabel) throws SQLException
	{
		return res.getByte(columnLabel);
	}

	public short getShort(String columnLabel) throws SQLException
	{
		return res.getShort(columnLabel);
	}

	public int getInt(String columnLabel) throws SQLException
	{
		return res.getInt(columnLabel);
	}

	public long getLong(String columnLabel) throws SQLException
	{
		return res.getLong(columnLabel);
	}

	public float getFloat(String columnLabel) throws SQLException
	{
		return res.getFloat(columnLabel);
	}

	public double getDouble(String columnLabel) throws SQLException
	{
		return res.getDouble(columnLabel);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException
	{
		return res.getBigDecimal(columnLabel, scale);
	}

	public byte[] getBytes(String columnLabel) throws SQLException
	{
		return res.getBytes(columnLabel);
	}

	public Date getDate(String columnLabel) throws SQLException
	{
		return res.getDate(columnLabel);
	}

	public Time getTime(String columnLabel) throws SQLException
	{
		return res.getTime(columnLabel);
	}

	public Timestamp getTimestamp(String columnLabel) throws SQLException
	{
		return toTimestamp(res.getString(columnLabel));
	}

	public InputStream getAsciiStream(String columnLabel) throws SQLException
	{
		return res.getAsciiStream(columnLabel);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(String columnLabel) throws SQLException
	{
		return res.getUnicodeStream(columnLabel);
	}

	public InputStream getBinaryStream(String columnLabel) throws SQLException
	{
		return res.getBinaryStream(columnLabel);
	}

	public SQLWarning getWarnings() throws SQLException
	{
		return res.getWarnings();
	}

	public void clearWarnings() throws SQLException
	{
		res.clearWarnings();
	}

	public String getCursorName() throws SQLException
	{
		return res.getCursorName();
	}

	public ResultSetMetaData getMetaData() throws SQLException
	{
		return res.getMetaData();
	}

	public Object getObject(int columnIndex) throws SQLException
	{
		return res.getObject(columnIndex);
	}

	public Object getObject(String columnLabel) throws SQLException
	{
		return res.getObject(columnLabel);
	}

	public int findColumn(String columnLabel) throws SQLException
	{
		return res.findColumn(columnLabel);
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException
	{
		return res.getCharacterStream(columnIndex);
	}

	public Reader getCharacterStream(String columnLabel) throws SQLException
	{
		return res.getCharacterStream(columnLabel);
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException
	{
		return res.getBigDecimal(columnIndex);
	}

	public BigDecimal getBigDecimal(String columnLabel) throws SQLException
	{
		return res.getBigDecimal(columnLabel);
	}

	public boolean isBeforeFirst() throws SQLException
	{
		return res.isBeforeFirst();
	}

	public boolean isAfterLast() throws SQLException
	{
		return res.isAfterLast();
	}

	public boolean isFirst() throws SQLException
	{
		return res.isFirst();
	}

	public boolean isLast() throws SQLException
	{
		return res.isLast();
	}

	public void beforeFirst() throws SQLException
	{
		res.beforeFirst();
	}

	public void afterLast() throws SQLException
	{
		res.afterLast();
	}

	public boolean first() throws SQLException
	{
		return res.first();
	}

	public boolean last() throws SQLException
	{
		return res.last();
	}

	public int getRow() throws SQLException
	{
		return res.getRow();
	}

	public boolean absolute(int row) throws SQLException
	{
		return res.absolute(row);
	}

	public boolean relative(int rows) throws SQLException
	{
		return res.relative(rows);
	}

	public boolean previous() throws SQLException
	{
		return res.previous();
	}

	public void setFetchDirection(int direction) throws SQLException
	{
		res.setFetchDirection(direction);
	}

	public int getFetchDirection() throws SQLException
	{
		return res.getFetchDirection();
	}

	public void setFetchSize(int rows) throws SQLException
	{
		res.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException
	{
		return res.getFetchSize();
	}

	public int getType() throws SQLException
	{
		return res.getType();
	}

	public int getConcurrency() throws SQLException
	{
		return res.getConcurrency();
	}

	public boolean rowUpdated() throws SQLException
	{
		return res.rowUpdated();
	}

	public boolean rowInserted() throws SQLException
	{
		return res.rowInserted();
	}

	public boolean rowDeleted() throws SQLException
	{
		return res.rowDeleted();
	}

	public void updateNull(int columnIndex) throws SQLException
	{
		res.updateNull(columnIndex);
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException
	{
		res.updateBoolean(columnIndex, x);
	}

	public void updateByte(int columnIndex, byte x) throws SQLException
	{
		res.updateByte(columnIndex, x);
	}

	public void updateShort(int columnIndex, short x) throws SQLException
	{
		res.updateShort(columnIndex, x);
	}

	public void updateInt(int columnIndex, int x) throws SQLException
	{
		res.updateInt(columnIndex, x);
	}

	public void updateLong(int columnIndex, long x) throws SQLException
	{
		res.updateLong(columnIndex, x);
	}

	public void updateFloat(int columnIndex, float x) throws SQLException
	{
		res.updateFloat(columnIndex, x);
	}

	public void updateDouble(int columnIndex, double x) throws SQLException
	{
		res.updateDouble(columnIndex, x);
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
	{
		res.updateBigDecimal(columnIndex, x);
	}

	public void updateString(int columnIndex, String x) throws SQLException
	{
		res.updateString(columnIndex, x);
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException
	{
		res.updateBytes(columnIndex, x);
	}

	public void updateDate(int columnIndex, Date x) throws SQLException
	{
		res.updateDate(columnIndex, x);
	}

	public void updateTime(int columnIndex, Time x) throws SQLException
	{
		res.updateTime(columnIndex, x);
	}

	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
	{
		res.updateTimestamp(columnIndex, x);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
	{
		res.updateAsciiStream(columnIndex, x, length);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
	{
		res.updateBinaryStream(columnIndex, x, length);
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
	{
		res.updateCharacterStream(columnIndex, x, length);
	}

	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException
	{
		res.updateObject(columnIndex, x, scaleOrLength);
	}

	public void updateObject(int columnIndex, Object x) throws SQLException
	{
		res.updateObject(columnIndex, x);
	}

	public void updateNull(String columnLabel) throws SQLException
	{
		res.updateNull(columnLabel);
	}

	public void updateBoolean(String columnLabel, boolean x) throws SQLException
	{
		res.updateBoolean(columnLabel, x);
	}

	public void updateByte(String columnLabel, byte x) throws SQLException
	{
		res.updateByte(columnLabel, x);
	}

	public void updateShort(String columnLabel, short x) throws SQLException
	{
		res.updateShort(columnLabel, x);
	}

	public void updateInt(String columnLabel, int x) throws SQLException
	{
		res.updateInt(columnLabel, x);
	}

	public void updateLong(String columnLabel, long x) throws SQLException
	{
		res.updateLong(columnLabel, x);
	}

	public void updateFloat(String columnLabel, float x) throws SQLException
	{
		res.updateFloat(columnLabel, x);
	}

	public void updateDouble(String columnLabel, double x) throws SQLException
	{
		res.updateDouble(columnLabel, x);
	}

	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException
	{
		res.updateBigDecimal(columnLabel, x);
	}

	public void updateString(String columnLabel, String x) throws SQLException
	{
		res.updateString(columnLabel, x);
	}

	public void updateBytes(String columnLabel, byte[] x) throws SQLException
	{
		res.updateBytes(columnLabel, x);
	}

	public void updateDate(String columnLabel, Date x) throws SQLException
	{
		res.updateDate(columnLabel, x);
	}

	public void updateTime(String columnLabel, Time x) throws SQLException
	{
		res.updateTime(columnLabel, x);
	}

	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException
	{
		res.updateTimestamp(columnLabel, x);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException
	{
		res.updateAsciiStream(columnLabel, x, length);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException
	{
		res.updateBinaryStream(columnLabel, x, length);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException
	{
		res.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException
	{
		res.updateObject(columnLabel, x, scaleOrLength);
	}

	public void updateObject(String columnLabel, Object x) throws SQLException
	{
		res.updateObject(columnLabel, x);
	}

	public void insertRow() throws SQLException
	{
		res.insertRow();
	}

	public void updateRow() throws SQLException
	{
		res.updateRow();
	}

	public void deleteRow() throws SQLException
	{
		res.deleteRow();
	}

	public void refreshRow() throws SQLException
	{
		res.refreshRow();
	}

	public void cancelRowUpdates() throws SQLException
	{
		res.cancelRowUpdates();
	}

	public void moveToInsertRow() throws SQLException
	{
		res.moveToInsertRow();
	}

	public void moveToCurrentRow() throws SQLException
	{
		res.moveToCurrentRow();
	}

	public Statement getStatement() throws SQLException
	{
		return res.getStatement();
	}

	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException
	{
		return res.getObject(columnIndex, map);
	}

	public Ref getRef(int columnIndex) throws SQLException
	{
		return res.getRef(columnIndex);
	}

	public Blob getBlob(int columnIndex) throws SQLException
	{
		return res.getBlob(columnIndex);
	}

	public Clob getClob(int columnIndex) throws SQLException
	{
		return res.getClob(columnIndex);
	}

	public Array getArray(int columnIndex) throws SQLException
	{
		return res.getArray(columnIndex);
	}

	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException
	{
		return res.getObject(columnLabel, map);
	}

	public Ref getRef(String columnLabel) throws SQLException
	{
		return res.getRef(columnLabel);
	}

	public Blob getBlob(String columnLabel) throws SQLException
	{
		return res.getBlob(columnLabel);
	}

	public Clob getClob(String columnLabel) throws SQLException
	{
		return res.getClob(columnLabel);
	}

	public Array getArray(String columnLabel) throws SQLException
	{
		return res.getArray(columnLabel);
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException
	{
		return res.getDate(columnIndex, cal);
	}

	public Date getDate(String columnLabel, Calendar cal) throws SQLException
	{
		return res.getDate(columnLabel, cal);
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException
	{
		return res.getTime(columnIndex, cal);
	}

	public Time getTime(String columnLabel, Calendar cal) throws SQLException
	{
		return res.getTime(columnLabel, cal);
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
	{
		return res.getTimestamp(columnIndex, cal);
	}

	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException
	{
		return res.getTimestamp(columnLabel, cal);
	}

	public URL getURL(int columnIndex) throws SQLException
	{
		return res.getURL(columnIndex);
	}

	public URL getURL(String columnLabel) throws SQLException
	{
		return res.getURL(columnLabel);
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException
	{
		res.updateRef(columnIndex, x);
	}

	public void updateRef(String columnLabel, Ref x) throws SQLException
	{
		res.updateRef(columnLabel, x);
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException
	{
		res.updateBlob(columnIndex, x);
	}

	public void updateBlob(String columnLabel, Blob x) throws SQLException
	{
		res.updateBlob(columnLabel, x);
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException
	{
		res.updateClob(columnIndex, x);
	}

	public void updateClob(String columnLabel, Clob x) throws SQLException
	{
		res.updateClob(columnLabel, x);
	}

	public void updateArray(int columnIndex, Array x) throws SQLException
	{
		res.updateArray(columnIndex, x);
	}

	public void updateArray(String columnLabel, Array x) throws SQLException
	{
		res.updateArray(columnLabel, x);
	}

	public RowId getRowId(int columnIndex) throws SQLException
	{
		return res.getRowId(columnIndex);
	}

	public RowId getRowId(String columnLabel) throws SQLException
	{
		return res.getRowId(columnLabel);
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException
	{
		res.updateRowId(columnIndex, x);
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException
	{
		res.updateRowId(columnLabel, x);
	}

	public int getHoldability() throws SQLException
	{
		return res.getHoldability();
	}

	public boolean isClosed() throws SQLException
	{
		return res.isClosed();
	}

	public void updateNString(int columnIndex, String nString) throws SQLException
	{
		res.updateNString(columnIndex, nString);
	}

	public void updateNString(String columnLabel, String nString) throws SQLException
	{
		res.updateNString(columnLabel, nString);
	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException
	{
		res.updateNClob(columnIndex, nClob);
	}

	public void updateNClob(String columnLabel, NClob nClob) throws SQLException
	{
		res.updateNClob(columnLabel, nClob);
	}

	public NClob getNClob(int columnIndex) throws SQLException
	{
		return res.getNClob(columnIndex);
	}

	public NClob getNClob(String columnLabel) throws SQLException
	{
		return res.getNClob(columnLabel);
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException
	{
		return res.getSQLXML(columnIndex);
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException
	{
		return res.getSQLXML(columnLabel);
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
	{
		res.updateSQLXML(columnIndex, xmlObject);
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
	{
		res.updateSQLXML(columnLabel, xmlObject);
	}

	public String getNString(int columnIndex) throws SQLException
	{
		return res.getNString(columnIndex);
	}

	public String getNString(String columnLabel) throws SQLException
	{
		return res.getNString(columnLabel);
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException
	{
		return res.getNCharacterStream(columnIndex);
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException
	{
		return res.getNCharacterStream(columnLabel);
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		res.updateNCharacterStream(columnIndex, x, length);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		res.updateNCharacterStream(columnLabel, reader, length);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		res.updateAsciiStream(columnIndex, x, length);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		res.updateBinaryStream(columnIndex, x, length);
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		res.updateCharacterStream(columnIndex, x, length);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		res.updateAsciiStream(columnLabel, x, length);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		res.updateBinaryStream(columnLabel, x, length);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		res.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
	{
		res.updateBlob(columnIndex, inputStream, length);
	}

	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
	{
		res.updateBlob(columnLabel, inputStream, length);
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		res.updateClob(columnIndex, reader, length);
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		res.updateClob(columnLabel, reader, length);
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		res.updateNClob(columnIndex, reader, length);
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		res.updateNClob(columnLabel, reader, length);
	}

	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		res.updateNCharacterStream(columnIndex, x);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		res.updateNCharacterStream(columnLabel, reader);
	}

	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
	{
		res.updateAsciiStream(columnIndex, x);
	}

	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
	{
		res.updateBinaryStream(columnIndex, x);
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		res.updateCharacterStream(columnIndex, x);
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
	{
		res.updateAsciiStream(columnLabel, x);
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
	{
		res.updateBinaryStream(columnLabel, x);
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		res.updateCharacterStream(columnLabel, reader);
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
	{
		res.updateBlob(columnIndex, inputStream);
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
	{
		res.updateBlob(columnLabel, inputStream);
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException
	{
		res.updateClob(columnIndex, reader);
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException
	{
		res.updateClob(columnLabel, reader);
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException
	{
		res.updateNClob(columnIndex, reader);
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException
	{
		res.updateNClob(columnLabel, reader);
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException
	{
		return res.getObject(columnIndex, type);
	}

	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException
	{
		return res.getObject(columnLabel, type);
	}

	public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException
	{
		res.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
	}

	public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException
	{
		res.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
	}

	public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException
	{
		res.updateObject(columnIndex, x, targetSqlType);
	}

	public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException
	{
		res.updateObject(columnLabel, x, targetSqlType);
	}
}
