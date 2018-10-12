package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.service.FingerService;
import com.jimi.bude.util.ResultUtil;

public class FingerController extends Controller {

	private FingerService fingerService = FingerService.me;

	public void add(String fingerName, String armName, Integer beadId) {
		if (fingerName == null || armName == null || beadId == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = fingerService.add(fingerName, armName, beadId);
		renderJson(result);
	}

	public void delete(String fingerName, String armName) {
		if (fingerName == null || armName == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = fingerService.delete(fingerName, armName);
		renderJson(result);
	}
}
