package org.panhandlers.sentimentalizer;

import java.util.HashMap;
import java.util.Set;

public class HashStorage implements ClassifierStorage {
	private HashMap<String, Integer> categoryCount;
	private HashMap<String,HashMap<Feature, Integer>> featureCount;
	
	public HashStorage() {
		categoryCount = new HashMap<String, Integer>();
		featureCount = new HashMap<String, HashMap<Feature, Integer>>();
	}
	
	@Override
	public void addFeature(String category, Feature feature) {
		setCategoryCount(category);
		try {
			setFeatureCount(category, feature);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private void setFeatureCount(String category, Feature feature) throws Exception {
		HashMap<Feature, Integer> mapForCategory = featureCount.get(category);
		if (mapForCategory == null) {
			throw new Exception("Could not find category");
		}
		if (mapForCategory.containsKey(feature)) {
			int newCount = mapForCategory.get(feature);
			mapForCategory.put(feature, newCount);
		} else {
			mapForCategory.put(feature, 1);
		}
	}

	private void setCategoryCount(String category) {
		if (categoryCount.containsKey(category)) {
			int newCount = categoryCount.get(category) + 1;
			categoryCount.put(category, newCount);
		} else {
			addCategory(category);
		}
	}

	private void addCategory(String category) {
		categoryCount.put(category, 1);
		featureCount.put(category, new HashMap<Feature, Integer>());
	}

	@Override
	public int getCategoryCount(String category) {
		if (categoryCount.containsKey(category)) {
			return categoryCount.get(category);
		} else {
			return 0;
		}
	}

	@Override
	public int getFeatureCount(String category, Feature feature) {
		if (featureCount.containsKey(category) && featureCount.get(category).containsKey(feature)){
			return featureCount.get(category).get(feature);
		} else {
			return 0;
		}
	}

	@Override
	public int getTotalCount() {
		int sum = 0;
		for(Integer value: categoryCount.values()) {
			sum += value; 
		}
		return sum;
	}

	@Override
	public Set<String> getCategories() {
		return this.categoryCount.keySet();
	}

}
