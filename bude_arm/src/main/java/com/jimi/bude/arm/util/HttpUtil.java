package com.jimi.bude.arm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.FormBody.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
	private final static OkHttpClient client = new OkHttpClient();
	
	/**
	 * 下载文件
	 * @param url 下载文件http接口
	 * @param args 携带参数Map(key：参数名，value：参数值)
	 * @param downloadPath 下载文件存储路径(文件若下载前已存在，则将会删除然后再进行下载)
	 * @return 下载结果(true/false)
	 * @throws IOException
	 */
	public static boolean downLoadFile(String url, Map <String, String> args, String filePath) throws IOException{
		Builder requestBuilder = new FormBody.Builder();
		if (args != null) {
			for (Map.Entry <String, String> entry : args.entrySet()) {
				requestBuilder.add(entry.getKey(), entry.getValue());
			}
		}
		RequestBody requestBody = requestBuilder.build();
		Request request = new Request.Builder().url(url).post(requestBody).build();
		Call call = client.newCall(request);
		Response response = null;
		try {
			response = call.execute();
		} catch (ConnectException e) {
			System.out.println("下载失败，网络连接问题");
			return false;
		}
		List<String> list = response.headers("Content-disposition");
		String fileName = "";
		FileOutputStream fos = null;
		if (list != null && !list.isEmpty() && list.get(0).contains("filename")) {
			fileName = list.get(0).substring(list.get(0).indexOf("\"")+1,list.get(0).lastIndexOf("\""));
			//byte[] data = response.body().bytes();
			InputStream in = response.body().byteStream();
			try {
				File path = new File(filePath);
				if (!path.exists()) {
					path.mkdirs();
				}
				File file = CommonUtil.findFile(filePath, fileName);
				if(file != null) {
					file.delete();
				}
				System.out.println(path.getAbsolutePath());
				file = new File(filePath+fileName);
				fos = new FileOutputStream(file);
				byte[] buf = new byte[8192];
				Integer len = 0;
				while ((len = in.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
			    fos.flush(); 
			    fos.close();
			} catch (Exception e) {
				e.printStackTrace();
				if (fos != null) {
					fos.close();
				}
				return false;
			}
		    return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		String url = "http://127.0.0.1:80/bude_server/bead/download";
		Map<String, String> map = new HashMap<String, String>();
		String headName = "SMT";
		String faceName = "Display";
		String firstCode = "2";
		String secondCode = "0";
		String debugCode = "1";
		String suffixTime = "201808091020";
		map.put("headName", headName);
		map.put("faceName", faceName);
		map.put("firstCode", firstCode);
		map.put("secondCode", secondCode);
		map.put("debugCode", debugCode);
		map.put("suffixTime", suffixTime);
		try {
			System.out.println(downLoadFile(url, map,"D:\\BEAD\\SMT\\Display\\2\\0\\1"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
