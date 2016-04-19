package com.tianyl.core.util.clazz;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.tianyl.core.util.StringUtil;

public class ClassUtil {

	public static void main(String[] args) {

	}

	public static List<Class<?>> getClassList(String pkgName, boolean isRecursive, Class<? extends Annotation> annotation) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			// 按文件的形式去查找
			String strFile = pkgName.replaceAll("\\.", "/");
			Enumeration<URL> urls = loader.getResources(strFile);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null) {
					String protocol = url.getProtocol();
					String pkgPath = url.getPath();
					if ("file".equals(protocol)) {
						// 本地代码
						findClassName(classList, pkgName, pkgPath, isRecursive, annotation);
					} else if ("jar".equals(protocol)) {
						// jar中的代码
						findClassName(classList, pkgName, url, isRecursive, annotation);
					} else {
						throw new RuntimeException("unknow protocol: " + protocol + ",url:" + url);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("read file error", e);
		}
		return classList;
	}

	private static void findClassName(List<Class<?>> clazzList, String pkgName, String pkgPath, boolean isRecursive, Class<? extends Annotation> annotation) {
		File[] files = filterClassFiles(pkgPath);// 过滤出.class文件及文件夹
		if (files != null) {
			for (File f : files) {
				String fileName = f.getName();
				if (f.isFile()) {
					String clazzName = getClassName(pkgName, fileName);
					addClassName(clazzList, clazzName, annotation);
				} else {
					// 文件夹的情况
					if (isRecursive) {
						String subPkgName = pkgName + "." + fileName;
						String subPkgPath = pkgPath + "/" + fileName;
						findClassName(clazzList, subPkgName, subPkgPath, true, annotation);
					}
				}
			}
		}
	}

	private static void findClassName(List<Class<?>> clazzList, String pkgName, URL url, boolean isRecursive, Class<? extends Annotation> annotation) throws IOException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarURLConnection.getJarFile();
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
			String clazzName = jarEntryName.replace("/", ".");
			int endIndex = clazzName.lastIndexOf(".");
			String prefix = null;
			if (endIndex > 0) {
				String prefix_name = clazzName.substring(0, endIndex);
				endIndex = prefix_name.lastIndexOf(".");
				if (endIndex > 0) {
					prefix = prefix_name.substring(0, endIndex);
				}
			}
			if (prefix != null && jarEntryName.endsWith(".class")) {
				if (prefix.equals(pkgName)) {
					addClassName(clazzList, clazzName, annotation);
				} else if (isRecursive && prefix.startsWith(pkgName)) {
					// 遍历子包名：子类
					addClassName(clazzList, clazzName, annotation);
				}
			}
		}
	}

	private static File[] filterClassFiles(String pkgPath) {
		return new File(pkgPath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
	}

	private static String getClassName(String pkgName, String fileName) {
		int endIndex = fileName.lastIndexOf(".");
		String clazz = null;
		if (endIndex >= 0) {
			clazz = fileName.substring(0, endIndex);
		}
		String clazzName = null;
		if (clazz != null) {
			clazzName = pkgName + "." + clazz;
		}
		return clazzName;
	}

	private static void addClassName(List<Class<?>> clazzList, String clazzName, Class<? extends Annotation> annotation) {
		if (clazzList != null && clazzName != null) {
			Class<?> clazz = null;
			try {
				clazz = Class.forName(clazzName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			if (clazz != null) {
				if (annotation == null) {
					clazzList.add(clazz);
				} else if (clazz.isAnnotationPresent(annotation)) {
					clazzList.add(clazz);
				}
			}
		}
	}

	public static Method getMethod(Class<?> clazz, String methodStr) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (methodStr.equals(method.getName())) {
				return method;
			}
		}
		return null;
	}

	public static Object getCastValue(String parameter, Class<?> type) {
		if (type.isPrimitive()) {
			if (type.getName().equals("int")) {
				return StringUtil.isBlank(parameter) ? 0 : Integer.valueOf(parameter.trim()).intValue();
			}
			if (type.getName().equals("long")) {
				return StringUtil.isBlank(parameter) ? 0 : Long.valueOf(parameter.trim()).longValue();
			}
			return 0;
		} else {
			if (parameter == null) {
				return null;
			}
			parameter = parameter.trim();
			if (type == String.class) {
				return parameter;
			}
			if (type == Integer.class) {
				return Integer.valueOf(parameter);
			}
			if (type == Long.class) {
				return Long.valueOf(parameter);
			}
			return null;
		}
	}
}
