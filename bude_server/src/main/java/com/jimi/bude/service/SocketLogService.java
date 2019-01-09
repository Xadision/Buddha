package com.jimi.bude.service;

import java.util.Date;

import com.jimi.bude.model.SocketLog;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.jiminal.pack.BasePackage;
import cc.darhao.jiminal.parse.PackageParser;


public class SocketLogService {

	public static SocketLogService me = new SocketLogService();


	public void add(BasePackage p, Class<? extends BasePackage> type, String senderIp, String receiverIp) {
		String message = BytesParser.parseBytesToHexString(PackageParser.serialize(p));
		Date time = new Date();
		SocketLog socketLog = new SocketLog();
		socketLog.setSenderIp(senderIp).setReceiverIp(receiverIp).setType(type.getSimpleName()).setMessage(message).setTime(time).save();
	}
}
