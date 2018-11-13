package com.tl.job002.iface.parser;

import java.text.ParseException;
import java.util.List;

import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.entity.NewsItemEntity;

public interface NewsItemParserInterface {
	public List<UrlTaskPojo> parserHtmlSource4RootUrl(String htmlSource) throws ParseException;

	public NewsItemEntity parserHtmlSource4CrawlTaskUrl(String htmlSource) throws ParseException;
}
