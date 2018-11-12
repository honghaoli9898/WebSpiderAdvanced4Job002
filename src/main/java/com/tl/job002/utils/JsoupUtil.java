package com.tl.job002.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.Asserts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tl.job002.iface.download.DownloadInterface;
import com.tl.job002.iface.parser.NewsItemParserInterface;
import com.tl.job002.parse.NewsItemParser4JsoupImpl;

/**
 * jsoup工具类
 * 
 * @author lihonghao
 * @date 2018年11月9日
 */
public class JsoupUtil {
	public static enum ContentSelectType {
		OUTHER_HTML, HTML, TEXT
	}

	public static Document getDoc(String htmlSource) {
		Document doc = Jsoup.parse(htmlSource);
		return doc;
	}

	public static Elements getElementsBySelector(String htmlSource, String selector) {
		Document document = getDoc(htmlSource);
		return document.select(selector);
	}

	public static List<String> getElementsBySelector(String htmlSource, String selector,
			ContentSelectType contentType) {
		Elements elements = getElementsBySelector(htmlSource, selector);
		List<String> eleList = new ArrayList<String>();

		switch (contentType) {
		case OUTHER_HTML:
			for (Element element : elements) {
				eleList.add(element.outerHtml());
				break;
			}
		case HTML:
			for (Element element : elements) {
				eleList.add(element.html());
				break;
			}
		case TEXT:
			for (Element element : elements) {
				eleList.add(element.text());
				break;
			}
		default:
			break;
		}
		return eleList;
	}

	public static List<String> getElementsBySelector(String htmlSource, String selector, String selectAttribute) {
		Asserts.notBlank(selectAttribute, "is not null");
		Elements elements = getElementsBySelector(htmlSource, selector);
		List<String> eleList = new ArrayList<String>();
		for (Element ele : elements) {
			eleList.add(ele.attr(selectAttribute));
		}
		return eleList;
	}

	public static String getAttributeValue(Element element, String attributeKey) {
		Asserts.notBlank(attributeKey, "is not null");
		return element.attr(attributeKey);
	}

	public static String getChildElementValue(Element element, int childIndex, ContentSelectType contentType) {
		String value = null;
		switch (contentType) {
		case OUTHER_HTML:
			value = element.child(childIndex).outerHtml();
			break;
		case HTML:
			value = element.child(childIndex).html();
			break;
		case TEXT:
			value = element.child(childIndex).text();
			break;
		default:
			break;
		}
		return value;
	}
	public static void main(String[] args) throws ParseException {
		String url = "http://news.youth.cn/gn/";
		DownloadInterface download = new WebpageDownloadUtil4HttpClient();
		String htmlSource = download.download(url);
		NewsItemParserInterface s = new NewsItemParser4JsoupImpl();
		s.parserHtmlSource(htmlSource);
	}
}
