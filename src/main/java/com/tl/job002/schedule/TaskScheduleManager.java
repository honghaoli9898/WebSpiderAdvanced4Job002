package com.tl.job002.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.persistence.DataPersistManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.utils.DateUtil;
import com.tl.job002.utils.RedisOperUtil;

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
	// public static Set<String> savedNewsEntityUrlSet = new HashSet<String>();
	// redis中与savedNewsEntityUrlSet对应的set结构的key声明
	public static String uniqUrlSetKey = "uniq_url_key";
	// redis中与todoTaskPojoList对应的list结构的key声明
	public static String todoTaskPojoListKey = "todo_task_pojo_list_key";
	// redis工具类初始化
	public static RedisOperUtil redisOperUtil = RedisOperUtil.getInstance();
	// 在static方法块中,将所有需要从数据库中恢复的数据进行查询及恢复
	static {
		recovery();
	}

	public static void recovery() {
		// 恢复历史的数据
		synchronized (uniqUrlSetKey) {
			MonitorManager.setTotalNewsEntityNumber(getSavedNewsEntityUrlSetSize());
			// 恢复当天的数据
			String currentDayFreqString = redisOperUtil.getJedis().hget(MonitorManager.currentDayStatisticKey,
					DateUtil.getCurrentDay());
			int currentDayFreq = (currentDayFreqString == null ? 0 : Integer.parseInt(currentDayFreqString));
			MonitorManager.setTotalCurrentDayEntityNumber(currentDayFreq);
		}
	}

	public static void addSavedNewsEntityUrlSet(String saveUrl) {
		synchronized (uniqUrlSetKey) {
			// savedNewsEntityUrlSet.add(saveUrl);
			redisOperUtil.getJedis().sadd(uniqUrlSetKey, saveUrl);
		}
	}

	// 在redis取得SavedNewsEntityUrlSet的长度
	public static long getSavedNewsEntityUrlSetSize() {
		synchronized (uniqUrlSetKey) {
			// savedNewsEntityUrlSet.add(saveUrl);
			return redisOperUtil.getJedis().scard(uniqUrlSetKey);
		}
	}

	// 判断是否在集合里
	public static boolean isInSaveNewsEntityUrlSet(String toSaveUrl) {
		synchronized (uniqUrlSetKey) {
			return redisOperUtil.getJedis().sismember(uniqUrlSetKey, toSaveUrl);
		}
	}

	public static void addUrlPojoList(List<UrlTaskPojo> todoAddTaskList) {
		//分布式后,将该直接进程的对象,转换为redis list操作
//		todoTaskPojoList.addAll(todoAddTaskList);
//		redisOperUtil.getJedis().lpu
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
