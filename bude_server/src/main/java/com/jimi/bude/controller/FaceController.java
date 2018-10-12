package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.FaceService;
import com.jimi.bude.util.ResultUtil;

public class FaceController extends Controller {

	private FaceService faceService = FaceService.me;

	public void add(Integer headId, String faceName) {
		if (headId == null || faceName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.add(headId, faceName);
		renderJson(result);
	}

	public void delete(Integer faceId) {
		if (faceId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.delete(faceId);
		renderJson(result);
	}

	public void update(Integer headId, Integer faceId, String faceName) {
		if (faceId == null || headId == null || faceName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.update(faceId, faceName, headId);
		renderJson(result);
	}

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

	public void publish(Integer beadId, String fingerName, String armName) {
		if (beadId == null || fingerName == null || armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = faceService.publish(armName, fingerName, beadId);
		renderJson(result);
	}
}
