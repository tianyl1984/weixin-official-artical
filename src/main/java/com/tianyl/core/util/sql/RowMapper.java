package com.tianyl.core.util.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

	T mapRow(ResultSet rs, int rowNum) throws SQLException;

}
