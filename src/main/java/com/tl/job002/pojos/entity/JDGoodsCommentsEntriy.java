package com.tl.job002.pojos.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JDGoodsCommentsEntriy implements Serializable{
	private static final long serialVersionUID = 1L;
	// 商品来源sku
	private String commentSourceSku;
	// 账号id
	private String nickName;
	// 用户级别
	private String userLevelName;
	// 账户等级
	private String userLevelId;
	// 用户访问来源
	private String userClientShow;
	// 用户手机版本
	private String mobileVersion;
	// 评论商品id
	private String referenceId;
	// 评论星数
	private String score;
	// 评论内容
	private String content;
	// 商品颜色
	private String productColor;
	// 商品配置
	private String productSize;
	// 参考购买时间
	private String referenceTime;
	// 评论时间
	private String creationTime;
	// 购买到评论时间
	private String days;
	// 到货后几天评论
	private String afterDays;
	// 下载时间
	private Date insertDate;

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public JDGoodsCommentsEntriy() {
		allAttributeList.add("nickname");
		allAttributeList.add("userLevelName");
		allAttributeList.add("userLevelId");
		allAttributeList.add("userClientShow");
		allAttributeList.add("mobileVersion");
		allAttributeList.add("referenceId");
		allAttributeList.add("score");
		allAttributeList.add("content");
		allAttributeList.add("productColor");
		allAttributeList.add("productSize");
		allAttributeList.add("referenceTime");
		allAttributeList.add("creationTime");
		allAttributeList.add("days");
		allAttributeList.add("afterDays");
	}

	public static List<String> allAttributeList = new ArrayList<String>();

	public void setAttribute(JDGoodsCommentsEntriy jdGoodsCommentsEntriy,
			Map<String, String> map, String... args) {
		for (String attribute : args) {
			if (allAttributeList.contains(attribute)) {
				int attrIndex = allAttributeList.indexOf(attribute);
				switch (attrIndex) {
				case 0:
					jdGoodsCommentsEntriy.setNickName(map.get(attribute));
					break;
				case 1:
					jdGoodsCommentsEntriy.setUserLevelName(map.get(attribute));
					break;
				case 2:
					jdGoodsCommentsEntriy.setUserLevelId(map.get(attribute));
					break;
				case 3:
					jdGoodsCommentsEntriy.setUserClientShow(map.get(attribute));
					break;
				case 4:
					jdGoodsCommentsEntriy.setMobileVersion(map.get(attribute));
					break;
				case 5:
					jdGoodsCommentsEntriy.setReferenceId(map.get(attribute));
					break;
				case 6:
					jdGoodsCommentsEntriy.setScore(attribute);
				case 7:
					jdGoodsCommentsEntriy.setContent(map.get(attribute));
					break;
				case 8:
					jdGoodsCommentsEntriy.setProductColor(map.get(attribute));
					break;
				case 9:
					jdGoodsCommentsEntriy.setProductSize(map.get(attribute));
					break;
				case 10:
					jdGoodsCommentsEntriy.setReferenceTime(map.get(attribute));
					break;
				case 11:
					jdGoodsCommentsEntriy.setCreationTime(map.get(attribute));
					break;
				case 12:
					jdGoodsCommentsEntriy.setDays(map.get(attribute));
					break;
				case 13:
					jdGoodsCommentsEntriy.setAfterDays(map.get(attribute));
				default:
					break;
				}
			}
		}
	}

	public String getCommentSourceSku() {
		return commentSourceSku;
	}

	public void setCommentSourceSku(String commentSourceSku) {
		this.commentSourceSku = commentSourceSku;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserLevelName() {
		return userLevelName;
	}

	public void setUserLevelName(String userLevelName) {
		this.userLevelName = userLevelName;
	}

	public String getUserLevelId() {
		return userLevelId;
	}

	public void setUserLevelId(String userLevelId) {
		this.userLevelId = userLevelId;
	}

	public String getUserClientShow() {
		return userClientShow;
	}

	public void setUserClientShow(String userClientShow) {
		this.userClientShow = userClientShow;
	}

	public String getMobileVersion() {
		return mobileVersion;
	}

	public void setMobileVersion(String mobileVersion) {
		this.mobileVersion = mobileVersion;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProductColor() {
		return productColor;
	}

	public void setProductColor(String productColor) {
		this.productColor = productColor;
	}

	public String getProductSize() {
		return productSize;
	}

	public void setProductSize(String productSize) {
		this.productSize = productSize;
	}

	public String getReferenceTime() {
		return referenceTime;
	}

	public void setReferenceTime(String referenceTime) {
		this.referenceTime = referenceTime;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getAfterDays() {
		return afterDays;
	}

	public void setAfterDays(String afterDays) {
		this.afterDays = afterDays;
	}

	@Override
	public String toString() {
		return "JDGoodsCommentsEntriy [commentSourceSku=" + commentSourceSku
				+ ", nickName=" + nickName + ", userLevelName=" + userLevelName
				+ ", userLevelId=" + userLevelId + ", userClientShow="
				+ userClientShow + ", mobileVersion=" + mobileVersion
				+ ", referenceId=" + referenceId + ", score=" + score
				+ ", content=" + content + ", productColor=" + productColor
				+ ", productSize=" + productSize + ", referenceTime="
				+ referenceTime + ", creationTime=" + creationTime + ", days="
				+ days + ", afterDays=" + afterDays + ", insertDate="
				+ insertDate + "]";
	}

}
