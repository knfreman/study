package com.patrick;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClassLoader extends ClassLoader {

	@Override
	public Class<?> findClass(String className) throws ClassNotFoundException {
		try {
			byte[] bytes = getBytes(getClassFileName(className));
			return this.defineClass(className, bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassNotFoundException("Exception occurs in MyClassLoader.findClass.");
		}
	}

	private String getClassFileName(String className) {
		int index = className.lastIndexOf(".") + 1;
		return className.substring(index);
	}

	private byte[] getBytes(String classFileName) throws IOException {
		int hasRead = 0;
		byte[] buffer = new byte[1024];
		
		try (InputStream inputStream = MyClassLoader.class.getClassLoader().getResourceAsStream(classFileName);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			while ((hasRead = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, hasRead);
			}
			return outputStream.toByteArray();
		}
	}
}
