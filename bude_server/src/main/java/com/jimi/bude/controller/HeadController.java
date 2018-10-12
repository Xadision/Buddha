package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.HeadService;
import com.jimi.bude.util.ResultUtil;

public class HeadController extends Controller {

	private HeadService headService = HeadService.me;

	public void add(String headName) {
		if (headName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = headService.add(headName);
		renderJson(result);
	}

	public void update(Integer headId, String headName) {
		if (headId == null || headName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = headService.update(headId, headName);
		renderJson(result);
	}

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

	public void delete(Integer headId) {
		if (headId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = headService.delete(headId);
		renderJson(result);
	}
}
