package org.panhandlers.sentimentalizer.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.panhandlers.sentimentalizer.GlobalConfig;
import org.panhandlers.sentimentalizer.features.Feature;

public class HashStorage implements ClassifierStorage {
	
	/*
	 * This class implements a simple hash storage data 
	 * structure along with other trivial data access
	 * and look up methods
	 */
	
	private HashMap<String, Integer> featuresInCategoryCount;
	private HashMap<String,HashMap<Feature, Integer>> featureCount;
	private int totalFeatureCount;
	private int totalItemCount;
	private HashMap<String, Integer> itemCount;
	
	public HashStorage() {
		featuresInCategoryCount = new HashMap<String, Integer>();
		featureCount = new HashMap<String, HashMap<Feature, Integer>>();
		itemCount = new HashMap<String, Integer>();
		totalFeatureCount = 0;
		totalItemCount = 0;
	}
	
	public void addFeature(String category, Feature feature) {
		setTotalFeaturesInCategoryCount(category);
		totalFeatureCount++;
		try {
			setFeatureCount(category, feature);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	private void setItemCount(String category) {
		Integer count = itemCount.get(category);
		if (count == null) {
			itemCount.put(category, 1);
		} else {
			itemCount.put(category, count + 1);
		}
	}

	private void setFeatureCount(String category, Feature feature) throws Exception {
		HashMap<Feature, Integer> mapForCategory = featureCount.get(category);
		if (mapForCategory == null) {
			throw new Exception("Could not find category");
		}
		if (mapForCategory.containsKey(feature)) {
			int newCount = mapForCategory.get(feature);
			mapForCategory.put(feature, newCount + 1);
		} else {
			mapForCategory.put(feature, 1);
		}
	}

	private void setTotalFeaturesInCategoryCount(String category) {
		if (featuresInCategoryCount.containsKey(category)) {
			int newCount = featuresInCategoryCount.get(category) + 1;
			featuresInCategoryCount.put(category, newCount);
		} else {
			addCategory(category);
		}
	}

	private void addCategory(String category) {
		featuresInCategoryCount.put(category, 1);
		featureCount.put(category, new HashMap<Feature, Integer>());
	}

	@Override
	public int getTotalFeaturesInCategoryCount(String category) {
		if (featuresInCategoryCount.containsKey(category)) {
			return featuresInCategoryCount.get(category);
		} else {
			if (GlobalConfig.DEBUG)
				System.out.println("Could not get total features in category count");
			return 0;
		}
	}

	@Override
	public int getFeatureCount(String category, Feature feature) {
		if (featureCount.containsKey(category) && featureCount.get(category).containsKey(feature)){
			return featureCount.get(category).get(feature);
		} else {
			if (GlobalConfig.DEBUG)
				System.out.println("Could not get feature count");
			return 0;
		}
	}

	@Override
	public int getTotalCount() {
		return totalFeatureCount;
	}

	@Override
	public Set<String> getCategories() {
		return this.featuresInCategoryCount.keySet();
	}

	@Override
	public void addItem(String category, List<Feature> features) {
		totalItemCount++;
		setItemCount(category);
		for (Feature feature : features) {
			addFeature(category, feature);
		}
	}

	@Override
	public int getItemsInCategoryCount(String category) {
		Integer count = itemCount.get(category);
		if (itemCount != null) {
			return count;
		} else {
			if (GlobalConfig.DEBUG)
				System.out.println("Could not get items in category count");
			return 0;
		}
	}

	@Override
	public int getTotalItemsCount() {
		return totalItemCount;
	}

}
