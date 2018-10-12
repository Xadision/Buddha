package com.jimi.bude.service;

import java.util.Date;

import com.jimi.bude.model.ErrorLog;

/**
 * 错误日志业务逻辑层
 * @type ErrorLogService
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月8日
 */
public class ErrorLogService {
	public static ErrorLogService me = new ErrorLogService();

	/**
	 * 添加错误日志
	 * @param type
	 * @param message
	 */
	public void add(String type, String message) {
		ErrorLog errorLog = new ErrorLog();
		errorLog.setType(type).setMessage(message).setTime(new Date());
		errorLog.save();
	}
}
