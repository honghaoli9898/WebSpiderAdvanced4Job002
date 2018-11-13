package com.tl.job002.persistence;

import java.sql.ResultSet;

import com.tl.job002.iface.persistence.DataPersistrnceInterface;
import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.SystemConfigParas;

/**
 * 持久化管理器
 * 
 * @author lihonghao
 * @date 2018年11月10日
 */
public class DataPersistManager {
	// 初始化实现类
	public static DataPersistrnceInterface persistrnceInterface = new DataPersist4MysqlImpl();
	// 定义某个页有多少条重复时,则认为真正的重复页
	public static int max_repeat_number_in_one_page = SystemConfigParas.max_repeat_number_in_one_page;

	// 该返回值boolean,若为true则代表发现重复采集出来的结果,若为false则没有重复
	public static synchronized boolean persist(NewsItemEntity itemEntity) {
		if (!TaskScheduleManager.isInSaveNewsEntityUrlSet(itemEntity.toUniqString())) {
			persistrnceInterface.persist(itemEntity);
			// 对数据监控管理器进行打点上报数据
			// 因为历史统计,也是基于当天的,故打点上报当天后,上报历史
			MonitorManager.addNewsEntityNumber4CurrentDay(1);
			TaskScheduleManager.addSavedNewsEntityUrlSet(itemEntity.toUniqString());
			return true;
		}
		return false;
	}

	public static ResultSet getResultSet(String sql) {
		return persistrnceInterface.getResultSet(sql);
	}
}
