package com.jimi.bude.service;

import java.util.ArrayList;
import java.util.List;

import com.jimi.bude.model.Face;
import com.jimi.bude.util.ResultUtil;

public class FaceService {

	public final static String SELECT_EXISTENT_FACE_SQL = "select * from face where head_id = ? and name = ?";
	private final static String SELECT_ALL_FACE_SQL = "select * from face";
	private final static String SELECT_FACE_BY_HEAD_SQL = "select * from face where head_id = ?";
	
	public final static FaceService me = new FaceService();
	public ResultUtil add(Integer headId,String faceName) {
		String result = "operation fail";
		Face face = Face.dao.findFirst(SELECT_EXISTENT_FACE_SQL, headId,faceName);
		if (face != null) {
			result = "face already exist";
			return ResultUtil.failed(401, result);
		}
		boolean flag = new Face().set("head_id", headId).set("name", faceName).save();
		if (flag) {
			result = "operation succeed";
			return ResultUtil.succeed(result);
		}
		return ResultUtil.failed(501, result);
	}
	
	public ResultUtil update(Integer faceId,String faceName,String headId) {
		String result = "operation fail";
		Face face = Face.dao.findFirst(SELECT_EXISTENT_FACE_SQL, headId,faceName);
		if (face != null) {
			result = "face already exist";
			return ResultUtil.failed(401, result);
		}
		boolean flag = Face.dao.findById(headId).set("head_id", headId).set("name", faceName).update();
		
		if (flag) {
			result = "operation succeed";
			return  ResultUtil.succeed(result);
		}
		return ResultUtil.failed(501, result);
	}
	
	public List<Face> select(Integer headId){
		List<Face> list = new ArrayList<Face>();
		if(headId == null) {
			list = Face.dao.find(SELECT_ALL_FACE_SQL);
		}else {
			list = Face.dao.find(SELECT_FACE_BY_HEAD_SQL, headId);
		}
		return list;
	}
	
	public ResultUtil delete(int faceId) {
		String result = "operation fail";
		boolean flag = Face.dao.deleteById(faceId);
		if (flag) {
			result = "operation succeed";
			return ResultUtil.succeed(result);	
		}
		return ResultUtil.failed(501, result);
	}
}
