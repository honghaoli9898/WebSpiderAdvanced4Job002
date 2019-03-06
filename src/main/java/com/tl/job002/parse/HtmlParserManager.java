package com.tl.job002.parse;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tl.job002.iface.parser.JDGoodsParseInterface;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.JsoupUtil;
import com.tl.job002.utils.RandomNumberUtil;

public class HtmlParserManager {
	public static Logger logger = Logger.getLogger(HtmlParserManager.class);
	public static JDGoodsParseInterface parserInterface = new JDGoodsParser4JsoupImpl();

	public static Map<String, JDGoodsEntriy> parserHtmlSource4RootUrl(
			String htmlSource) throws ParseException {
		return parserInterface.parserHtmlSource(htmlSource);
	}

	public static List<JDGoodsCommentsEntriy> parserHtmlSource(
			String htmlSource, UrlTaskPojo taskPojo) throws ClassNotFoundException, IOException {
		List<JDGoodsCommentsEntriy> jdGoodsCommentsEntriyList = null;
		htmlSource = JsoupUtil.getElementsBySelector(htmlSource, "body").text();
		jdGoodsCommentsEntriyList = parserInterface.parserHtmlSource(
				htmlSource, TaskScheduleManager.getJDGoodsEntriy(taskPojo.getTitle()));
		logger.info("商品skuID"+taskPojo.getTitle()+"评论信息解析成功");
		int time = RandomNumberUtil.getRandomNumber();
		logger.info("即将休息" + time / 1000 + "秒");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return jdGoodsCommentsEntriyList;
	}
}
