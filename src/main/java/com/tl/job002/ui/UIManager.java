package com.tl.job002.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tl.job002.download.DownloadManager;
import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.IOUtil;
import com.tl.job002.utils.StaticValue;

/**
 * 该类作用为用户接口的管理类，包括种子添加接口 种子添加的不同方式和来源
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class UIManager {
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
		String dataFilePath = "seed.txt";
		List<UrlTaskPojo> seedUrlPojoList = UIManager.getRootUrlBySeedFileForClassPath(dataFilePath, false);
		TaskScheduleManager.addUrlPojoList(seedUrlPojoList);
	}
}
