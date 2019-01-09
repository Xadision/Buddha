package com.jimi.bude.controller;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jimi.bude.annotation.Open;
import com.jimi.bude.exception.OperationException;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.model.User;
import com.jimi.bude.service.UserService;
import com.jimi.bude.util.ResultUtil;
import com.jimi.bude.util.TokenBox;

import cc.darhao.dautils.api.MD5Util;


/**
 * 用户管理控制层
 * @type UserController
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月10日
 */
public class UserController extends Controller {

	private UserService userService = Enhancer.enhance(UserService.class);
	public static final String SESSION_KEY_LOGIN_USER = "loginUser";


	/**
	 * 添加用户
	 * @param name
	 * @param password
	 */
	public void add(String name, String password) {
		if (name == null || password == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = userService.add(name, password);
		renderJson(result);
	}


	/**
	 * 删除用户
	 * @param id
	 */
	public void delete(Integer id) {
		if (id == null) {
			throw new ParameterException("Parameters can not be null");
		}
		ResultUtil result = userService.delete(id);
		renderJson(result);
	}


	/**
	 * 登录
	 * @param name
	 * @param password
	 */
	@Open
	public void login(String name, String password) {
		if (name == null || password == null) {
			throw new ParameterException("Parameters can not be null");
		}
		User user = userService.selectUser(name);
		if (user == null) {
			throw new ParameterException("user is not exist");
		}
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		if (tokenId != null) {
			User user2 = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
			if (user2 != null && user.getId().equals(user2.getId())) {
				throw new OperationException("user already login");
			}
		}
		String passwordMd5 = MD5Util.MD5(password);
		if (!passwordMd5.equals(user.getPassword())) {
			throw new ParameterException("password is wrong");
		}
		tokenId = TokenBox.createTokenId();
		user.put(TokenBox.TOKEN_ID_KEY_NAME, tokenId);
		TokenBox.put(tokenId, SESSION_KEY_LOGIN_USER, user);
		renderJson(ResultUtil.succeed(user));

	}


	/**
	 * 登出
	 */
	public void logout() {
		String tokenId = getPara(TokenBox.TOKEN_ID_KEY_NAME);
		User user = TokenBox.get(tokenId, SESSION_KEY_LOGIN_USER);
		if (user == null) {
			throw new OperationException("no user login");
		}
		TokenBox.remove(tokenId);
		renderJson(ResultUtil.succeed());
	}
}
