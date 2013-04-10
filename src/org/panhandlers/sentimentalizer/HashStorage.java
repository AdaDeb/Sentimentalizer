package org.panhandlers.sentimentalizer;

import java.util.HashMap;

public class HashStorage implements ClassifierStorage {
	private HashMap<String, Integer> categoryCount;
	private HashMap<String,HashMap<String, Integer>> featureCount;
	
	public HashStorage() {
		categoryCount = new HashMap<String, Integer>();
		featureCount = new HashMap<String, HashMap<String, Integer>>();
	}
	
	@Override
	public void addFeature(String category, String feature) {
		setCategoryCount(category);
		try {
			setFeatureCount(category, feature);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private void setFeatureCount(String category, String feature) throws Exception {
		HashMap<String, Integer> mapForCategory = featureCount.get(category);
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
		featureCount.put(category, new HashMap<String, Integer>());
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
	public int getFeatureCount(String category, String feature) {
		if (featureCount.containsKey(category) && featureCount.get(category).containsKey(feature)){
			return featureCount.get(category).get(feature);
		} else {
			return 0;
		}
	}

}
