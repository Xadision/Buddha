package com.jimi.bude.finger.demo;


import com.jimi.bude.finger.Finger;
import com.jimi.bude.finger.config.FingerConfig;
import com.jimi.bude.finger.inter.CallBackReplyCallBack;
import com.jimi.bude.finger.inter.ConnectCallBack;
import com.jimi.bude.finger.inter.ExceptionCallBack;
import com.jimi.bude.finger.inter.LoginReplyCallBack;
import com.jimi.bude.finger.inter.UpdateCallBack;
import com.jimi.bude.finger.pack.CallBackPackage;
import com.jimi.bude.finger.pack.CallBackReplyPackage;
import com.jimi.bude.finger.pack.LoginPackage;
import com.jimi.bude.finger.pack.LoginReplyPackage;
import com.jimi.bude.finger.pack.UpdatePackage;
import com.jimi.bude.finger.pack.UpdateReplyPackage;

public class Demo {
	
	public void test() {
		System.out.println("------------------开始测试-----------------");
		//Finger设备端接口配置
		FingerConfig config = new FingerConfig();
		//Finger设备端Socket配置
		Finger finger = new Finger("127.0.0.1", 43215, null, config);
		//配置Socket连接成功后执行的方法
		config.setConnectCallBack(new ConnectCallBack() {
			

			public void onConnect(Finger session) {
				LoginPackage login = new LoginPackage();
				login.setArmName("USB111");
				login.setFingerIp("127.0.0.1");
				login.setFingerName("BBB1111");
				login.setType(1);
				//发送登录包
				session.sendLoginPackage(login);
				
			}
		});
		//配置接收到登录回复包后执行的方法
		config.setLoginReplyCallBack(new LoginReplyCallBack() {
			
			public void onReplyArrived(LoginReplyPackage r, Finger session) {
				if (r.getResultCode() != 20) {
					
					LoginPackage login = new LoginPackage();
					login.setArmName("USB111");
					login.setFingerIp("127.0.0.1");
					login.setFingerName("BBB1111");
					login.setType(1);
					//发送登录包
					session.sendLoginPackage(login);
				}else {
					
				}
				
			}
		});
		//配置接收到更新包后执行的方法
		config.setUpdateCallBack(new UpdateCallBack() {
			
			public void onPackageArrvied(UpdateReplyPackage p, UpdatePackage r, Finger session) {
				//填充更新回复包
				p.setResultCode(20);
				
				Integer controllId = r.getControllId();
				System.out.println("接收到更新包，controllId：" + controllId+"信息序列号：" + session.getSerialNo() + "MD5 :"  +r.getMd5());
				//填充CallBack包
				CallBackPackage c = new CallBackPackage();
				c.setControllId(controllId);
				c.setStatus(25);
				//发送CallBack包
				session.sendCallBackPackage(c);
				//填充CallBack包
				c = new CallBackPackage();
				c.setControllId(controllId);
				c.setStatus(21);
				//发送CallBack包
				session.sendCallBackPackage(c);
				
			}
		});
		//配置接收到CallBack回复包后执行的方法
		config.setCallBackReplyCallBack(new CallBackReplyCallBack() {
			
			public void onReplyArrived(CallBackReplyPackage r, Finger session) {
				System.out.println("收到CallBack回复包");
			}
		});
		//配置捕获到异常后执行的方法
		config.setExceptionCallBack(new ExceptionCallBack() {
			
			@Override
			public void onCathcException(Exception e, Finger session) {
				e.printStackTrace();
				System.exit(0);
				
			}
		});
		//启动Socket，连接服务器端
		finger.start();
		
		//停止finger，断开Socket连接
		finger.stop();
		
	}
}
