package com.jimi.bude.finger.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;

@Protocol(0x4C)
public class LoginPackage extends BasePackage {
	@Parse(value = {0,32}, utf8 = true)
	private String armName;
	@Parse({32,1})
	private int type;
	@Parse(value = {33,32}, utf8 = true)
	private String fingerName;
	@Parse(value = {65,15}, utf8 = true)
	private String fingerIp;
	
	public String getArmName() {
		return armName;
	}
	public void setArmName(String armName) {
		this.armName = armName;
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
	public String getFingerIp() {
		return fingerIp;
	}
	public void setFingerIp(String fingerIp) {
		this.fingerIp = fingerIp;
	}
	
}
