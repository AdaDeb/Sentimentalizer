package org.panhandlers.sentimentalizer;

public class OccurrenceFeature implements Feature, TokenFeature {
	private String token;
	private int occurrences;
	public OccurrenceFeature(int n) {
		this.occurrences = n;
	}
	public OccurrenceFeature() {
		this.occurrences = 0;
	}
	
	public OccurrenceFeature(String token) {
		this.token = token;
		this.occurrences = 0;
	}
	
	public OccurrenceFeature(String token, int n) {
		this.token = token;
		this.occurrences = n;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getOccurrences() {
		return occurrences;
	}
	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + occurrences;
		result = prime * result + "occurence".hashCode();
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
		OccurrenceFeature other = (OccurrenceFeature) obj;
		if (occurrences != other.occurrences)
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}
	
	public void increment() {
		occurrences++;
	}
	
	public String toString() {
		return "occurrence" + token + occurrences;
	}
	@Override
	public int getValue() {
		return occurrences;
	}
}
