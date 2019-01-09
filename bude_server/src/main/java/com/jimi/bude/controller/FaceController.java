package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.FaceService;
import com.jimi.bude.util.ResultUtil;


/**
 * 模块管理控制层
 * @type FaceController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2019年1月9日
 */
public class FaceController extends Controller {

	private FaceService faceService = FaceService.me;


	/**
	 *添加模块
	 * @param headId
	 * @param faceName
	 */
	public void add(Integer headId, String faceName) {
		if (headId == null || faceName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.add(headId, faceName);
		renderJson(result);
	}


	/**
	 * 删除模块
	 * @param faceId
	 */
	public void delete(Integer faceId) {
		if (faceId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.delete(faceId);
		renderJson(result);
	}


	/**
	 * 更新模块
	 * @param headId
	 * @param faceId
	 * @param faceName
	 */
	public void update(Integer headId, Integer faceId, String faceName) {
		if (faceId == null || headId == null || faceName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.update(faceId, faceName, headId);
		renderJson(result);
	}


	/**
	 * 查询模块
	 * @param headId
	 * @param currentPage
	 * @param pageSize
	 */
	public void select(Integer headId, Integer currentPage, Integer pageSize) {
		if (headId == null) {
			throw new ParameterException("headId can not be null");
		}
		if ((currentPage == null || pageSize == null) && !(currentPage == null && pageSize == null)) {
			throw new ParameterException("currentPage and pageSize should either empty or not empty");
		}
		if (!(currentPage == null && pageSize == null) && (currentPage <= 0 || pageSize <= 0)) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = faceService.select(headId, currentPage, pageSize);
		renderJson(result);
	}


	/**
	 * 查询应用模块的设备端
	 * @param faceId
	 * @param firstCode
	 * @param secondCode
	 * @param debugCode
	 * @param suffixTime
	 * @param currentPage
	 * @param pageSize
	 */
	public void selectFinger(Integer faceId, Integer firstCode, Integer secondCode, Integer debugCode, String suffixTime, Integer currentPage, Integer pageSize) {
		if (faceId == null || currentPage == null || pageSize == null) {
			throw new ParameterException("Parameters can not be null");
		}
		if (currentPage <= 0 || pageSize <= 0) {
			throw new ParameterException("currentPage and pageSize must be greater than 0");
		}
		ResultUtil result = faceService.selectFinger(faceId, firstCode, secondCode, debugCode, suffixTime, currentPage, pageSize);
		renderJson(result);
	}


	/**
	 * 发布软件包
	 * @param beadId
	 * @param fingerName
	 * @param armName
	 */
	public void publish(Integer beadId, String fingerName, String armName) {
		if (beadId == null || fingerName == null || armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.publish(armName, fingerName, beadId);
		renderJson(result);
	}
}
