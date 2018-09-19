package com.jimi.bude.finger.inter;

import com.jimi.bude.finger.Finger;
import com.jimi.bude.finger.pack.LoginReplyPackage;

/**
 * 登录包回复包接口：接收到登录回复包后调用的方法
 * @type LoginReplyCallBack
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public interface LoginReplyCallBack{
	
	/**
	 * 登录回复包到达方法
	 * @param r 接收到的登录回复包
	 * @param session 通信过程的会话
	 */
	public void onReplyArrived(LoginReplyPackage r, Finger session);
	//public void onLoginSucceed(LoginReplyPackage r, Finger session);
	//public void onLoginFailed(LoginReplyPackage r, Finger session);
}
