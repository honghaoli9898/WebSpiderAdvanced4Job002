package com.tl.job002.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date工具类
 * 
 * @author lihonghao
 * @date 2018年11月9日
 */
public class DateUtil {
	public static final String pattern = "yyyy-MM-dd HH:mm:ss";
	public static final String pattern_year_month_day = "yyyy-MM-dd";

	public static SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	public static SimpleDateFormat sdf_year_month_day = new SimpleDateFormat(pattern_year_month_day);

	// 拿到当前所属的date对象
	public static Date getDate() {
		return new Date();
	}

	public static Date parserStringToDate(String dateString) throws ParseException {
		Date datetimeObj = null;
		synchronized (sdf) {
			datetimeObj = sdf.parse(dateString);
		}
		return datetimeObj;
	}

	public static String formatDateToString(Date date) {
		synchronized (sdf) {
			return sdf.format(date);
		}
	}

	public static String formatDateToStringYearMonthDay(Date date) {
		synchronized (sdf_year_month_day) {
			return sdf_year_month_day.format(date);
		}
	}

	public static String getCurrentDay() {
		return formatDateToStringYearMonthDay(DateUtil.getDate());
	}
	public static void main(String[] args) {
		System.out.println(getCurrentDay());
	}
}
