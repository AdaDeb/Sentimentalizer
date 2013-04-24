package org.panhandlers.sentimentalizer.storage;

import java.util.List;
import java.util.Set;

import org.panhandlers.sentimentalizer.features.Feature;


public interface ClassifierStorage {
	public void addItem(String category, List<Feature> feature);
	public int getTotalFeaturesInCategoryCount(String category);
	public int getFeatureCount(String category, Feature feature);
	public int getItemsInCategoryCount(String category);
	public int getTotalItemsCount();
	public int getTotalCount();
	public Set<String> getCategories();
}
