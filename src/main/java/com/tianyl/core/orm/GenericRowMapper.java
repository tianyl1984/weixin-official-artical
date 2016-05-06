package com.tianyl.core.orm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.tianyl.core.orm.annotation.Column;
import com.tianyl.core.orm.annotation.Transient;

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
				setModelValue(t, column, value);
			}
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("row map error ", e);
		}
	}

	private static void setModelValue(Object model, String column, Object value) {
		Field[] fs = model.getClass().getDeclaredFields();
		for (Field f : fs) {
			String temp = f.getName();
			Transient t = f.getAnnotation(Transient.class);
			if (t != null) {
				continue;
			}
			Column col = f.getAnnotation(Column.class);
			if (col != null) {
				temp = col.value();
			}
			if (temp.equals(column)) {
				f.setAccessible(true);
				try {
					f.set(model, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException("set value error " + f.getName() + " " + model);
				}
			}
		}
	}
}
