package com.patrick;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "*** Subscription Key ***";

	public enum IdentificationProfile {

		PATRICK("*** Patrick Id ***"), 
		CAESAR("*** Caesar Id ***"), 
		TOM("*** Tom Id ***"),
		GEORGIO("*** Georgio Id ***");

		private String profileId;

		private IdentificationProfile(String profileId) {
			this.profileId = profileId;
		}

		public String getProfileId() {
			return profileId;
		}
	}
}
