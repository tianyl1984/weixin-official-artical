package com.tianyl.weixin.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tianyl.core.ioc.annotation.Component;
import com.tianyl.core.orm.GenericRowMapper;
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

	public List<OfficialAccount> findUnread() {
		return JdbcUtil.query("select * from wx_officialaccount where id in (select id_officialaccount from wx_artical where hasRead = ? )", new GenericRowMapper<>(OfficialAccount.class), false);
	}

	public Integer findAllCount() {
		return JdbcUtil.queryIntOne("select count(1) cnt from wx_officialaccount");
	}

	public Integer findUnreadCount() {
		return JdbcUtil.queryIntOne("select count(distinct id_officialaccount) from wx_artical where hasRead = ?", false);
	}

}
