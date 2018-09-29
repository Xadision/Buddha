package com.jimi.bude.arm;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.jimi.bude.arm.pack.CallBackPackage;
import com.jimi.bude.arm.pack.CallBackReplyPackage;
import com.jimi.bude.arm.pack.LoginPackage;
import com.jimi.bude.arm.pack.LoginReplyPackage;
import com.jimi.bude.arm.pack.PingPackage;
import com.jimi.bude.arm.pack.PongPackage;
import com.jimi.bude.arm.pack.PongReplyPackage;
import com.jimi.bude.arm.pack.UpdatePackage;
import com.jimi.bude.arm.pack.UpdateReplyPackage;
import com.jimi.bude.arm.util.FileUtil;
import com.jimi.bude.arm.util.HttpUtil;

import cc.darhao.jiminal.callback.JiminalCallback;
import cc.darhao.jiminal.config.PackageConfig;
import cc.darhao.jiminal.config.SocketConfig;
import cc.darhao.jiminal.pack.BasePackage;
import cc.darhao.jiminal.socket.Jiminal;

/**
 * 中转端Socekt(用于连接Bude服务器)
 * @type ArmClient
 * @Company 几米物联技术有限公司-自动化部
 * @author 汤如杰
 * @date 2018年9月25日
 */
public class ArmClient {

	private static final String DEFAULT_FINGER_NAME = "---";
	private static final String UNLESS_IP = "0 0 0 0";
	private static final int DOWNLOAD_BEAD_SUCCEED = 20;
	private static final int USE_LOCAL_CACHE = 22;
	private static final int CHECKING_BEAD_MD5 = 24;
	private static final int CAN_NOT_FIND_BEAD = 41;
	private static final int CHECK_BEAD_MD5_FAIL = 42;
	private static final int DEFAULT_PACKAGE_ID = 0;
	private static final int MAX_DOWNLOAD_COUNT = 2;
	private static final int ARM_TYPE = 0;
	private static final int SUCCEED = 20;
	private static final int FAIL = 41;
	private static final int  LOGIN_TIME = 3000;
	private static PackageConfig packageConfig;
	private static ArmClient me;

	public static Map<Short, BasePackage> replyPackage;
	public static Map<Short, CountDownLatch> replyCountMap;

	private Jiminal jiminal;
	private String  url;
	private String downloadPath;
	private String armName;
	
	static {
		packageConfig = new PackageConfig();
		packageConfig.add(LoginPackage.class, true);
		packageConfig.add(CallBackPackage.class, true);
		packageConfig.add(PongPackage.class, true);
		packageConfig.add(PingPackage.class, false);
		packageConfig.add(UpdatePackage.class, false);
		replyPackage = new ConcurrentHashMap<Short, BasePackage>();
		replyCountMap = new ConcurrentHashMap<Short, CountDownLatch>();
		me = new ArmClient();
	}

	public static ArmClient getInstance() {
		return me;
	}
	
	private ArmClient() {

	}

