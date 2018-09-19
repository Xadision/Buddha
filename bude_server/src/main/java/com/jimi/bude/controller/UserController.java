package com.jimi.bude.controller;

import java.util.HashMap;

import com.jfinal.core.Controller;
import com.jimi.bude.service.UserService;

public class UserController extends Controller {

	public void add(int id,String name,String password) {
		String a = UserService.me.addUser(id,name, password);
		java.util.Map<String,String> map = new HashMap<>();
		map.put("result", a);
		renderJson(map);
	}
}
