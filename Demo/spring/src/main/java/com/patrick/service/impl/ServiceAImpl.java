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
		System.out.println(Thread.currentThread().getName() + " - ServiceA#function1 requires a transaction.");
	}

	@Override
	@Transactional
	public void function2() {
		System.out.println(Thread.currentThread().getName() + " - ServiceA#function2 requires a transaction.");
	}

	@Override
	public void function3() {
		System.out.println(Thread.currentThread().getName() + " - ServiceA#function3 doesn't require a transaction.");
	}

}
