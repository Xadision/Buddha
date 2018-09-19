package com.jimi.bude.finger;

import com.jimi.bude.finger.config.FingerConfig;
import com.jimi.bude.finger.inter.CallBackReplyCallBack;
import com.jimi.bude.finger.inter.ConnectCallBack;
import com.jimi.bude.finger.inter.ExceptionCallBack;
import com.jimi.bude.finger.inter.LoginReplyCallBack;
import com.jimi.bude.finger.inter.PingCallBack;
import com.jimi.bude.finger.inter.UpdateCallBack;
import com.jimi.bude.finger.pack.CallBackPackage;
import com.jimi.bude.finger.pack.CallBackReplyPackage;
import com.jimi.bude.finger.pack.LoginPackage;
import com.jimi.bude.finger.pack.LoginReplyPackage;
import com.jimi.bude.finger.pack.PingPackage;
import com.jimi.bude.finger.pack.PingReplyPackage;
import com.jimi.bude.finger.pack.UpdatePackage;
import com.jimi.bude.finger.pack.UpdateReplyPackage;

import cc.darhao.jiminal.callback.JiminalCallback;
import cc.darhao.jiminal.config.PackageConfig;
import cc.darhao.jiminal.config.SocketConfig;
import cc.darhao.jiminal.pack.BasePackage;
import cc.darhao.jiminal.socket.Jiminal;

/**
 * Bude_Finger设备端接口
 * @type Finger
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月18日
 */
public class Finger{

	private Jiminal jiminal;
	
	private String remoteIp;
	
	private Integer serialNo;
	
	private FingerConfig fingerConfig;
	
	private static PackageConfig packageConfig;
	
	static {
		packageConfig = new PackageConfig();
		packageConfig.add(LoginPackage.class, true);
		packageConfig.add(PingPackage.class, false);
		packageConfig.add(CallBackPackage.class, true);
		packageConfig.add(UpdatePackage.class, false);
	}
	
	/**
	 * 设备端构造器
	 * @param ip 连接的Socket服务器IP地址
	 * @param port 连接的Socket服务器端口号
	 * @param config 设备端接口配置
	 * @param maxReplyTime 最大回复时间，超时则断开连接(可传空值，默认为5000ms)
	 */
	public Finger(String ip, Integer port, Integer maxReplyTime, FingerConfig config){
		fingerConfig = config;
		remoteIp = ip;
		if (maxReplyTime != null) {
			SocketConfig socketConfig = new SocketConfig();
			socketConfig.setMaxReplyTime(maxReplyTime);
			jiminal.setSocketConfig(socketConfig);
		}
		jiminal = new Jiminal(ip, port, packageConfig, new JiminalCallback() {
			
			public void onReplyArrived(BasePackage r, Jiminal session) {
				
				if (r instanceof LoginReplyPackage) {
					serialNo = session.getSerialNo();
					LoginReplyCallBack callBack = fingerConfig.getLoginReplyCallBack();
					if (callBack != null) {
						callBack.onReplyArrived((LoginReplyPackage) r, Finger.this);
					}
					
				}else if (r instanceof CallBackReplyPackage) {
					serialNo = session.getSerialNo();
					CallBackReplyCallBack callBack = fingerConfig.getCallBackReplyCallBack();
					if (callBack != null) {
						callBack.onReplyArrived((CallBackReplyPackage) r, Finger.this);
					}
				}
			}
			
			public void onPackageArrived(BasePackage p, BasePackage r, Jiminal session) {
			
				if (p instanceof PingPackage) {
					serialNo = session.getSerialNo();
					PingCallBack callBack = fingerConfig.getPingCallBack();
					if (callBack != null) {
						callBack.onPackageArrvied((PingReplyPackage)r,(PingPackage) p, Finger.this);
					}
				}else if (p instanceof UpdatePackage) {
					serialNo = session.getSerialNo();
					UpdateCallBack callBack = fingerConfig.getUpdateCallBack();
					if (callBack != null) {
						callBack.onPackageArrvied((UpdateReplyPackage)r,(UpdatePackage) p, Finger.this);
					}
				}
			}
			
			public void onCatchException(Exception e, Jiminal session) {
				serialNo = session.getSerialNo();
				ExceptionCallBack callBack = fingerConfig.getExceptionCallBack();
				if (callBack != null) {
					callBack.onCathcException(e, Finger.this);
				}else {
					e.printStackTrace();
				}
			}
			
			public void onConnected() {
				ConnectCallBack callBack = fingerConfig.getConnectCallBack();
				if (callBack != null) {
					callBack.onConnect(Finger.this);
				}
			}
		});
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
	 * 发送登录包
	 * @param p 登录包
	 */
	public void sendLoginPackage(final LoginPackage p) {
		new Thread(() -> {
				jiminal.send(p);
		}).start();
	}
	
	/**
	 * 发送CallBack包
	 * @param p CallBack包
	 */
	public void sendCallBackPackage(final CallBackPackage p) {
		new Thread(() -> {
			jiminal.send(p);
		}).start();
		
	}
	
	/**
	 * 停止并断开Socket连接
	 */
	public void stop() {
		jiminal.close();
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public Integer getSerialNo() {
		return serialNo;
	}
	
	
}
