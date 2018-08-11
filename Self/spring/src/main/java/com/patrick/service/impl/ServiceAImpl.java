package com.patrick.service.impl;

import com.patrick.annotation.Transactional;
import com.patrick.service.ServiceA;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ServiceAImpl implements ServiceA {

	@Override
	@Transactional
	public void function1() {
		System.out.println("=== ServiceA#function1 需要事务。===");
	}

	@Override
	@Transactional
	public void function2() {
		System.out.println("=== ServiceA#function2需要事务。 ===");
	}

	@Override
	public void function3() {
		System.out.println("=== ServiceA#function3不需要事务。 ===");
	}

}
