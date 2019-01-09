package com.jimi.bude.service;

import java.util.Date;

import com.jimi.bude.model.ActionLog;


/**
 * 接口调用日志业务逻辑层
 * @type ActionLogService
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月8日
 */
public class ActionLogService {

	public final static ActionLogService me = new ActionLogService();


	/**
	 * 添加接口调用日志
	 * @param userId
	 * @param ip
	 * @param message
	 * @param resultCode
	 * @param url
	 */
	public void add(Integer userId, String ip, String message, Integer resultCode, String url) {
		ActionLog actionLog = new ActionLog();
		actionLog.setUserId(userId).setIp(ip).setMessage(message).setResultCode(resultCode).setUrl(url).setTime(new Date());
		actionLog.save();
	}
}
