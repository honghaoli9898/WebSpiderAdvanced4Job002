package com.tl.job002.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {
	public static List<String> readFileToList(String filePath,boolean isClassPath,String charset) throws IOException {
		InputStream is=null;
		if(isClassPath){
			is = IOUtil.class.getClassLoader().getResourceAsStream(filePath);
		}
		else{
			is=new FileInputStream(filePath);
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
	public static BufferedReader getBR(URLConnection urlConnection,String charSet) throws IOException{
		InputStream is = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is,charSet);
		BufferedReader br =new BufferedReader(isr);
		return br;
	}
	public static BufferedReader getBR(String url,String charset) throws IOException{
		URL urlObj=new URL(url);
		URLConnection urlconnection=urlObj.openConnection();
		return getBR(urlconnection,charset);
	}
	public static byte[] convertInputStreamToByteArray(InputStream is) throws IOException{
		byte[] byteBuffer = new byte[4096];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int readLength=0;
		while((readLength=is.read(byteBuffer))!=-1){
			bos.write(byteBuffer,0,readLength);
		}
		return bos.toByteArray();
	}
	public static URLConnection getUrlConnection(String url) throws IOException{
		URL urlObj = new URL(url);
		URLConnection urlconnection = urlObj.openConnection();
		return urlconnection;
	}
}
