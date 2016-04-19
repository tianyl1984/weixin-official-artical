package com.tianyl.core.util.webClient;

import java.io.UnsupportedEncodingException;

public class RequestResult {

	private boolean isOk;

	private String resultStr;

	private byte[] resultBytes;

	private int responseCode;

	public RequestResult() {

	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	public String getResultStr() {
		try {
			resultStr = new String(resultBytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resultStr;
	}

	public byte[] getResultBytes() {
		return resultBytes;
	}

	public void setResultBytes(byte[] resultBytes) {
		this.resultBytes = resultBytes;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

}
