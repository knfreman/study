package com.patrick.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClass2Test {

	private MyClass2 myClass2;

	@Before
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
