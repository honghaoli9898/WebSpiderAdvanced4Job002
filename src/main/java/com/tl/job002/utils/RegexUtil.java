package com.tl.job002.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配工具类
 * @author lihonghao
 * @date 2018年11月7日
 */
public class RegexUtil {
	public static String getMatchText(String input, String regex, int groupNum) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(groupNum);
		}
		return null;
	}
}
