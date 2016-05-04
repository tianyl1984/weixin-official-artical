package com.tianyl.weixin.web;

import com.alibaba.fastjson.JSONArray;
import com.tianyl.core.ioc.annotation.Autowired;
import com.tianyl.core.mvc.annotation.Controller;
import com.tianyl.weixin.service.ArticalService;
import com.tianyl.weixin.service.OfficialAccountService;

@Controller("/wx")
public class WeiXinController {

	@Autowired
	private ArticalService articalService;

	@Autowired
	private OfficialAccountService officialAccountService;

	public Object getAccountInfo() {
		JSONArray result = officialAccountService.getAccountInfo();
		return result;
	}

	public Object getUnreadAccount() {
		JSONArray result = officialAccountService.getUnreadAccount();
		return result;
	}

	public Object getArticals(Integer officialAccountId) {
		JSONArray result = articalService.find(officialAccountId);
		return result;
	}

	public Object getUnreadArticals(Integer officialAccountId) {
		JSONArray result = articalService.findUnreadArticals(officialAccountId);
		return result;
	}

	public Object searchByName(String name) {
		return officialAccountService.searchByName(name);
	}

	public void save(String wxId, String name) {
		officialAccountService.save(wxId, name);
	}

	public void setHasRead(Integer articalId) {
		articalService.setHasRead(articalId);
	}
}
