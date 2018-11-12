package com.tl.job002.monitor;

import org.apache.log4j.Logger;

import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.DateUtil;
import com.tl.job002.utils.SystemConfigParas;

/**
 * 线程监控类
 * 
 * @author lihonghao
 * @date 2018年11月11日
 */
public class MonitorManager {
	public static Logger logger = Logger.getLogger(MonitorManager.class);
	// 一共采集入库了多少条新闻数据
	public static int totalNewsEntityNumber = 0;

	public static synchronized void addNewsEntityNumber4History(int newsAddNumber) {
		totalNewsEntityNumber += newsAddNumber;
	}

	// 直接数据值恢复-历史值
	public static synchronized void setTotalNewsEntityNumber(int totalNewsEntityNumber) {
		MonitorManager.totalNewsEntityNumber += totalNewsEntityNumber;
	}

	// 直接数据值恢复-当天值
	public static synchronized void setTotalCurrentDayEntityNumber(int totalCurrentDayEntityNumber) {
		MonitorManager.totalCurrentDayEntityNumber += totalCurrentDayEntityNumber;
	}

	// 当天一共采集了多少条数据
	public static int totalCurrentDayEntityNumber = 0;
	public static String currenyDay = DateUtil.getCurrentDay();

	public static synchronized void addNewsEntityNumber4CurrentDay(int newsAddNumber) {
		if (currenyDay.equals(DateUtil.getCurrentDay())) {
			totalCurrentDayEntityNumber += newsAddNumber;
		} else {
			totalCurrentDayEntityNumber = newsAddNumber;
			currenyDay = DateUtil.getCurrentDay();
		}
		addNewsEntityNumber4History(newsAddNumber);
	}

	// 带采集url和已采集
	public static int getTotalDoneUrlNumber() {
		return TaskScheduleManager.getDoneTaskSize();
	}

	public static int getTotalTodoUrlNumber() {
		return TaskScheduleManager.getTodoTaskSize();
	}

	public static class MonitorThread extends Thread {
		@SuppressWarnings("unused")
		private String name;

		public MonitorThread(String name) {
			super(name);
			this.name = name;
		}

		@Override
		public void run() {
			while (true) {
				try {
					logger.info("监控线程即将休眠" + SystemConfigParas.monitor_sleep_time / 1000 + "秒");
					Thread.sleep(SystemConfigParas.monitor_sleep_time);
					logger.info("监控线程休眠结束----");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("\n");
				stringBuilder.append("一共采集了" + totalNewsEntityNumber + "条数据");
				stringBuilder.append("\n");
				stringBuilder.append("今天一共采集了" + totalCurrentDayEntityNumber + "条数据");
				stringBuilder.append("\n");
				stringBuilder.append("已采集完成URL任务" + getTotalDoneUrlNumber() + "个");
				stringBuilder.append("\n");
				stringBuilder.append("带采集URL任务" + getTotalTodoUrlNumber() + "个");
				logger.info(stringBuilder.toString());
			}
		}
	}

	public static void start() {
		String name = "系统监控线程---";
		new MonitorThread(name).start();
	}

	public static void main(String[] args) {
		start();
	}
}
