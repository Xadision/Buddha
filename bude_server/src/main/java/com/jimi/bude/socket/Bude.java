package com.jimi.bude.socket;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.jimi.bude.entity.ControllType;
import com.jimi.bude.entity.Infomation;
import com.jimi.bude.model.Arm;
import com.jimi.bude.model.Finger;
import com.jimi.bude.pack.CallBackPackage;
import com.jimi.bude.pack.CallBackReplyPackage;
import com.jimi.bude.pack.LoginPackage;
import com.jimi.bude.pack.LoginReplyPackage;
import com.jimi.bude.pack.PingPackage;
import com.jimi.bude.pack.PingReplyPackage;
import com.jimi.bude.pack.PongPackage;
import com.jimi.bude.pack.PongReplyPackage;
import com.jimi.bude.pack.UpdatePackage;
import com.jimi.bude.pack.UpdateReplyPackage;
import com.jimi.bude.redis.ControllRedisDao;
import com.jimi.bude.service.ArmService;
import com.jimi.bude.service.FingerService;
import com.jimi.bude.service.SocketLogService;
import com.jimi.bude.util.CommonUtil;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.jiminal.callback.JiminalServerCallback;
import cc.darhao.jiminal.config.PackageConfig;
import cc.darhao.jiminal.pack.BasePackage;
import cc.darhao.jiminal.socket.Jiminal;
import cc.darhao.jiminal.socket.JiminalServer;

/**
 * BudeSocket监听器
 * @type Bude
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年10月8日
 */
public class Bude {
	public static Bude me;
	private static JiminalServer BudeSocket;
	private static final int ARM_TYPE = 0;
	private static final int FINGER_TYPE = 1;
	private static final int DEFAULE_PACKAGE_ID = 0;
	private static final int SUCCEED = 20;
	private static final int FAIL = 41;
	private static final String DEFAULT_FINGER_NAME = "---";
	private static String loaclhost;
	private static List<Jiminal> connectList;
	public static Map<String, ArmClient> armClientMap;
	public static Map<Jiminal, String> jiminalMap;
	public static Map<Short, Infomation> controllMap;
	public static Map<Short, CountDownLatch> countDownMap;

