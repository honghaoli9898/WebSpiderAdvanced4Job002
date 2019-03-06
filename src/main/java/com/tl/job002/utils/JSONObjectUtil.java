package com.tl.job002.utils;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class JSONObjectUtil {
	public static Map<String, String> getJSONObjectValues(
			Map<String, String> kvMap, JSONObject jsonObject, String... args) {
		for (String key : args) {
			kvMap.put(key, jsonObject.getString(key));
		}
		return kvMap;
	}
}
