package com.patrick;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "***********************";

	public enum PersonGroup {

		Family("patrick-family", 
				new Person[] { new Person("Patrick", "***********************") }),
		
		Colleague("patrick-colleague", 
				new Person[] { new Person("Patrick", "***********************") });

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
	}
}
