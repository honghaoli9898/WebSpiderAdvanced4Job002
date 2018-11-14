package com.tl.job002.parse;

import java.text.ParseException;
import java.util.List;

import com.tl.job002.iface.download.DownloadInterface;
import com.tl.job002.iface.parser.NewsItemParserInterface;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.utils.WebpageDownloadUtil4HttpClient;

public class HtmlParserManager {
	public static NewsItemParserInterface parserInterface = new NewsItemParser4JsoupImpl();

	public static NewsItemEntity parserHtmlSource4CrawlUrl(String htmlSource) throws ParseException {
		return parserInterface.parserHtmlSource4CrawlTaskUrl(htmlSource);
	}

	public static List<UrlTaskPojo> parserHtmlSource4RootUrl(String htmlSource) throws ParseException {
		return parserInterface.parserHtmlSource4RootUrl(htmlSource);
	}

	public static void main(String[] args) throws ParseException {
		String url = "http://news.youth.cn/gn/201811/t20181110_11781927.htm";
		DownloadInterface download = new WebpageDownloadUtil4HttpClient();
		String htmlSource = download.download(url);
		NewsItemEntity itemEntityList = parserHtmlSource4CrawlUrl(htmlSource);
		System.out.println(itemEntityList.getSourceName());
	}
}
