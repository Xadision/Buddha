package com.jimi.bude.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;

@Protocol(0x4C)
public class LoginPackage extends BasePackage {

	@Parse(value = {0, 64}, utf8 = true)
	private String armName;
	@Parse({64, 1})
	private int type;
	@Parse(value = {65, 64}, utf8 = true)
	private String fingerName;
	@Parse(value = {129, 4})
	private String fingerIp;
	@Parse({133, 2})
	private int packageId;

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

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

}
