package com.jimi.bude.finger.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;

@Protocol(0x51)
public class PongPackage extends BasePackage{

	@Parse(value = {0,64}, utf8 = true)
	private String fingerName;

	public String getFingerName() {
		return fingerName;
	}

	public void setFingerName(String fingerName) {
		this.fingerName = fingerName;
	}
	
}
