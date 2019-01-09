package com.jimi.bude.entity;

/**
 * 记录用户发送命令的信息
 * @type Infomation
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月10日
 */
public class Information {

	private String fingerName;
	private String armName;
	private ControlType type;
	private Integer beadId;


	public String getFingerName() {
		return fingerName;
	}


	public void setFingerName(String fingerName) {
		this.fingerName = fingerName;
	}


	public ControlType getType() {
		return type;
	}


	public void setType(ControlType type) {
		this.type = type;
	}


	public String getArmName() {
		return armName;
	}


	public void setArmName(String armName) {
		this.armName = armName;
	}


	public Integer getBeadId() {
		return beadId;
	}


	public void setBeadId(Integer beadId) {
		this.beadId = beadId;
	}

}
