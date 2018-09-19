package com.jimi.bude.finger.inter;

import com.jimi.bude.finger.Finger;
import com.jimi.bude.finger.pack.PingPackage;
import com.jimi.bude.finger.pack.PingReplyPackage;

/**
 * Ping包接口：接收到Ping包后调用的方法
 * @type PingCallBack
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public interface PingCallBack{
	
	/**
	 * Ping包到达方法
	 * @param p 接收到的Ping包
	 * @param r Ping回复包(不需要主动发送，只需要填充该回复包，方法结束后自动发送该回复包)
	 * @param session 通信过程的会话
	 */
	public void onPackageArrvied(PingReplyPackage p, PingPackage r, Finger session);
}
