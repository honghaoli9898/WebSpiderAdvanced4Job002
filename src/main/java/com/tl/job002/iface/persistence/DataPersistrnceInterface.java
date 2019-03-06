package com.tl.job002.iface.persistence;

import java.sql.ResultSet;
import java.util.List;

import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;

/**
 * 数据持久化接口类,定义持久化接口方法
 * 
 * @author lihonghao
 * @date 2018年11月10日
 */
public interface DataPersistrnceInterface {
	// 批量保存
	public boolean persist(List<JDGoodsCommentsEntriy> jdGoodsCommentList);

	// 单条保存
	public boolean persist(JDGoodsCommentsEntriy jdGoodsComment);
	
	public boolean persist(JDGoodsEntriy jdGoods);
	
	//获取数据库内容
	public ResultSet getResultSet(String sql);
}
