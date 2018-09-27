package com.patrick.service.impl;

import com.patrick.annotation.Transactional;
import com.patrick.service.ServiceA;
import com.patrick.service.ServiceB;

/**
 * 
 * @author Patrick Pan
 *
 */
public class ServiceBImpl implements ServiceB {

	private ServiceA serviceA;

	public void setService(ServiceA serviceA) {
		this.serviceA = serviceA;
	}

	@Override
	@Transactional
	public void function() {
		System.out.println(Thread.currentThread().getName() + " - ServiceB#function requires a transaction.");
		this.serviceA.function1();
		this.serviceA.function2();
	}

}