	/**
	 * 配置中转端Socket(需先行配置才可启动)
	 * @param ip 远程连接服务器Ip地址
	 * @param port 远程服务器端口号
	 * @param maxReplyTime 超时时间
	 * @param armName 中转端名称
	 * @param downloadPath 下载基本路径，末尾不加“\”(软件包下载路径将为：基本路径/项目名/模块名/主版本号/次版本号/修正版本号)
	 * @param url 软件包下载连接，不含参数
	 */
	public void setConfig(String ip, Integer port, Integer maxReplyTime, String armName, String downloadPath, String url) {
		this.armName = armName;
		if (downloadPath == null) {
			downloadPath = "";
		}
		this.downloadPath = downloadPath + File.separator + "BEAD";
		this.url = url;
		jiminal = new Jiminal(ip, port, packageConfig, new JiminalCallback() {

			@Override
			public void onReplyArrived(BasePackage r, Jiminal session) {
				if (r instanceof LoginReplyPackage) {
					LoginReplyPackage reply = (LoginReplyPackage) r;
					if (reply.getType() == ARM_TYPE) {
						// 接收到中转端登录回复包
						if (reply.getResultCode() != SUCCEED) {
							try {
								Thread.sleep(LOGIN_TIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							LoginPackage login = new LoginPackage();
							login.setPackageId(DEFAULT_PACKAGE_ID);
							login.setArmName(armName);
							login.setType(ARM_TYPE);
							login.setFingerName(DEFAULT_FINGER_NAME);
							try {
								login.setFingerIp(Inet4Address.getLocalHost().getHostAddress());
							} catch (UnknownHostException e) {
								e.printStackTrace();
							}
							ArmClient.me.sendLoginPackage(login);
						}
					} else {
						// 接收到设备端登录回复包
						CountDownLatch countDownLatch = replyCountMap.get((short) reply.getPackageId());
						if (countDownLatch != null) {
							replyPackage.put((short) reply.getPackageId(), reply);
							countDownLatch.countDown();
						}
					}
				} else if (r instanceof CallBackReplyPackage) {
					CallBackReplyPackage reply = (CallBackReplyPackage) r;
					if (reply.getType() == ARM_TYPE) {
						// 接收到中转端CallBack回复包
					} else {
						// 接收到设备端CallBack回复包
						CountDownLatch countDownLatch = replyCountMap.get((short) reply.getPackageId());
						if (countDownLatch != null) {
							replyPackage.put((short) reply.getPackageId(), reply);
							countDownLatch.countDown();
						}
					}
				} else if (r instanceof PongReplyPackage) {
					//接收到Pong回复包
					
				}
			}

			@Override
			public void onPackageArrived(BasePackage p, BasePackage r, Jiminal session) {
				if (p instanceof UpdatePackage) {
					UpdatePackage recieve = (UpdatePackage) p;
					UpdateReplyPackage reply = (UpdateReplyPackage) r;
					String fingerName = recieve.getFingerName();
					FingerClient finger = ArmServer.socketMap.get(fingerName);
					Integer controllId = recieve.getControllId();
					//填充更新回复包
					reply.setControllId(controllId);
					if (finger == null) {
						reply.setResultCode(FAIL);
					}else {
						reply.setResultCode(SUCCEED);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								//下载次数，最多2次
								int downloadCount = 0;
								String headName = recieve.getHeadName();
								String faceName = recieve.getFaceName();
								Integer firstCode = recieve.getFirstCode();
								Integer secondCode = recieve.getSecondCode();
								Integer debugCode = recieve.getDebugCode();
								String suffixTime = recieve.getSuffixTime();
								System.out.println("suffixTime"+suffixTime);
								String md5 = recieve.getMd5().replace(" ", "");
								//填充转发给设备端的更新包
								UpdatePackage tranPackage = new UpdatePackage();
								tranPackage.setControllId(controllId);
								tranPackage.setHeadName(headName);
								tranPackage.setFaceName(faceName);
								tranPackage.setFingerName(fingerName);
								tranPackage.setFirstCode(firstCode);
								tranPackage.setSecondCode(secondCode);
								tranPackage.setDebugCode(debugCode);
								tranPackage.setSuffixTime(suffixTime);
								tranPackage.setMd5(recieve.getMd5());
								//拼接文件名和文件路径
								String fileName = FileUtil.getFileName(headName, faceName, firstCode.toString(), secondCode.toString(), debugCode.toString(), suffixTime);
								String filePath = FileUtil.getFilePath(getDownloadPath(), headName, faceName, firstCode.toString(), secondCode.toString(), debugCode.toString());
								synchronized (ArmClient.this.downloadPath) {
									File bead = FileUtil.findFile(filePath, fileName);
									if (bead != null) {
										String beadMd5 = FileUtil.getMD5(bead);
										//System.out.println("发送CallBack回复包:24，中转端校验Md5");
										sendCallBackPackage(controllId, DEFAULT_PACKAGE_ID, CHECKING_BEAD_MD5, DEFAULT_FINGER_NAME, ARM_TYPE);
										if (md5.toUpperCase().equals(beadMd5.toUpperCase())) {
											//System.out.println("发送CallBack回复包:22，使用中转端本地缓存");
											sendCallBackPackage(controllId,DEFAULT_PACKAGE_ID,USE_LOCAL_CACHE, DEFAULT_FINGER_NAME, ARM_TYPE);
											return ;
										}else {
											bead.delete();
										}
									}
									Map<String, String> args = new HashMap<String, String>();
									args.put("headName", headName);
									args.put("faceName", faceName);
									args.put("firstCode", firstCode.toString());
									args.put("secondCode", secondCode.toString());
									args.put("debugCode", debugCode.toString());
									args.put("suffixTime", suffixTime);
									while(downloadCount < MAX_DOWNLOAD_COUNT) {
										boolean flag = false;
										try {
											flag = HttpUtil.downLoadFile(getUrl(), args, filePath);
										} catch (IOException e) {
											e.printStackTrace();
											downloadCount++;
											continue;
										}
										if (!flag) {
											downloadCount++;
											continue;
										}
										//System.out.println("下载成功");
										bead = FileUtil.findFile(filePath, fileName);
										if (bead == null) {
											downloadCount++;
											continue;
										}
										//System.out.println("发送CallBack回复包:20，中转端下载成功");
										sendCallBackPackage(controllId,DEFAULT_PACKAGE_ID,DOWNLOAD_BEAD_SUCCEED, DEFAULT_FINGER_NAME, ARM_TYPE);
										String beadMd5 = FileUtil.getMD5(bead);
										//System.out.println("发送CallBack回复包:24，中转端校验Md5");
										sendCallBackPackage(controllId,DEFAULT_PACKAGE_ID,CHECKING_BEAD_MD5, DEFAULT_FINGER_NAME, ARM_TYPE);
										if (md5.toUpperCase().equals(beadMd5.toUpperCase())) {
											finger.sendUpdatePackage(tranPackage);
											//System.out.println("发送CallBack回复包:22");
											sendCallBackPackage(controllId,DEFAULT_PACKAGE_ID,USE_LOCAL_CACHE, DEFAULT_FINGER_NAME, ARM_TYPE);
											return ;
										}else {
											bead.delete();
											if (downloadCount == (MAX_DOWNLOAD_COUNT-1)) {
												//System.out.println("发送CallBack回复包:42，md5校验失败");
												sendCallBackPackage(controllId,DEFAULT_PACKAGE_ID,CHECK_BEAD_MD5_FAIL, DEFAULT_FINGER_NAME, ARM_TYPE);
												return;
											}
											downloadCount++;
											continue;
										}
									}
									//System.out.println("发送CallBack回复包:41，服务器端不存在软件包");
									sendCallBackPackage(controllId,DEFAULT_PACKAGE_ID,CAN_NOT_FIND_BEAD, DEFAULT_FINGER_NAME, ARM_TYPE);
								}
							}
						}).start();
					}

				} else if (p instanceof PingPackage) {
					//接收到ping包，转发ping包给客户端
					for (Entry<String, FingerClient> entry : ArmServer.socketMap.entrySet()) {
						PingPackage tranPackage = new PingPackage();
						entry.getValue().sendPingPackage(tranPackage);
					}
				}

			}

			@Override
			public void onCatchException(Exception e, Jiminal session) {
				e.printStackTrace();
			}

			@Override
			public void onConnected() {
				LoginPackage login = new LoginPackage();
				login.setPackageId(DEFAULT_PACKAGE_ID);
				login.setArmName(getArmName());
				login.setType(ARM_TYPE);
				login.setFingerName(DEFAULT_FINGER_NAME);
				login.setFingerIp(UNLESS_IP);
				ArmClient.me.sendLoginPackage(login);
			}
		});

