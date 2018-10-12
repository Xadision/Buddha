package com.jimi.bude.arm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.jimi.bude.arm.pack.CallBackPackage;
import com.jimi.bude.arm.pack.CallBackReplyPackage;
import com.jimi.bude.arm.pack.LoginPackage;
import com.jimi.bude.arm.pack.LoginReplyPackage;
import com.jimi.bude.arm.pack.PingPackage;
import com.jimi.bude.arm.pack.PingReplyPackage;
import com.jimi.bude.arm.pack.PongPackage;
import com.jimi.bude.arm.pack.UpdatePackage;
import com.jimi.bude.arm.pack.UpdateReplyPackage;

import cc.darhao.jiminal.callback.JiminalServerCallback;
import cc.darhao.jiminal.config.PackageConfig;
import cc.darhao.jiminal.pack.BasePackage;
import cc.darhao.jiminal.socket.Jiminal;
import cc.darhao.jiminal.socket.JiminalServer;

/**
 * 中转监听端：用于监听设备端连接
 * @type ArmServer
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月25日
 */
public class ArmServer {

	private static final int FINGER_RECIEVE_UPDATE_SUCCEED = 27;
	private static final int FINGER_RECIEVE_UPDATE_FAIL = 28;
	private static final int DEFAULT_PACKAGE_ID = 0;
	private static final int SUCCEED = 20;
	private static final int MAX_REPLY_TIME = 5000;
	private static final int FINGER_TYPE = 1;
	private JiminalServer jiminalServer;
	private List<Jiminal> lists;
	private static short packageId;
	public static Map<Jiminal, String> jiminalMap;
	public static Map<String, FingerClient> fingerMap;
	private static PackageConfig config;
	private ArmClient armClient;

	static {
		config = new PackageConfig();
		config.add(LoginPackage.class, false);
		config.add(CallBackPackage.class, false);
		config.add(UpdatePackage.class, true);
		config.add(PingPackage.class, true);
		jiminalMap = new ConcurrentHashMap<Jiminal, String>();
		fingerMap = new ConcurrentHashMap<String, FingerClient>();
		packageId = DEFAULT_PACKAGE_ID;
	}

	public ArmServer(Integer listenPort) {
		lists = new ArrayList<Jiminal>();
		armClient = ArmClient.getInstance();
		jiminalServer = new JiminalServer(listenPort, config, new JiminalServerCallback() {

			@Override
			public void onReplyArrived(BasePackage r, Jiminal session) {
				if (r instanceof UpdateReplyPackage) {
					String fingerName = jiminalMap.get(session);
					if (fingerName != null) {
						UpdateReplyPackage reply = (UpdateReplyPackage) r;
						if (reply.getResultCode() == SUCCEED) {
							armClient.sendCallBackPackage(reply.getControllId(), DEFAULT_PACKAGE_ID, FINGER_RECIEVE_UPDATE_SUCCEED, fingerName, FINGER_TYPE);
						} else {
							armClient.sendCallBackPackage(reply.getControllId(), DEFAULT_PACKAGE_ID, FINGER_RECIEVE_UPDATE_FAIL, fingerName, FINGER_TYPE);
						}
					}
				} else if (r instanceof PingReplyPackage) {
					String fingerName = jiminalMap.get(session);
					PongPackage pong = new PongPackage();
					pong.setFingerName(fingerName);
					armClient.sendPongPackage(pong);
					System.out.println(fingerName +"ping通");
				}

			}

			@Override
			public void onPackageArrived(BasePackage p, BasePackage r, Jiminal session) {
				if (p instanceof LoginPackage) {
					LoginPackage receive = (LoginPackage) p;
					// 添加登录jiminal映射Map
					String fingerName = receive.getFingerName();
					FingerClient finger = new FingerClient(fingerName, session);
					ArmServer.fingerMap.put(fingerName, finger);
					jiminalMap.put(session, fingerName);

					synchronized (lists) {
						lists.remove(session);
					}
					// 填充转发的登录包
					short tranPackageId = genPackageId();
					LoginPackage login = new LoginPackage();
					login.setPackageId(tranPackageId);
					login.setArmName(receive.getArmName());
					login.setFingerName(receive.getFingerName());
					login.setFingerIp(receive.getFingerIp());
					login.setType(receive.getType());
					System.out.println("fingerIp:"+receive.getFingerIp() + receive.getFingerName() + receive.getArmName() + "    "+packageId);
					try {
						CountDownLatch countDownLatch = new CountDownLatch(1);
						ArmClient.replyCountMap.put(tranPackageId, countDownLatch);
						// 发送转发登录包
						armClient.sendLoginPackage(login);
						countDownLatch.await(MAX_REPLY_TIME, TimeUnit.MILLISECONDS);
						LoginReplyPackage armRecieveReply = (LoginReplyPackage) ArmClient.replyPackage.get(tranPackageId);
						if (armRecieveReply != null) {
							LoginReplyPackage reply = (LoginReplyPackage) r;
							reply.setPackageId(receive.getPackageId());
							reply.setFingerName(armRecieveReply.getFingerName());
							reply.setResultCode(armRecieveReply.getResultCode());
							reply.setType(armRecieveReply.getType());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (p instanceof CallBackPackage) {
					CallBackPackage receive = (CallBackPackage) p;
					short tranPackageId = genPackageId();
					try {
						CountDownLatch countDownLatch = new CountDownLatch(1);
						ArmClient.replyCountMap.put(tranPackageId, countDownLatch);
						armClient.sendCallBackPackage(receive.getControllId(), (int) tranPackageId, receive.getStatus(), receive.getFingerName(), receive.getType());
						countDownLatch.await(MAX_REPLY_TIME, TimeUnit.MILLISECONDS);
						CallBackReplyPackage armRecieveReply = (CallBackReplyPackage) ArmClient.replyPackage.get(tranPackageId);
						if (armRecieveReply != null) {
							CallBackReplyPackage reply = (CallBackReplyPackage) r;
							reply.setFingerName(armRecieveReply.getFingerName());
							reply.setType(armRecieveReply.getType());
							reply.setPackageId(receive.getPackageId());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onCatchException(Exception e, Jiminal session) {
				e.printStackTrace();
				String fingerName = jiminalMap.get(session);
				if (fingerName != null) {
					System.out.println("fingerName : " + fingerName + "已断开");
					jiminalMap.remove(session);
					ArmServer.fingerMap.remove(fingerName);
				} else {
					synchronized (lists) {
						lists.remove(session);
					}

				}
				session.close();
			}

			@Override
			public void onCatchClient(Jiminal session) {
				synchronized (lists) {
					if (!lists.contains(session)) {
						lists.add(session);
					}
				}

			}
		});
	}

	/**
	 * 生成登录包和CallBack的packageId，用于转发标识
	 * @return
	 */
	public synchronized Short genPackageId() {
		packageId %= 65536;
		packageId++;
		return packageId;
	}

	/**
	 * 启动SocketServer开始监听
	 */
	public void start() { 
		new Thread(() -> {
			jiminalServer.listenConnect();
		}).start();
		;
	}

	/**
	 * 停止监听
	 */
	public void stop() {
		jiminalServer.close();
	}
}
