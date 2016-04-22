package com.tianyl.weixin.util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeiXinUtil {

	public static String getClazzValue(Element ele, String clazzName) {
		Elements eles = ele.getElementsByClass(clazzName);
		if (eles != null && eles.size() > 0) {
			return eles.get(0).text();
		}
		return null;
	}

	public static Element getByTag(Element ele, String tagName) {
		Elements eles = ele.getElementsByTag(tagName);
		if (eles != null && eles.size() > 0) {
			return eles.get(0);
		}
		return null;
	}

}
