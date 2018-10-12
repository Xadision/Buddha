package com.jimi.bude.finger.demo;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import cc.darhao.dautils.api.BytesParser;

public class Demo {
	static int i = 0;
	static int j = 0;
	static int k = 0;
	static int z = 0;
	static short packageId = 0;
	List<Finger> lists = new ArrayList<Finger>();

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		System.out.println("------------------开始测试-----------------");
		String sip = "";
		try {
			sip = Inet4Address.getLocalHost().getHostAddress();
			sip = sip.replace(".", " ");
			System.out.println(sip);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		final String dip = sip;
		String remoteIp = "";
		Integer port = 0;
		Scanner in = new Scanner(System.in);
		System.out.println("请输入中转端IP地址:");
		remoteIp = in.next();
		System.out.println("请输入中转端监听端口端口:");
		port = in.nextInt();
		System.out.println("请输入中转端名称:");
		final String armName = in.next();
		System.out.println("请输入设备端名称:");
		final String fingerName = in.next();
		// Finger设备端接口配置
		FingerConfig config = new FingerConfig();
		// Finger设备端Socket配置
		Finger finger = new Finger(remoteIp, port, 10000, config);
		// 配置Socket连接成功后执行的方法
		config.setConnectCallBack(new ConnectCallBack() {

			public void onConnect(Finger session) {
				LoginPackage login = new LoginPackage();
				login.setArmName(armName);
				// 发送前需将ip转化成16进制
				String ip = BytesParser.parseBytesToHexString((BytesParser.parseXRadixStringToBytes(dip, 10)));
				login.setFingerIp(ip);
				login.setFingerName(fingerName);
				login.setType(1);
				login.setPackageId(0);
				// 发送登录包
				session.sendLoginPackage(login);

			}
		});
		// 配置接收到登录回复包后执行的方法
		config.setLoginReplyCallBack(new LoginReplyCallBack() {

			public void onReplyArrived(LoginReplyPackage r, Finger session) {
				if (r.getResultCode() != 20) {
					System.out.println("--------------登录失败------------------");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					LoginPackage login = new LoginPackage();
					login.setArmName(armName);
					String ip = BytesParser.parseBytesToHexString((BytesParser.parseXRadixStringToBytes("127 0 0 1", 10)));
					login.setFingerIp(ip);
					login.setFingerName(fingerName);
					login.setType(1);
					login.setPackageId(genPackageId());
					// 发送登录包
					session.sendLoginPackage(login);
				} else {
					System.out.println("---------------登录成功------------------");
				}

			}
		});
		// 配置接收到更新包后执行的方法
		config.setUpdateCallBack(new UpdateCallBack() {

			public void onPackageArrvied(UpdatePackage p, UpdateReplyPackage r, Finger session) {
				z++;
				Integer controllId = p.getControllId();
				System.out.println("接收到更新包:，" + z + "controllId：" + controllId + "信息序列号：" + session.getSerialNo() + "MD5 :" + p.getMd5());
				// 填充CallBack包
				UpdateReplyPackage reply = (UpdateReplyPackage) r;
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						CallBackPackage c = new CallBackPackage();
						c.setControllId(controllId);
						c.setStatus(21);
						c.setFingerName(fingerName);
						c.setType(1);
						// 发送CallBack包
						session.sendCallBackPackage(c);
						// 填充CallBack包
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						c = new CallBackPackage();
						c.setControllId(controllId);
						c.setStatus(25);
						c.setFingerName(fingerName);
						c.setType(1);
						// 发送CallBack包
						session.sendCallBackPackage(c);
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						c = new CallBackPackage();
						c.setControllId(controllId);
						c.setStatus(26);
						c.setFingerName(fingerName);
						c.setType(1);
						// 发送CallBack包
						session.sendCallBackPackage(c);
					}
				}).start();
				reply.setControllId(p.getControllId());
				reply.setResultCode(20);

			}
		});
		// 配置接收到CallBack回复包后执行的方法
		config.setCallBackReplyCallBack(new CallBackReplyCallBack() {

			public void onReplyArrived(CallBackReplyPackage r, Finger session) {
				System.out.println("收到CallBack回复包" + r.getFingerName());
			}
		});
		// 配置捕获到异常后执行的方法
		config.setExceptionCallBack(new ExceptionCallBack() {

			@Override
			public void onCathcException(Exception e, Finger session) {
				e.printStackTrace();
				// 方便测试，在捕获到错误后退出程序
				System.exit(0);

			}
		});
		// 启动Socket，连接服务器端
		finger.start();

		while (true) {

		}
		// 停止finger，断开Socket连接
		// finger.stop();

	}

	public synchronized static Short genPackageId() {
		packageId %= 65536;
		packageId++;
		return packageId;
	}

}
