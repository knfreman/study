package com.patrick;

public class Main {
	public static void main(String[] args) throws Exception {
		Class<?> clazz = new MyClassLoader().findClass("com.patrick.MyObject");
		Object obj = clazz.newInstance();
		obj = (MyObject) obj;
	}
}
