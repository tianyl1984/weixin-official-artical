package com.tianyl.core.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tianyl.core.orm.annotation.Column;
import com.tianyl.core.orm.annotation.Table;
import com.tianyl.core.orm.annotation.Transient;
import com.tianyl.core.util.StringUtil;

public class JdbcUtil {

	static {
		String driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Integer save(String sql, Object... args) {
		List<Object[]> valueList = new ArrayList<Object[]>();
		valueList.add(args);
		return saveList(sql, valueList).get(0);
	}

	public static List<Integer> saveList(String sql, List<Object[]> valueList) {
		if (valueList == null || valueList.size() == 0) {
			return new ArrayList<Integer>();
		}
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (Object[] args : valueList) {
				for (int i = 1; i <= args.length; i++) {
					ps.setObject(i, args[i - 1]);
				}
				ps.addBatch();
			}
			ps.executeBatch();
			List<Integer> results = new ArrayList<Integer>();
			ResultSet rs = ps.getGeneratedKeys();
			while (rs.next()) {
				results.add(rs.getInt(1));
			}
			close(ps);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public static int update(String sql, Object... args) {
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}
			int result = ps.executeUpdate();
			close(ps);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public static <T> List<T> query(String sql, RowMapper<T> mapper, Object... args) {
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}
			ResultSet rs = ps.executeQuery();
			int rowNum = 0;
			List<T> result = new ArrayList<T>();
			while (rs.next()) {
				T t = mapper.mapRow(rs, rowNum);
				result.add(t);
				rowNum++;
			}
			close(rs);
			close(ps);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close(conn);
		}
	}

	public static List<String> queryStr(String sql, Object... args) {
		return query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		}, args);
	}

	public static String queryStrOne(String sql, Object... args) {
		List<String> list = queryStr(sql, args);
		return list.size() > 0 ? list.get(0) : null;
	}

	public static List<Integer> queryInt(String sql, Object... args) {
		return query(sql, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
		}, args);
	}

	public static Integer queryIntOne(String sql, Object... args) {
		List<Integer> list = queryInt(sql, args);
		return list.size() > 0 ? list.get(0) : null;
	}

	private static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	private static void close(PreparedStatement ps) throws SQLException {
		if (ps != null) {
			ps.close();
		}
	}

	private static Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://127.0.0.1:3306/weixin?useUnicode=true&characterEncoding=utf-8";
		Connection conn = DriverManager.getConnection(url, "root", "tyl123");
		return conn;
	}

	public static List<Integer> saveList(List<?> models) {
		List<Integer> result = new ArrayList<>();
		for (Object obj : models) {
			result.add(save(obj));
		}
		return result;
	}

	public static Integer save(Object model) {
		String tabName = getTableName(model.getClass());
		StringBuilder sql = new StringBuilder();
		sql.append("insert into " + tabName + "(");
		List<String> columns = getPersistentColumns(model.getClass());
		columns.remove("id");
		if (columns.size() == 0) {
			throw new RuntimeException("no persistent column found in class " + model.getClass());
		}
		for (int i = 0; i < columns.size(); i++) {
			String col = columns.get(i);
			sql.append(col);
			if (i != columns.size() - 1) {
				sql.append(",");
			}
		}
		sql.append(") values(");
		for (int i = 0; i < columns.size(); i++) {
			sql.append("?");
			if (i != columns.size() - 1) {
				sql.append(",");
			}
		}
		sql.append(")");
		List<Object> values = new ArrayList<>();
		for (String column : columns) {
			values.add(getModelValue(model, column));
		}
		Integer result = save(sql.toString(), values.toArray(new Object[] {}));
		setModelValue(model, "id", result);
		return result;
	}

	public static void update(Object model) {
		Integer id = (Integer) getModelValue(model, "id");
		if (id == null) {
			throw new RuntimeException("id is null,can not update " + model);
		}
		String tabName = getTableName(model.getClass());
		StringBuilder sql = new StringBuilder();
		sql.append("update " + tabName + " set ");
		List<String> columns = getPersistentColumns(model.getClass());
		columns.remove("id");
		if (columns.size() == 0) {
			throw new RuntimeException("no persistent column found in class " + model.getClass());
		}
		for (int i = 0; i < columns.size(); i++) {
			String col = columns.get(i);
			sql.append(col + "=" + "?");
			if (i != columns.size() - 1) {
				sql.append(",");
			}
		}
		sql.append(" where id = ? ");
		List<Object> values = new ArrayList<>();
		for (String column : columns) {
			values.add(getModelValue(model, column));
		}
		values.add(id);
		update(sql.toString(), values.toArray());
	}

	public static Integer saveOrUpdate(Object model) {
		Integer id = (Integer) getModelValue(model, "id");
		if (id == null) {
			return save(model);
		} else {
			update(model);
			return id;
		}
	}

	private static String getTableName(Class<? extends Object> clazz) {
		Table tab = clazz.getAnnotation(Table.class);
		if (tab == null) {
			throw new RuntimeException("no @table annotation found in class " + clazz);
		}
		String tableName = tab.value();
		if (StringUtil.isBlank(tableName)) {
			throw new RuntimeException("no table name found in class " + clazz);
		}
		return tableName;
	}

	private static Object getModelValue(Object model, String column) {
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
					Object val = f.get(model);
					return val;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException("get value error " + f.getName() + " " + model);
				}
			}
		}
		return null;
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

	private static List<String> getPersistentColumns(Class<? extends Object> clazz) {
		Field[] fs = clazz.getDeclaredFields();
		List<String> result = new ArrayList<>();
		for (Field f : fs) {
			String name = f.getName();
			Transient t = f.getAnnotation(Transient.class);
			if (t != null) {
				continue;
			}
			Column col = f.getAnnotation(Column.class);
			if (col != null) {
				result.add(col.value());
			} else {
				result.add(name);
			}
		}
		return result;
	}

	public static <T> List<T> queryAll(Class<T> clazz) {
		String tabName = getTableName(clazz);
		return query("select * from " + tabName + " order by id desc", new GenericRowMapper<T>(clazz));
	}

	public static <T> T get(Integer id, Class<T> clazz) {
		String tabName = getTableName(clazz);
		String sql = "select * from " + tabName + " where id = ? ";
		List<T> ts = query(sql, new GenericRowMapper<>(clazz), id);
		return ts.isEmpty() ? null : ts.get(0);
	}

}
