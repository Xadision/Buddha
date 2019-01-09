package com.jimi.bude.util;

import java.io.File;
import java.io.FileFilter;

import com.jfinal.kit.PathKit;


public class FileUtil {

	public static String getFilePath(String basePath, String... params) {
		StringBuffer filePath = new StringBuffer();
		filePath.append(PathKit.getWebRootPath());
		filePath.append(File.separator);
		filePath.append(basePath);

		for (String param : params) {
			filePath.append(File.separator);
			filePath.append(param);
		}
		filePath.append(File.separator);
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
		String name = headName + "-" + faceName + "_" + firstCode + "." + secondCode + "." + debugCode + "_" + suffixTime;
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

				String name = file.getName().substring(0, file.getName().lastIndexOf("."));
				if (name.equals(fileName)) {
					return true;
				}
				return false;
			}
		});
		if (tempFile == null || tempFile.length <= 0) {
			return null;
		}
		return tempFile[0];
	}


	public static void main(String[] args) {
		System.out.println(getFilePath("BEAD", "SMT", "Display"));
		System.out.println(getFileName("SMT", "Display", "3", "1", "0", "201801021225"));
	}
}
