package com.tl.job002.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

public class WebCharsetDetecorUtil {
	public static String regex = "charset=[\"]?([\\s\\S]*?)\"?\\s?/?[\">]";

	public static String getCharsetByHttpHeader(URLConnection urlConnection) {
		Map<String, List<String>> headerMap = urlConnection.getHeaderFields();
		Set<String> keySet = headerMap.keySet();
		String findCharset = null;
		for (String key : keySet) {
			if (key != null && key.equalsIgnoreCase("Content-Type")) {
				List<String> valueList = headerMap.get(key);
				String line = valueList.get(0);
				String[] valueArray = line.split(StaticValue.sep_semicolon);
				if (valueArray.length == 2) {
					String charsetString = valueArray[1];
					charsetString = charsetString.trim();
					valueArray = charsetString.split(StaticValue.sep_equales);
					if (valueArray.length == 2) {
						findCharset = valueArray[1].trim();
						break;
					}
				}
			}
		}
		return findCharset;
	}

	public static String getCharsetByHtmlSource(String htmlSource) throws IOException {
		StringReader sr = new StringReader(htmlSource);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		String findCharset = null;
		while ((line = br.readLine()) != null) {
			line = line.trim().toLowerCase();
			if (line.contains("<meta")) {
				findCharset = RegexUtil.getMatchText(line, WebCharsetDetecorUtil.regex, 1);
				if (findCharset != null) {
					break;
				}
			} else if (line.contains("</head>")) {
				break;
			}
		}
		br.close();
		return findCharset;
	}

	public static String getCharset(String url) throws IOException {
		URL urlObj = new URL(url);
		URLConnection urlconnection = urlObj.openConnection();
		String findCharset = null;
		findCharset = getCharsetByHttpHeader(urlconnection);
		// 如果header里没找到就去meta里找
		if (findCharset == null) {
			BufferedReader br = IOUtil.getBR(urlconnection, StaticValue.defaultENCODING);
			;
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.contains("<meta")) {
					findCharset = RegexUtil.getMatchText(line, regex, 1);
					if (findCharset != null) {
						break;
					}
				} else if (line.contains("</head>")) {
					break;
				}
			}
			br.close();
		}
		return findCharset == null ? StaticValue.defaultENCODING : findCharset;
	}

	@SuppressWarnings("deprecation")
	public static String getCharset(HttpEntity entity, String defaultCharset)
			throws IOException {
		String findCharset = null;
		findCharset = EntityUtils.getContentCharSet(entity);
		if (findCharset == null) {
			byte[] contextByteArray = IOUtil.convertInputStreamToByteArray(entity.getContent());
			String htmlSource = new String(contextByteArray, defaultCharset);
			StringReader sr = new StringReader(htmlSource);
			BufferedReader br = new BufferedReader(sr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.contains("<meta")) {
					findCharset = RegexUtil.getMatchText(line, regex, 1);
					if (findCharset != null) {
						break;
					}
				} else if (line.contains("</head>")) {
					break;
				}
			}
			br.close();
		}
		return findCharset == null ? defaultCharset : findCharset;
	}

	public static void main(String[] args) throws IOException {
		String url = "https://www.baidu.com/";
		System.out.println(WebCharsetDetecorUtil.getCharset(url));
	}
}
