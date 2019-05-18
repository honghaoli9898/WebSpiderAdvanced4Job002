package com.tl.job002.utils;

import java.io.IOException;
import java.util.List;

import com.tl.job002.utils.ReadConfigUtil;

;

/**
 * 系统配置参数工具类,集中读取
 * 
 * @author lihonghao
 * @date 2018年11月8日
 */
public class SystemConfigParas {
	public static ReadConfigUtil configUtil = null;
	public static ReadConfigUtil configUtil_db = null;
	public static ReadConfigUtil configUtil_info = null;
	static {
		try {
			configUtil = new ReadConfigUtil("spider.properties");
			configUtil_db = new ReadConfigUtil("jdbc.properties");
			configUtil_info = new ReadConfigUtil("userinfo.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 睡眠等待时间
	public static int sleep_wait_time = Integer.parseInt(configUtil_info
			.getValue("sleep_wait_time"));
	// jd搜索title
	public static String jd_search_title = configUtil_info
			.getValue("jd_search_title");
	// jd登录页url
	public static String jd_login_url = configUtil_info
			.getValue("jd_login_url");
	// 用户名
	public static List<String> login_user_name = RegexUtil
			.getConfigList(configUtil_info.getValue("login_user_name"));
	// 密码
	public static List<String> login_password = RegexUtil
			.getConfigList(configUtil_info.getValue("login_password"));
	// chromedriver路径
	public static String chrome_driver_path = configUtil_info
			.getValue("chrome_driver_path");
	// options参数集合
	public static List<String> options_arguments = RegexUtil
			.getConfigList(configUtil.getValue("options_arguments"));
	// 是否设置代理
	public static String is_setting_proxy = configUtil
			.getValue("is_setting_proxy");
	// 爬取商品主页多少页
	public static int download_page_num = Integer.parseInt(configUtil
			.getValue("download_page_num"));
	public static int download_comment_page_num = Integer.parseInt(configUtil
			.getValue("download_comment_page_num"));
	// 评论url
	public static String comment_url = configUtil.getValue("comment_url");
	// 代理ip池
	public static List<String> proxy_ip_pool = RegexUtil
			.getConfigList(configUtil.getValue("proxy_ip_pool"));
	/**
	 * 读取爬虫节点角色
	 */
	// persist相关的参数
	public static int init_persist_consumer_number = Integer
			.parseInt(configUtil.getValue("init_persist_consumer_number"));
	public static boolean is_master = configUtil.getBooleanValue("is_master");
	// 初始化的下载消费线程数量
	public static int init_download_consumer_number = Integer
			.parseInt(configUtil.getValue("init_download_consumer_number"));
	// 每次遇到空任务时的睡眠时间单位为秒
	public static int once_sleep_time_for_empty_task = Integer
			.parseInt(configUtil.getValue("once_sleep_time_for_empty_task")) * 1000;
	// 监控线程睡眠时间
	public static int monitor_sleep_time = Integer.parseInt(configUtil
			.getValue("monitor_sleep_time")) * 1000;
	// 每页最大重复次数
	public static int max_repeat_number_in_one_page = Integer
			.parseInt(configUtil.getValue("max_repeat_number_in_one_page"));
	// 一轮添加种子的时间间隔
	public static int add_seed_time_one_circle = Integer.parseInt(configUtil
			.getValue("add_seed_time_one_circle")) * 1000;
	// redis数据库
	public static String redis_ip = configUtil.getValue("redis_ip");
	public static int redis_passpost = Integer.parseInt(configUtil
			.getValue("redis_passpost"));
	public static String redis_auth = configUtil.getValue("redis_auth");

	// 数据库
	public static String db_driver = configUtil_db.getValue("db_driver");
	public static String db_url = configUtil_db.getValue("db_url");
	public static String db_username = configUtil_db.getValue("db_username");
	public static String db_password = configUtil_db.getValue("db_password");
	public static String es_cluster_name = configUtil_db
			.getValue("es_cluster_name");
	public static boolean es_client_transport_sniff = Boolean
			.getBoolean(configUtil_db.getValue("es_client_transport_sniff"));
	public static String es_local_name = configUtil_db
			.getValue("es_local_name");
}
