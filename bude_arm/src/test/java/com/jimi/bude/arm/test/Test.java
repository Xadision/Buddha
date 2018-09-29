package com.jimi.bude.arm.test;

import com.jimi.bude.arm.ArmClient;
import com.jimi.bude.arm.ArmServer;

public class Test {
	@org.junit.Test
	public void test() {
		//获取中转端连接服务端对象
		ArmClient armClient = ArmClient.getInstance();
		//中转端连接服务器端配置
		armClient.setConfig("127.0.0.1", 54321, null,  "BBBBBBBB", "" , "http://127.0.0.1:80/bude_server/bead/download");
		//启动中转端连接服务器
		armClient.start();
		//获取中转端的设备端监听器，并配置监听端口
		ArmServer armServer = new ArmServer(28456);
		//启动监听器
		armServer.start();
		while(true) {
			
		}
	}
}
