package org.panhandlers.sentimentalizer;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HashStorage implements ClassifierStorage {
	private HashMap<String, Integer> featuresInCategoryCount;
	private HashMap<String,HashMap<Feature, Integer>> featureCount;
	private int totalCount;
	private HashMap<String, Integer> itemCount;
	
	public HashStorage() {
		featuresInCategoryCount = new HashMap<String, Integer>();
		featureCount = new HashMap<String, HashMap<Feature, Integer>>();
		itemCount = new HashMap<String, Integer>();
		totalCount = 0;
	}
	
	public void addFeature(String category, Feature feature) {
		setCategoryCount(category);
		setItemCount(category);
		totalCount++;
		try {
			setFeatureCount(category, feature);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			mapForCategory.put(feature, newCount);
		} else {
			mapForCategory.put(feature, 1);
		}
	}

	private void setCategoryCount(String category) {
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
		for(Integer value: featuresInCategoryCount.values()) {
			sum += value; 
		}
		return sum;
	}

	@Override
	public Set<String> getCategories() {
		return this.featuresInCategoryCount.keySet();
	}

	@Override
	public void addItem(String category, List<Feature> features) {
		for (Feature feature : features) {
			addFeature(category, feature);
		}
		
	}

	@Override
	public int getItemsInCategoryCount(String category) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalItemsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
