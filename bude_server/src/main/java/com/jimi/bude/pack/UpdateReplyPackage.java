package com.jimi.bude.pack;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.pack.BasePackage;


@Protocol(0x55)
public class UpdateReplyPackage extends BasePackage {

	@Parse({0, 1})
	private int resultCode;
	@Parse({1, 2})
	private int controllId;


	public int getResultCode() {
		return resultCode;
	}


	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}


	public int getControllId() {
		return controllId;
	}


	public void setControllId(int controllId) {
		this.controllId = controllId;
	}

}
