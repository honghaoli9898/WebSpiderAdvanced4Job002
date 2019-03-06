package com.tl.job002.iface.parser;

import java.util.List;
import java.util.Map;

import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;

public interface JDGoodsParseInterface {
	public Map<String, JDGoodsEntriy> parserHtmlSource(String htmlSource);

	public List<JDGoodsCommentsEntriy> parserHtmlSource(String htmlSource,
			JDGoodsEntriy jdGoodsEntriyp) ;
}
