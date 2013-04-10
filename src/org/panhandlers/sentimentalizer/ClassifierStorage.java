package org.panhandlers.sentimentalizer;

import java.util.Set;

public interface ClassifierStorage {
	public void addFeature(String category, String feature);
	public int getCategoryCount(String category);
	public int getFeatureCount(String category, String feature);
	public int getTotalCount();
	public Set<String> getCategories();
}
