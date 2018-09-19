package com.jimi.bude.service;

import com.jimi.bude.model.User;

public class UserService {
	public static final UserService me = new UserService();
	private User dao = User.dao;
	
	private static String FINDBYNAMEANDPWD = "select * from user where name = ? and password = ?";
	private static String FINDBYNAME = "select * from user where name = ?";
	
	public String addUser(int id,String name, String password) {
		
		User user = new User();
		user.setName(name);
		user.setPassword(password);
		user.save();
		return "用户添加成功";
	}
	
	public User selectUser(String name, String password) {
		return dao.findFirst(FINDBYNAMEANDPWD, name, password);
		
	}
	public void deleteById(int id) {
		dao.deleteById(id);
	}
}
