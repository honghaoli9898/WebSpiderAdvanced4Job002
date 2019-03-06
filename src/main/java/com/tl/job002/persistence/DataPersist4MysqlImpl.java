package com.tl.job002.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.tl.job002.iface.persistence.DataPersistrnceInterface;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
import com.tl.job002.utils.DataBaseUtil;
import com.tl.job002.utils.DateUtil;
import com.tl.job002.utils.SystemConfigParas;

public class DataPersist4MysqlImpl implements DataPersistrnceInterface {
	private DataBaseUtil databaseUtil;
	String sql = "insert into news_item_info_v2(title,source_url,post_time,source_name,body,insert_time) values(?,?,?,?,?,?)";

	public DataPersist4MysqlImpl() {
		try {
			databaseUtil = new DataBaseUtil(SystemConfigParas.db_driver,
					SystemConfigParas.db_url, SystemConfigParas.db_username,
					SystemConfigParas.db_password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean persist(List<JDGoodsCommentsEntriy> jdGoodsCommentsList) {
		if (jdGoodsCommentsList != null && jdGoodsCommentsList.size() > 0) {
			try {
				PreparedStatement ps = databaseUtil.getPreparedStatement(sql);
				for (JDGoodsCommentsEntriy jdGoodsComment : jdGoodsCommentsList) {
					ps.setString(1, jdGoodsComment.getNickName());
					ps.setString(2, jdGoodsComment.getUserLevelName());
					ps.setString(3, jdGoodsComment.getUserLevelId());
					ps.setString(4, jdGoodsComment.getUserClientShow());
					ps.setString(5, jdGoodsComment.getMobileVersion());
					ps.setString(6, jdGoodsComment.getReferenceId());
					ps.setString(7, jdGoodsComment.getScore());
					ps.setString(8, jdGoodsComment.getContent());
					ps.setString(9, jdGoodsComment.getProductColor());
					ps.setString(10, jdGoodsComment.getProductSize());
					ps.setString(11, jdGoodsComment.getReferenceTime());
					ps.setString(12, jdGoodsComment.getCreationTime());
					ps.setString(13, jdGoodsComment.getDays());
					ps.setString(14, jdGoodsComment.getAfterDays());
					ps.setTimestamp(15, new java.sql.Timestamp(jdGoodsComment
							.getInsertDate().getTime()));
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
	public boolean persist(JDGoodsCommentsEntriy jdGoodsComment) {
		try {
			PreparedStatement ps = databaseUtil.getPreparedStatement(sql);
			ps.setString(1, jdGoodsComment.getNickName());
			ps.setString(2, jdGoodsComment.getUserLevelName());
			ps.setString(3, jdGoodsComment.getUserLevelId());
			ps.setString(4, jdGoodsComment.getUserClientShow());
			ps.setString(5, jdGoodsComment.getMobileVersion());
			ps.setString(6, jdGoodsComment.getReferenceId());
			ps.setString(7, jdGoodsComment.getScore());
			ps.setString(8, jdGoodsComment.getContent());
			ps.setString(9, jdGoodsComment.getProductColor());
			ps.setString(10, jdGoodsComment.getProductSize());
			ps.setString(11, jdGoodsComment.getReferenceTime());
			ps.setString(12, jdGoodsComment.getCreationTime());
			ps.setString(13, jdGoodsComment.getDays());
			ps.setString(14, jdGoodsComment.getAfterDays());
			ps.setTimestamp(15, new java.sql.Timestamp(jdGoodsComment
					.getInsertDate().getTime()));
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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

	@Override
	public boolean persist(JDGoodsEntriy jdGoods) {
		try {
			PreparedStatement ps = databaseUtil.getPreparedStatement(sql);
			ps.setString(1, jdGoods.getGoodsSKU());
			ps.setString(1, jdGoods.getGoodsTitle());
			ps.setString(1, jdGoods.getGoodsStyle());
			ps.setString(1, jdGoods.getGoodsName());
			ps.setString(1, jdGoods.getGoodsCommentCount());
			ps.setString(1, jdGoods.getGoodsPrice());
			ps.setString(1, jdGoods.getGoodsStoreInfo());
			ps.setString(1, jdGoods.getGoodsPag());
			ps.setString(1, jdGoods.getImageListCount());
			ps.setString(1, jdGoods.getCommentCountStr());
			ps.setString(1, jdGoods.getGoodRateShow());
			ps.setString(1, jdGoods.getGoodCountStr());
			ps.setString(1, jdGoods.getGeneralCountStr());
			ps.setString(1, jdGoods.getPoorCountStr());
			ps.setString(1, jdGoods.getVideoCountStr());
			ps.setString(1, jdGoods.getAfterCountStr());
			ps.setString(1, jdGoods.getCommentTypeArrayStr());
			ps.setString(1,
					DateUtil.formatDateToString(jdGoods.getInsertDate()));
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
