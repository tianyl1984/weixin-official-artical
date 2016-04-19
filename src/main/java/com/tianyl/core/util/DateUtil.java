package com.tianyl.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date parseDate(String dateStr) {
		dateStr = dateStr != null ? dateStr.trim() : "";
		Date result = null;
		if (dateStr.length() == 10) {
			try {
				result = new SimpleDateFormat("yyyy/MM/dd").parse(dateStr);
			} catch (ParseException e) {
				// e.printStackTrace();
			}
		}
		if (dateStr.length() == 19) {
			try {
				result = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dateStr);
			} catch (ParseException e) {
				// e.printStackTrace();
			}
		}
		if (dateStr.length() == 11) {
			try {
				result = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(Calendar.getInstance().get(Calendar.YEAR) + "/" + dateStr);
			} catch (ParseException e) {
				// e.printStackTrace();
			}
		}
		if (result == null) {
			throw new RuntimeException("format time error:" + dateStr);
		}
		return result;
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		System.out.println(year);
		String dateStr = "2016/02/09";
		// dateStr = "02/11 12:44";
		// dateStr = "2016/02/11 12:44:05";
		Date d = parseDate(dateStr);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
	}
}
