package com.patrick;

/**
 * 
 * @author Patrick Pan
 *
 */
public class Authentication {

	public static final String SUBSCRIPTION_KEY = "***********************";

	public enum IdentificationProfile {

		Patrick("***********************"), Lazy("***********************"), Mavis("***********************");

		private String profileId;

		private IdentificationProfile(String profileId) {
			this.profileId = profileId;
		}

		public String getProfileId() {
			return profileId;
		}
	}
}
