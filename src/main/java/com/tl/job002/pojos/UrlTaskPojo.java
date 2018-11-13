package com.tl.job002.pojos;

/**
 * 对url任务的封装类
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class UrlTaskPojo {
	private String title;
	private String url;
	private String postTime;

	public String getPostTime() {
		return postTime;
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	private TaskTypeEnum taskType = TaskTypeEnum.ROOT_URL;

	public static enum TaskTypeEnum {
		ROOT_URL, CRAWL_TASK
	}

	public UrlTaskPojo() {

	}

	public UrlTaskPojo(String title, String url) {
		super();
		this.title = title;
		this.url = url;
	}

	public UrlTaskPojo(String title, String url, String postTime, TaskTypeEnum taskType) {
		this(title, url, postTime);
		this.taskType = taskType;
	}

	public UrlTaskPojo(String title, String url, String postTime) {
		this(title, url);
		this.postTime = postTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "UrlTaskPojo [title=" + title + ", url=" + url + ", taskType=" + taskType + "]";
	}

}
