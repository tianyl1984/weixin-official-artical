package com.tianyl.weixin.web;

import java.util.Date;

import com.tianyl.core.mvc.annotation.Controller;
import com.tianyl.core.orm.JdbcUtil;
import com.tianyl.weixin.model.Artical;

@Controller("/wx")
public class WeiXinController {

	public Object m1(String name) {
		Artical model = new Artical();
		model.setOfficialAccountId(1);
		model.setPublishDate(new Date());
		model.setTitle("中文");
		model.setUrl("safddddddddddd");
		// Integer result = JdbcUtil.save(model);
		// List<OfficialAccount> oas = JdbcUtil.queryAll(OfficialAccount.class);
		// List<Artical> result = JdbcUtil.queryAll(Artical.class);
		model.setId(1);
		Artical result = JdbcUtil.get(1, Artical.class);
		return result;
	}
}