	private static PackageConfig config;
	private static ArmService armService = ArmService.me;
	private static FingerService fingerService = FingerService.me;
	private static SocketLogService socketLogService = SocketLogService.me;
	static {
		config = new PackageConfig();
		config.add(LoginPackage.class, false);
		config.add(CallBackPackage.class, false);
		config.add(PongPackage.class, false);
		config.add(UpdatePackage.class, true);
		config.add(PingPackage.class, true);

		jiminalMap = new ConcurrentHashMap<Jiminal, String>();
		armClientMap = new ConcurrentHashMap<String, ArmClient>();
		controllMap = new ConcurrentHashMap<Short, Infomation>();
		countDownMap = new ConcurrentHashMap<Short, CountDownLatch>();
		connectList = new ArrayList<>();
		BudeSocket = null;
		try {
			loaclhost = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		me = new Bude();
	}

	private Bude() {
	}

	/**
	 * 配置Socket监听器
	 * @param port 监听端口
	 */
	public static void setConfig(int port) {
		if (BudeSocket == null) {
			BudeSocket = new JiminalServer(port, config, new JiminalServerCallback() {

				@Override
				public void onReplyArrived(BasePackage r, Jiminal session) {
					if (r instanceof UpdateReplyPackage) {
						// 接收到更新回复包
						socketLogService.add(r, UpdateReplyPackage.class, session.getRemoteIp(), loaclhost);
						UpdateReplyPackage receive = (UpdateReplyPackage) r;
						short controllId = (short) receive.getControllId();
						CountDownLatch countDownLatch = countDownMap.get(controllId);
						if (countDownLatch != null) {
							if (receive.getResultCode() == SUCCEED) {
								countDownLatch.countDown();
							}
						}
					} else if (r instanceof PingReplyPackage) {
						// 接收到Ping回复包
						socketLogService.add(r, PingReplyPackage.class, session.getRemoteIp(), loaclhost);
						String armName = jiminalMap.get(session);
						if (armName != null) {
							Arm arm = armService.select(armName);
							if (arm != null) {
								arm.setLastPingTime(new Date());
								armService.update(arm);
							}
						}
					}
				}

				@Override
				public void onPackageArrived(BasePackage p, BasePackage r, Jiminal session) {
					if (p instanceof LoginPackage) {
						// 接收到登录包
						socketLogService.add(p, LoginPackage.class, session.getRemoteIp(), loaclhost);
						LoginPackage receive = (LoginPackage) p;
						LoginReplyPackage reply = (LoginReplyPackage) r;
						if (receive.getType() == ARM_TYPE) {
							// 中转端登录
							String armName = receive.getArmName();
							Arm arm = armService.select(armName);
							reply.setFingerName(DEFAULT_FINGER_NAME);
							reply.setPackageId(DEFAULE_PACKAGE_ID);
							reply.setType(ARM_TYPE);
							reply.setResultCode(FAIL);
							System.err.println("中转端：" + armName + " 开始登录");
							if (arm != null) {
								ArmClient armClient = new ArmClient(armName, session);
								armClientMap.put(armName, armClient);
								jiminalMap.put(session, armName);
								synchronized (connectList) {
									connectList.remove(session);
								}
								arm.setIp(session.getRemoteIp());
								arm.setLastPingTime(new Date());
								arm.update();
								reply.setResultCode(SUCCEED);
								System.err.println("中转端：" + armName + " 登录成功");
							}
						} else {
							// 设备端登录
							String armName = receive.getArmName();
							String fingerName = receive.getFingerName();
							Finger finger = fingerService.select(fingerName, armName);
							reply.setFingerName(fingerName);
							reply.setPackageId(receive.getPackageId());
							reply.setType(FINGER_TYPE);
							reply.setResultCode(FAIL);
							System.err.println("中转端：" + armName + " 设备端：" + fingerName + " 开始登录");
							if (armClientMap.get(armName) != null) {
								if (finger != null) {
									String ip = CommonUtil.parseBytesToXRadixString(BytesParser.parseHexStringToBytes(receive.getFingerIp()), 10);
									ip = ip.replace(" ", ".");
									finger.setIp(ip);
									finger.setLastPingTime(new Date());
									fingerService.update(finger);
									reply.setResultCode(SUCCEED);
									System.err.println("中转端：" + armName + " 设备端：" + fingerName + " 登录成功");
								}
							}
						}
						socketLogService.add(r, LoginReplyPackage.class, loaclhost, session.getRemoteIp());
					} else if (p instanceof PongPackage) {
						// 接收到Pong包
						socketLogService.add(p, PongPackage.class, session.getRemoteIp(), loaclhost);
						PongPackage receive = (PongPackage) p;
						String armName = jiminalMap.get(session);
						if (armName != null) {
							String fingerName = receive.getFingerName();
							Finger finger = fingerService.select(fingerName, armName);
							if (finger != null) {
								finger.setLastPingTime(new Date());
								fingerService.update(finger);
							}
						}
						socketLogService.add(r, PongReplyPackage.class, loaclhost, session.getRemoteIp());
					} else if (p instanceof CallBackPackage) {
						// 接收到CallBack包
						socketLogService.add(p, CallBackPackage.class, session.getRemoteIp(), loaclhost);
						CallBackPackage receive = (CallBackPackage) p;
						CallBackReplyPackage reply = (CallBackReplyPackage) r;

						System.err.println("接收到callBack包" + receive.getType() + "  " + receive.getStatus());
						if (receive.getType() == ARM_TYPE) {
							reply.setPackageId(receive.getPackageId());
							reply.setFingerName(receive.getFingerName());
							reply.setType(ARM_TYPE);
						} else {
							reply.setPackageId(receive.getPackageId());
							reply.setFingerName(receive.getFingerName());
							reply.setType(FINGER_TYPE);
						}
						short controllId = (short) receive.getControllId();
						int status = receive.getStatus();
						Infomation info = controllMap.get(controllId);
						String armName = info.getArmName();
						String fingerName = info.getFingerName();
						switch (info.getType()) {
						// CallBack包对应更新请求
						case UPDATE:
							ControllRedisDao.put(armName, fingerName, ControllType.UPDATE, status);
							if (status == 26) {
								Finger finger = fingerService.select(fingerName, armName);
								finger.setBeadId(info.getBeadId());
								// 更新成功时更新设备端软件包版本
								fingerService.update(finger);
							}
							break;
						// CallBack包对应重启请求
						case RESTART:
							ControllRedisDao.put(armName, fingerName, ControllType.RESTART, status);
							break;
						default:
							break;
						}
						socketLogService.add(r, CallBackReplyPackage.class, loaclhost, session.getRemoteIp());
					}
				}

				@Override
				public void onCatchException(Exception e, Jiminal session) {
					// 捕获到异常
					e.printStackTrace();
					// 删除jiminalMap，socketMap和connectList的相应记录
					String name = jiminalMap.get(session);
					if (name != null) {
						jiminalMap.remove(session);
						armClientMap.remove(name);
					} else {
						synchronized (connectList) {
							connectList.remove(session);
						}
					}
					session.close();
				}

				@Override
				public void onCatchClient(Jiminal session) {
					// 捕获到客户端连接，添加进connectList
					synchronized (connectList) {
						connectList.add(session);
					}
				}
			});
		}
	}

	/**
	 * 开启监听器
	 */
	public void start() {
		new Thread(() -> {
			BudeSocket.listenConnect();
		}).start();
	}

	/**
	 * 停止监听器
	 */
	public void stop() {
		BudeSocket.close();
	}

	/**
	 * 获取本地IP地址
	 * @return
	 */
	public String getLoaclhost() {
		return loaclhost;
	}
}
