package com.jimi.bude.interceptor;

import java.lang.reflect.Method;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jimi.bude.annotation.Open;
import com.jimi.bude.controller.UserController;
import com.jimi.bude.exception.AccessException;
import com.jimi.bude.model.User;
import com.jimi.bude.util.TokenBox;


public class AccessInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Method method = inv.getMethod();
		Open open = method.getAnnotation(Open.class);
		if (open != null) {
			inv.invoke();
			return;
		}
		String token = inv.getController().getPara(TokenBox.TOKEN_ID_KEY_NAME);
		User user = TokenBox.get(token, UserController.SESSION_KEY_LOGIN_USER);
		if (user == null) {
			throw new AccessException("access denied");
		} else {
			inv.invoke();
		}
	}
}
