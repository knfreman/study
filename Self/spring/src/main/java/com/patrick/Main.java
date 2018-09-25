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
		
		// ======================= 普通事务 ======================= //
		ServiceA serviceA = (ServiceA) factory.getBean("serviceA");
		serviceA.function1();
		serviceA.function2();
		serviceA.function3();
		
		System.out.println("------------------------------------");
		
		// ======================= 嵌套事务 ======================= //
		ServiceB serviceB = (ServiceB) factory.getBean("serviceB");
		serviceB.function();
	}

}
