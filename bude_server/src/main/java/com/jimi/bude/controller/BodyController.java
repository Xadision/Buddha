package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.BodyService;
import com.jimi.bude.util.ResultUtil;

/**
 * 中转组管理控制层
 * @type BodyController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月4日
 */
public class BodyController extends Controller {

	private BodyService bodyService = BodyService.me;

	/**
	 * 添加中转组
	 * @param bodyName
	 */
	public void add(String bodyName) {
		if (bodyName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = bodyService.add(bodyName);
		renderJson(result);
	}

	/**
	 * 删除中转组
	 * @param bodyId
	 */
	public void delete(Integer bodyId) {
		if (bodyId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = bodyService.delete(bodyId);
		renderJson(result);
	}

	/**
	 * 更新中转组名称
	 * @param bodyId
	 * @param bodyName
	 */
	public void update(Integer bodyId, String bodyName) {
		if (bodyId == null || bodyName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = bodyService.update(bodyId, bodyName);
		renderJson(result);
	}

	/**
	 * 查询中转组
	 * @param currentPage
	 * @param pageSize
	 */
	public void select(Integer currentPage, Integer pageSize) {
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("currentPage and pageSize should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = bodyService.select(currentPage, pageSize);
		renderJson(result);
	}

	/**
	 * 查询中转组下中转端
	 * @param bodyId
	 * @param currentPage
	 * @param pageSize
	 */
	public void selectArm(Integer bodyId, Integer currentPage, Integer pageSize) {
		if (bodyId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("currentPage and pageSize should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = bodyService.selectArm(bodyId, currentPage, pageSize);
		renderJson(result);
	}
}
