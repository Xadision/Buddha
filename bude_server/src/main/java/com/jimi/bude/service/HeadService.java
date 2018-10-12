package com.jimi.bude.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.Face;
import com.jimi.bude.model.Head;
import com.jimi.bude.model.vo.HeadVO;
import com.jimi.bude.model.vo.PageUtil;
import com.jimi.bude.service.base.SelectService;
import com.jimi.bude.util.CommonUtil;
import com.jimi.bude.util.ResultUtil;

public class HeadService extends SelectService {
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	public final static  HeadService me = new HeadService();
	private final static String SELECT_EXISTENT_HEAD_SQL = "select * from head where name = ?";
	private final static String SELECT_FACE_BY_HEAD_SQL = "select * from face where face.head_id = ?";
	public ResultUtil add(String headName) {
		String result = "";
		Head head = Head.dao.findFirst(SELECT_EXISTENT_HEAD_SQL, headName);
		if (head != null) {
			result = "head already exist";
			throw new OperationException(result);
		}
		new Head().set("name", headName).save();
		return ResultUtil.succeed();
	}

	public synchronized ResultUtil update(Integer headId, String headName) {
		String result = "";
		Head head = Head.dao.findFirst(SELECT_EXISTENT_HEAD_SQL, headName);
		if (head != null && !(head.getId() == headId)) {
			result = "headName already existed, cannot repeated";
			throw new OperationException(result);
		}
		head = Head.dao.findById(headId);
		if (head == null) {
			result = "head is not exist";
			throw new OperationException(result);
		}
		List<Face> faces =  Face.dao.find(SELECT_FACE_BY_HEAD_SQL, headId);
		for (Face face : faces) {
			File[] files = CommonUtil.findFaceFiles(CommonUtil.getFilePath("BEAD", head.getName(), face.getName()));
			if (files != null && files.length > 0) {
				for (File file : files) {
					String fileName = file.getName();
					String tail = fileName.substring(fileName.indexOf("-"));
					String newFileName = headName + tail;
					CommonUtil.rename(CommonUtil.getFilePath("BEAD", head.getName(), face.getName()), fileName, newFileName);
				}
			}
		}
		CommonUtil.rename(CommonUtil.getFilePath("BEAD"), head.getName(), headName);
		head.setName(headName);
		head.update();
		return ResultUtil.succeed();
	}

	public ResultUtil select(Integer currentPage, Integer pageSize) {
		List<HeadVO> list = new ArrayList<>();
		Page<Record> pageRecord = new Page<>();
		PageUtil<HeadVO> pageUtil = new PageUtil<>();
		pageRecord = selectService.select("head", currentPage, pageSize, null, null, null, null);
		list = HeadVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);
	}

	public ResultUtil delete(Integer headId) {
		String result = "";
		Head head = Head.dao.findById(headId);
		if (head == null) {
			result = "head is not exist";
			throw new OperationException(result);
		}
		String headName = head.getName();
		try {
			head.delete();
		} catch (Exception e) {
			result = "the head has been referenced";
			throw new OperationException(result);
		}
		File dir = new File(CommonUtil.getFilePath("BEAD", headName));
		if (dir.exists()) {
			dir.delete();
		}
		return ResultUtil.succeed();
	}
}
