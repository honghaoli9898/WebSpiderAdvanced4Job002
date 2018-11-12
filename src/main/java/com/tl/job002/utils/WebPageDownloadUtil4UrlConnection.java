package com.tl.job002.utils;

import java.io.IOException;
import java.net.URLConnection;

import com.tl.job002.iface.download.DownloadInterface;

/**
 * 用于下载给定任意网址对应的html代码
 * 
 * @author lihonghao
 * @date 2018年11月6日
 */
public class WebPageDownloadUtil4UrlConnection implements DownloadInterface {
	public static String downloadStatic(String url, String defaultCharset) throws IOException {
		// 1.声明最终的html源码变量
		String htmlSource = null;
		// 2.将url的urlConnection拿到
		URLConnection urlConnection = IOUtil.getUrlConnection(url);
		// 3.将流转换为字节数组
		byte[] contentByteArray = IOUtil.convertInputStreamToByteArray(urlConnection.getInputStream());
		// 4.从http header中获取编码 如果拿到则为最终网页编码
		String findCharset = WebCharsetDetecorUtil.getCharsetByHttpHeader(urlConnection);
		// 5.从htmlSource中获取编码
		if (findCharset == null) {
			htmlSource = new String(contentByteArray, defaultCharset);
			findCharset = WebCharsetDetecorUtil.getCharsetByHtmlSource(htmlSource);
		}
		findCharset = (findCharset == null ? StaticValue.defaultENCODING : findCharset);
		if (htmlSource == null || findCharset != defaultCharset) {
			htmlSource = new String(contentByteArray, findCharset);
		}
		return htmlSource;
	}

	@Override
	public String download(String url) {
		try {
			return downloadStatic(url, StaticValue.defaultENCODING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String url = "http://www.sohu.com";
		// String url = "http://news.youth.cn/gn";
		// String url = "http://www.baidu.com";
		// String url = "http://www.qq.com";
		// String url = "http://www.163.com";
		DownloadInterface d = new WebPageDownloadUtil4UrlConnection();
		System.out.println(d.download(url));
	}
}
