package com.tl.job002.parse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tl.job002.iface.parser.NewsItemParserInterface;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.UrlTaskPojo.TaskTypeEnum;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.utils.JsoupUtil;
import com.tl.job002.utils.JsoupUtil.ContentSelectType;
import com.tl.job002.utils.RegexUtil;
import com.tl.job002.utils.StaticValue;

public class NewsItemParser4JsoupImpl implements NewsItemParserInterface {
	public static Logger logger = Logger.getLogger(NewsItemParser4JsoupImpl.class);

	// @Override
	// public List<NewsItemEntity> parserHtmlSource(String htmlSource) throws
	// ParseException {
	// List<NewsItemEntity> itemList = new ArrayList<NewsItemEntity>();
	// String liSelector = "ul.tj3_1>li>a";
	// List<String> titleList = JsoupUtil.getElementsBySelector(htmlSource,
	// liSelector, ContentSelectType.TEXT);
	// String hrefSelector = "ul.tj3_1>li>a[href]";
	// List<String> hrefList = JsoupUtil.getElementsBySelector(htmlSource,
	// hrefSelector, "href");
	// String postTimeSelector = "ul.tj3_1>li>font";
	// List<String> postTimeList = JsoupUtil.getElementsBySelector(htmlSource,
	// postTimeSelector,
	// ContentSelectType.TEXT);
	//
	// return null;
	// }
	@Override
	public List<UrlTaskPojo> parserHtmlSource4RootUrl(String htmlSource) {
		List<UrlTaskPojo> crawlTaskList = new ArrayList<UrlTaskPojo>();
		String selector = "ul.tj3_1>li";
		Elements liElements = JsoupUtil.getElementsBySelector(htmlSource, selector);
		UrlTaskPojo urlTaskPojo = null;
		String title = null;
		String postTime = null;
		String href = null;
		for (Element element : liElements) {
			title = JsoupUtil.getChildElementValue(element, 1, ContentSelectType.TEXT);
			postTime = JsoupUtil.getChildElementValue(element, 0, ContentSelectType.TEXT);
			href = JsoupUtil.getAttributeValue(element.child(1), "href");
			if (href.startsWith("../")) {
				href = StaticValue.indexUrl + href.substring(3);
			} else {
				href = StaticValue.rootUrl + href.substring(2);
			}
			urlTaskPojo = new UrlTaskPojo(title, href, postTime, TaskTypeEnum.CRAWL_TASK);
			crawlTaskList.add(urlTaskPojo);
		}
		return crawlTaskList;
	}

	@Override
	public NewsItemEntity parserHtmlSource4CrawlTaskUrl(String htmlSource) throws ParseException {
		NewsItemEntity itemEntity = null;
		// 首先拿到doc对象
		Document doc = JsoupUtil.getDoc(htmlSource);
		// // 取得title文本值
		// String titleSelector = "p.pbt";
		// Elements elements = doc.select(titleSelector);
		// String title = null;
		// if (elements.size() > 0) {
		// title = elements.get(0).text();
		// } else {
		// titleSelector = "div.page_title>h1";
		// elements = doc.select(titleSelector);
		// title = elements.get(0).text();
		// }
		// title = title.trim();
		// 发布时间由于不一致,采用外部发布时间为准

		// 来源
		String sourceName = "";
		String sourceNameRegex = "[\\s]+来源：[\\s\\S]*?>([\\s\\S]*?)<";
		String sourceNameRegexBak = "[\\s]+来源：[\\s\\S]*?[>]*([\\s\\S]*?)<";
		sourceName = RegexUtil.getMatchText(htmlSource, sourceNameRegex, 1);
		if (StringUtil.isBlank(sourceName)) {
			sourceName = RegexUtil.getMatchText(htmlSource, sourceNameRegexBak, 1);
		}
		if (StringUtil.isBlank(sourceName)) {
			sourceNameRegex = "来源：([\\s\\S]*?)<";
			sourceName = RegexUtil.getMatchText(htmlSource, sourceNameRegex, 1);
		}
		sourceName = sourceName.trim();
		// 抽取正文
		String bodySelector = "div.page_text";
		Elements elements = doc.select(bodySelector);
		String body = "";
		if (elements.size() > 0) {
			body = elements.get(0).text();
		}
		if (body == null) {
			bodySelector = "div.TRS_Editor";
			elements = doc.select(bodySelector);
			body = elements.get(0).text();
		}
		if (body == null) {
			bodySelector = "#container";
			elements = doc.select(bodySelector);
			body = elements.get(0).text();
		}

		body = body.trim();
		itemEntity = new NewsItemEntity();
		itemEntity.setSourceName(sourceName);
		itemEntity.setBody(body);
		return itemEntity;
	}

	public static void main(String[] args) {
		String text = "<span id=\"source_baidu\" style=\"padding-right: 31px;\">来源：中新网</span>";
		String postTimeRegex = "来源：([\\s\\S]*?)<";
		System.out.println(RegexUtil.getMatchText(text, postTimeRegex, 1));
	}
}
