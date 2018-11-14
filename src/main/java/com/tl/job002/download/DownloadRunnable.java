package com.tl.job002.download;

import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.tl.job002.iface.download.DownloadInterface;
import com.tl.job002.parse.HtmlParserManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.ui.UIManager;
import com.tl.job002.utils.ObjectAndByteArrayConvertor;
import com.tl.job002.utils.RedisOperUtil;
import com.tl.job002.utils.StaticValue;
import com.tl.job002.utils.SystemConfigParas;
import com.tl.job002.utils.WebpageDownloadUtil4HttpClient;

public class DownloadRunnable implements Runnable {
	// 定义存储解析出来的对象的list的key
	public static String toSaveNewsItemEntityListKey = "to_save_news_item_entity_list_key";
	private boolean enableRunning = true;
	private String name;
	public static Logger logger = Logger.getLogger(DownloadRunnable.class);
	public static int repetitionNumber = 0;

	public DownloadRunnable(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		DownloadInterface downloadInterface = new WebpageDownloadUtil4HttpClient();
		RedisOperUtil redisOperUtil = RedisOperUtil.getInstance();
		while (enableRunning) {
			UrlTaskPojo taskPojo = null;
			try {
				taskPojo = TaskScheduleManager.take();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (taskPojo != null) {
				// 1.打印下载的内容
				String htmlSource = downloadInterface.download(taskPojo.getUrl());
				if (htmlSource != null) {
					try {
						logger.info(taskPojo.getUrl() + "将进入解析环节");
						NewsItemEntity itemEntity = HtmlParserManager.parserHtmlSource4CrawlUrl(htmlSource);
						itemEntity.setTitle(taskPojo.getTitle());
						itemEntity.setPostTimeString(taskPojo.getPostTime());
						itemEntity.setSourceURL(taskPojo.getUrl());
						// 将解析出来的结果,回传到redis中
						byte[] byteArray = ObjectAndByteArrayConvertor.convertObjectToByteArray(itemEntity);
						redisOperUtil.getJedis()
								.lpush(toSaveNewsItemEntityListKey.getBytes(StaticValue.defaultENCODING), byteArray);
						logger.info(taskPojo.getUrl() + ",数据下载与解析完成,已推送到redis中");
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// 如果htmlSource==null,代表下载出错了
					logger.error(this.name + "下载出错,该任务为=" + taskPojo.getUrl());
				}
			} else {
				logger.info(
						this.name + "没有带采集的任务,线程将睡眠" + SystemConfigParas.once_sleep_time_for_empty_task / 1000 + "秒");
				try {
					Thread.sleep(SystemConfigParas.once_sleep_time_for_empty_task);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info(this.name + "本次睡眠结束");
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

	public static void main(String[] args) throws Exception {
		// 将带采集的url加入到Task任务当中
		UIManager.addSeedUrlsToTaskSchedule();
		// 启动线程
		DownloadManager.start();
	}
}
