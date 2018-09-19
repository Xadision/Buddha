package com.jimi.bude.controller;

import com.jfinal.core.Controller;
import com.jimi.bude.service.FaceService;
import com.jimi.bude.util.ResultUtil;

public class FaceController extends Controller {

	private FaceService faceService = FaceService.me;
	public void add(Integer headId, String faceName) {
		ResultUtil result = faceService.add(headId, faceName);
		renderJson(result);
	}
	
	public void delete(Integer faceId) {
		ResultUtil result = faceService.delete(faceId);
		renderJson(result);
	}
	
	public void update(Integer headId, Integer faceId, String faceName) {
		
	}
}
