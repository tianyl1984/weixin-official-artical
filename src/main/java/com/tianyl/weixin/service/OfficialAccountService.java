package com.tianyl.weixin.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tianyl.core.ioc.annotation.Autowired;
import com.tianyl.core.ioc.annotation.Service;
import com.tianyl.core.util.webClient.RequestResult;
import com.tianyl.core.util.webClient.WebUtil;
import com.tianyl.weixin.dao.ArticalDAO;
import com.tianyl.weixin.dao.OfficialAccountDAO;
import com.tianyl.weixin.model.OfficialAccount;
import com.tianyl.weixin.util.WeiXinUtil;

@Service
public class OfficialAccountService {

	@Autowired
	private OfficialAccountDAO officialAccountDAO;

	@Autowired
	private ArticalDAO articalDAO;

	public JSONArray getAccountInfo() {
		List<OfficialAccount> accounts = officialAccountDAO.findAll();
		Map<Integer, Integer> articalCountMap = articalDAO.findCountMap();
		JSONArray result = new JSONArray();
		for (OfficialAccount oa : accounts) {
			JSONObject obj = new JSONObject();
			obj.put("id", oa.getId());
			obj.put("name", oa.getName());
			obj.put("count", articalCountMap.get(oa.getId()));
			result.add(obj);
		}
		return result;
	}

	public JSONArray searchByName(String name) {
		String url = null;
		try {
			url = "http://weixin.sogou.com/weixin?type=1&query=" + URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		RequestResult request = WebUtil.getUrlResponse(url, null, null, true);
		if (!request.isOk()) {
			return null;
		}
		JSONArray result = parseAccount(request.getResultStr());
		return result;
	}

	private JSONArray parseAccount(String html) {
		Document doc = Jsoup.parse(html);
		Elements eles = doc.getElementsByClass("_item");
		JSONArray result = new JSONArray();
		if (eles != null) {
			for (int index = 0; index < eles.size(); index++) {
				Element ele = eles.get(index);
				String name = WeiXinUtil.getValue(ele, ".txt-box h3");
				String wxId = WeiXinUtil.getValue(ele, ".txt-box h4 label");
				String url = ele.attr("href");
				JSONObject obj = new JSONObject();
				obj.put("name", name);
				obj.put("wxId", wxId);
				obj.put("url", url);
				result.add(obj);
			}
		}
		return result;
	}

	public void save(String wxId, String name) {
		OfficialAccount oa = new OfficialAccount();
		oa.setName(name);
		oa.setWxId(wxId);
		officialAccountDAO.save(oa);
	}
}
