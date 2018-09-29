package com.jimi.bude.finger.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;

@Protocol(0x43)
public class CallBackPackage extends BasePackage {

	@Parse({0, 2})
	private int controllId;
	@Parse({2, 1})
	private int status;
	@Parse({3, 2})
	private int packageId;
	@Parse({5, 1})
	private int type;
	@Parse(value = {6, 64}, utf8 = true)
	private String fingerName;

	public int getControllId() {
		return controllId;
	}

	public void setControllId(int controllId) {
		this.controllId = controllId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
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
