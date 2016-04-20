package com.tianyl.weixin.dao;

import com.tianyl.core.orm.JdbcUtil;
import com.tianyl.weixin.model.OfficialAccount;

public class OfficialAccountDAO {

	public static Integer save(OfficialAccount oa) {
		return JdbcUtil.save(oa);
	}

}
