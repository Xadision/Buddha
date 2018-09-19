package com.jimi.bude.finger.inter;

import com.jimi.bude.finger.Finger;

/**
 * Connect成功接口：Socket连接成功后执行的方法
 * @type ConnectCallBack
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public interface ConnectCallBack {
	
	/**
	 * 连接成功调用方法
	 * @param session 通信过程的会话
	 */
	public void onConnect(Finger session);
}
