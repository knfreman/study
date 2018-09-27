package com.patrick.sso.service.face.impl.ms;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Person {

	private String name;
	private String id;

	public Person(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
}
