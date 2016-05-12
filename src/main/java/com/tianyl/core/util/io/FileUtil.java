package com.tianyl.core.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

	public static String read(File file) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.trim().equals("")) {
					sb.append(line.trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static void deleteFiles(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			deleteDir(file);
		} else {
			deleteFile(file);
		}
	}

	public static void deleteDir(File file) {
		if (file.isFile()) {
			return;
		}
		for (File child : file.listFiles()) {
			if (child.isFile()) {
				deleteFile(child);
			} else {
				deleteDir(child);
			}
		}
		file.delete();
	}

	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
		}
	}

}
