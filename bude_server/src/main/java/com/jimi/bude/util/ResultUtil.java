package com.jimi.bude.util;

/**
 * 返回一个带result字段和data字段的json，result为succeed时，data为正常数据；result为failed时，data为错误信息
 * 
 */
public class ResultUtil {

	private int result;

	private Object data;


	public static ResultUtil succeed() {
		return succeed("operation succeed");
	}


	public static ResultUtil succeed(Object data) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.result = 200;
		resultUtil.data = data;
		return resultUtil;
	}


	public static ResultUtil failed() {
		return failed("operation failed");
	}


	public static ResultUtil failed(int result) {
		return failed(result, "operation failed");
	}


	public static ResultUtil failed(Object errorMsg) {
		return failed(501, errorMsg);
	}


	public static ResultUtil failed(int result, Object errorMsg) {
		ResultUtil resultUtil = new ResultUtil();
		resultUtil.result = result;
		resultUtil.data = errorMsg;
		return resultUtil;
	}


	public int getResult() {
		return result;
	}


	public void setResult(int result) {
		this.result = result;
	}


	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}

}
