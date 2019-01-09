package com.jimi.bude.model.vo;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;


public class HeadVO {

	private Integer id;
	private String name;


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public static List<HeadVO> fillList(List<Record> records) {
		List<HeadVO> list = new ArrayList<HeadVO>();
		for (Record record : records) {
			HeadVO head = new HeadVO();
			head.setId(record.getInt("Head_Id"));
			head.setName(record.getStr("Head_Name"));
			list.add(head);
		}
		return list;
	}

}