		if (maxReplyTime != null) {
			SocketConfig socketConfig = new SocketConfig();
			socketConfig.setMaxReplyTime(maxReplyTime);
			jiminal.setSocketConfig(socketConfig);
		}
	}

	/**
	 * 启动并连接Socket
	 */
	public void start() {
		new Thread(() -> {
			jiminal.connect();
		}).start();
	}

	/**
	 * 发送登录包
	 * @param p登录包
	 */
	public void sendLoginPackage(final LoginPackage p) {
		jiminal.send(p);
	}

	/**
	 * 发送CallBack包
	 * @param p CallBack包
	 */
	public void sendCallBackPackage(Integer controllId, Integer packageId, Integer status, String fingerName, Integer type) {
		CallBackPackage callBack = new CallBackPackage();
		callBack.setControllId(controllId);
		callBack.setPackageId(packageId);
		callBack.setStatus(status);
		callBack.setFingerName(fingerName);
		callBack.setType(type);
		jiminal.send(callBack);
	}

	/**
	 * 发送Pong包
	 * @param p Pong包
	 */
	public void sendPongPackage(final PongPackage p) {
		jiminal.send(p);
	}

	/**
	 * 发送更新回复包
	 * @param p 更新回复包
	 */
	public void sendUpdateReplyPackage(UpdateReplyPackage p) {
		jiminal.send(p);
	}

	/**
	 * 停止并断开Socket连接
	 */
	public void stop() {
		jiminal.close();
	}

	/**
	 * 获取远程连接Ip地址
	 * @return
	 */
	public String getRemoteIp() {
		return jiminal.getRemoteIp();
	}

	/**
	 * 获取序列号
	 * @return
	 */
	public Integer getSerialNo() {
		return jiminal.getSerialNo();
	}

	public String getUrl() {
		return url;
	}

	public String getDownloadPath() {
		return downloadPath;
	}
	
	public String getArmName() {
		return armName;
	}
}
