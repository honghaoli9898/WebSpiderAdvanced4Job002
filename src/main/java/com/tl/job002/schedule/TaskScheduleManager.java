package com.tl.job002.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.persistence.DataPersistManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.utils.DateUtil;

/**
 * 负责任务的调度,决定什么任务先被采集,什么任务后被采集
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class TaskScheduleManager {
	public static Logger logger = Logger.getLogger(TaskScheduleManager.class);
	public static LinkedList<UrlTaskPojo> todoTaskPojoList = new LinkedList<UrlTaskPojo>();
	public static LinkedList<UrlTaskPojo> doneTaskPojoList = new LinkedList<UrlTaskPojo>();
	// 新闻实体数据的已采集URL的集合,用于判重和增量采集
	public static Set<String> savedNewsEntityUrlSet = new HashSet<String>();
	// 在static方法块中,将所有需要从数据库中恢复的数据进行查询及恢复
	static {
		recovery();
	}

	public static void recovery() {
		synchronized (savedNewsEntityUrlSet) {
			String sql = "select * from news_item_info_middle";
			ResultSet rs = DataPersistManager.getResultSet(sql);
			try {
				while (rs.next()) {
					String title = rs.getString("title");
					String source_url = rs.getString("source_url");
					savedNewsEntityUrlSet.add(title + source_url);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// 恢复历史的数据
			MonitorManager.setTotalNewsEntityNumber(savedNewsEntityUrlSet.size());
			// 恢复当天的数据
			sql = "select COUNT(1) from news_item_info_middle where DATE_FORMAT(insert_time,'%Y-%m-%d')='"
					+ DateUtil.getCurrentDay() + "'";
			ResultSet rsCount = DataPersistManager.getResultSet(sql);
			try {
				if (rsCount.next()) {
					int countValue = rsCount.getInt(1);
					MonitorManager.setTotalCurrentDayEntityNumber(countValue);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rsCount.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void addSavedNewsEntityUrlSet(String saveUrl) {
		synchronized (savedNewsEntityUrlSet) {
			savedNewsEntityUrlSet.add(saveUrl);
		}
	}

	// 判断是否在集合里
	public static boolean isInSaveNewsEntityUrlSet(String toSaveUrl) {
		synchronized (savedNewsEntityUrlSet) {
			return savedNewsEntityUrlSet.contains(toSaveUrl);
		}
	}

	public static void addUrlPojoList(List<UrlTaskPojo> todoAddTaskList) {
		todoTaskPojoList.addAll(todoAddTaskList);
	}

	public static void removeUrlTaskPojoList(List<UrlTaskPojo> todoRemoveTaskList) {
		todoTaskPojoList.removeAll(todoRemoveTaskList);
	}

	public static void addOneUrlPojo(UrlTaskPojo todoAddTask) {
		todoTaskPojoList.add(todoAddTask);
	}

	public static void addDoneUrlPojo(UrlTaskPojo doneAddTask) {
		doneTaskPojoList.add(doneAddTask);
	}

	public static void removeOneUrlTaskPojo(UrlTaskPojo todoRemoveTask) {
		todoTaskPojoList.remove(todoRemoveTask);
	}

	// 已采集的url大小
	public static int getDoneTaskSize() {
		return doneTaskPojoList.size();
	}

	// 带采集的
	public static int getTodoTaskSize() {
		return todoTaskPojoList.size();
	}

	public static UrlTaskPojo take() {
		synchronized (todoTaskPojoList) {
			UrlTaskPojo taskPojo = todoTaskPojoList.pollFirst();
			return taskPojo;
		}
	}

	public static synchronized void cleanTodoTaskList() {
		todoTaskPojoList.clear();
	}
}
