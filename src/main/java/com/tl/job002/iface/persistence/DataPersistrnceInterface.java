package com.tl.job002.iface.persistence;

import java.sql.ResultSet;
import java.util.List;

import com.tl.job002.pojos.entity.NewsItemEntity;

/**
 * 数据持久化接口类,定义持久化接口方法
 * 
 * @author lihonghao
 * @date 2018年11月10日
 */
public interface DataPersistrnceInterface {
	// 批量保存
	public boolean persist(List<NewsItemEntity> itemList);

	// 单条保存
	public boolean persist(NewsItemEntity item);
	
	//获取数据库内容
	public ResultSet getResultSet(String sql);
}
