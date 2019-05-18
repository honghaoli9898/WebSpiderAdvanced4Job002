package com.tl.job002.pojos.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JDGoodsEntriy implements Serializable {
	private static final long serialVersionUID = 1L;
	// 商品id
	private String goodsSKU;
	// 商品标题
	private String goodsTitle;
	// 商品风格颜色
	private String goodsStyle;
	// 商品名称
	private String goodsName;
	// 商品评论数
	private String goodsCommentCount;
	// 商品价格
	private String goodsPrice;
	// 商品商店信息
	private String goodsStoreInfo;
	// 商品标签
	private String goodsPag;
	// 晒图数量
	private String imageListCount;
	// 全部评论数
	private String commentCountStr;
	// 好评度
	private String goodRateShow;
	// 好评数
	private String goodCountStr;
	// 中评数
	private String generalCountStr;
	// 差评数
	private String poorCountStr;
	// 视频晒单
	private String videoCountStr;
	// 追评数
	private String afterCountStr;
	// 商品评论标签json字符串
	private String commentTypeArrayStr;
	// 商品下载时间
	private Date insertDate;

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public JDGoodsEntriy() {

	}

	public void setAttribute(JDGoodsEntriy jdGoodsEntriy,
			Map<String, String> map, String... args) {
		List<String> allAttributeList = new ArrayList<String>();
		allAttributeList.add("afterCountStr");
		allAttributeList.add("commentCountStr");
		allAttributeList.add("goodCountStr");
		allAttributeList.add("goodRateShow");
		allAttributeList.add("generalCountStr");
		allAttributeList.add("poorCountStr");
		allAttributeList.add("videoCountStr");
		for (String attribute : args) {
			if (allAttributeList.contains(attribute)) {
				int attrIndex = allAttributeList.indexOf(attribute);
				switch (attrIndex) {
				case 0:
					jdGoodsEntriy.setAfterCountStr(map.get(attribute));
					break;
				case 1:
					jdGoodsEntriy.setCommentCountStr(map.get(attribute));
					break;
				case 2:
					jdGoodsEntriy.setGoodCountStr(map.get(attribute));
					break;
				case 3:
					jdGoodsEntriy.setGoodRateShow(map.get(attribute));
					break;
				case 4:
					jdGoodsEntriy.setGeneralCountStr(map.get(attribute));
					break;
				case 5:
					jdGoodsEntriy.setPoorCountStr(map.get(attribute));
					break;
				case 6:
					jdGoodsEntriy.setVideoCountStr(map.get(attribute));
					break;
				default:
					break;
				}
			}
		}
	}

	public String getGoodsSKU() {
		return goodsSKU;
	}

	public String getImageListCount() {
		return imageListCount;
	}

	public void setImageListCount(String imageListCount) {
		this.imageListCount = imageListCount;
	}

	public String getCommentCountStr() {
		return commentCountStr;
	}

	public void setCommentCountStr(String commentCountStr) {
		this.commentCountStr = commentCountStr;
	}

	public String getGoodRateShow() {
		return goodRateShow;
	}

	public void setGoodRateShow(String goodRateShow) {
		this.goodRateShow = goodRateShow;
	}

	public String getGoodCountStr() {
		return goodCountStr;
	}

	public void setGoodCountStr(String goodCountStr) {
		this.goodCountStr = goodCountStr;
	}

	public String getGeneralCountStr() {
		return generalCountStr;
	}

	public void setGeneralCountStr(String generalCountStr) {
		this.generalCountStr = generalCountStr;
	}

	public String getPoorCountStr() {
		return poorCountStr;
	}

	public void setPoorCountStr(String poorCountStr) {
		this.poorCountStr = poorCountStr;
	}

	public String getVideoCountStr() {
		return videoCountStr;
	}

	public void setVideoCountStr(String videoCountStr) {
		this.videoCountStr = videoCountStr;
	}

	public String getAfterCountStr() {
		return afterCountStr;
	}

	public void setAfterCountStr(String afterCountStr) {
		this.afterCountStr = afterCountStr;
	}

	public String getCommentTypeArrayStr() {
		return commentTypeArrayStr;
	}

	public void setCommentTypeArrayStr(String commentTypeArrayStr) {
		this.commentTypeArrayStr = commentTypeArrayStr;
	}

	public void setGoodsSKU(String goodsSKU) {
		this.goodsSKU = goodsSKU;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getGoodsStyle() {
		return goodsStyle;
	}

	public void setGoodsStyle(String goodsStyle) {
		this.goodsStyle = goodsStyle;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsCommentCount() {
		return goodsCommentCount;
	}

	public void setGoodsCommentCount(String goodsCommentCount) {
		this.goodsCommentCount = goodsCommentCount;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getGoodsStoreInfo() {
		return goodsStoreInfo;
	}

	public void setGoodsStoreInfo(String goodsStoreInfo) {
		this.goodsStoreInfo = goodsStoreInfo;
	}

	public String getGoodsPag() {
		return goodsPag;
	}

	public void setGoodsPag(String goodsPag) {
		this.goodsPag = goodsPag;
	}

	@Override
	public String toString() {
		return "JDGoodsEntriy [goodsSKU=" + goodsSKU + ", goodsTitle="
				+ goodsTitle + ", goodsStyle=" + goodsStyle + ", goodsName="
				+ goodsName + ", goodsCommentCount=" + goodsCommentCount
				+ ", goodsPrice=" + goodsPrice + ", goodsStoreInfo="
				+ goodsStoreInfo + ", goodsPag=" + goodsPag
				+ ", imageListCount=" + imageListCount + ", commentCountStr="
				+ commentCountStr + ", goodRateShow=" + goodRateShow
				+ ", goodCountStr=" + goodCountStr + ", generalCountStr="
				+ generalCountStr + ", poorCountStr=" + poorCountStr
				+ ", videoCountStr=" + videoCountStr + ", afterCountStr="
				+ afterCountStr + ", commentTypeArrayStr="
				+ commentTypeArrayStr + ", insertDate=" + insertDate + "]";
	}

}
