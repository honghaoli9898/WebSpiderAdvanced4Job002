package com.tl.job002.persistence;

import java.util.ArrayList;
import java.util.List;

import com.tl.job002.utils.SystemConfigParas;

/**
 * 持久化管理器
 * 
 * @author lihonghao
 * @date 2018年11月10日
 */
public class DataPersistManager {
	public static ThreadGroup tGroup = new ThreadGroup("persist_thread_group");
	public static List<DataPersisRunnable> runnableList = new ArrayList<DataPersisRunnable>();

	/**
	 * 开启到少个下载线程
	 */
	public static void start() {
		List<Runnable> runnableList = new ArrayList<Runnable>();
		for (int i = 0; i < SystemConfigParas.init_persist_consumer_number; i++) {
			DataPersisRunnable oneRunnable = new DataPersisRunnable("persist_consumer_" + i);
			new Thread(tGroup, oneRunnable, "thread_" + i).start();
			runnableList.add(oneRunnable);
		}
	}

	// 获取线程的状态信息-多少个还活着的下载线程
	public static int getActiveDownloadThreads() {
		return tGroup.activeCount();
	}

	// 一共初始化多少线程
	public static int getInitThreadNumber() {
		return SystemConfigParas.init_persist_consumer_number;
	}

	// 停止掉所有线程
	public static void stopAllDownloadThreads() {
		for (Runnable oneRun : runnableList) {
			DataPersisRunnable tempObj = (DataPersisRunnable) oneRun;
			tempObj.setEnableRunning(false);
		}
	}

	// 停掉某个runnable对象
	public static void stopOneDownloadThread(DataPersisRunnable runnable) {
		runnable.setEnableRunning(false);
	}
}
