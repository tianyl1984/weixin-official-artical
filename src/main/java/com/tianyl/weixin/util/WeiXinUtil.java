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

	public static Element getByClass(Element ele, String tagName) {
		Elements eles = ele.getElementsByClass(tagName);
		if (eles != null && eles.size() > 0) {
			return eles.get(0);
		}
		return null;
	}

	public static String getTagValue(Element ele, String tag) {
		Element temp = getByTag(ele, tag);
		if (temp != null) {
			return temp.text();
		}
		return null;
	}

	public static String getValue(Element ele, String filter) {
		if (ele == null) {
			return null;
		}
		if (filter.contains(" ")) {
			int splitIndex = filter.indexOf(" ");
			String nextFilter = filter.substring(splitIndex + 1);
			String curFilter = filter.substring(0, splitIndex);
			Element nextEle = getEleByFilter(ele, curFilter);
			return getValue(nextEle, nextFilter);
		} else {
			Element curEle = getEleByFilter(ele, filter);
			if (filter.contains("[")) {
				return curEle.attr(filter.substring(filter.indexOf("[") + 1, filter.length() - 1));
			} else {
				return curEle.text();
			}
		}
	}

	private static Element getEleByFilter(Element ele, String curFilter) {
		Element nextEle = null;
		if (curFilter.contains("[")) {
			curFilter = curFilter.substring(0, curFilter.indexOf("["));
		}
		if (curFilter.startsWith(".")) {
			nextEle = getByClass(ele, curFilter.substring(1));
		} else {
			nextEle = getByTag(ele, curFilter);
		}
		return nextEle;
	}

}
