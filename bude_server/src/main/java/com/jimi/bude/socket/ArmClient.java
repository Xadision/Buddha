package com.jimi.bude.socket;

import com.jimi.bude.pack.PingPackage;
import com.jimi.bude.pack.UpdatePackage;
import com.jimi.bude.service.SocketLogService;

import cc.darhao.jiminal.socket.Jiminal;

/**
 * 中转端Socket客户端
 * @type ArmClient
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月8日
 */
public class ArmClient {
	private String armName;
	private Jiminal jiminal;
	private static SocketLogService socketLogService = SocketLogService.me;
	private static short controllId = 0;

	public ArmClient(String armName, Jiminal jiminal) {
		this.armName = armName;
		this.jiminal = jiminal;
	}

	/**
	 * 发送更新包
	 * @param p 更新包
	 */
	public void sendUpdatePackage(UpdatePackage p) {
		jiminal.send(p);
		socketLogService.add(p, UpdatePackage.class, Bude.me.getLoaclhost(), jiminal.getRemoteIp());
	}

	/**
	 * 发送Ping包
	 * @param p Ping包
	 */
	public void sendPingPackage(PingPackage p) {
		jiminal.send(p);
		socketLogService.add(p, PingPackage.class, Bude.me.getLoaclhost(), jiminal.getRemoteIp());
	}

	/**
	 * 停止关闭Socket
	 */
	public void stop() {
		jiminal.close();
	}

	public String getArmName() {
		return armName;
	}

	/**
	 * 生成ControllId
	 * @return
	 */
	public synchronized static Short genControllId() {
		controllId %= 65536;
		controllId++;
		return controllId;
	}
}
