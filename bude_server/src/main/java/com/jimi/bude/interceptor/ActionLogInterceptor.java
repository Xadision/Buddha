package com.jimi.bude.interceptor;

import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.render.FileRender;
import com.jfinal.render.JsonRender;
import com.jfinal.render.NullRender;
import com.jfinal.render.Render;
import com.jimi.bude.controller.UserController;
import com.jimi.bude.model.User;
import com.jimi.bude.service.ActionLogService;
import com.jimi.bude.util.TokenBox;

public class ActionLogInterceptor implements Interceptor {

	private ActionLogService actionLogService = ActionLogService.me;

	@Override
	public void intercept(Invocation inv) {
		inv.invoke();
		// 解析返回值
		Controller controller = inv.getController();
		Integer resultCode = 200;
		String message = "";
		String result = "返回值: ";
		try {
			Render render = (Render) controller.getRender();
			if (render instanceof JsonRender) {
				JsonRender jsonRender = (JsonRender) render;
				String jsonString = jsonRender.getJsonText();
				JSONObject object = (JSONObject) JSONArray.parse(jsonString);
				resultCode = object.getInteger("result");
				try {
					JSONArray data = object.getJSONArray("data");
					result += data.toString();
				} catch (ClassCastException e) {
					JSONObject data = object.getJSONObject("data");
					result += data.toString();
				} catch (JSONException e) {
					String dataString = object.getString("data");
					result += dataString;
				}
			} else if (render instanceof NullRender) {
				result += "无返回值，找不到文件";
			} else if (render instanceof FileRender) {
				result += "文件";
			}

		} catch (Exception e) {
			e.printStackTrace();
			result += "返回值解析出错";

		}

		// 解析请求参数
		String paras = "参数: {";
		Set<Entry<String, String[]>> set = controller.getParaMap().entrySet();
		for (Entry<String, String[]> entry : set) {
			paras += "{" + entry.getKey() + "=";
			String[] values = entry.getValue();
			for (String string : values) {
				paras += string;
			}
			paras += "}";
		}
		paras += "}";
		message = paras + result;
		// 获取请求IP地址，链接和用户
		HttpServletRequest request = controller.getRequest();
		String ip = request.getRemoteAddr();
		String url = request.getRequestURI();
		User user = TokenBox.get(controller.getPara(TokenBox.TOKEN_ID_KEY_NAME), UserController.SESSION_KEY_LOGIN_USER);
		Integer userId = null;
		if (user != null) {
			userId = user.getId();
		}
		actionLogService.add(userId, ip, message, resultCode, url);
	}

}
