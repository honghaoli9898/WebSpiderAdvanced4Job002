package com.tl.job002.schedule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.utils.DateUtil;
import com.tl.job002.utils.ObjectAndByteArrayConvertor;
import com.tl.job002.utils.RedisOperUtil;

/**
 * 负责任务的调度,决定什么任务先被采集,什么任务后被采集
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class TaskScheduleManager {
	public static Logger logger = Logger.getLogger(TaskScheduleManager.class);
	public static LinkedList<UrlTaskPojo> doneTaskPojoList = new LinkedList<UrlTaskPojo>();
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
		synchronized (redisOperUtil) {
			MonitorManager.setTotalNewsEntityNumber(getSavedNewsEntityUrlSetSize());
			// 恢复当天的数据
			String currentDayFreqString = redisOperUtil.getJedis().hget(MonitorManager.currentDayStatisticKey,
					DateUtil.getCurrentDay());
			int currentDayFreq = (currentDayFreqString == null ? 0 : Integer.parseInt(currentDayFreqString));
			MonitorManager.setTotalCurrentDayEntityNumber(currentDayFreq);
		}
	}

	// 在redis取得SavedNewsEntityUrlSet的长度
	public static long getSavedNewsEntityUrlSetSize() {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().scard(uniqUrlSetKey);
		}
	}

	// 判断是否在redis redisOperUtil集合里
	public static boolean isInSaveNewsEntityUrlSet(String toSaveUrl) {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().sismember(uniqUrlSetKey, toSaveUrl);
		}
	}

	// 将一个集合添加到redis todoTaskPojoListKey集合中
	public static void addUrlPojoList(List<UrlTaskPojo> todoAddTaskList) throws IOException {
		// 分布式后,将该直接进程的对象,转换为redis list操作
		for (UrlTaskPojo taskPojo : todoAddTaskList) {
			if (!isInSaveNewsEntityUrlSet(taskPojo.uniqString())) {
				addOneUrlPojo(taskPojo);
			}
		}
		logger.info("当前的todoTaskPojoList.size()=" + getTodoTaskSize());
	}

	// 添加到redis 的uniqUrlSetKey中 set结构
	public static void addSavedNewsEntityUrlSet(String saveUrl) {
		synchronized (redisOperUtil) {
			// savedNewsEntityUrlSet.add(saveUrl);
			redisOperUtil.getJedis().sadd(uniqUrlSetKey, saveUrl);
		}
	}

	// 添加到 redis 的todoTaskPojoListKey中 list结构
	public static void addOneUrlPojo(UrlTaskPojo taskPojo) throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor.convertObjectToByteArray(taskPojo);
			redisOperUtil.getJedis().lpush(todoTaskPojoListKey.getBytes("utf-8"), byteArray);
		}
	}

	// 添加到已经下载完的集合中
	public static synchronized void addDoneUrlTaskPojo(UrlTaskPojo doneAddTask) {
		doneTaskPojoList.add(doneAddTask);
	}

	// 已采集的url大小
	public static synchronized int getDoneTaskSize() {
		return doneTaskPojoList.size();
	}

	// 带采集的集合长度
	public static long getTodoTaskSize() {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().llen(todoTaskPojoListKey);
		}
	}

	// 从redis todoTaskPojoListKey集合中获得一个待二次采集的对象
	public static UrlTaskPojo take() throws ClassNotFoundException, IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = redisOperUtil.getJedis().rpop(todoTaskPojoListKey.getBytes("utf-8"));
			UrlTaskPojo urlTaskPojo = null;
			if (byteArray != null) {
				urlTaskPojo = (UrlTaskPojo) ObjectAndByteArrayConvertor.convertByteArrayToObj(byteArray);
			}
			return urlTaskPojo;
		}
	}

	// 清空redis中todoTaskPojoListKey待二次采集的集合
	public static void cleanTodoTaskList() throws UnsupportedEncodingException {
		synchronized (redisOperUtil) {
			redisOperUtil.getJedis().ltrim(todoTaskPojoListKey.getBytes("utf-8"), 1, 0);
		}
	}

	// 删除redis的带二次下载的一个对象
	public static void removeOneUrlTaskPojo(UrlTaskPojo todoRemoveTask)
			throws UnsupportedEncodingException, IOException {
		synchronized (redisOperUtil) {
			redisOperUtil.getJedis().lrem(todoTaskPojoListKey.getBytes("utf-8"), 0,
					ObjectAndByteArrayConvertor.convertObjectToByteArray(todoRemoveTask));
		}
	}

	// 删除redis的带二次下载的一个对象集合
	public static void removeUrlTaskPojoList(List<UrlTaskPojo> todoRemoveTaskList) throws IOException {
		for (UrlTaskPojo urlTaskPojo : todoRemoveTaskList) {
			removeOneUrlTaskPojo(urlTaskPojo);
		}
	}

}
