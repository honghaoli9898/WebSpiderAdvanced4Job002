package com.tl.job002.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.tl.job002.parse.HtmlParserManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.UrlTaskPojo.TaskTypeEnum;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.IOUtil;
import com.tl.job002.utils.StaticValue;
import com.tl.job002.utils.SystemConfigParas;
import com.tl.job002.utils.WebDriverUtil;
import com.tl.job002.utils.WebPageDownloadUtil4ChromeDriver;

/**
 * 该类作用为用户接口的管理类，包括种子添加接口 种子添加的不同方式和来源
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class UIManager {
	// 实例化一个网页下载接口实现类httpcline下载模式
	public static WebPageDownloadUtil4ChromeDriver downloadInterface = new WebPageDownloadUtil4ChromeDriver();
	static {
		downloadInterface.setWebDriver(WebDriverUtil.createWebDriver(false,
				true));
	}
	public static Logger logger = Logger.getLogger(UIManager.class);

	public static UrlTaskPojo getRootUrlByDirect() {
		return new UrlTaskPojo(
				"京东商城-手机商品",
				"https://search.jd.com/Search?keyword=手机&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&cid2=653&cid3=655&page=1&s=1&click=0");
	}

	public static UrlTaskPojo getRootUrlByStaticValue() {
		return new UrlTaskPojo(StaticValue.rootTitle, StaticValue.rootUrl);
	}

	public static List<UrlTaskPojo> getRootUrlBySeedFileForClassPath(
			String filePath, boolean isClassPath) throws Exception {
		List<String> lineList = IOUtil.readFileToList(filePath, isClassPath,
				StaticValue.defaultENCODING);
		List<UrlTaskPojo> resultTaskPojo = new ArrayList<UrlTaskPojo>();
		for (String line : lineList) {
			line = line.trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				String[] columnArray = line.split("\\s");
				if (columnArray.length == 2) {
					UrlTaskPojo tempPojo = new UrlTaskPojo(
							columnArray[0].trim(), columnArray[1].trim());
					resultTaskPojo.add(tempPojo);
				} else {
					logger.error("错误行为:" + line);
					throw new Exception("存在不规范行,请检查!");
				}
			}
		}
		return resultTaskPojo;
	}

	/**
	 * 将给定的种子任务,先进行采集和解析,将二级任务加入任务调度管理器中
	 * 
	 * @throws Exception
	 */
	public static void parseSeedUrlsTaskToSchedule() throws Exception {
		// 将种子任务从种子文件中读取出来,形成种子任务集合
		List<UrlTaskPojo> seedRootUrlPojoList = getRootDownLoadPagUrl();
		// 此时做与终极版的改动:将任务不再直接放到任务调度管理器,而是先逐个种子URL进行采集和解析,将解析出来的二级任务(子任务)再添加到任务调度里
		for (UrlTaskPojo urlTaskPojo : seedRootUrlPojoList) {
			String htmlSource = downloadInterface
					.download(urlTaskPojo.getUrl());
			Map<String, JDGoodsEntriy> urlTaskPojoMap = HtmlParserManager
					.parserHtmlSource4RootUrl(htmlSource);
			List<UrlTaskPojo> urlTaskPojoList = new ArrayList<UrlTaskPojo>();
			for (Entry<String, JDGoodsEntriy> url : urlTaskPojoMap.entrySet()) {
				String commentUrl = SystemConfigParas.comment_url.replace("()",
						url.getValue().getGoodsSKU());
				for (int num = 0; num < SystemConfigParas.download_comment_page_num; num++) {
					commentUrl = commentUrl.replace("{}", "" + num);
					UrlTaskPojo urlSecondTaskPojo = new UrlTaskPojo(url
							.getValue().getGoodsSKU(), commentUrl,
							TaskTypeEnum.CRAWL_TASK);
					if (!TaskScheduleManager
							.isInsaveSeedUrlSet(urlSecondTaskPojo)
							&& !TaskScheduleManager
									.isInSaveJDCommentsUrlSet(urlSecondTaskPojo
											.uniqString())) {
						urlTaskPojoList.add(urlSecondTaskPojo);
						TaskScheduleManager.addJDGoodsEntriyMap(url);
					}
				}
			}
			// 将root种子解析出来的二级任务对象加入任务管理器
			TaskScheduleManager.addUrlPojoList(urlTaskPojoList);
			logger.info("当前的todoJDGoodsEntriySize()="
					+ TaskScheduleManager.getToDoJDGoodsSize());
		}
	}

	public static List<UrlTaskPojo> getRootDownLoadPagUrl() throws Exception {
		String dataFilePath = "seed.txt";
		List<UrlTaskPojo> seedUrlPojoList = UIManager
				.getRootUrlBySeedFileForClassPath(dataFilePath, false);
		List<UrlTaskPojo> urlPojoList = new ArrayList<UrlTaskPojo>();
		for (UrlTaskPojo urlTaskPojo : seedUrlPojoList) {
			for (int i = 1; i <= SystemConfigParas.download_page_num; i++) {
				urlPojoList.add(new UrlTaskPojo(urlTaskPojo.getTitle(),
						urlTaskPojo.getUrl().replace("()", "" + (2 * i - 1))));
			}

		}
		return urlPojoList;
	}

	public static void main(String[] args) throws Exception {
		parseSeedUrlsTaskToSchedule();
	}
}
