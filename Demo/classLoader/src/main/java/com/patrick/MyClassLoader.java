package com.patrick;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClassLoader extends ClassLoader {

	@Override
	public Class<?> findClass(String classFile) throws ClassNotFoundException {
		try {
			byte[] bytes = getBytes();
			return this.defineClass(classFile, bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassNotFoundException("Exception occurs in MyClassLoader.findClass.");
		}
	}

	private byte[] getBytes() throws IOException {
		try (InputStream inputStream = MyClassLoader.class.getClassLoader().getResourceAsStream("MyObject")) {
			int len = inputStream.available();
			byte[] bytes = new byte[len];
			inputStream.read(bytes, 0, len);
			return bytes;
		}
	}
}
