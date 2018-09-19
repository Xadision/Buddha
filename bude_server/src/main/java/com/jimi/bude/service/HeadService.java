package com.jimi.bude.service;

import java.util.ArrayList;
import java.util.List;

import com.jimi.bude.model.Head;

public class HeadService {
	public final static String SELECT_EXISTENT_HEAD_SQL = "select * from head where name = ?";
	private final static String SELECT_ALL_HEAD_SQL = "select * from head";
	
	public String add(String headName) {
		String result = "operation fail";
		Head head = Head.dao.findFirst(SELECT_EXISTENT_HEAD_SQL, headName);
		if (head != null) {
			result = "head already exist";
			return result;
		}
		new Head().set("name", headName).save();
		result = "operation succeed";
		return result;
	}
	
	public String update(Integer headId,String headName) {
		String result = "operation fail";
		Head head = Head.dao.findFirst(SELECT_EXISTENT_HEAD_SQL, headName);
		if (head != null) {
			result = "head already exist";
			return result;
		}
		Head.dao.findById(headId).set("name", headName).update();
		result = "operation succeed";
		return result;
	}
	
	public List<Head> select(){
		List<Head> list = new ArrayList<Head>();
		list = Head.dao.find(SELECT_ALL_HEAD_SQL);
		return list;
	}
	
	public String delete(Integer headId) {
		Head.dao.deleteById(headId);
		return "operation succeed";
	}
}
