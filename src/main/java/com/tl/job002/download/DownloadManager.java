package com.tl.job002.download;

import java.util.ArrayList;
import java.util.List;

import com.tl.job002.utils.SystemConfigParas;

/**
 * 网页下载管理器
 * 
 * @author lihonghao
 * @date 2018年11月8日
 */
public class DownloadManager {
	public static ThreadGroup tGroup = new ThreadGroup("download_thread_group");
	public static List<Runnable> runnableList = new ArrayList<Runnable>();

	/**
	 * 开启到少个下载线程
	 */
	public static void start() {
		List<Runnable> runnableList = new ArrayList<Runnable>();
		for (int i = 0; i < SystemConfigParas.init_consumer_number; i++) {
			DownloadRunnable oneRunnable = new DownloadRunnable("download_consumer_" + i);
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
		return SystemConfigParas.init_consumer_number;
	}

	// 停止掉所有线程
	public static void stopAllDownloadThreads() {
		for (Runnable oneRun : runnableList) {
			DownloadRunnable tempObj = (DownloadRunnable) oneRun;
			tempObj.setEnableRunning(false);
		}
	}

	// 停掉某个runnable对象
	public static void stopOneDownloadThread(DownloadRunnable runnable) {
		runnable.setEnableRunning(false);
	}

	public static void main(String[] args) throws InterruptedException {
		start();
	}
}
