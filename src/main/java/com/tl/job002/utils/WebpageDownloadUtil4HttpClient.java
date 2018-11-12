package com.tl.job002.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.tl.job002.iface.download.DownloadInterface;

public class WebpageDownloadUtil4HttpClient implements DownloadInterface {
	@SuppressWarnings("deprecation")
	public static String parseEntity(HttpEntity entity, String defaultCharset) throws IOException {
		String findCharset = null;
		String htmlSource = null;
		// 不管那个编码,这个字节数组都会运行,故先拿出来
		byte[] contextByteArray = IOUtil.convertInputStreamToByteArray(entity.getContent());
		findCharset = EntityUtils.getContentCharSet(entity);
		if (findCharset == null) {
			htmlSource = new String(contextByteArray, defaultCharset);
			StringReader sr = new StringReader(htmlSource);
			BufferedReader br = new BufferedReader(sr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.contains("<meta")) {
					findCharset = RegexUtil.getMatchText(line, WebCharsetDetecorUtil.regex, 1);
					if (findCharset != null) {
						break;
					}
				} else if (line.contains("</head>")) {
					break;
				}
			}
			br.close();
		}
		// 无论如何都滴有个编码
		findCharset = (findCharset == null ? defaultCharset : findCharset);
		if (htmlSource == null || findCharset != defaultCharset) {
			htmlSource = new String(contextByteArray, findCharset);
		}
		return htmlSource;
	}

	public static String downloadStatic(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String htmlSource = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				htmlSource = parseEntity(entity1, StaticValue.defaultENCODING);
				EntityUtils.consume(entity1);
			} finally {
				response1.close();
			}
		} finally {
			httpclient.close();
		}
		return htmlSource;
	}

	@Override
	public String download(String url) {
		try {
			return downloadStatic(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// String url = "http://www.sohu.com";
		String url = "http://news.youth.cn/gn";
		// String url = "http://www.baidu.com";
		// String url = "http://www.qq.com";
		// String url = "http://www.163.com";
		String htmlSource = downloadStatic(url);
		System.out.println(htmlSource);
	}

}
