package com.tl.job002.controller;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tl.job002.download.DownloadManager;
import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.ui.UIManager;
import com.tl.job002.utils.SystemConfigParas;

public class SystemController {
	static {
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.properties");
	}
	public static Logger logger = Logger.getLogger(SystemController.class);

	public static void main(String[] args) throws Exception {
		// 启动uiManager注入种子任务
		// UIManager.addSeedUrlsToTaskSchedule();
		// 启动下载线程 并完成解析
		DownloadManager.start();
		// 采用系统监控管理器
		MonitorManager.start();

		// 周期执行
		int circleCounter = 1;
		while (true) {
			logger.info("第" + circleCounter + "轮添加种子任务开始");
			UIManager.addSeedUrlsToTaskSchedule();
			logger.info("第" + circleCounter + "轮添加种子任务结束");
			circleCounter++;
			logger.info("即将休息" + SystemConfigParas.add_seed_time_one_circle / 1000 + "秒");
			Thread.sleep(SystemConfigParas.add_seed_time_one_circle);
			logger.info("第" + circleCounter + "轮执行结束");
		}
	}
}
