package com.tl.job002.pojos;
/**
 * 对url任务的封装类
 * @author lihonghao
 * @date 2018年11月6日
 */
public class UrlTaskPojo {
	private String title;
	private String url;
	public UrlTaskPojo(){
		
	}
	public UrlTaskPojo(String title, String url) {
		super();
		this.title = title;
		this.url = url;
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
		return "UrlTaskPojo [title=" + title + ", url=" + url + "]";
	}
	
}
