package com.tianyl.core.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bs = new byte[1024];
		int hasRead = -1;
		while ((hasRead = in.read(bs)) != -1) {
			baos.write(bs, 0, hasRead);
		}
		return baos.toByteArray();
	}

}
