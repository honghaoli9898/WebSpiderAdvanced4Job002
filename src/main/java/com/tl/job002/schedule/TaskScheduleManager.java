package com.tl.job002.schedule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
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
	// 存放已用账号集合
	public static List<String> userNameList = new ArrayList<String>();
	// 存放已用代理
	public static List<String> proxyIpPoolList = new ArrayList<String>();
	public static LinkedList<UrlTaskPojo> doneTaskPojoList = new LinkedList<UrlTaskPojo>();
	// redis中完整的商品详情 completeGoods
	public static String completeGoodsKey = "complete_goods_key";
	// redis中与savedNewsEntityUrlSet对应的set结构的key声明
	public static String uniqUrlSetKey = "uniq_url_key";
	// redis中已填充完的主页商品信息的set结构的key声明
	public static String uniqGoodsSetKey = "uniq_goods_key";
	// redis中以解析的商品评论set结构key声明
	public static String uniqGoodsCommentSetKey = "uniq_goods_comment_key";
	// redis中与todoTaskPojoSet对应的set结构的key声明
	public static String todoTaskPojoSetKey = "to_do_task_pojo_set_key";
	// redis中与todoJDGoodsEntriyMap对应的map结构声明
	public static String todoJDGoodsEntriyMapKey = "to_do_jd_goods_map_key";
	// redis中与todoJDCommentEntriyList对应的key声明
	public static String todoJDCommentListKey = "to_do_jd_comment_list_key";
	// redis工具类初始化
	public static RedisOperUtil redisOperUtil = RedisOperUtil.getInstance();
	// 在static方法块中,将所有需要从数据库中恢复的数据进行查询及恢复
	static {
		recovery();
	}

	public static void recovery() {
		// 恢复历史的数据
		synchronized (redisOperUtil) {
			MonitorManager.setTotalJDGoodsNumber(getSavedJDGoodsUrlSetSize());
			// 恢复当天的数据
			String currentDayFreqString = redisOperUtil.getJedis().hget(
					MonitorManager.currentDayStatisticKey,
					DateUtil.getCurrentDay());
			int currentDayFreq = (currentDayFreqString == null ? 0 : Integer
					.parseInt(currentDayFreqString));
			MonitorManager.setTotalCurrentDayEntityNumber(currentDayFreq);
		}
	}

	// 在redis取得SavedNewsEntityUrlSet的长度
	public static long getSavedJDGoodsUrlSetSize() {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().scard(uniqUrlSetKey);
		}
	}

	// 判断redis中是否存在解析的商品评论
	public static boolean isInSaveJDCommentSet(
			JDGoodsCommentsEntriy jdGoodsCommentsEntriy) throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(jdGoodsCommentsEntriy);
			return redisOperUtil.getJedis().sismember(
					uniqGoodsCommentSetKey.getBytes("utf-8"), byteArray);
		}
	}

	// 判断主页商品是否再redis中
	public static boolean isInSaveJDGoodsSet(String skuID) {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().sismember(uniqGoodsSetKey, skuID);
		}
	}

	// 判断是否在redis redisOperUtil集合里
	public static boolean isInSaveJDCommentsUrlSet(String toSaveUrl) {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().sismember(uniqUrlSetKey, toSaveUrl);
		}
	}

	// 判断子url是否重复
	public static boolean isInsaveSeedUrlSet(UrlTaskPojo urlTaskPojo)
			throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(urlTaskPojo);
			return redisOperUtil.getJedis().sismember(
					todoTaskPojoSetKey.getBytes("utf-8"), byteArray);
		}
	}

	// 添加到 redis 的todoTaskPojoListKey中 set结构
	public static void addOneUrlPojo(UrlTaskPojo taskPojo) throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(taskPojo);
			redisOperUtil.getJedis().sadd(todoTaskPojoSetKey.getBytes("utf-8"),
					byteArray);
		}
	}

	// 带采集的集合长度
	public static long getTodoTaskSize() throws UnsupportedEncodingException {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().scard(
					todoTaskPojoSetKey.getBytes("utf-8"));
		}
	}

	// 从redis todoTaskPojoSetKey集合中获得一个待二次采集的对象
	public static UrlTaskPojo take() throws ClassNotFoundException, IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = redisOperUtil.getJedis().spop(
					todoTaskPojoSetKey.getBytes("utf-8"));
			UrlTaskPojo urlTaskPojo = null;
			if (byteArray != null) {
				urlTaskPojo = (UrlTaskPojo) ObjectAndByteArrayConvertor
						.convertByteArrayToObj(byteArray);
			}
			return urlTaskPojo;
		}
	}

	// 清空redis中todoTaskPojoListKey待二次采集的集合
	public static void cleanTodoTaskList() throws UnsupportedEncodingException {
		synchronized (redisOperUtil) {
			redisOperUtil.getJedis().srem(todoTaskPojoSetKey.getBytes("utf-8"));
		}
	}

	// 删除redis的带二次下载的一个对象
	public static void removeOneUrlTaskPojo(UrlTaskPojo todoRemoveTask)
			throws UnsupportedEncodingException, IOException {
		synchronized (redisOperUtil) {
			redisOperUtil.getJedis().lrem(
					todoTaskPojoSetKey.getBytes("utf-8"),
					0,
					ObjectAndByteArrayConvertor
							.convertObjectToByteArray(todoRemoveTask));
		}
	}

	// 向redis中添加解析的评论信息
	public static void addOneJDCommentEntriy(JDGoodsCommentsEntriy jdComment)
			throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(jdComment);
			redisOperUtil.getJedis().lpush(
					todoJDCommentListKey.getBytes("utf-8"), byteArray);
		}
	}

	public static void addJDCommentEntriyList(
			List<JDGoodsCommentsEntriy> jdGoodsCommentsEntriyList)
			throws IOException {
		for (JDGoodsCommentsEntriy jdGoodsCommentsEntriy : jdGoodsCommentsEntriyList) {
			if (!isInSaveJDCommentSet(jdGoodsCommentsEntriy)) {
				addOneJDCommentEntriy(jdGoodsCommentsEntriy);
			}
		}
	}

	// 向redis中添加已经解析过的商品评论
	public static void addUniqGoodsCommentSet(JDGoodsCommentsEntriy jdComment)
			throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(jdComment);
			redisOperUtil.getJedis().sadd(
					uniqGoodsCommentSetKey.getBytes("utf-8"), byteArray);
		}
	}

	// 将主页解析的商品内容添加到redis todoJDGoodsEntriyMap
	public static void addJDGoodsEntriyMap(Entry<String, JDGoodsEntriy> entry)
			throws IOException {
		if (!isInSaveJDGoodsSet(entry.getKey())) {
			addOneGoodsEntriy(entry.getKey(), entry.getValue());
		}
	}

	// 将一个集合添加到redis todoTaskPojoListKey集合中
	public static void addUrlPojoList(List<UrlTaskPojo> todoAddTaskList)
			throws IOException {
		// 分布式后,将该直接进程的对象,转换为redis list操作
		for (UrlTaskPojo taskPojo : todoAddTaskList) {
			addOneUrlPojo(taskPojo);
		}
		logger.info("当前的todoTaskPojoList.size()=" + getTodoTaskSize());
	}

	// 再redis中添加完整商品
	public static void addCompleteGoods(JDGoodsEntriy jdGoodsEntriy)
			throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(jdGoodsEntriy);
			redisOperUtil.getJedis().lpush(completeGoodsKey.getBytes("utf-8"),
					byteArray);
		}
	}

	// 添加一个主页商品信息
	public static void addOneGoodsEntriy(String skuID, JDGoodsEntriy jdGoods)
			throws IOException {
		synchronized (redisOperUtil) {
			byte[] byteArray = ObjectAndByteArrayConvertor
					.convertObjectToByteArray(jdGoods);
			redisOperUtil.getJedis().hset(
					todoJDGoodsEntriyMapKey.getBytes("utf-8"),
					skuID.getBytes("utf-8"), byteArray);
		}
	}

	// 删除待补全的商品
	public static void deleteOneDiscomplementGoodsEntriy(String skuID)
			throws UnsupportedEncodingException {
		synchronized (redisOperUtil) {
			redisOperUtil.getJedis().hdel(
					todoJDGoodsEntriyMapKey.getBytes("utf-8"),
					skuID.getBytes("utf-8"));
		}
	}

	// 带完善的goods集合长度
	public static long getToDoJDGoodsSize() throws UnsupportedEncodingException {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().hlen(
					todoJDGoodsEntriyMapKey.getBytes("utf-8"));
		}
	}

	// 从redis todoJDGoodsEntriyMapKey集合中获得一个待填充的JDGoods
	public static JDGoodsEntriy getJDGoodsEntriy(String skuID)
			throws IOException, ClassNotFoundException {
		synchronized (redisOperUtil) {
			byte[] byteArray = redisOperUtil
					.getJedis()
					.hmget(todoJDGoodsEntriyMapKey.getBytes("utf-8"),
							skuID.getBytes("utf-8")).get(0);
			JDGoodsEntriy jdGoods = null;
			if (byteArray != null) {
				jdGoods = (JDGoodsEntriy) ObjectAndByteArrayConvertor
						.convertByteArrayToObj(byteArray);
			}
			return jdGoods;
		}
	}

	// 添加到redis的 uniqJDGoodsSet
	public static void addSavedJDGoodsSet(String skuID) {
		synchronized (redisOperUtil) {
			redisOperUtil.getJedis().sadd(uniqGoodsSetKey, skuID);
		}
	}

	// 添加到redis 的uniqUrlSetKey中 set结构
	public static void addSavedJDCommentsUrlSet(String saveUrl) {
		synchronized (redisOperUtil) {
			// savedNewsEntityUrlSet.add(saveUrl);
			redisOperUtil.getJedis().sadd(uniqUrlSetKey, saveUrl);
		}
	}

	// 添加到已经下载完的集合中
	public static synchronized void addDoneUrlTaskPojo(UrlTaskPojo doneAddTask) {
		doneTaskPojoList.add(doneAddTask);
	}

	//
	// 已采集的url大小
	public static synchronized int getDoneTaskSize() {
		return doneTaskPojoList.size();
	}

	// 得到以解析的商品评论集合长度
	public static long getUniqGoodsCommentSize()
			throws UnsupportedEncodingException {
		synchronized (redisOperUtil) {
			return redisOperUtil.getJedis().scard(
					uniqGoodsCommentSetKey.getBytes("utf-8"));
		}
	}

	// 从redis中取完整的商品
	public static JDGoodsEntriy getOneCompleteJDGoodsEntriy()
			throws IOException, ClassNotFoundException {
		JDGoodsEntriy jdGoods = null;
		synchronized (redisOperUtil) {
			byte[] byteArray = redisOperUtil.getJedis().lpop(
					completeGoodsKey.getBytes("utf-8"));
			jdGoods = (JDGoodsEntriy) ObjectAndByteArrayConvertor
					.convertByteArrayToObj(byteArray);
			return jdGoods;
		}
	}

	// 从redis中获得一个评论信息
	public static JDGoodsCommentsEntriy getOneJDCommentEntriy()
			throws IOException, ClassNotFoundException {
		JDGoodsCommentsEntriy jdGoodsComment = null;
		synchronized (redisOperUtil) {
			byte[] byteArray = redisOperUtil.getJedis().lpop(
					todoJDCommentListKey.getBytes("utf-8"));
			jdGoodsComment = (JDGoodsCommentsEntriy) ObjectAndByteArrayConvertor
					.convertByteArrayToObj(byteArray);
			return jdGoodsComment;
		}
	}

	// 删除redis的带二次下载的一个对象集合
	public static void removeUrlTaskPojoList(
			List<UrlTaskPojo> todoRemoveTaskList) throws IOException {
		for (UrlTaskPojo urlTaskPojo : todoRemoveTaskList) {
			removeOneUrlTaskPojo(urlTaskPojo);
		}
	}

	// 清空userNameList
	public static synchronized void cleanUserNameList() {
		userNameList.clear();
	}

	// 添加一个账号到userNameList
	public static synchronized void addUserNameList(String userName) {
		userNameList.add(userName);
	}

	// 得到userNameList最后一个值
	public static String getLastListForUserNameList() {
		return userNameList.get(userNameList.size() - 1);
	}

	// 得到userNameList大小
	public static int getUserNameListSize() {
		return userNameList.size();
	}

	// 判断userNameList是否为空
	public static boolean isNull4UserNameList() {
		return userNameList.isEmpty();
	}

	// 清空proxyIpPoolList
	public static synchronized void cleanProxyIpPoolList() {
		proxyIpPoolList.clear();
	}

	// 添加一个账号到proxyIpPoolList
	public static synchronized void addProxyIpPoolList(String userName) {
		proxyIpPoolList.add(userName);
	}

	// 得到proxyIpPoolList大小
	public static int getProxyIpPoolListSize() {
		return proxyIpPoolList.size();
	}

	// 判断proxyIpPoolList是否为空
	public static boolean isNull4ProxyIpPoolList() {
		return proxyIpPoolList.isEmpty();
	}

}
