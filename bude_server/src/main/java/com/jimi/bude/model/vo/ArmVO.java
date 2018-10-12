package com.jimi.bude.model.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class ArmVO {
	String armName;
	String ip;
	Integer serverPort;
	Date lastPingTime;
	Integer bodyId;
	Boolean status;

	public String getArmName() {
		return armName;
	}

	public void setArmName(String armName) {
		this.armName = armName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public Date getLastPingTime() {
		return lastPingTime;
	}

	public void setLastPingTime(Date lastPingTime) {
		this.lastPingTime = lastPingTime;
	}

	public Integer getBodyId() {
		return bodyId;
	}

	public void setBodyId(Integer bodyId) {
		this.bodyId = bodyId;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public static List<ArmVO> fillList(List<Record> records) {
		Calendar calendar = Calendar.getInstance();
		List<ArmVO> list = new ArrayList<ArmVO>();
		for (Record record : records) {
			ArmVO arm = new ArmVO();
			arm.setArmName(record.getStr("Arm_Name"));
			arm.setIp(record.getStr("Arm_Ip"));
			arm.setServerPort(record.getInt("Arm_ServerPort"));
			arm.setBodyId(record.getInt("Arm_BodyId"));
			Date time = record.getDate("Arm_LastPingTime");
			arm.setLastPingTime(time);
			if (time != null) {
				// 根据时间设置中转端状态，两分钟内未ping通则处于掉线状态
				Date currentTime = new Date(System.currentTimeMillis());
				calendar.setTime(time);
				int min = calendar.get(Calendar.MINUTE) + 2;
				calendar.set(Calendar.MINUTE, min);
				Date tempTime = calendar.getTime();
				if (tempTime.after(currentTime) || tempTime.equals(currentTime)) {
					arm.setStatus(true);
				} else {
					arm.setStatus(false);
				}
			} else {
				arm.setStatus(false);
			}

			list.add(arm);
		}
		return list;
	}
}
