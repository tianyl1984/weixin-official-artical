package com.tianyl.weixin.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.tianyl.weixin.vo.AccountInfoVO;

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
		Set<String> savedWxIds = officialAccountDAO.findExistWxIds();
		List<AccountInfoVO> vos = parseAccount(request.getResultStr());
		JSONArray result = new JSONArray();
		Set<String> wxIds = new HashSet<String>();
		for (AccountInfoVO vo : vos) {
			if (wxIds.contains(vo.getWxId())) {
				continue;
			}
			JSONObject obj = new JSONObject();
			obj.put("name", vo.getName());
			obj.put("wxId", vo.getWxId());
			obj.put("url", vo.getUrl());
			obj.put("exist", savedWxIds.contains(vo.getWxId()));
			result.add(obj);
			wxIds.add(vo.getWxId());
		}
		return result;
	}

	private List<AccountInfoVO> parseAccount(String html) {
		List<AccountInfoVO> result = new ArrayList<AccountInfoVO>();
		Document doc = Jsoup.parse(html);
		Elements eles = doc.getElementsByClass("_item");
		if (eles != null) {
			for (int index = 0; index < eles.size(); index++) {
				Element ele = eles.get(index);
				String name = WeiXinUtil.getValue(ele, ".txt-box h3");
				String wxId = WeiXinUtil.getValue(ele, ".txt-box h4 label");
				String url = ele.attr("href");
				AccountInfoVO vo = new AccountInfoVO();
				vo.setName(name);
				vo.setWxId(wxId);
				vo.setUrl(url);
				result.add(vo);
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
