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

	private Timer timer = new Timer();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				LogManager.log("start job");
				try {
					ApplicationContext.getBean(ArticalService.class).crawl();
				} catch (Exception e) {
					LogManager.log(e);
				}
				LogManager.log("end job");
			}
		}, 10 * 1000, 1000 * 60 * 60 * 20);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		timer.cancel();
	}

}
