package com.tianyl.core.util.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {

	private static Logger logger = null;

	private static synchronized Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger("film-manage");
			try {
				String logFile = System.getProperty("user.home") + File.separator + "logs" + File.separator + "weixin-official.log";
				File fileDir = new File(logFile).getParentFile();
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				FileHandler handler = new FileHandler(logFile, true);
				handler.setEncoding("utf-8");
				handler.setFormatter(new SimpleFormatter());
				logger.addHandler(handler);
				logger.setLevel(Level.ALL);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return logger;
	}

	public static void log(String msg) {
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		String sourceClass = "";
		String sourceMethod = "";
		if (stackElements.length > 1) {
			sourceClass = stackElements[1].getClassName();
			sourceMethod = "method:" + stackElements[1].getMethodName() + " line:" + stackElements[1].getLineNumber();
		}
		getLogger().logp(Level.INFO, sourceClass, sourceMethod, msg);
	}

	public static void log(Exception e) {
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		String sourceClass = "";
		String sourceMethod = "";
		if (stackElements.length > 1) {
			sourceClass = stackElements[1].getClassName();
			sourceMethod = "method:" + stackElements[1].getMethodName() + " line:" + stackElements[1].getLineNumber();
		}
		getLogger().logp(Level.SEVERE, sourceClass, sourceMethod, e.getMessage(), e);
	}
}
