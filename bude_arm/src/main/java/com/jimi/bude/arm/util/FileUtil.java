package com.jimi.bude.arm.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;


public class FileUtil {
	
	public static String  getFilePath(String basePath, String ...params) {
		StringBuffer filePath = new StringBuffer();
		if (basePath != null) {
			filePath.append(basePath+File.separator);
		}
		for (String param : params) {
			filePath.append(param);
			filePath.append(File.separator);
		}
		return filePath.toString();
		
	}
	
	/**
	 * 拼接文件名，无后缀
	 * @param headName
	 * @param faceName
	 * @param firstCode
	 * @param secondCode
	 * @param debugCode
	 * @param suffixTime
	 * @return
	 */
	public static String getFileName(String headName, String faceName, String firstCode, String secondCode, String debugCode, String suffixTime) {
		String name = headName+"-"+faceName+"_"+firstCode+"."+secondCode+"."+debugCode+"_"+suffixTime;
		return name;
	}
	
	/**
	 * 根据路径和文件名(无文件后缀)查找文件，只返回第一个文件
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public static File findFile(String filePath, String fileName) {
		File dir = new File(filePath);
		File[] tempFile = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				if (file.getName().contains(".")) {
					String name = file.getName().substring(0, file.getName().lastIndexOf("."));
					if (name.equals(fileName)) {
						return true;
					}
					return false;
				}
				return false;
				
			}
		});
		if (tempFile == null || tempFile.length <= 0) {
			return null;
		}
		return tempFile[0];
	}
	
	/**
	 * 计算文件MD5
	 * @param file
	 * @return
	 */
	public static String getMD5(File file) {
		FileInputStream fileInputStream = null;
		try {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(MD5.digest()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) {
		System.out.println(getFilePath("BEAD","SMT","Display"));
		System.out.println(getFileName("SMT", "Display", "3", "1", "0", "201801021225"));
	}
}
