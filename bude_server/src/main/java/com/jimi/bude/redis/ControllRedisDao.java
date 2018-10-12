package com.jimi.bude.redis;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jimi.bude.entity.ControllType;

public class ControllRedisDao {

	public static Cache controllCache = Redis.use("controll");
	private static String UPDATE_SUFFIX = "_update";
	private static String RESTRAT_SUFFIX = "_restart";

	public synchronized static boolean put(String armName, String fingerName, ControllType type, int status) {
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
			controllCache.set(key, status);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized static Integer get(String armName, String fingerName, ControllType type) {
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
			value = controllCache.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	public synchronized static void del(String armName, String fingerName, ControllType type) {
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
		controllCache.del(key);
	}

	public synchronized static boolean isExist(String armName, String fingerName, ControllType type) {
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
			flag = controllCache.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;

	}

	public synchronized static void clear() {
		try {
			controllCache.flushAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		RedisPlugin rp = new RedisPlugin("controll", "localhost", "newttl1234!@#$");
		// 与web下唯一区别是需要这里调用一次start()方法
		rp.start();
		controllCache = Redis.use("controll");
		ControllRedisDao.put("AAA", "String", ControllType.UPDATE, 20);
		Integer b = ControllRedisDao.get("CCC", "String", ControllType.UPDATE);
		/*
		 * if (b == null) { System.out.println("c"); }
		 */
		System.out.println(b);
	}
}
