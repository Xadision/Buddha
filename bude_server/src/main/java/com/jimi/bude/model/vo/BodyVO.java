package com.jimi.bude.model.vo;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;

public class BodyVO {
	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static List<BodyVO> fillList(List<Record> records) {
		List<BodyVO> list = new ArrayList<BodyVO>();
		for (Record record : records) {
			BodyVO bodyVO = new BodyVO();
			bodyVO.setId(record.getInt("Body_Id"));
			bodyVO.setName(record.getStr("Body_Name"));
			list.add(bodyVO);
		}
		return list;
	}
}
