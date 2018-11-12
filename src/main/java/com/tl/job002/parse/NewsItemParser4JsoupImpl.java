package com.tl.job002.parse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tl.job002.iface.parser.NewsItemParserInterface;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.utils.JsoupUtil;
import com.tl.job002.utils.JsoupUtil.ContentSelectType;
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
	public List<NewsItemEntity> parserHtmlSource(String htmlSource) {
		List<NewsItemEntity> itemList = new ArrayList<NewsItemEntity>();
		String selector = "ul.tj3_1>li";
		Elements liElements = JsoupUtil.getElementsBySelector(htmlSource, selector);
		NewsItemEntity itemEntity = null;
		String title = null;
		String postTime = null;
		String href = null;
		for (Element element : liElements) {
			title = JsoupUtil.getChildElementValue(element, 1, ContentSelectType.TEXT);
			postTime = JsoupUtil.getChildElementValue(element, 0, ContentSelectType.TEXT);
			href = JsoupUtil.getAttributeValue(element.child(1), "href");
			if(href.startsWith("../")){
				href = StaticValue.indexUrl+href.substring(3);
			}else{
				href = StaticValue.rootUrl+href.substring(2);
			}
			try {
				itemEntity = new NewsItemEntity(title, href, postTime);
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error("出错信息:"+title+'\n'+href+'\n'+postTime);
			}
			itemList.add(itemEntity);
		}
		return itemList;
	}
}
