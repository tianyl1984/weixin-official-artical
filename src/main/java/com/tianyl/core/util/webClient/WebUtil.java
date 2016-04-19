package com.tianyl.core.util.webClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tianyl.core.util.io.IOUtils;

/**
 * Http与Servlet工具类.
 * 
 */
public class WebUtil {

	/**
	 * 获取url响应内容
	 * 
	 * @param url
	 * @param paramMap
	 * @param isGet
	 * @return
	 */
	public static RequestResult getUrlResponse(String url,
			Map<String, String> paramMap, Map<String, String> cookieMap,
			boolean isGet) {
		RequestResult rr = new RequestResult();
		if (paramMap == null) {
			paramMap = new HashMap<String, String>();
		}
		if (cookieMap == null) {
			cookieMap = new HashMap<String, String>();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url)).openConnection());
			if (!paramMap.isEmpty()) {
				conn.setDoInput(true);
			}
			conn.setDoOutput(true);
			conn.setRequestMethod(isGet ? "GET" : "POST");
			conn.setUseCaches(false);
			// 仅对当前请求自动重定向
			conn.setInstanceFollowRedirects(true);
			// header 设置编码
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:42.0) Gecko/20100101 Firefox/42.0");
			if (!cookieMap.isEmpty()) {
				String cookie = "";
				for (String key : cookieMap.keySet()) {
					cookie += key + "=" + cookieMap.get(key) + "; ";
				}
				cookie = cookie.substring(0, cookie.length() - 2);
				conn.setRequestProperty("Cookie", cookie);
			}
			// 连接
			conn.connect();
			writeParameters(conn, paramMap);
			rr.setResponseCode(conn.getResponseCode());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				rr.setOk(false);
				rr.setResultBytes(IOUtils.toByteArray(conn.getErrorStream()));
			} else {
				rr.setOk(true);
				rr.setResultBytes(IOUtils.toByteArray(conn.getInputStream()));
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rr;
	}

	private static String checkUrl(String url) {
		String result = url;
		if (url.startsWith("http://")) {
			url = url.replaceFirst("http://", "");
			if (url.contains("//")) {
				url = url.replaceAll("//", "/");
			}
			result = "http://" + url;
		}
		return result;
	}

	private static void writeParameters(HttpURLConnection conn,
			Map<String, String> map) throws IOException {
		if (map == null) {
			return;
		}
		String content = "";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			String val = map.get(key);
			content += (i == 0 ? "" : "&") + key + "="
					+ URLEncoder.encode(val, "utf-8");
			i++;
		}
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		out.flush();
		out.close();
	}

}
