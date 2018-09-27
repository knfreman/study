package com.patrick;

import com.patrick.factory.BeanFactory;
import com.patrick.factory.impl.MyFactory;
import com.patrick.service.ServiceA;
import com.patrick.service.ServiceB;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Main {

	public static void main(String[] args) {
		BeanFactory factory = new MyFactory();

		// ======================= General Transaction ======================= //
		ServiceA serviceA = (ServiceA) factory.getBean("serviceA");
		new Thread(new Runnable() {
			public void run() {
				serviceA.function1();
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				serviceA.function2();
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				serviceA.function3();
			}
		}).start();

		// ======================= Nested Transaction ======================= //
		ServiceB serviceB = (ServiceB) factory.getBean("serviceB");
		new Thread(new Runnable() {
			public void run() {
				serviceB.function();
			}
		}).start();
	}

}
