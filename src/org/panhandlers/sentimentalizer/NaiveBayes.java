package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NaiveBayes implements Classifier {
	private ClassifierStorage storage;
	
	public NaiveBayes() {
		storage = new HashStorage();
	}
	
	@Override
	public void train(String category, List<Feature> features) {
		for(Feature f : features) {
			storage.addFeature(category, f);
		}
	}

	@Override
	public ClassificationResult classify(List<Feature> features) {
		Set<String> categories = storage.getCategories();
		ArrayList<ClassificationResult> results = new ArrayList<ClassificationResult>(categories.size());
		ClassificationResult result;
		float prob;
		for (String category : categories)
		{
			prob = pOfCategoryGivenFeatures(category, features) * pOfCategory(category);
			result = new ClassificationResult(category, prob);
			results.add(result);
		}
		Collections.sort(results, Collections.reverseOrder());
		return results.get(0);
	}

	private float pOfFeatureGivenCategory(Feature feature, String category) {
		return storage.getFeatureCount(category, feature) / storage.getCategoryCount(category);
	}
	
	private float pOfCategoryGivenFeatures(String category, List<Feature> features) {
		float sum = 1f;
		for (Feature feature : features) {
			float probability = pOfFeatureGivenCategory(feature, category);
			sum *= probability;
		}
		return sum;
	}
	
	private float pOfCategory(String category) {
		return storage.getCategoryCount(category) / storage.getTotalCount();
	}
	

}
