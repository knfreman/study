package com.patrick.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClass1Test {

	private MyClass1 myClass1;

	@Before
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
