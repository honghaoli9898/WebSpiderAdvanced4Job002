package com.tl.job002.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tl.job002.iface.download.DownloadInterface;
import com.tl.job002.parse.HtmlParserManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.IOUtil;
import com.tl.job002.utils.StaticValue;
import com.tl.job002.utils.WebpageDownloadUtil4HttpClient;

/**
 * 该类作用为用户接口的管理类，包括种子添加接口 种子添加的不同方式和来源
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class UIManager {
	// 实例化一个网页下载接口实现类httpcline下载模式
	public static DownloadInterface downloadInterface = new WebpageDownloadUtil4HttpClient();
	public static Logger looger = Logger.getLogger(UIManager.class);

	public static UrlTaskPojo getRootUrlByDirect() {
		return new UrlTaskPojo("中国青年网-国内新闻", "http://news.youth.cn/gn/");
	}

	public static UrlTaskPojo getRootUrlByStaticValue() {
		return new UrlTaskPojo(StaticValue.rootTitle, StaticValue.rootUrl);
	}

	public static List<UrlTaskPojo> getRootUrlBySeedFileForClassPath(String filePath, boolean isClassPath)
			throws Exception {
		List<String> lineList = IOUtil.readFileToList(filePath, isClassPath, StaticValue.defaultENCODING);
		List<UrlTaskPojo> resultTaskPojo = new ArrayList<UrlTaskPojo>();
		for (String line : lineList) {
			line = line.trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				String[] columnArray = line.split("\\s");
				if (columnArray.length == 2) {
					UrlTaskPojo tempPojo = new UrlTaskPojo(columnArray[0].trim(), columnArray[1].trim());
					resultTaskPojo.add(tempPojo);
				} else {
					looger.error("错误行为:" + line);
					throw new Exception("存在不规范行,请检查!");
				}
			}
		}
		return resultTaskPojo;
	}

	public static void addSeedUrlsToTaskSchedule() throws Exception {
		// 定义种子文件路径
		String dataFilePath = "seed.txt";
		// 将种子任务从种子文件中读取出来,形成种子任务集合
		List<UrlTaskPojo> seedUrlPojoList = UIManager.getRootUrlBySeedFileForClassPath(dataFilePath, false);
		// 此时做与终极版的改动:将任务不再直接放到任务调度管理器,而是先逐个种子URL进行采集和解析,将解析出来的二级任务(子任务)再添加到任务调度里
		TaskScheduleManager.addUrlPojoList(seedUrlPojoList);
	}

	/**
	 * 将给定的种子任务,先进行采集和解析,将二级任务加入任务调度管理器中
	 * 
	 * @throws Exception
	 */
	public static void parseSeedUrlsTaskToSchedule() throws Exception {
		// 定义种子文件路径
		String dataFilePath = "seed.txt";
		// 将种子任务从种子文件中读取出来,形成种子任务集合
		List<UrlTaskPojo> seedUrlPojoList = UIManager.getRootUrlBySeedFileForClassPath(dataFilePath, false);
		// 此时做与终极版的改动:将任务不再直接放到任务调度管理器,而是先逐个种子URL进行采集和解析,将解析出来的二级任务(子任务)再添加到任务调度里
		for (UrlTaskPojo urlTaskPojo : seedUrlPojoList) {
			String htmlSource = downloadInterface.download(urlTaskPojo.getUrl());
			List<UrlTaskPojo> urlTaskPojoList = HtmlParserManager.parserHtmlSource4RootUrl(htmlSource);
			//将root种子解析出来的二级任务对象加入任务管理器
			TaskScheduleManager.addUrlPojoList(urlTaskPojoList);
		}
	}

	public static void main(String[] args) throws Exception {
		parseSeedUrlsTaskToSchedule();
	}
}
