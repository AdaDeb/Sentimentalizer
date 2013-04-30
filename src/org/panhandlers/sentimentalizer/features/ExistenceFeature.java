package org.panhandlers.sentimentalizer.features;

public class ExistenceFeature implements Feature, TokenFeature {
	
	/*
	 * This class represents an existence feature object
	 */
	


	private boolean exists; // token exists or not?
	private String token; // concerned token
	public ExistenceFeature(String token) {
		this.token = token;
		exists = false;
	}
	public ExistenceFeature() {
		this.exists = false;
	}
	public String toString() {
		return "existence" + token + exists;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean doesExist() {
		return exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
	/*
	 * Returns whether a token existence or not in integer representation
	 * 1 = exists / 0 = does not exist
	 */
	public int getValue() {
		return exists ? 1 : 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (exists ? 1231 : 1237);
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExistenceFeature other = (ExistenceFeature) obj;
		if (exists != other.exists)
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}
}
