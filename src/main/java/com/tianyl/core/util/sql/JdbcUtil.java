package com.tianyl.core.util.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcUtil {

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

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/film?useUnicode=true&characterEncoding=utf-8";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, "root", "tyl123");
		return conn;
	}

	public static Integer getInteger(ResultSet rs, String label) throws SQLException {
		if (rs.getObject(label) != null) {
			return rs.getInt(label);
		}
		return null;
	}

}
