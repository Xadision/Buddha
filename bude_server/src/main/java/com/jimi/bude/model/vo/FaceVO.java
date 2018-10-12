package com.jimi.bude.model.vo;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.jimi.bude.model.Face;

public class FaceVO {
	private Integer id;
	private String name;
	private Integer headId;

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

	public Integer getHeadId() {
		return headId;
	}

	public void setHeadId(Integer headId) {
		this.headId = headId;
	}

	public static List<FaceVO> fillList(List<Record> records) {
		List<FaceVO> list = new ArrayList<FaceVO>();
		for (Record record : records) {
			FaceVO face = new FaceVO();
			face.setId(record.getInt("Face_Id"));
			face.setHeadId(record.getInt("Face_HeadId"));
			face.setName(record.getStr("Face_Name"));
			list.add(face);
		}
		return list;
	}

	public static List<FaceVO> fillListByFace(List<Face> records) {
		List<FaceVO> list = new ArrayList<FaceVO>();
		for (Face record : records) {
			FaceVO face = new FaceVO();
			face.setId(record.getId());
			face.setHeadId(record.getHeadId());
			face.setName(record.getName());
			list.add(face);
		}
		return list;
	}
}
