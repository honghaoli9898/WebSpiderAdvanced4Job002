package com.tl.job002.persistence;

import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tl.job002.iface.persistence.DataPersistrnceInterface;
import com.tl.job002.pojos.entity.JDGoodsCommentsEntriy;
import com.tl.job002.pojos.entity.JDGoodsEntriy;
import com.tl.job002.utils.DateUtil;
import com.tl.job002.utils.TransportClientUtil;

public class DataPersist4EsImpl implements DataPersistrnceInterface {
	private static TransportClientUtil transportClientUtil;
	private static String insertName = "index_from_tc";
	private static String typeName = "type_from_tc";

	public DataPersist4EsImpl() {
		try {
			transportClientUtil = new TransportClientUtil();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean persist(List<JDGoodsCommentsEntriy> jdGoodsCommentsList) {
		return true;
	}

	@Override
	public boolean persist(JDGoodsCommentsEntriy jdGoodsComment) {
		try {
			Map<String, String> kvMap = new HashMap<String, String>();
			kvMap.put("username", jdGoodsComment.getNickName());
			kvMap.put("user_level_name", jdGoodsComment.getUserLevelName());
			kvMap.put("user_level_id", jdGoodsComment.getUserLevelId());
			kvMap.put("user_client_show", jdGoodsComment.getUserClientShow());
			kvMap.put("mobile_version", jdGoodsComment.getMobileVersion());
			kvMap.put("reference_id", jdGoodsComment.getReferenceId());
			kvMap.put("score", jdGoodsComment.getScore());
			kvMap.put("content", jdGoodsComment.getContent());
			kvMap.put("product_color", jdGoodsComment.getProductColor());
			kvMap.put("product_size", jdGoodsComment.getProductSize());
			kvMap.put("reference_time", jdGoodsComment.getReferenceTime());
			kvMap.put("creation_time", jdGoodsComment.getCreationTime());
			kvMap.put("days", jdGoodsComment.getDays());
			kvMap.put("after_days", jdGoodsComment.getAfterDays());
			kvMap.put("insert_data",
					DateUtil.formatDateToString(jdGoodsComment.getInsertDate()));
			transportClientUtil.addOneDocument(insertName, typeName, kvMap);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public ResultSet getResultSet(String sql) {
		return null;
	}

	@Override
	public boolean persist(JDGoodsEntriy jdGoods) {
		try {
			Map<String, String> kvMap = new HashMap<String, String>();
			kvMap.put("goods_sku", jdGoods.getGoodsSKU());
			kvMap.put("goods_title", jdGoods.getGoodsTitle());
			kvMap.put("goods_style", jdGoods.getGoodsStyle());
			kvMap.put("goods_name", jdGoods.getGoodsName());
			kvMap.put("goods_comment_count", jdGoods.getGoodsCommentCount());
			kvMap.put("goods_price", jdGoods.getGoodsPrice());
			kvMap.put("goods_store_info", jdGoods.getGoodsStoreInfo());
			kvMap.put("goods_pag", jdGoods.getGoodsPag());
			kvMap.put("image_list_count", jdGoods.getImageListCount());
			kvMap.put("comment_count", jdGoods.getCommentCountStr());
			kvMap.put("good_rate", jdGoods.getGoodRateShow());
			kvMap.put("good_count", jdGoods.getGoodCountStr());
			kvMap.put("general_count", jdGoods.getGeneralCountStr());
			kvMap.put("poor_count", jdGoods.getPoorCountStr());
			kvMap.put("video_count", jdGoods.getVideoCountStr());
			kvMap.put("after_count", jdGoods.getAfterCountStr());
			kvMap.put("comment_type_array", jdGoods.getCommentTypeArrayStr());
			kvMap.put("insert_data",
					DateUtil.formatDateToString(jdGoods.getInsertDate()));
			transportClientUtil.addOneDocument(insertName, typeName, kvMap);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
