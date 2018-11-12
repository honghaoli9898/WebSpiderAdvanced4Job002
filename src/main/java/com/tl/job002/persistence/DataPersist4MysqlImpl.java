package com.tl.job002.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.tl.job002.iface.persistence.DataPersistrnceInterface;
import com.tl.job002.pojos.entity.NewsItemEntity;
import com.tl.job002.utils.DataBaseUtil;
import com.tl.job002.utils.SystemConfigParas;

public class DataPersist4MysqlImpl implements DataPersistrnceInterface {
	private DataBaseUtil databaseUtil;
	String sql = "insert into news_item_info_middle(title,source_url,post_time,insert_time) values(?,?,?,?)";

	public DataPersist4MysqlImpl() {
		try {
			databaseUtil = new DataBaseUtil(SystemConfigParas.db_driver, SystemConfigParas.db_url,
					SystemConfigParas.db_username, SystemConfigParas.db_password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean persist(List<NewsItemEntity> itemList) {
		if (itemList != null && itemList.size() > 0) {
			try {
				PreparedStatement ps = databaseUtil.getPreparedStatement(sql);
				for (NewsItemEntity itemEntity : itemList) {
					ps.setString(1, itemEntity.getTitle());
					ps.setString(2, itemEntity.getSourceURL());
					ps.setTimestamp(3, new java.sql.Timestamp(itemEntity.getPostDateObj().getTime()));
					ps.setTimestamp(4, new java.sql.Timestamp(itemEntity.getInsertDate().getTime()));
					ps.addBatch();
				}
				ps.executeBatch();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	@Override
	public boolean persist(NewsItemEntity itemEntity) {
		try {
			PreparedStatement ps = databaseUtil.getPreparedStatement(sql);
			ps.setString(1, itemEntity.getTitle());
			ps.setString(2, itemEntity.getSourceURL());
			ps.setTimestamp(3, new java.sql.Timestamp(itemEntity.getPostDateObj().getTime()));
			ps.setTimestamp(4, new java.sql.Timestamp(itemEntity.getInsertDate().getTime()));
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseException {
		DataPersistrnceInterface mysqlPersist = new DataPersist4MysqlImpl();
		NewsItemEntity itemEntity = new NewsItemEntity("≤‚ ‘title", "http://www.baidu.com", "2018-07-27 15:04:06");
		mysqlPersist.persist(itemEntity);
		System.out.println("done!");
	}

	@Override
	public ResultSet getResultSet(String sql) {
		try {
			return databaseUtil.getResultSetByStat(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
