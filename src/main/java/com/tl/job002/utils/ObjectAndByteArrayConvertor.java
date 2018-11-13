package com.tl.job002.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * java对象与字节数组转换工具
 * 
 * @author lihonghao
 * @date 2018年11月13日
 */
public class ObjectAndByteArrayConvertor {
	public static byte[] convertObjectToByteArray(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.flush();
		byte[] objByArray = baos.toByteArray();
		return objByArray;
	}

	public static Object convertByteArrayToObj(byte[] objByArray) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(objByArray);
		ObjectInputStream osi = new ObjectInputStream(bais);
		Object obj = osi.readObject();
		return obj;
	}
}
