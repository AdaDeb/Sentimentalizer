package org.panhandlers.sentimentalizer;

public interface ClassifierStorage {
	public void addFeature(String category, String feature);
	public int getCategoryCount(String category);
	public int getFeatureCount(String category, String feature);
}
