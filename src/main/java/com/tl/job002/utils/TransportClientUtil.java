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
	// ����һ��client����
	public TransportClient client = null;

	public TransportClientUtil() throws UnknownHostException {
		init();
	}

	// ��ʼ��TransportClient
	public void init() throws UnknownHostException {
		/**
		 * ������ز����� ������Ⱥ����:��ʶ�����ĸ�es��Ⱥ �Ƿ�����̽������ȺȺ����ڵ������,ʹ�������Ƴ��ļ�Ⱥ�ڵ��Զ���֪��
		 * ����̽����ʹ��Ч���������룬������ֱ����ʽ��ӷ���ڵ�
		 */
		Settings settings = Settings.builder().put("cluster.name", "tianliangedu_es_cluster")
				.put("client.transport.sniff", true).build();
		// ������Ӧ�õ�ĳ��client������
		client = new PreBuiltTransportClient(settings);
		// ��ʽ�����Ӧes��Ⱥ�����Ľڵ������ע����9300����˿�
		client.addTransportAddress(new TransportAddress(InetAddress.getByName("DESKTOP-TR9A93O"), 9300));
	};

	// ��������,������������
	public void createIndex(String indexName) {
		client.admin().indices().prepareCreate(indexName).execute().actionGet();
	}

	// ɾ������,������������
	public void deleteIndex(String indexName) {
		client.admin().indices().prepareDelete(indexName).execute().actionGet();
	}

	// ������������
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
	 * ��ָ����index��type����ӵ����ĵ�
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void addOneDocument(String indexName, String typeName, Map map) {
		// �����ݷ��͵���������
		this.client.prepareIndex(indexName, typeName).setSource(map).execute().actionGet();
	}

	/**
	 * ��������ĵ���Ŀ�����������͵���
	 *
	 * @param indexName
	 * @param typeName
	 * @return
	 */
	public boolean addBatchDocument(String indexName, String typeName) {
		// ͨ��map����kv�ṹ���ݶ���
		Map<String, String> kvMap = new HashMap<String, String>();
		kvMap.put("title", "�Զ������ű���-bulk");
		kvMap.put("source_url", "�Զ�������url");
		kvMap.put("post_time", "2018-08-04 12:12:12");
		kvMap.put("insert_time", "2018-08-04 12:13:12");
		// ��ʼ������ִ�ж���
		BulkRequestBuilder brb = this.client.prepareBulk();
		// ��ʼ�������������ݵ�builder����
		IndexRequestBuilder irb = this.client.prepareIndex(indexName, typeName).setSource(kvMap);
		// �����������������ִ�ж����Σ��൱��ͬʱ����3������
		brb.add(irb);
		brb.add(irb);
		brb.add(irb);
		// ��ʽ������������������
		BulkResponse bulkResponse = brb.execute().actionGet();
		// �����Ƿ�������ʧ�ܵ���Ϣ
		return bulkResponse.hasFailures();
	}

	/**
	 * ����docID��ѯ��Ӧ���ĵ���Ϣ
	 *
	 * @param indexName
	 * @param typeName
	 * @param docID
	 */
	public void selectOneDocumentByID(String indexName, String typeName, String docID) {
		// �����ݷ��͵���������
		SearchResponse response = this.client.prepareSearch(indexName).setTypes(typeName)
				.setQuery(QueryBuilders.termQuery("_id", docID)).execute().actionGet();
		System.out.println(response.toString());
	}

	/**
	 * ����query��Ϣ�����������������Ľ���ĵ�����
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void searchDocumentByQuery(String indexName, String typeName) {
		// �����ݷ��͵���������
		SearchResponse response = this.client.prepareSearch(indexName).setTypes(typeName)
				.setQuery(QueryBuilders.termQuery("title", "����")).execute().actionGet();
		SearchHits hits = response.getHits();
		for (SearchHit searchHit : hits) {
			Map source = searchHit.getSourceAsMap();
			System.out.println(source);
		}
	}

	/**
	 * ɾ��ָ��idֵ���ĵ�
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void removeOneDocument(String indexName, String typeName, String docID) {
		// �����ݷ��͵���������
		this.client.prepareDelete(indexName, typeName, docID).execute().actionGet();
	}

	/**
	 * ����ָ��idֵ���ĵ�,ֻ��������ʽָ�����ֶΣ�δ��ָ���޸ĵ��򲻸ı�
	 *
	 * @param indexName
	 * @param typeName
	 */
	public void updateOneDocument(String indexName, String typeName, String docID) {
		// ͨ��map����kv�ṹ���ݶ���
		Map<String, String> kvMap = new HashMap<String, String>();
		kvMap.put("title", "���Ǳ�update��title");
		kvMap.put("source_url", "�Զ�������url");
		kvMap.put("post_time", "2018-08-04 12:12:12");
		kvMap.put("insert_time", "2018-08-04 12:13:12");
		// ��Ҫ�������ݷ��͵���������
		this.client.prepareUpdate(indexName, typeName, docID).setDoc(kvMap).execute().actionGet();
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		// ��ʼ��client������
		TransportClientUtil transportClient = new TransportClientUtil();
		// ��������
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