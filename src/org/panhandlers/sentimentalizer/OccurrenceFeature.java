package org.panhandlers.sentimentalizer;

public class OccurrenceFeature implements Feature {
	private String text;
	private int occurrences;
	public OccurrenceFeature(int n) {
		this.occurrences = n;
	}
	public OccurrenceFeature() {
		this.occurrences = 0;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	public void increment() {
		occurrences++;
	}
	
	
}
