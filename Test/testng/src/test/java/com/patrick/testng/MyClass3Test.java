package com.patrick.testng;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClass3Test {

	private MyClass3 myClass3;

	@BeforeMethod
	public void init() {
		myClass3 = new MyClass3();
	}

	@Test
	public void testFun1() {
		assertEquals("fun1", myClass3.fun1());
	}

	@Test
	public void testFun2() {
		assertEquals("fun2", myClass3.fun2());
	}
}
