package com.jimi.bude.service;

import com.jimi.bude.exception.OperationException;
import com.jimi.bude.model.User;
import com.jimi.bude.util.ResultUtil;

import cc.darhao.dautils.api.MD5Util;

public class UserService {
	public static final UserService me = new UserService();

	// private static String FINDBYNAMEANDPWD = "select * from user where name = ?
	// and password = ?";
	private static String FINDBYNAME = "select * from user where name = ?";

	public ResultUtil add(String name, String password) {
		User user = User.dao.findFirst(FINDBYNAME, name);
		String result = "";
		if (user != null) {
			result = "user is already exist";
			throw new OperationException(result);
		}
		user = new User();
		user.setName(name);
		String passwordMd5 = MD5Util.MD5(password);
		user.setPassword(passwordMd5);
		user.save();
		return ResultUtil.succeed();
	}

	public User selectUser(String name) {
		User user = User.dao.findFirst(FINDBYNAME, name);
		return user;

	}

	public ResultUtil delete(Integer id) {
		User user = User.dao.findById(id);
		String result = "";
		if (user == null) {
			result = "user is not exist";
			throw new OperationException(result);
		}
		User.dao.deleteById(id);
		return ResultUtil.succeed();
	}

}
