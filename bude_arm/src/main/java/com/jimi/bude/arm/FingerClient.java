package com.jimi.bude.arm;

import com.jimi.bude.arm.pack.CallBackReplyPackage;
import com.jimi.bude.arm.pack.LoginReplyPackage;
import com.jimi.bude.arm.pack.PingPackage;
import com.jimi.bude.arm.pack.UpdatePackage;

import cc.darhao.jiminal.socket.Jiminal;

/**
 * 设备端Socket(无法单独使用，为中转监听端监听到设备端连接后生成)
 * @type Finger
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月25日
 */
public class FingerClient {
	
	
	
	private String fingerName;
	private Jiminal jiminal;
	
	static {
		
	}
	
	/**
	 * 设备端构造器Socket(无法单独使用，为中转监听端监听到设备端连接后生成)
	 * @param fingerName 设备端名称
	 * @param jiminal 设备端 Jiminal (Socket)
	 */
	public FingerClient(String fingerName, Jiminal jiminal) {
		this.jiminal = jiminal;
		this.fingerName = fingerName;
	}
	
	/**
	 * 启动并连接Socket
	 */
	public void start() {
		new Thread(() -> {
				jiminal.connect();
		}).start();
	}
	
	/**
	 * 发送登录回复包
	 * @param p 登录回复包
	 */
	public void sendLoginReplyPackage(LoginReplyPackage p) {
		jiminal.send(p);
	}
	
	/**
	 * 发送CallBack回复包
	 * @param p CallBack回复包
	 */
	public void sendCallBackReplyPackage(CallBackReplyPackage p) {
			jiminal.send(p);
		
	}
	
	/**
	 * 发送Ping回复包
	 * @param p Ping回复包
	 */
	public void sendPingPackage(PingPackage p) {
		jiminal.send(p);
	}
	
	/**
	 * 发送更新包
	 * @param p 更新包
	 */
	public void sendUpdatePackage(UpdatePackage p) {
		jiminal.send(p);
	}
	/**
	 * 停止并断开Socket连接
	 */
	public void stop() {
		jiminal.close();
	}

	/**
	 * 获取远程连接Ip地址
	 * @return
	 */
	public String getRemoteIp() {
		return jiminal.getRemoteIp();
	}

	/**
	 * 获取序列号
	 * @return
	 */
	public Integer getSerialNo() {
		return jiminal.getSerialNo();
	}
	
	
	public String getFingerName() {
		return fingerName;
	}
	
	public void setFingerName(String fingerName) {
		this.fingerName = fingerName;
	}
	
	
	
}
