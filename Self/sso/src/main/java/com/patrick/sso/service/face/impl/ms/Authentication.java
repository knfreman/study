package com.patrick.sso.service.face.impl.ms;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "*** Subscription Key ***";

	public enum PersonGroup {
		
		COLLEAGUE("colleague", 
				new Person[] { new Person("Patrick", "*** Patrick Id ***"),
						new Person("Bella", "*** Bella Id ***"),
						new Person("Lesley", "*** Lesley Id ***"),
						new Person("Georgio", "*** Georgio Id ***"),
						new Person("Angie", "*** Angie Id ***"), 
						new Person("Kevin", "*** Kevin Id ***"), 
						new Person("Sue", "*** Sue Id ***") });

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
