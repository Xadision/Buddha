package com.jimi.bude.service;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.Arm;
import com.jimi.bude.model.Body;
import com.jimi.bude.model.vo.ArmVO;
import com.jimi.bude.model.vo.FingerAndBeadVO;
import com.jimi.bude.model.vo.PageUtil;
import com.jimi.bude.service.base.SelectService;
import com.jimi.bude.util.ResultUtil;

/**
 * 中转端管理业务逻辑层
 * 
 * @type ArmService
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月3日
 */
public class ArmService extends SelectService {
	private static SelectService selectService = Enhancer.enhance(SelectService.class);
	public static final ArmService me = new ArmService();

	/**
	 * 添加中转端
	 * 
	 * @param armName
	 * @param serverPort
	 * @return
	 */
	public ResultUtil add(String armName, Integer serverPort) {
		Arm arm = Arm.dao.findById(armName);
		String result = "";
		if (arm != null) {
			result = "the arm is already exist";
			throw new OperationException(result);
		}
		arm = new Arm();
		arm.setName(armName);
		arm.setServerPort(serverPort);
		arm.save();
		return ResultUtil.succeed();
	}

	/**
	 * 删除中转端
	 * 
	 * @param armName
	 * @return
	 */
	public ResultUtil delete(String armName) {
		String result = "";
		Arm arm = Arm.dao.findById(armName);
		if (arm == null) {
			result = "the arm is not exist";
			throw new OperationException(result);
		}
		try {
			Arm.dao.deleteById(armName);
		} catch (Exception e) {
			result = "the arm has been referenced";
			throw new OperationException(result);
		}
		return ResultUtil.succeed();
	}

	/**
	 * 查询中转端
	 * 
	 * @param armName
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public ResultUtil select(String armName, Integer currentPage, Integer pageSize) {
		Page<Record> pageRecord = new Page<>();
		PageUtil<ArmVO> pageUtil = new PageUtil<>();
		List<ArmVO> list = new ArrayList<ArmVO>();
		String filter = null;
		if (armName != null) {
			filter = "name = " + armName;
		}
		pageRecord = selectService.select("arm", currentPage, pageSize, null, null, filter, null);
		list = ArmVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);
	}

	/**
	 * 查询中转端下设备端信息
	 * 
	 * @param armName
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public ResultUtil selectFinger(String armName, Integer currentPage, Integer pageSize) {
		Page<Record> pageRecord = new Page<>();
		PageUtil<FingerAndBeadVO> pageUtil = new PageUtil<>();
		List<FingerAndBeadVO> list = new ArrayList<FingerAndBeadVO>();
		String filter = "arm_name = " + armName;
		String[] tables = {"finger", "bead", "face", "head"};
		String[] refers = {"finger.bead_id = bead.id", "bead.face_id = face.id", "face.head_id = head.id"};
		String[] discard = {"face.id", "head.id", "face.head_id"};
		pageRecord = selectService.select(tables, refers, currentPage, pageSize, null, null, filter, discard);
		list = FingerAndBeadVO.fillList(pageRecord.getList());
		pageUtil.fill(pageRecord, list);
		return ResultUtil.succeed(pageUtil);
	}

	/**
	 * 移入中转组
	 * 
	 * @param armName
	 * @param bodyId
	 * @return
	 */
	public ResultUtil editBody(String armName, Integer bodyId) {
		Arm arm = Arm.dao.findById(armName);
		String result = "";
		if (arm == null) {
			result = "arm is not exist";
			throw new OperationException(result);
		}
		if (bodyId == null) {
			arm.setBodyId(null).update();
			return ResultUtil.succeed();
		}
		Body body = Body.dao.findById(bodyId);
		if (body == null) {
			result = "body is not exist";
			throw new OperationException(result);
		}
		arm.setBodyId(bodyId).update();
		return ResultUtil.succeed();
	}

	/**
	 * 移出中转组
	 * 
	 * @param armName
	 * @return
	 */
	public ResultUtil moveOutBody(String armName) {
		Arm arm = Arm.dao.findById(armName);
		String result = "";
		if (arm == null) {
			result = "arm is not exist";
			throw new OperationException(result);
		}
		if (arm.getBodyId() == null) {
			result = "arm is not in body";
			throw new OperationException(result);
		}
		arm.setBodyId(null).update();
		return ResultUtil.succeed();
	}

	/**
	 * 查询中转端(供Socket调用)
	 * 
	 * @param armName
	 * @return
	 */
	public Arm select(String armName) {
		Arm arm = Arm.dao.findById(armName);
		return arm;
	}

	/**
	 * 更新中转端(供Socket调用)
	 * 
	 * @param arm
	 * @return
	 */
	public void update(Arm arm) {
		arm.update();
	}
}
