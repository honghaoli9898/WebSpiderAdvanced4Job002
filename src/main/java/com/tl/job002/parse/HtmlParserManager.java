package com.tl.job002.parse;

import java.text.ParseException;
import java.util.List;

import com.tl.job002.iface.download.DownloadInterface;
import com.tl.job002.iface.parser.NewsItemParserInterface;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.utils.WebpageDownloadUtil4HttpClient;

public class HtmlParserManager {
	public static NewsItemParserInterface parserInterface = new NewsItemParser4JsoupImpl();

	public static List<NewsItemEntity> parserHtmlSource(String htmlSource) throws ParseException {
		return parserInterface.parserHtmlSource(htmlSource);
	}

	public static void main(String[] args) throws ParseException {
		String url = "http://news.youth.cn/gn";
		DownloadInterface download = new WebpageDownloadUtil4HttpClient();
		String htmlSource = download.download(url);
		List<NewsItemEntity> itemEntityList = parserHtmlSource(htmlSource);
		for (NewsItemEntity newsItemEntity : itemEntityList) {
			System.out.println(newsItemEntity);
		}
		System.out.println(itemEntityList.size());
	}
}
