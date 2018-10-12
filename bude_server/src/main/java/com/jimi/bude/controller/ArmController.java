package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.ArmService;
import com.jimi.bude.util.ResultUtil;

/**
 * 中转端管理控制层
 * @type ArmController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月3日
 */
public class ArmController extends Controller {
	private ArmService armService = ArmService.me;

	/**
	 * 添加中转端
	 * @param armName
	 * @param serverPort
	 */
	public void add(String armName, Integer serverPort) {
		if (armName == null || serverPort == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = armService.add(armName, serverPort);
		renderJson(result);
	}

	/**
	 * 删除中转端
	 * @param armName
	 */
	public void delete(String armName) {
		if (armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = armService.delete(armName);
		renderJson(result);
	}

	/**
	 * 移入移出中转组
	 * @param armName
	 * @param bodyId
	 */
	public void editBody(String armName, Integer bodyId) {
		if (armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = armService.editBody(armName, bodyId);
		renderJson(result);
	}

	/**
	 * 查询中转端
	 * @param armName 可空
	 * @param currentPage
	 * @param pageSize
	 */
	public void select(String armName, Integer currentPage, Integer pageSize) {
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("currentPage and pageSize should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = armService.select(armName, currentPage, pageSize);
		renderJson(result);
	}

	/**
	 * 查询中转端下的设备端信息和设备端应用的软件包信息
	 * @param armName
	 * @param currentPage
	 * @param pageSize
	 */
	public void selectFinger(String armName, Integer currentPage, Integer pageSize) {
		if (armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("currentPage and pageSize should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = armService.selectFinger(armName, currentPage, pageSize);
		renderJson(result);
	}
}
