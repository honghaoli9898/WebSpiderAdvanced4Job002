package com.tl.job002.iface.parser;

import java.text.ParseException;
import java.util.List;

import com.tl.job002.pojos.entity.NewsItemEntity;

public interface NewsItemParserInterface {
	public List<NewsItemEntity> parserHtmlSource(String htmlSource) throws ParseException;
}
