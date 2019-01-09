package com.jimi.bude.config;

import java.util.Map.Entry;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jimi.bude.controller.ArmController;
import com.jimi.bude.controller.BeadController;
import com.jimi.bude.controller.BodyController;
import com.jimi.bude.controller.FaceController;
import com.jimi.bude.controller.FingerController;
import com.jimi.bude.controller.HeadController;
import com.jimi.bude.controller.UserController;
import com.jimi.bude.interceptor.AccessInterceptor;
import com.jimi.bude.interceptor.ActionLogInterceptor;
import com.jimi.bude.interceptor.CORSInterceptor;
import com.jimi.bude.interceptor.ErrorLogInterceptor;
import com.jimi.bude.model._MappingKit;
import com.jimi.bude.socket.ArmClient;
import com.jimi.bude.socket.Bude;
import com.jimi.bude.timer.PingTimer;
import com.jimi.bude.util.TokenBox;


public class MainConfig extends JFinalConfig {

	/**
	 * 配置JFinal常量
	 */
	@Override
	public void configConstant(Constants me) {
		// 读取数据库配置文件
		PropKit.use("config.properties");
		// 设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		// 设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath("BEAD/");
		// 设置上传最大限制尺寸
		me.setMaxPostSize(1024 * 1024 * 1000);
		// 设置默认下载文件路径 renderFile使用
		me.setBaseDownloadPath("BEAD/");
		// 设置默认视图类型
		me.setViewType(ViewType.JFINAL_TEMPLATE);
		// 设置404渲染视图
		// me.setError404View("404.html");
		me.setJsonFactory(new MixedJsonFactory());

	}


	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		me.add("/user", UserController.class);
		me.add("/bead", BeadController.class);
		me.add("/arm", ArmController.class);
		me.add("/body", BodyController.class);
		me.add("/finger", FingerController.class);
		me.add("/face", FaceController.class);
		me.add("/head", HeadController.class);
	}


	/**
	 * 配置JFinal插件
	 * 数据库连接池
	 * ORM
	 * 缓存等插件
	 * 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		// 配置数据库连接池插件
		DruidPlugin dbPlugin = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"), PropKit.get("driver"));
		// orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dbPlugin);
		arp.setShowSql(PropKit.getBoolean("devMode"));
		arp.setDialect(new MysqlDialect());
		RedisPlugin controlRedis = new RedisPlugin(PropKit.get("redisName"), PropKit.get("redisIp"), PropKit.get("redisPassword"));
		/********在此添加数据库 表-Model 映射*********/
		// 如果使用了JFinal Model 生成器 生成了BaseModel 把下面注释解开即可
		_MappingKit.mapping(arp);

		// 添加到插件列表中
		me.add(dbPlugin);
		me.add(arp);
		me.add(controlRedis);
	}


	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new CORSInterceptor());
		me.addGlobalActionInterceptor(new SessionInViewInterceptor());
		me.addGlobalActionInterceptor(new ActionLogInterceptor());
		me.addGlobalActionInterceptor(new ErrorLogInterceptor());
		me.addGlobalActionInterceptor(new AccessInterceptor());
		me.addGlobalServiceInterceptor(new Tx());
	}


	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
	}


	/**
	 * JFinal启动后调用
	 */
	@Override
	public void afterJFinalStart() {
		try {
			TokenBox.start(PropKit.getInt("sessionTimeout"));
			Bude.setConfig(PropKit.getInt("listenPort"));
			Bude bude = Bude.me;
			bude.start();
			System.out.println("Bude Socket Server is running");
			PingTimer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Bude Server is running");
	}


	/**
	 * JFinal Stop之前调用 
	 */
	@Override
	public void beforeJFinalStop() {
		Bude bude = Bude.me;
		bude.stop();
		for (Entry<String, ArmClient> entry : Bude.armClientMap.entrySet()) {
			entry.getValue().stop();
		}
		PingTimer.stop();

	}


	/**
	 * 配置模板引擎 
	 */
	@Override
	public void configEngine(Engine me) {
		// 这里只有选择JFinal TPL的时候才用
		// 配置共享函数模板
		// me.addSharedFunction("/view/common/layout.html")
	}


	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/", 5);
	}

}