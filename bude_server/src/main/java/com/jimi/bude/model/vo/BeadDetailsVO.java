package com.jimi.bude.model.vo;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class BeadDetailsVO {
	private Integer id;
	private Integer faceId;
	private String alias;
	private String md5;
	private Integer firstCode;
	private Integer secondCode;
	private Integer debugCode;
	private String suffixTime;
	private String updateDescribe;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFaceId() {
		return faceId;
	}
	public void setFaceId(Integer faceId) {
		this.faceId = faceId;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public Integer getFirstCode() {
		return firstCode;
	}
	public void setFirstCode(Integer firstCode) {
		this.firstCode = firstCode;
	}
	public Integer getSecondCode() {
		return secondCode;
	}
	public void setSecondCode(Integer secondCode) {
		this.secondCode = secondCode;
	}
	public Integer getDebugCode() {
		return debugCode;
	}
	public void setDebugCode(Integer debugCode) {
		this.debugCode = debugCode;
	}
	public String getSuffixTime() {
		return suffixTime;
	}
	public void setSuffixTime(String suffixTime) {
		this.suffixTime = suffixTime;
	}
	public String getUpdateDescribe() {
		return updateDescribe;
	}
	public void setUpdateDescribe(String updateDescribe) {
		this.updateDescribe = updateDescribe;
	}
	
	public static List<BeadDetailsVO> fillList(List<Record> records){
		List<BeadDetailsVO> list = new ArrayList<BeadDetailsVO>();
		for (Record record : records) {
			BeadDetailsVO bead = new BeadDetailsVO();
			bead.setId(record.getInt("Bead_Id"));
			bead.setFaceId(record.getInt("Bead_FaceId"));
			bead.setAlias(record.getStr("Bead_Alias"));
			bead.setFirstCode(record.getInt("Bead_FirstCode"));
			bead.setSecondCode(record.getInt("Bead_SecondCode"));
			bead.setDebugCode(record.getInt("Bead_DebugCode"));
			bead.setMd5(record.getStr("Bead_Md5"));
			bead.setUpdateDescribe(record.getStr("Bead_UpdateDescribe"));
			bead.setSuffixTime(record.getStr("Bead_SuffixTime"));
			list.add(bead);
		}
		return list;
	}
}
