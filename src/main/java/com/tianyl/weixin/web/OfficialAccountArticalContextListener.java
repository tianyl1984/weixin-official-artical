package com.tianyl.weixin.web;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.tianyl.core.ioc.ApplicationContext;
import com.tianyl.core.util.log.LogManager;
import com.tianyl.weixin.service.ArticalService;

@WebListener
public class OfficialAccountArticalContextListener implements ServletContextListener {

	private Timer crawlTimer = new Timer();

	private Timer offlineTimer = new Timer();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		crawlTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				LogManager.log("start crawl job");
				try {
					ApplicationContext.getBean(ArticalService.class).crawl();
				} catch (Exception e) {
					LogManager.log(e);
				}
				LogManager.log("end crawl job");
			}
		}, 10 * 1000, 1000 * 60 * 60 * 10);// 20小时执行一次
		offlineTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				LogManager.log("start offline job");
				try {
					ApplicationContext.getBean(ArticalService.class).offlineArtical();
				} catch (Exception e) {
					LogManager.log(e);
				}
				LogManager.log("end offline job");
			}
		}, 50 * 1000, 1000 * 60 * 60 * 10);// 10小时执行一次
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		crawlTimer.cancel();
		offlineTimer.cancel();
	}

}
