package com.tl.job002.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {
	public static List<String> readFileToList(String filePath,
			boolean isClassPath, String charset) throws IOException {
		InputStream is = null;
		if (isClassPath) {
			is = IOUtil.class.getClassLoader().getResourceAsStream(filePath);
		} else {
			is = new FileInputStream(filePath);
		}
		InputStreamReader isr = new InputStreamReader(is, charset);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		List<String> lineList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			lineList.add(line);
		}
		br.close();
		return lineList;
	}

	public static BufferedReader getBR(URLConnection urlConnection,
			String charSet) throws IOException {
		InputStream is = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, charSet);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}

	public static BufferedReader getBR(String url, String charset)
			throws IOException {
		URL urlObj = new URL(url);
		URLConnection urlconnection = urlObj.openConnection();
		return getBR(urlconnection, charset);
	}

	public static byte[] convertInputStreamToByteArray(InputStream is)
			throws IOException {
		byte[] byteBuffer = new byte[4096];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int readLength = 0;
		while ((readLength = is.read(byteBuffer)) != -1) {
			bos.write(byteBuffer, 0, readLength);
		}
		byte[] byteArray = bos.toByteArray();
		bos.close();
		is.close();
		return byteArray;
	}

	public static boolean deleteFile(String filrDir) {
		return new File(filrDir).delete();
	}

	public static String readFile(String filePath) throws IOException {
		FileInputStream fis = new FileInputStream(filePath);
		byte[] byteArray = convertInputStreamToByteArray(fis);
		return new String(byteArray, "utf-8");
	}

	public static URLConnection getUrlConnection(String url) throws IOException {
		URL urlObj = new URL(url);
		URLConnection urlconnection = urlObj.openConnection();
		return urlconnection;
	}

	public static void writeFile(String context, String fileName)
			throws IOException {
		FileOutputStream fops = new FileOutputStream(fileName);
		OutputStreamWriter osw = new OutputStreamWriter(fops);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(context);
		bw.flush();
		bw.close();
	}

	// 向文件里追加内容方法A
	public static synchronized void writeFile(String context, String fileName,
			boolean isAppend) throws IOException {
		FileOutputStream fops = new FileOutputStream(fileName, isAppend);
		OutputStreamWriter osw = new OutputStreamWriter(fops);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(context);
		bw.write("\n");
		bw.flush();
		bw.close();
	}

	// 向文件里追加方法B
	public static void appendContentToFile(String fileName, String content) {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。在该位置发生下一个读取或写入操作。
			randomFile.seek(fileLength);
			// 按字节序列将该字符串写入该文件。
			randomFile.writeBytes(content);
			// 关闭此随机访问文件流并释放与该流关联的所有系统资源。
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
