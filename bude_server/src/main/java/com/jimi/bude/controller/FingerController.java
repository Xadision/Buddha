package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.FingerService;
import com.jimi.bude.util.ResultUtil;


/**
 * 设备端管理
 * @type FingerController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2019年1月9日
 */
public class FingerController extends Controller {

	private FingerService fingerService = FingerService.me;


	/**
	 * 添加设备端
	 * @param fingerName
	 * @param armName
	 * @param beadId
	 */
	public void add(String fingerName, String armName, Integer beadId) {
		if (fingerName == null || armName == null || beadId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = fingerService.add(fingerName, armName, beadId);
		renderJson(result);
	}


	/**
	 * 删除设备端
	 * @param fingerName
	 * @param armName
	 */
	public void delete(String fingerName, String armName) {
		if (fingerName == null || armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = fingerService.delete(fingerName, armName);
		renderJson(result);
	}
}
