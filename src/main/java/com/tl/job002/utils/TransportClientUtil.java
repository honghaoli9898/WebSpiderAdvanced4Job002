package com.tl.job002.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TransportClientUtil {
	public static TransportClient client = null;

	public TransportClientUtil() throws UnknownHostException {
		init();
	}

	public static void init() throws UnknownHostException {
		Settings settings = Settings
				.builder()
				.put("cluster.name", SystemConfigParas.es_cluster_name)
				.put("client.transport.sniff",
						SystemConfigParas.es_client_transport_sniff).build();
		client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(new TransportAddress(InetAddress
				.getByName(SystemConfigParas.es_local_name), 9300));
	}

	// 创建索引,传入索引名称
	public void createIndex(String indexName) {
		client.admin().indices().prepareCreate(indexName).execute().actionGet();
	}

	// 删除索引,传入索引名称
	public void deleteIndex(String indexName) {
		client.admin().indices().prepareDelete(indexName).execute().actionGet();
	}

	// 创建类型
	public void createType4JDComments(String indexName, String typeName)
			throws IOException {
		XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
				.startObject().startObject("properties")
				.startObject("nickname").field("type", "text")
				.field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("userLevelName").field("type", "keyword")
				.endObject().startObject("userLevelId")
				.field("type", "keyword").endObject()
				.startObject("userClientShow").field("type", "keyword")
				.endObject().startObject("item_url").field("type", "keyword")
				.endObject().startObject("mobileVersion")
				.field("type", "keyword").endObject()
				.startObject("referenceId").field("type", "keyword")
				.endObject().startObject("score").field("type", "keyword")
				.endObject().startObject("content").field("type", "text")
				.field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("productColor").field("type", "keyword")
				.endObject().startObject("productSize").field("type", "text")
				.field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("referenceTime").field("type", "keyword")
				.endObject().startObject("creationTime")
				.field("type", "keyword").endObject().startObject("days")
				.field("type", "keyword").endObject().startObject("afterDays")
				.field("type", "keyword").endObject()
				.startObject("jd_comment_insert_time").field("type", "date")
				.field("format", "yyyy-MM-dd HH:mm:ss").endObject().endObject()
				.endObject();
		PutMappingRequest putMappingRequest = Requests
				.putMappingRequest(indexName).type(typeName)
				.source(contentBuilder);
		client.admin().indices().putMapping(putMappingRequest).actionGet();
	}

	// 创建类型
	public void createType4JDJoods(String indexName, String typeName)
			throws IOException {
		XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
				.startObject().startObject("properties")
				.startObject("goodsSKU").field("type", "keyword").endObject()
				.startObject("goodsTitle").field("type", "text")
				.field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("goodsName").field("type", "text")
				.field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("goodsCommentCount").field("type", "keyword")
				.endObject().startObject("goodsPrice").field("type", "keyword")
				.endObject().startObject("goodsStoreInfo")
				.field("type", "text").field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("goodsPag").field("type", "keyword").endObject()
				.startObject("imageListCount").field("type", "keyword")
				.endObject().startObject("commentCountStr")
				.field("type", "keyword").endObject()
				.startObject("goodRateShow").field("type", "keyword")
				.endObject().startObject("goodCountStr")
				.field("type", "keyword").endObject()
				.startObject("generalCountStr").field("type", "keyword")
				.endObject().startObject("poorCountStr")
				.field("type", "keyword").endObject()
				.startObject("videoCountStr").field("type", "keyword")
				.endObject().startObject("afterCountStr")
				.field("type", "keyword").endObject()
				.startObject("commentTypeArrayStr").field("type", "text")
				.field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject()
				.startObject("jd_goods_insert_time").field("type", "date")
				.field("format", "yyyy-MM-dd HH:mm:ss").endObject().endObject()
				.endObject();
		PutMappingRequest putMappingRequest = Requests
				.putMappingRequest(indexName).type(typeName)
				.source(contentBuilder);
		client.admin().indices().putMapping(putMappingRequest).actionGet();
	}

	// crud单条插入
	@SuppressWarnings("unchecked")
	public void addOneDocument(String indexName, String typeName,
			@SuppressWarnings("rawtypes") Map kvMap) {
		client.prepareIndex(indexName, typeName).setSource(kvMap).execute()
				.actionGet();
	}

	// 修改
	public void updateOneDocument(String indexName, String typeName,
			String docID) {
		Map<String, String> kvMap = new HashMap<String, String>();
		kvMap.put("title", "自定义新闻标题");
		kvMap.put("source_url", "自定义新闻url");
		kvMap.put("port_time", "2018-08-04 12:12:12");
		kvMap.put("insert_time", "2018-08-04 12:13:12");
		client.prepareUpdate(indexName, typeName, docID).setDoc(kvMap)
				.execute().actionGet();
	}

	// 批量插入
	public boolean addBatchDocument(String indexName, String typeName) {
		Map<String, String> kvMap = new HashMap<String, String>();
		kvMap.put("title", "自定义新闻标题");
		kvMap.put("source_url", "自定义新闻url");
		kvMap.put("port_time", "2018-08-04 12:12:12");
		kvMap.put("insert_time", "2018-08-04 12:13:12");
		BulkRequestBuilder brb = client.prepareBulk();
		IndexRequestBuilder irb = client.prepareIndex(indexName, typeName)
				.setSource(kvMap);
		brb.add(irb);
		brb.add(irb);
		BulkResponse bulkResponse = brb.execute().actionGet();
		return bulkResponse.hasFailures();
	}

	// 查询数据
	public void selectOneDocumentByID(String indexName, String typeName,
			String docID) {
		SearchResponse response = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setQuery(QueryBuilders.termQuery("_id", docID)).execute()
				.actionGet();
		System.out.println(response.toString());
	}

	// 通过条件搜索
	public void searchDocumentByQuery(String indexName, String typeName,
			String field, String condition) {
		SearchResponse response = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setQuery(QueryBuilders.termQuery(field, condition)).execute()
				.actionGet();
		SearchHits hits = response.getHits();
		for (SearchHit searchHit : hits) {
			Map<String, Object> source = searchHit.getSourceAsMap();
			System.out.println(source);
		}
	}

	// 通过条件搜索是否存在字段
	public void searchMissingByQuery(String indexName, String typeName,
			String field, String fields) {
		ExistsQueryBuilder existsQueryBuilder = QueryBuilders
				.existsQuery(fields);
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().mustNot(
				existsQueryBuilder);
		SearchResponse response = client.prepareSearch(indexName)
				.setTypes(typeName).setQuery(boolQueryBuilder).execute()
				.actionGet();
		SearchHits hits = response.getHits();
		for (SearchHit searchHit : hits) {
			Map<String, Object> source = searchHit.getSourceAsMap();
			System.out.println(source);
		}
	}

	// 删除
	public void deleteDocumentByID(String indexName, String typeName,
			String docID) {
		client.prepareDelete(indexName, typeName, docID).execute().actionGet();
	}

	// 聚合
	public void searchMaxPostTimeItem(String indexName, String typeName,
			String searchWord) {
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",
				searchWord);
		SearchResponse response = client
				.prepareSearch(indexName)
				.setTypes(typeName)
				.setQuery(termQueryBuilder)
				.addSort("post_time", SortOrder.DESC)
				.setFrom(0)
				.setSize(20)
				.addAggregation(
						AggregationBuilders.max("max_post_time").field(
								"post_time"))
				.addAggregation(
						AggregationBuilders.min("min_post_time").field(
								"post_time")).execute().actionGet();
		SearchHits hits = response.getHits();
		for (SearchHit searchHit : hits) {
			Map source = searchHit.getSourceAsMap();
			System.out.println(source);
		}
		Aggregations aggregations = response.getAggregations();
		System.out.println(aggregations.getAsMap());
	}

	// 对所爬新闻列表数据中，对应的发布时间分布
	public void searchPostTimeDist4History(String indexName, String typeName) {
		AggregationBuilder aggregation = AggregationBuilders
				.dateHistogram("agg").field("post_time").format("yyyy-MM-dd")
				.dateHistogramInterval(DateHistogramInterval.DAY);
		SearchResponse response = client.prepareSearch(indexName)
				.setTypes(typeName).setQuery(QueryBuilders.matchAllQuery())
				.addSort("post_time", SortOrder.DESC).setFrom(0).setSize(20)
				.addAggregation(aggregation).execute().actionGet();
		SearchHits hits = response.getHits();
		for (SearchHit searchHit : hits) {
			Map source = searchHit.getSourceAsMap();
			System.out.println(source);
		}
		Aggregations aggregations = response.getAggregations();
		for (Aggregation aggregation2 : aggregations) {
			System.out.println(aggregation2);
		}
	}

	private static String insertName_1 = "jd_joods_index";
	private static String typeName_1 = "jd_joods_type";
	private static String insertName_2 = "jd_comments_index";
	private static String typeName_2 = "jd_comments_type";

	public static void main(String[] args) throws IOException {
		TransportClientUtil t = new TransportClientUtil();
		t.createIndex(insertName_1);
		t.createIndex(insertName_1);
		// t.deleteIndex("index_from_tc");
		// t.createType4JDComments(insertName_1, typeName_1);
		// t.addOneDocument(indexName, typeName);
		// t.selectOneDocumentByID(indexName, typeName, "x6UpgGkBQPyEqDQuiJrx");
//		t.searchDocumentByQuery(insertName_2, typeName_2, "productColor", "极夜黑");
		System.out.println("done");
	}

}
