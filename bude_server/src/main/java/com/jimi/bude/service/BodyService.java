package com.jimi.bude.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.Body;
import com.jimi.bude.model.vo.ArmVO;
import com.jimi.bude.model.vo.BodyVO;
import com.jimi.bude.model.vo.PageUtil;
import com.jimi.bude.service.base.SelectService;
import com.jimi.bude.util.ResultUtil;

/**
 * 中转组业务逻辑层
 * @type BodyService
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月4日
 */
public class BodyService extends SelectService {
	private static final String SELECT_BODY_EXIST_SQL = "select * from body where name = ?";

	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	public static final BodyService me = new BodyService();

	/**
	 * 添加中转组
	 * @param bodyName
	 * @return
	 */
	public ResultUtil add(String bodyName) {
		Body body = Body.dao.findFirst(SELECT_BODY_EXIST_SQL, bodyName);
		String result = "";
		if (body != null) {
			result = "body has already exist";
			throw new OperationException(result);
		}
		body = new Body();
		body.setName(bodyName).save();
		return ResultUtil.succeed();
	}

	/**
	 * 删除中转组
	 * @param bodyId
	 * @return
	 */
	public ResultUtil delete(Integer bodyId) {
		Body body = Body.dao.findById(bodyId);
		String result = "";
		if (body == null) {
			result = "body is not exist";
			throw new OperationException(result);
		}
		try {
			Body.dao.deleteById(bodyId);
		} catch (Exception e) {
			result = "body has arm";
			throw new OperationException(result);
		}
		return ResultUtil.succeed();
	}

	/**
	 * 更新中转组名称
	 * @param bodyId
	 * @param bodyName
	 * @return
	 */
	public ResultUtil update(Integer bodyId, String bodyName) {
		Body body = Body.dao.findById(bodyId);
		String result = "";
		if (body == null) {
			result = "body is not exist";
			throw new OperationException(result);
		}
		body.setName(bodyName).update();
		return ResultUtil.succeed();
	}

	/**
	 * 查询中转组下的中转端
	 * @param bodyId
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public ResultUtil selectArm(Integer bodyId, Integer currentPage, Integer pageSize) {
		PageUtil<ArmVO> pageUtil = new PageUtil<>();
		Page<Record> pageRecord = new Page<>();
		List<ArmVO> list = new ArrayList<ArmVO>();
		String filter = "body_id = " + bodyId;
		String[] tables = {"body", "arm"};
		String[] refers = {"body.id = arm.body_id"};
		String[] discard = {"body.id", "body.name"};
		pageRecord = selectService.select(tables, refers, currentPage, pageSize, null, null, filter, discard);
		list = ArmVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);
	}

	/**
	 * 查询中转组
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public ResultUtil select(Integer currentPage, Integer pageSize) {
		PageUtil<BodyVO> pageUtil = new PageUtil<>();
		Page<Record> pageRecord = new Page<>();
		List<BodyVO> list = new ArrayList<BodyVO>();
		pageRecord = selectService.select("body", currentPage, pageSize, null, null, null, null);
		list = BodyVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);
	}
}
