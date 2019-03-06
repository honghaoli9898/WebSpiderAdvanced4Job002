package com.tl.job002.monitor;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.DateUtil;
import com.tl.job002.utils.RedisOperUtil;
import com.tl.job002.utils.SystemConfigParas;

import redis.clients.jedis.Jedis;

/**
 * 线程监控类
 * 
 * @author lihonghao
 * @date 2018年11月11日
 */
public class MonitorManager {
	public static Logger logger = Logger.getLogger(MonitorManager.class);
	// 一共采集入库了多少条商品数据
	public static long totalJDGoodsEntityNumber = 0;
	// 初始化redis工具类
	// 定义hashmap对象
	public static String currentDayStatisticKey = "current_day_statistic_key";
	public static RedisOperUtil redisOperUtil = RedisOperUtil.getInstance();

	public static synchronized void addJDGoodsNumber4History(int newsAddNumber) {
		totalJDGoodsEntityNumber += newsAddNumber;
	}

	// 直接数据值恢复-历史值
	public static synchronized void setTotalJDGoodsNumber(long totalNewsEntityNumber) {
		MonitorManager.totalJDGoodsEntityNumber += totalNewsEntityNumber;
	}

	// 直接数据值恢复-当天值
	public static synchronized void setTotalCurrentDayEntityNumber(int totalCurrentDayEntityNumber) {
		MonitorManager.totalCurrentDayEntityNumber += totalCurrentDayEntityNumber;
	}

	// 当天一共采集了多少条数据
	public static int totalCurrentDayEntityNumber = 0;
	public static String currenyDay = DateUtil.getCurrentDay();

	public static synchronized void addJDGoodsNumber4CurrentDay(int newsAddNumber) {
		Jedis jedis = redisOperUtil.getJedis();
		String currentDay = DateUtil.getCurrentDay();
		if (jedis.hexists(currentDayStatisticKey, currentDay)) {
			int oldCurrentDayFreq = Integer.parseInt(jedis.hget(currentDayStatisticKey, currentDay));
			totalCurrentDayEntityNumber = oldCurrentDayFreq + 1;
			jedis.hset(currentDayStatisticKey, currentDay, totalCurrentDayEntityNumber + "");
		} else {
			jedis.hset(currentDayStatisticKey, currentDay, "1");
		}
		addJDGoodsNumber4History(newsAddNumber);
	}

	// 带采集url和已采集
	public static int getTotalDoneUrlNumber() {
		return TaskScheduleManager.getDoneTaskSize();
	}

	public static long getTotalTodoUrlNumber() throws UnsupportedEncodingException {
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
				stringBuilder.append("一共存储了" + totalJDGoodsEntityNumber + "个商品信息");
				stringBuilder.append("\n");
				stringBuilder.append("今天一共存储了" + totalCurrentDayEntityNumber + "个商品信息");
				stringBuilder.append("\n");
				stringBuilder.append("已采集完成URL任务" + getTotalDoneUrlNumber() + "个");
				stringBuilder.append("\n");
				try {
					stringBuilder.append("带采集URL任务" + getTotalTodoUrlNumber() + "个");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
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
