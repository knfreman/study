package com.patrick.testng;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClass1Test {

	private MyClass1 myClass1;

	@BeforeMethod
	public void init() {
		myClass1 = new MyClass1();
	}

	@Test
	public void testFun1() {
		assertEquals("fun1", myClass1.fun1());
	}

	@Test
	public void testFun2() {
		assertEquals("fun2", myClass1.fun2());
	}
}
