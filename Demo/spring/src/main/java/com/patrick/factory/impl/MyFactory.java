package com.patrick.factory.impl;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.patrick.Main;
import com.patrick.aop.ObjectDecorator;
import com.patrick.factory.BeanFactory;
import com.patrick.service.ServiceA;
import com.patrick.service.ServiceB;
import com.patrick.service.impl.ServiceAImpl;
import com.patrick.service.impl.ServiceBImpl;

/**
 * 
 * @author Patrick Pan
 *
 */
public class MyFactory implements BeanFactory {

	private Map<String, Object> map;

	public MyFactory() {
		this.map = new HashMap<>();
		init();
	}

	@Override
	public Object getBean(String beanName) {
		return map.get(beanName);
	}

	private void init() {
		// ======================== ServiceA ======================== //
		ObjectDecorator proxy = new ObjectDecorator(new ServiceAImpl());
		ServiceA serviceA = (ServiceA) Proxy.newProxyInstance(Main.class.getClassLoader(),
				new Class<?>[] { ServiceA.class }, proxy);
		map.put("serviceA", serviceA);

		// ======================== ServiceB ======================== //
		ServiceBImpl s = new ServiceBImpl();
		s.setService(serviceA);
		proxy = new ObjectDecorator(s);
		map.put("serviceB",
				Proxy.newProxyInstance(Main.class.getClassLoader(), new Class<?>[] { ServiceB.class }, proxy));
	}
}
