package com.jimi.bude.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSystemConfig<M extends BaseSystemConfig<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setKey(java.lang.String key) {
		set("key", key);
		return (M)this;
	}
	
	public java.lang.String getKey() {
		return getStr("key");
	}

	public M setValue(java.lang.String value) {
		set("value", value);
		return (M)this;
	}
	
	public java.lang.String getValue() {
		return getStr("value");
	}

}
