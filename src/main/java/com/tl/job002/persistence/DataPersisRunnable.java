package com.tl.job002.persistence;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.tl.job002.iface.persistence.DataPersistrnceInterface;
import com.tl.job002.monitor.MonitorManager;
import com.tl.job002.pojos.UrlTaskPojo;
import com.tl.job002.pojos.UrlTaskPojo.TaskTypeEnum;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
import com.tl.job002.schedule.TaskScheduleManager;
import com.tl.job002.utils.SystemConfigParas;

/**
 * 存储线程
 * 
 * @author lihonghao
 * @date 2018年11月14日
 */
public class DataPersisRunnable implements Runnable {
	// 初始化实现类
	public static DataPersistrnceInterface persistrnceInterface;
	private boolean enableRunning = true;
	private String name;
	public static Logger logger = Logger.getLogger(DataPersisRunnable.class);
	public static int repetitionNumber = 0;
	public static Object obj = new Object();

	public DataPersisRunnable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnableRunning() {
		return enableRunning;
	}

	public void setEnableRunning(boolean enableRunning) {
		this.enableRunning = enableRunning;
	}

	@Override
	public void run() {
		persistrnceInterface = new DataPersist4EsImpl();
		// 初始化redis工具类
		while (enableRunning) {
			try {
				JDGoodsCommentsEntriy jdGoodsComment = TaskScheduleManager
						.getOneJDCommentEntriy();
				JDGoodsEntriy jdGoods = TaskScheduleManager
						.getOneCompleteJDGoodsEntriy();
				if (jdGoods != null) {
					persist(jdGoods);
				} else {
					logger.info(this.name + "没有带存储的完整商品");
				}
				if (jdGoodsComment != null) {
					try {
						boolean isSaveOK = persist(jdGoodsComment);
						synchronized (obj) {
							if (!isSaveOK) {
								if (repetitionNumber > SystemConfigParas.max_repeat_number_in_one_page) {
									TaskScheduleManager.cleanTodoTaskList();
									logger.info("已发现重复采集的数据,将清空本轮的带采集URL任务列表");
									repetitionNumber = 0;
								}
								repetitionNumber++;
							} else {
								repetitionNumber = 0;
							}
						}
						TaskScheduleManager.addDoneUrlTaskPojo(new UrlTaskPojo(
								jdGoodsComment.getCommentSourceSku(),
								SystemConfigParas.comment_url.replace("()",
										jdGoodsComment.getCommentSourceSku()),
								TaskTypeEnum.CRAWL_TASK));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					logger.info(this.name + "没有带存储的任务,线程将睡眠"
							+ SystemConfigParas.once_sleep_time_for_empty_task
							/ 1000 + "秒");
					try {
						Thread.sleep(SystemConfigParas.once_sleep_time_for_empty_task);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					logger.info(this.name + "持久化线程,本次睡眠结束");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static synchronized boolean persist(
			JDGoodsCommentsEntriy jdGoodsComment) throws IOException {
		if (!TaskScheduleManager.isInSaveJDCommentSet(jdGoodsComment)) {
			persistrnceInterface.persist(jdGoodsComment);
			// 对数据监控管理器进行打点上报数据
			TaskScheduleManager.addUniqGoodsCommentSet(jdGoodsComment);
			return true;
		}
		return false;
	}

	public static synchronized boolean persist(JDGoodsEntriy jdGoods)
			throws IOException {
		if (!TaskScheduleManager.isInSaveJDGoodsSet(jdGoods.getGoodsSKU())) {
			persistrnceInterface.persist(jdGoods);
			// 对数据监控管理器进行打点上报数据
			// 因为历史统计,也是基于当天的,故打点上报当天后,上报历史
			MonitorManager.addJDGoodsNumber4CurrentDay(1);
			TaskScheduleManager.addCompleteGoods(jdGoods);
			return true;
		}
		return false;
	}
}
