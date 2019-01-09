package com.jimi.bude.model.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.jimi.bude.entity.ControlType;
import com.jimi.bude.redis.ControlRedisDao;


public class FingerAndBeadVO {

	private String name;
	private String armName;
	private String ip;
	private Date lastPingTime;
	private Integer beadId;
	private BeadVO bead;
	private String faceName;
	private String headName;
	private Boolean status;
	private String update;


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


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


	public Date getLastPingTime() {
		return lastPingTime;
	}


	public void setLastPingTime(Date lastPingTime) {
		this.lastPingTime = lastPingTime;
	}


	public Integer getBeadId() {
		return beadId;
	}


	public void setBeadId(Integer beadId) {
		this.beadId = beadId;
	}


	public BeadVO getBead() {
		return bead;
	}


	public void setBead(BeadVO bead) {
		this.bead = bead;
	}


	public String getFaceName() {
		return faceName;
	}


	public void setFaceName(String faceName) {
		this.faceName = faceName;
	}


	public String getHeadName() {
		return headName;
	}


	public void setHeadName(String headName) {
		this.headName = headName;
	}


	public Boolean getStatus() {
		return status;
	}


	public void setStatus(Boolean status) {
		this.status = status;
	}


	public String getUpdate() {
		return update;
	}


	public void setUpdate(String update) {
		this.update = update;
	}


	public static List<FingerAndBeadVO> fillList(List<Record> records) {
		Calendar calendar = Calendar.getInstance();
		List<FingerAndBeadVO> list = new ArrayList<FingerAndBeadVO>();
		for (Record record : records) {
			try {
				FingerAndBeadVO fingerAndBeadVO = new FingerAndBeadVO();
				fingerAndBeadVO.setName(record.getStr("Finger_Name"));
				fingerAndBeadVO.setArmName(record.getStr("Finger_ArmName"));
				fingerAndBeadVO.setIp(record.getStr("Finger_Ip"));
				fingerAndBeadVO.setBeadId(record.getInt("Finger_BeadId"));
				fingerAndBeadVO.setFaceName(record.getStr("Face_Name"));
				fingerAndBeadVO.setHeadName(record.getStr("Head_Name"));
				Date time = record.getDate("Finger_LastPingTime");
				fingerAndBeadVO.setLastPingTime(time);
				// 根据时间设置中转端状态，两分钟内未ping通则处于掉线状态
				if (time != null) {
					Date currentTime = new Date(System.currentTimeMillis());
					calendar.setTime(time);
					int min = calendar.get(Calendar.MINUTE) + 2;
					calendar.set(Calendar.MINUTE, min);
					Date tempTime = calendar.getTime();
					if (tempTime.after(currentTime) || tempTime.equals(currentTime)) {
						fingerAndBeadVO.setStatus(true);
					} else {
						fingerAndBeadVO.setStatus(false);
					}
				} else {
					fingerAndBeadVO.setStatus(false);
				}
				Integer updateNo = ControlRedisDao.get(record.getStr("Finger_ArmName"), record.getStr("Finger_Name"), ControlType.UPDATE);
				if (updateNo != null) {
					switch (updateNo) {
					case 20:
						fingerAndBeadVO.setUpdate("中转端从服务端下载软件包完成");
						break;
					case 21:
						fingerAndBeadVO.setUpdate("设备端从中转端下载软件包完成");
						break;
					case 22:
						fingerAndBeadVO.setUpdate("中转端正在使用本地缓存");
						break;
					case 23:
						fingerAndBeadVO.setUpdate("设备端正在使用本地缓存");
						break;
					case 24:
						fingerAndBeadVO.setUpdate("中转端正在校验MD5");
						break;
					case 25:
						fingerAndBeadVO.setUpdate("设备端正在校验MD5");
						break;
					case 26:
						fingerAndBeadVO.setUpdate("设备端软件重启（更新）完成");
						break;
					case 27:
						fingerAndBeadVO.setUpdate("设备端已接收更新指令");
						break;
					case 28:
						fingerAndBeadVO.setUpdate("设备端接收更新指令失败");
						break;
					case 30:
						fingerAndBeadVO.setUpdate("当前版本与要求更新的版本相同");
						break;
					case 40:
						fingerAndBeadVO.setUpdate("其他错误");
						break;
					case 41:
						fingerAndBeadVO.setUpdate("无法从上层节点找到该软件包");
						break;
					case 42:
						fingerAndBeadVO.setUpdate("从服务器下载的软件包与记录的MD5不一致");
						break;
					default:
						fingerAndBeadVO.setUpdate("NULL");
						break;
					}
				} else {
					fingerAndBeadVO.setUpdate("NULL");
				}
				BeadVO bead = new BeadVO();
				bead.setId(record.getInt("Bead_Id"));
				bead.setFaceId(record.getInt("Bead_FaceId"));
				bead.setAlias(record.getStr("Bead_Alias"));
				bead.setFirstCode(record.getInt("Bead_FirstCode"));
				bead.setSecondCode(record.getInt("Bead_SecondCode"));
				bead.setDebugCode(record.getInt("Bead_DebugCode"));
				bead.setMd5(record.getStr("Bead_Md5"));
				bead.setUpdateDescribe(record.getStr("Bead_UpdateDescribe"));
				bead.setSuffixTime(record.getStr("Bead_SuffixTime"));
				fingerAndBeadVO.setBead(bead);
				list.add(fingerAndBeadVO);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return list;
	}
}
