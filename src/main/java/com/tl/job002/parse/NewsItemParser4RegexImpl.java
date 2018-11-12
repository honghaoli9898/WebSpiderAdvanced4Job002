package com.tl.job002.parse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tl.job002.iface.parser.NewsItemParserInterface;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.utils.RegexUtil;
import com.tl.job002.utils.StaticValue;

public class NewsItemParser4RegexImpl implements NewsItemParserInterface {

	@Override
	public List<NewsItemEntity> parserHtmlSource(String htmlSource) throws ParseException {
		List<NewsItemEntity> itemEntityList = new ArrayList<NewsItemEntity>();
		NewsItemEntity itemEntity = null;
		/**
		 * 正则解析开始
		 */
		// 第一步拿到整个数据块的大块数据,一般略大于需要的数据块
		String blockRegex = "<ul class=\"tj3_1\">([\\s\\S]*?)</ul>";
		String matchContent = RegexUtil.getMatchText(htmlSource, blockRegex, 0);
		// 第二步逐条拿匹配内容块
		String itemRegex = "<li>[\\s\\S]*?</li>";
		Pattern pattern = Pattern.compile(itemRegex);
		Matcher matcher = pattern.matcher(matchContent);
		while (matcher.find()) {
			String input = matcher.group(0);
			// 先获取准确标题
			String titleRegex = "<a[\\s\\S]*?>([\\s\\S]*?)</a>";
			String title = RegexUtil.getMatchText(input, titleRegex, 1);
			// 获取链接
			String hrefRegex = "<a href=\"([\\s\\S]*?)\">";
			String href = RegexUtil.getMatchText(input, hrefRegex, 1);
			if (href.startsWith("../")) {
				href = StaticValue.indexUrl + href.substring(3);
			} else {
				href = StaticValue.rootUrl + href.substring(2);
			}
			// 获取发布时间
			String postTimeRegex = "<font>([\\s\\S]*?)</font>";
			String postTime = RegexUtil.getMatchText(input, postTimeRegex, 1);
			itemEntity = new NewsItemEntity(title, href, postTime);
			itemEntityList.add(itemEntity);
		}
		return itemEntityList;
	}

}
