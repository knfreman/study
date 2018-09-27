package com.patrick;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "*** Subscription Key ***";

	public enum PersonGroup {
		FAMILY("family", 
				new Person[] { new Person("Patrick", "*** Patrick Id ***"),
						new Person("Mum", "*** Mum Id ***"),
						new Person("Dad", "*** Dad Id ***") }),
		COLLEAGUE("Colleague", 
				new Person[] { new Person("Patrick", "*** Patrick Id ***"),
						new Person("Caesar", "*** Caesar Id ***"),
						new Person("Tom", "*** Tom Id ***") });

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
	}
}
