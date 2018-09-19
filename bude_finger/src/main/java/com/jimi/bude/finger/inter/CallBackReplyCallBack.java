package com.jimi.bude.finger.inter;

import com.jimi.bude.finger.Finger;
import com.jimi.bude.finger.pack.CallBackReplyPackage;

/**
 * CallBack回复包接口：接收到CallBack回复包后执行的方法
 * @type CallBackReplyCallBack
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public interface CallBackReplyCallBack {
	
	/**
	 * CallBack回复包到达方法
	 * @param r 接收到的CallBack回复包
	 * @param session 通信过程的会话
	 */
	public void onReplyArrived(CallBackReplyPackage r, Finger session);
}
