package org.panhandlers.sentimentalizer;

import java.util.List;
import java.util.Set;

public interface ClassifierStorage {
	public void addItem(String category, List<Feature> feature);
	public int getTotalFeaturesInCategoryCount(String category);
	public int getFeatureCount(String category, Feature feature);
	public int getItemsInCategoryCount(String category);
	public int getTotalItemsCount();
	public int getTotalCount();
	public Set<String> getCategories();
}
