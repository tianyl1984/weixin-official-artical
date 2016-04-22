package com.tianyl.weixin.dao;

import java.util.List;

import com.tianyl.core.ioc.annotation.Component;
import com.tianyl.core.orm.JdbcUtil;
import com.tianyl.weixin.model.OfficialAccount;

@Component
public class OfficialAccountDAO {

	public static Integer save(OfficialAccount oa) {
		return JdbcUtil.save(oa);
	}

	public List<OfficialAccount> findAll() {
		return JdbcUtil.queryAll(OfficialAccount.class);
	}

}
