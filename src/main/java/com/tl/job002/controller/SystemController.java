package com.tl.job002.controller;

/**
 * 系统启动类,入口类
 * @author lihonghao
 * @date 2018年11月10日
 */
import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tl.job002.download.DownloadManager;
import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.ui.UIManager;
import com.tl.job002.utils.SystemConfigParas;

public class SystemController {
	// 将log4j配置文件放到jar包外面的路径更新
//	static {
//		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.properties");
//	}
	// 添加日志功能
	public static Logger logger = Logger.getLogger(SystemController.class);

	// 主线程开始
	public static void main(String[] args) throws Exception {
		// 启动下载线程 并完成解析
		DownloadManager.start();
		// 采用系统监控管理器
		MonitorManager.start();

		// 周期执行
		int circleCounter = 1;
		while (true) {
			logger.info("第" + circleCounter + "轮添加种子任务开始");
			UIManager.parseSeedUrlsTaskToSchedule();
			logger.info("第" + circleCounter + "轮添加种子任务结束");
			circleCounter++;
			logger.info("即将休息" + SystemConfigParas.add_seed_time_one_circle / 1000 + "秒");
			Thread.sleep(SystemConfigParas.add_seed_time_one_circle);
			logger.info("第" + circleCounter + "轮执行结束");
		}
	}
}
