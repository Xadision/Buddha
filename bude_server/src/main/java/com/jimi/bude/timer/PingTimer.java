package com.jimi.bude.timer;

import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.jimi.bude.pack.PingPackage;
import com.jimi.bude.socket.ArmClient;
import com.jimi.bude.socket.Bude;


/**
 * Ping定时器：搜索在线的中转端并进行ping操作，判断是否掉线
 * @type PingTimer
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月10日
 */
public class PingTimer {

	// 两份钟ping一次
	private final static Integer DELAY_TIME = 60000;
	private final static Integer PERIOD_TIME = 10000;
	private static Timer timer = new Timer();


	public static void start() {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (Bude.armClientMap != null && Bude.armClientMap.size() > 0) {
					for (Entry<String, ArmClient> entry : Bude.armClientMap.entrySet()) {
						PingPackage ping = new PingPackage();
						System.err.println("Ping " + entry.getKey());
						entry.getValue().sendPingPackage(ping);
					}
				}

			}
		}, DELAY_TIME, PERIOD_TIME);
	}


	public static void stop() {
		timer.cancel();
	}
}
