package com.tianyl.core.ioc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tianyl.core.ioc.annotation.Autowired;
import com.tianyl.core.ioc.annotation.Component;
import com.tianyl.core.ioc.annotation.Service;
import com.tianyl.core.mvc.annotation.Controller;
import com.tianyl.core.util.clazz.ClassUtil;

public class ApplicationContext {

	private static Map<Class<?>, Object> BEAN_MAP = new HashMap<Class<?>, Object>();

	static {
		init();
	}

	private ApplicationContext() {

	}

	private static void init() {
		List<Class<?>> clazzList = ClassUtil.getClassList("com.tianyl", true, Controller.class);
		clazzList.addAll(ClassUtil.getClassList("com.tianyl", true, Component.class));
		clazzList.addAll(ClassUtil.getClassList("com.tianyl", true, Service.class));
		for (Class<?> clazz : clazzList) {
			if (BEAN_MAP.containsKey(clazz)) {
				continue;
			}
			Object obj = initBean(clazz);
			BEAN_MAP.put(clazz, obj);
		}
	}

	private static Object initBean(Class<?> clazz) {
		try {
			Object result = clazz.newInstance();
			Field[] fs = clazz.getDeclaredFields();
			for (Field field : fs) {
				Autowired autowired = field.getAnnotation(Autowired.class);
				if (autowired == null) {
					continue;
				}
				Object value = null;
				Class<?> fieldClazz = field.getType();
				if (BEAN_MAP.containsKey(fieldClazz)) {
					value = BEAN_MAP.get(fieldClazz);
				}
				if (value == null) {
					value = initBean(fieldClazz);
				}
				field.setAccessible(true);
				field.set(result, value);
			}
			return result;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) {
		return (T) BEAN_MAP.get(clazz);
	}

}
