package com.tl.job002.download;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.tl.job002.parse.HtmlParserManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.SystemConfigParas;
import com.tl.job002.utils.WebDriverUtil;
import com.tl.job002.utils.WebPageDownloadUtil4ChromeDriver;

public class JDDownloadRunnable implements Runnable {
	public static int repetitionNumber = 0;
	private boolean enableRunning = true;
	private String name;
	public static Logger logger = Logger.getLogger(JDDownloadRunnable.class);

	public JDDownloadRunnable(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		WebPageDownloadUtil4ChromeDriver downloadInterface = new WebPageDownloadUtil4ChromeDriver();
		downloadInterface.setWebDriver(WebDriverUtil.createWebDriver(false,
				false));
		while (enableRunning) {
			UrlTaskPojo taskPojo;
			try {
				taskPojo = TaskScheduleManager.take();

				if (taskPojo != null) {
					// 1.打印下载的内容
					String htmlSource = downloadInterface.download(taskPojo
							.getUrl());
					if (htmlSource != null) {
						TaskScheduleManager.addSavedJDCommentsUrlSet(taskPojo
								.getUrl());
						List<JDGoodsCommentsEntriy> jdGoodsCommentsEntriyList = HtmlParserManager
								.parserHtmlSource(htmlSource, taskPojo);
						TaskScheduleManager
								.addJDCommentEntriyList(jdGoodsCommentsEntriyList);
						logger.info("商品skuID" + taskPojo.getTitle()
								+ "评论信息以回传给redis");
					} else {
						// 如果htmlSource==null,代表下载出错了
						logger.error(this.name + "所有下载方法已尝试完毕,该任务为="
								+ taskPojo.getUrl());
						TaskScheduleManager.addOneUrlPojo(taskPojo);
						;
						try {
							logger.info("-----------即将休息2个小时-----------");
							Thread.sleep(7200000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					logger.info(this.name + "没有带采集的任务,线程将睡眠"
							+ SystemConfigParas.once_sleep_time_for_empty_task
							/ 1000 + "秒");
					try {
						Thread.sleep(SystemConfigParas.once_sleep_time_for_empty_task);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					logger.info(this.name + "本次睡眠结束");
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		this.enableRunning = false;
		logger.info(this.name + "线程结束");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnableRunning() {
		return enableRunning;
	}

	public void setEnableRunning(boolean enableRunning) {
		this.enableRunning = enableRunning;
	}
}
