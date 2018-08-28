package com.patrick.testng;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClass2Test {

	private MyClass2 myClass2;

	@BeforeMethod
	public void init() {
		myClass2 = new MyClass2();
	}

	@Test
	public void testFun1() {
		assertEquals("fun1", myClass2.fun1());
	}

	@Test
	public void testFun2() {
		assertEquals("fun2", myClass2.fun2());
	}
}
