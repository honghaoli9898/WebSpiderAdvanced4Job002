package com.tl.job002.pojos.entity;

/**
 * 解析后的对象类
 * @author lihonghao
 * @date 2018年11月9日
 */
import java.text.ParseException;
import java.util.Date;

import com.tl.job002.utils.DateUtil;

public class NewsItemEntity {
	private String title;
	private String sourceURL;
	private Date insertDate;
	private Date postDateObj;
	private String postTimeString;

	public NewsItemEntity() {

	}

	public NewsItemEntity(String title, String sourceURL, String postTimeString) throws ParseException {
		super();
		this.title = title;
		this.sourceURL = sourceURL;
		this.postTimeString = postTimeString;
		this.insertDate = DateUtil.getDate();
		this.postDateObj = DateUtil.parserStringToDate(postTimeString);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public Date getPostDateObj() {
		return postDateObj;
	}

	public void setPostDateObj(Date postDateObj) {
		this.postDateObj = postDateObj;
	}

	public String getPostTimeString() {
		return postTimeString;
	}

	public void setPostTimeString(String postTimeString) {
		this.postTimeString = postTimeString;
	}

	@Override
	public String toString() {
		return "NewsItemEntity [title=" + title + ", sourceURL=" + sourceURL + ", insertDate=" + insertDate
				+ ", postDateObj=" + postDateObj + ", postTimeString=" + postTimeString + "]";
	}

	public String toUniqString() {
		return title + sourceURL;
	}
}
