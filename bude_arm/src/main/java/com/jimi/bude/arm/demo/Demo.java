package com.jimi.bude.arm.demo;

import java.util.Scanner;

import com.jimi.bude.arm.ArmClient;
import com.jimi.bude.arm.ArmServer;

public class Demo {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		String remoteIp = "";
		Integer port = 0;
		Scanner in = new Scanner(System.in);
		System.out.println("请输入Bude服务器远程连接IP地址:");
		remoteIp = in.next();
		System.out.println("请输入Bude服务器远程连接Socket端口:");
		port = in.nextInt();
		System.out.println("请输入中转端名称:");
		final String armName = in.next();
		System.out.println("请输入Bude服务器http端口:");
		Integer httpPort = in.nextInt();
		System.out.println("请输入中转端监听端口:");
		Integer listenPort = in.nextInt();
		//获取中转端连接服务端对象
		ArmClient armClient = ArmClient.getInstance();
		//中转端连接服务器端配置
		armClient.setConfig(remoteIp, port, null,  armName, "" , "http://"+remoteIp+":"+httpPort+"/bude_server/bead/download");
		//启动中转端连接服务器
		armClient.start();
		//获取中转端的设备端监听器，并配置监听端口
		ArmServer armServer = new ArmServer(listenPort);
		//启动监听器
		armServer.start();
		while(true) {
			
		}
	}
}
