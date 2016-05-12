package com.tianyl.core.util.webClient;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
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
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(30 * 1000);
			if (!paramMap.isEmpty()) {
				conn.setDoOutput(true);
			}
			conn.setDoInput(true);
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

	public static String getUrlResponse(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url)).openConnection());
			// 必须大写
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			// 请求头
			// 仅对当前请求自动重定向
			conn.setInstanceFollowRedirects(false);
			// header 设置编码
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:42.0) Gecko/20100101 Firefox/42.0");
			conn.setConnectTimeout(10000);
			// 连接
			conn.connect();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException();
			}
			String result = new String(IOUtils.toByteArray(conn.getInputStream()));
			conn.disconnect();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		if (map == null || map.isEmpty()) {
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

	public static void downloadFileSimple(String url, File file) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(checkUrl(url)).openConnection();
			con.setUseCaches(false);
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("download error response code : " + con.getResponseCode() + " url : " + url);
			}
			InputStream is = con.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			byte[] b = new byte[1024];
			int length = 0;
			while ((length = bis.read(b)) != -1) {
				fos.write(b, 0, length);
			}
			fos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static String getRealURL(String docUrl, String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		String aa = "";
		try {
			URI uri = new URI(docUrl);
			aa = uri.getScheme() + "://" + uri.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aa + url;
	}

	public static void main(String[] args) {
		// 文章页
		String url = "http://mp.weixin.qq.com/s?timestamp=1462959204&src=3&ver=1&signature=W029zTYLzc2FkePsbEHcZmaE3iR3MjSyhRmH0ETUYtIxX4ZNDtosRgmf7vR-uhy3ENziuGHHlYcERSNGsbWBIbGkGnosGZx35O-zrHsT4a4m-fz9MLy-cYu4MuJlB3ifgxAgsPkYnnnFos5HIJWOZFHsJPoBDK9HKWX2aI8gINw=";
		// 历史页
		url = "http://mp.weixin.qq.com/profile?src=3&timestamp=1463031441&ver=1&signature=6XnFE15hYT3PtRbwbPlJjzOBMetU1uI914M-q6uL*7TuhpEa2GlKCeDzJTBd86M6nXxvwugInETSj43ckD57tw==";
		// System.out.println(getUrlResponse(url));
		// System.out.println(getUrlResponse(url, null, null, true).getResultStr());
		url = "http://mmbiz.qpic.cn/mmbiz/z7ZD1WagSLhDonNDyvib5Gorv4ibOYT6utD3U73C02fv2zHG4Gb3tic71fGPRyEEHoImU3meicBPam4OCQxfLEU2xA/0?wx_fmt=jpeg";
		downloadFileSimple(url, new File("e:/aa.jpeg"));
	}

}
