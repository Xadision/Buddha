package com.jimi.bude.finger.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;

@Protocol(0x4C)
public class LoginReplyPackage extends BasePackage {

	@Parse({0,1})
	private int resultCode;
	@Parse({1,1})
	private int type;
	@Parse(value = {2,64}, utf8 = true)
	private String fingerName;
	
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getFingerName() {
		return fingerName;
	}
	public void setFingerName(String fingerName) {
		this.fingerName = fingerName;
	}
	
}
