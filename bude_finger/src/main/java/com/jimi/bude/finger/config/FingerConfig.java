package com.jimi.bude.finger.config;

import com.jimi.bude.finger.inter.CallBackReplyCallBack;
import com.jimi.bude.finger.inter.ConnectCallBack;
import com.jimi.bude.finger.inter.ExceptionCallBack;
import com.jimi.bude.finger.inter.LoginReplyCallBack;
import com.jimi.bude.finger.inter.PingCallBack;
import com.jimi.bude.finger.inter.UpdateCallBack;

/**
 * 设备端接口配置类
 * @type FingerConfig
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public class FingerConfig {

	private LoginReplyCallBack loginReplyCallBack;
	private CallBackReplyCallBack callBackReplyCallBack;
	private PingCallBack pingCallBack;
	private UpdateCallBack updateCallBack;
	private ConnectCallBack connectCallBack;
	private ExceptionCallBack exceptionCallBack;
	public LoginReplyCallBack getLoginReplyCallBack() {
		return loginReplyCallBack;
	}

	public void setLoginReplyCallBack(LoginReplyCallBack loginReplyCallBack) {
		this.loginReplyCallBack = loginReplyCallBack;
	}

	public CallBackReplyCallBack getCallBackReplyCallBack() {
		return callBackReplyCallBack;
	}

	public void setCallBackReplyCallBack(CallBackReplyCallBack callBackReplyCallBack) {
		this.callBackReplyCallBack = callBackReplyCallBack;
	}

	public PingCallBack getPingCallBack() {
		return pingCallBack;
	}

	public void setPingCallBack(PingCallBack pingCallBack) {
		this.pingCallBack = pingCallBack;
	}

	public UpdateCallBack getUpdateCallBack() {
		return updateCallBack;
	}

	public void setUpdateCallBack(UpdateCallBack updateCallBack) {
		this.updateCallBack = updateCallBack;
	}

	public ConnectCallBack getConnectCallBack() {
		return connectCallBack;
	}

	public void setConnectCallBack(ConnectCallBack connectCallBack) {
		this.connectCallBack = connectCallBack;
	}

	public ExceptionCallBack getExceptionCallBack() {
		return exceptionCallBack;
	}

	public void setExceptionCallBack(ExceptionCallBack exceptionCallBack) {
		this.exceptionCallBack = exceptionCallBack;
	}
	
	
}
