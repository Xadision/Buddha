package com.jimi.bude.redis;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jimi.bude.entity.ControlType;


/**
 * redis操作
 * @type ControlRedisDao
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2019年1月9日
 */
public class ControlRedisDao {

	public static Cache controlCache = Redis.use("control");
	
	private static String UPDATE_SUFFIX = "_update";
	
	private static String RESTRAT_SUFFIX = "_restart";


	/**
	 * 添加控制包的唯一识别码及回复状态，识别码由中转端，设备端，控制包类型决定
	 * @param armName
	 * @param fingerName
	 * @param type
	 * @param status
	 * @return
	 */
	public synchronized static boolean put(String armName, String fingerName, ControlType type, int status) {
		String key = armName + "_" + fingerName;
		switch (type) {
		case UPDATE:
			key = key + UPDATE_SUFFIX;
			break;
		case RESTART:
			key = key + RESTRAT_SUFFIX;
			break;
		default:
			break;
		}
		try {
			controlCache.set(key, status);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * 获取控制包回复状态
	 * @param armName
	 * @param fingerName
	 * @param type
	 * @return
	 */
	public synchronized static Integer get(String armName, String fingerName, ControlType type) {
		String key = armName + "_" + fingerName;
		switch (type) {
		case UPDATE:
			key = key + UPDATE_SUFFIX;
			break;
		case RESTART:
			key = key + RESTRAT_SUFFIX;
			break;
		default:
			break;
		}
		Integer value = null;
		try {
			value = controlCache.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}


	/**
	 * 删除
	 * @param armName
	 * @param fingerName
	 * @param type
	 */
	public synchronized static void del(String armName, String fingerName, ControlType type) {
		String key = armName + "_" + fingerName;
		switch (type) {
		case UPDATE:
			key = key + UPDATE_SUFFIX;
			break;
		case RESTART:
			key = key + RESTRAT_SUFFIX;
			break;
		default:
			break;
		}
		controlCache.del(key);
	}


	/**
	 * 判断是否存在
	 * @param armName
	 * @param fingerName
	 * @param type
	 * @return
	 */
	public synchronized static boolean isExist(String armName, String fingerName, ControlType type) {
		String key = armName + "_" + fingerName;
		switch (type) {
		case UPDATE:
			key = key + UPDATE_SUFFIX;
			break;
		case RESTART:
			key = key + RESTRAT_SUFFIX;
			break;
		default:
			break;
		}
		boolean flag = false;
		try {
			flag = controlCache.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}


	/**
	 * 清空
	 */
	public synchronized static void clear() {
		try {
			controlCache.flushAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void main(String[] args) {
		RedisPlugin rp = new RedisPlugin("control", "localhost", "newttl1234!@#$");
		// 与web下唯一区别是需要这里调用一次start()方法
		rp.start();
		controlCache = Redis.use("control");
		ControlRedisDao.put("AAA", "String", ControlType.UPDATE, 20);
		Integer b = ControlRedisDao.get("CCC", "String", ControlType.UPDATE);
		/*
		 * if (b == null) { System.out.println("c"); }
		 */
		System.out.println(b);
	}
}
