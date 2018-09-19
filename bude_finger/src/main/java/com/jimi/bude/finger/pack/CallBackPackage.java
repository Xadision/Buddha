package com.jimi.bude.finger.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;

@Protocol(0x43)
public class CallBackPackage extends BasePackage {

	@Parse({0,2})
	private int controllId;
	@Parse({2,1})
	private int status;
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
	
	
}
