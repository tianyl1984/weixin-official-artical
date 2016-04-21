package com.tianyl.core.mvc.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.tianyl.core.ioc.ApplicationContext;
import com.tianyl.core.mvc.annotation.Controller;
import com.tianyl.core.util.clazz.ClassUtil;
import com.tianyl.core.util.clazz.MethodParamNamesScaner;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -539366993155810143L;

	private Map<String, Object> controllerMap = new HashMap<String, Object>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		String basePackage = config.getInitParameter("basePackage");
		List<Class<?>> controllerClazz = ClassUtil.getClassList(basePackage, true, Controller.class);
		for (Class<?> clazz : controllerClazz) {
			try {
				Object controller = ApplicationContext.getBean(clazz);
				String path = clazz.getAnnotation(Controller.class).value();
				controllerMap.put(path, controller);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("init error", e);
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		commonSet(req, resp);
		String uri = req.getRequestURI();
		String ctxPath = req.getContextPath();
		String path = uri.substring(ctxPath.length(), uri.length());
		Object controller = null;
		String methodStr = null;
		for (String key : controllerMap.keySet()) {
			if (path.startsWith(key)) {
				controller = controllerMap.get(key);
				methodStr = path.substring(key.length() + 1, path.length());
			}
		}
		if (controller == null) {
			goto404(req, resp);
			return;
		}
		Method method = ClassUtil.getMethod(controller.getClass(), methodStr);
		if (method == null) {
			goto404(req, resp);
			return;
		}
		List<String> methodNames = MethodParamNamesScaner.getParamNames(method);
		Class<?>[] types = method.getParameterTypes();
		if (methodNames.size() != types.length) {
			goto500(req, resp, "get method error");
			return;
		}
		List<Object> params = new ArrayList<>();
		for (int i = 0; i < methodNames.size(); i++) {
			String mname = methodNames.get(i);
			Class<?> type = types[i];
			if (type == HttpServletRequest.class) {
				params.add(req);
			} else if (type == HttpServletResponse.class) {
				params.add(resp);
			} else {
				params.add(ClassUtil.getCastValue(req.getParameter(mname), type));
			}
		}
		Object result = null;
		try {
			result = method.invoke(controller, params.toArray());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			goto500(req, resp);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			goto500(req, resp);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			goto500(req, resp);
		}
		Class<?> retType = method.getReturnType();
		if (retType.getName().equals("void")) {
			return;
		}
		String ret = null;
		if (result.getClass() == String.class) {
			ret = result.toString();
		} else {
			ret = JSONObject.toJSONString(result);
		}
		resp.getWriter().println(ret);
	}

	private void commonSet(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
	}

	private void goto500(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendError(500);
	}

	private void goto500(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
		resp.sendError(500, msg);
	}

	private void goto404(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendError(404);
	}

}
