package com.jimi.bude.finger.inter;

import com.jimi.bude.finger.Finger;

/**
 * 异常捕获接口：Finger连接和处理过程中捕获到的异常处理接口
 * @type ExceptionCallBack
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public interface ExceptionCallBack {

	/**
	 * 异常处理方法
	 * @param e 通信过程和包处理过程捕获到的异常
	 * @param session 通信过程的会话
	 */
	public void onCathcException(Exception e, Finger session);
}
