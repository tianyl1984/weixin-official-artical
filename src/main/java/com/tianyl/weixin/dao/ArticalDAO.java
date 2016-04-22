package com.tianyl.weixin.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tianyl.core.ioc.annotation.Component;
import com.tianyl.core.orm.JdbcUtil;
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

}
