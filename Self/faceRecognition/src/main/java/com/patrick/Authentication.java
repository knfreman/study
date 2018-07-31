package com.patrick;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "*******************";

	public enum PersonGroup {
		Family("*******************", 
				new Person[] { new Person("Patrick", "*******************"),
						new Person("Mum", "*******************"),
						new Person("Dad", "*******************") }),
		Colleague("*******************", 
				new Person[] { new Person("Patrick", "*******************"),
						new Person("Caesar", "*******************"),
						new Person("Tom", "*******************") });

		private String personGroupId;
		private Person[] persons;

		private PersonGroup(String personGroup, Person[] persons) {
			this.personGroupId = personGroup;
			this.persons = persons;
		}

		public String getPersonGroupId() {
			return this.personGroupId;
		}
		
		public Person[] getPersons() {
			return this.persons;
		}
		
		public static class Person {

			public String name;
			public String id;

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
	}
}
