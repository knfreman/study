package com.patrick.sso.service.face.impl.ms;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "********************";

	public enum PersonGroup {
		
		AIA("aia", 
				new Person[] { new Person("Patrick", "********************"),
						new Person("Bella", "********************"),
						new Person("Lesley", "********************"),
						new Person("Georgio", "********************"),
						new Person("Angie", "********************"), 
						new Person("Kevin", "********************"), 
						new Person("Sue", "********************") });

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
