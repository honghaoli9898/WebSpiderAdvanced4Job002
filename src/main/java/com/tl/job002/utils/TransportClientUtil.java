package com.tl.job002.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TransportClientUtil {
	// 声明一个client变量
	public TransportClient client = null;

	public TransportClientUtil() throws UnknownHostException {
		init();
	}

	// 初始化TransportClient
	public void init() throws UnknownHostException {
		/**
		 * 配置相关参数， 包括集群名称:标识链接哪个es集群 是否开启嗅探其它集群群服务节点的能力,使新增或移除的集群节点自动感知到
		 * 其嗅探功能使用效果并不理想，还是以直接显式添加服务节点
		 */
		Settings settings = Settings.builder().put("cluster.name", "tianliangedu_es_cluster")
				.put("client.transport.sniff", true).build();
		// 将参数应用到某个client对象中
		client = new PreBuiltTransportClient(settings);
		// 显式添加响应es集群操作的节点机器，注意是9300管理端口
		client.addTransportAddress(new TransportAddress(InetAddress.getByName("DESKTOP-TR9A93O"), 9300));
	};

	// 创建索引,传入索引名称
	public void createIndex(String indexName) {
		client.admin().indices().prepareCreate(indexName).execute().actionGet();
	}

	// 删除索引,传入索引名称
	public void deleteIndex(String indexName) {
		client.admin().indices().prepareDelete(indexName).execute().actionGet();
	}

	// 创建数据类型
	public void createType(String indexName, String typeName) throws IOException {
		XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties")
				.startObject("title").field("type", "text").field("analyzer", "index_ansj")
				.field("search_analyzer", "query_ansj").endObject().startObject("source_url").field("type", "keyword")
				.endObject().startObject("post_time").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss")
				.endObject().startObject("insert_time").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss")
				.endObject().endObject().endObject();
		PutMappingRequest putMappingRequest = Requests.putMappingRequest(indexName).type(typeName)
				.source(contentBuilder);
		this.client.admin().indices().putMapping(putMappingRequest).actionGet();
	}

	/**
	 * 向指定的index和type中添加单个文档
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void addOneDocument(String indexName, String typeName, Map map) {
		// 将数据发送到服务器端
		this.client.prepareIndex(indexName, typeName).setSource(map).execute().actionGet();
	}

	/**
	 * 批量添加文档至目标索引的类型当中
	 *
	 * @param indexName
	 * @param typeName
	 * @return
	 */
	public boolean addBatchDocument(String indexName, String typeName) {
		// 通过map定义kv结构数据对象
		Map<String, String> kvMap = new HashMap<String, String>();
		kvMap.put("title", "自定义新闻标题-bulk");
		kvMap.put("source_url", "自定义新闻url");
		kvMap.put("post_time", "2018-08-04 12:12:12");
		kvMap.put("insert_time", "2018-08-04 12:13:12");
		// 初始化批量执行对象
		BulkRequestBuilder brb = this.client.prepareBulk();
		// 初始化单个索引数据的builder对象
		IndexRequestBuilder irb = this.client.prepareIndex(indexName, typeName).setSource(kvMap);
		// 将单个对象加入批量执行对三次，相当于同时索引3条数据
		brb.add(irb);
		brb.add(irb);
		brb.add(irb);
		// 正式发起批量索引的请求
		BulkResponse bulkResponse = brb.execute().actionGet();
		// 返回是否有索引失败的消息
		return bulkResponse.hasFailures();
	}

	/**
	 * 根据docID查询对应的文档信息
	 *
	 * @param indexName
	 * @param typeName
	 * @param docID
	 */
	public void selectOneDocumentByID(String indexName, String typeName, String docID) {
		// 将数据发送到服务器端
		SearchResponse response = this.client.prepareSearch(indexName).setTypes(typeName)
				.setQuery(QueryBuilders.termQuery("_id", docID)).execute().actionGet();
		System.out.println(response.toString());
	}

	/**
	 * 根据query信息，搜索出命中条件的结果文档集合
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void searchDocumentByQuery(String indexName, String typeName) {
		// 将数据发送到服务器端
		SearchResponse response = this.client.prepareSearch(indexName).setTypes(typeName)
				.setQuery(QueryBuilders.termQuery("title", "标题")).execute().actionGet();
		SearchHits hits = response.getHits();
		for (SearchHit searchHit : hits) {
			Map source = searchHit.getSourceAsMap();
			System.out.println(source);
		}
	}

	/**
	 * 删除指定id值的文档
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void removeOneDocument(String indexName, String typeName, String docID) {
		// 将数据发送到服务器端
		this.client.prepareDelete(indexName, typeName, docID).execute().actionGet();
	}

	/**
	 * 更新指定id值的文档,只更新有显式指定的字段，未被指定修改的则不改变
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void updateOneDocument(String indexName, String typeName, String docID) {
		// 通过map定义kv结构数据对象
		Map<String, String> kvMap = new HashMap<String, String>();
		kvMap.put("title", "我是被update的title");
		kvMap.put("source_url", "自定义新闻url");
		kvMap.put("post_time", "2018-08-04 12:12:12");
		kvMap.put("insert_time", "2018-08-04 12:13:12");
		// 将要更新数据发送到服务器端
		this.client.prepareUpdate(indexName, typeName, docID).setDoc(kvMap).execute().actionGet();
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		// 初始化client工具类
		TransportClientUtil transportClient = new TransportClientUtil();
		// 创建索引
		// String indexName = "index_for_tc";
		// transportClient.createIndex(indexName);
		System.out.println(transportClient.client);
		// String typeName = "type_from_tc";
		// transportClient.createType(indexName, typeName);
		// transportClient.addBatchDocument(indexName, typeName);
		// String docID = "28C_1GcByGetHIn1Vdd0";
		// transportClient.selectOneDocumentByID(indexName, typeName, docID);
		// transportClient.searchDocumentByQuery(indexName, typeName);
		System.out.println("done!");
	}
}