package com.tianyl.weixin.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tianyl.core.ioc.annotation.Component;
import com.tianyl.core.orm.JdbcUtil;
import com.tianyl.weixin.model.OfficialAccount;

@Component
public class OfficialAccountDAO {

	public Integer save(OfficialAccount oa) {
		return JdbcUtil.save(oa);
	}

	public List<OfficialAccount> findAll() {
		return JdbcUtil.queryAll(OfficialAccount.class);
	}

	public Set<String> findExistWxIds() {
		List<String> temp = JdbcUtil.queryStr("select wxId from wx_officialaccount");
		Set<String> result = new HashSet<String>();
		result.addAll(temp);
		return result;
	}

}
