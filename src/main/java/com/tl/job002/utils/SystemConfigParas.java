package com.tl.job002.utils;

import java.io.IOException;

/**
 * 系统配置参数工具类,集中读取
 * 
 * @author lihonghao
 * @date 2018年11月8日
 */
public class SystemConfigParas {
	public static ReadConfigUtil configUtil = null;
	public static ReadConfigUtil configUtil_db = null;
	static {
		try {
			configUtil = new ReadConfigUtil("spider.properties");
			configUtil_db = new ReadConfigUtil("jdbc.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 初始化的下载消费线程数量
	public static int init_consumer_number = Integer.parseInt(configUtil.getValue("init_consumer_number"));
	// 每次遇到空任务时的睡眠时间单位为秒
	public static int once_sleep_time_for_empty_task = Integer
			.parseInt(configUtil.getValue("once_sleep_time_for_empty_task")) * 1000;
	// 监控线程睡眠时间
	public static int monitor_sleep_time = Integer.parseInt(configUtil.getValue("monitor_sleep_time")) * 1000;
	//每页最大重复次数
	public static int max_repeat_number_in_one_page = Integer.parseInt(configUtil.getValue("max_repeat_number_in_one_page"));
	//一轮添加种子的时间间隔
	public static int add_seed_time_one_circle = Integer.parseInt(configUtil.getValue("add_seed_time_one_circle"))*1000;
	// 数据库
	public static String db_driver = configUtil_db.getValue("db_driver");
	public static String db_url = configUtil_db.getValue("db_url");
	public static String db_username = configUtil_db.getValue("db_username");
	public static String db_password = configUtil_db.getValue("db_password");
}
