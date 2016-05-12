package com.tianyl.weixin.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tianyl.core.ioc.annotation.Component;
import com.tianyl.core.orm.GenericRowMapper;
import com.tianyl.core.orm.JdbcUtil;
import com.tianyl.core.orm.RowMapper;
import com.tianyl.weixin.model.Artical;

@Component
public class ArticalDAO {

	public Set<String> findUuids(Integer oaId) {
		List<String> uuids = JdbcUtil.queryStr("select uuid from wx_artical where id_officialaccount = ? ", oaId);
		Set<String> results = new HashSet<>();
		results.addAll(uuids);
		return results;
	}

	public void save(List<Artical> articals) {
		JdbcUtil.saveList(articals);
	}

	public Map<Integer, Integer> findCountMap() {
		String sql = "select count(1) cnt,id_officialaccount oaId from wx_artical group by id_officialaccount";
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		JdbcUtil.query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				result.put(rs.getInt("oaId"), rs.getInt("cnt"));
				return null;
			}
		});
		return result;
	}

	public List<Artical> find(Integer officialAccountId) {
		String sql = "select * from wx_artical where id_officialaccount = ? order by publishDate desc";
		return JdbcUtil.query(sql, new GenericRowMapper<>(Artical.class), officialAccountId);
	}

	public Map<Integer, Integer> findUnreadCountMap() {
		String sql = "select count(1) cnt,id_officialaccount oaId from wx_artical where hasRead = ? group by id_officialaccount";
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		JdbcUtil.query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				result.put(rs.getInt("oaId"), rs.getInt("cnt"));
				return null;
			}
		}, false);
		return result;
	}

	public void updateToRead(Integer articalId) {
		String sql = "update wx_artical set hasRead = ? where id = ?";
		JdbcUtil.update(sql, true, articalId);
	}

	public List<Artical> findUnreadArticals(Integer officialAccountId) {
		String sql = "select * from wx_artical where id_officialaccount = ? and hasRead = ? order by publishDate desc";
		return JdbcUtil.query(sql, new GenericRowMapper<>(Artical.class), officialAccountId, false);
	}

	public void updateToReadByOfficialAccountId(Integer officialAccountId) {
		String sql = "update wx_artical set hasRead = ? where id_officialaccount = ? ";
		JdbcUtil.update(sql, true, officialAccountId);
	}

	public void updateToUnRead(Integer articalId) {
		String sql = "update wx_artical set hasRead = ? where id = ?";
		JdbcUtil.update(sql, false, articalId);
	}

	public List<Artical> findNeedOfflineArtical() {
		String sql = "select * from wx_artical where offlineUrl is null limit 1000";
		return JdbcUtil.query(sql, new GenericRowMapper<>(Artical.class));
	}

}
