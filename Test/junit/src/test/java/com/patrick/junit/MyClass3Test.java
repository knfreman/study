package com.patrick.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyClass3Test {

	private MyClass3 myClass3;

	@Before
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
