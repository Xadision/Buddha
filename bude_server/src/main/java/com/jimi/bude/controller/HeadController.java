package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.HeadService;
import com.jimi.bude.util.ResultUtil;


/**
 * 项目管理控制层
 * @type HeadController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2019年1月9日
 */
public class HeadController extends Controller {

	private HeadService headService = HeadService.me;


	/**
	 * 添加项目
	 * @param headName
	 */
	public void add(String headName) {
		if (headName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = headService.add(headName);
		renderJson(result);
	}


	/**
	 * 更新项目
	 * @param headId
	 * @param headName
	 */
	public void update(Integer headId, String headName) {
		if (headId == null || headName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = headService.update(headId, headName);
		renderJson(result);
	}


	/**
	 * 查询项目
	 * @param currentPage
	 * @param pageSize
	 */
	public void select(Integer currentPage, Integer pageSize) {
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("Parameters should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = headService.select(currentPage, pageSize);
		renderJson(result);
	}


	/**
	 * 删除项目
	 * @param headId
	 */
	public void delete(Integer headId) {
		if (headId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = headService.delete(headId);
		renderJson(result);
	}
}
