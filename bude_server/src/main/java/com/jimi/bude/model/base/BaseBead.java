package com.jimi.bude.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseBead<M extends BaseBead<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setFirstCode(java.lang.Integer firstCode) {
		set("first_code", firstCode);
		return (M)this;
	}
	
	public java.lang.Integer getFirstCode() {
		return getInt("first_code");
	}

	public M setSecondCode(java.lang.Integer secondCode) {
		set("second_code", secondCode);
		return (M)this;
	}
	
	public java.lang.Integer getSecondCode() {
		return getInt("second_code");
	}

	public M setDebugCode(java.lang.Integer debugCode) {
		set("debug_code", debugCode);
		return (M)this;
	}
	
	public java.lang.Integer getDebugCode() {
		return getInt("debug_code");
	}

	public M setSuffixTime(java.lang.String suffixTime) {
		set("suffix_time", suffixTime);
		return (M)this;
	}
	
	public java.lang.String getSuffixTime() {
		return getStr("suffix_time");
	}

	public M setMd5(java.lang.String md5) {
		set("md5", md5);
		return (M)this;
	}
	
	public java.lang.String getMd5() {
		return getStr("md5");
	}

	public M setAlias(java.lang.String alias) {
		set("alias", alias);
		return (M)this;
	}
	
	public java.lang.String getAlias() {
		return getStr("alias");
	}

	public M setFaceId(java.lang.Integer faceId) {
		set("face_id", faceId);
		return (M)this;
	}
	
	public java.lang.Integer getFaceId() {
		return getInt("face_id");
	}

	public M setUpdateDescribe(java.lang.String updateDescribe) {
		set("update_describe", updateDescribe);
		return (M)this;
	}
	
	public java.lang.String getUpdateDescribe() {
		return getStr("update_describe");
	}

}
