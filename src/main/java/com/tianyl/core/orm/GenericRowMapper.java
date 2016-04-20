package com.tianyl.core.orm;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class GenericRowMapper<T> implements RowMapper<T> {

	private Class<T> clazz;

	public GenericRowMapper(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			T t = clazz.newInstance();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String column = rsmd.getColumnLabel(i);
				Object value = rs.getObject(column);
				JdbcUtil.setModelValue(t, column, value);
			}
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("row map error ", e);
		}
	}
}
