package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.Set;

public class NaiveBayes implements Classifier {
	private ClassifierStorage storage;
	
	public NaiveBayes() {
		storage = new HashStorage();
	}
	
	@Override
	public void train(String category, String text) {
		// TODO: need to split strings and maybe stemming and stuff like that...
		storage.addFeature(category, text);
	}

	@Override
	public ClassificationResult classify(String text) {
		String[] parsedText = parse(text); 
		Set<String> categories = storage.getCategories();
		ArrayList<ClassificationResult> results = new ArrayList<ClassificationResult>(categories.size());
		ClassificationResult result;
		float prob;
		for (String category : categories)
		{
			prob = pOfCategoryGivenFeatures(category, parsedText) * pOfCategory(category);
			result = new ClassificationResult(category, prob);
			results.add(result);
		}
		return null;
	}
	
	private String[] parse(String text) {
		String[] parsed = text.trim().split("\\s");
		return parsed;
	}

	private float pOfFeatureGivenCategory(String feature, String category) {
		return storage.getFeatureCount(category, feature) / storage.getCategoryCount(category);
	}
	
	private float pOfCategoryGivenFeatures(String category, String[] features) {
		float sum = 1f;
		for (String feature : features) {
			float probability = pOfFeatureGivenCategory(feature, category);
			sum *= probability;
		}
		return sum;
	}
	
	private float pOfCategory(String category) {
		return storage.getCategoryCount(category) / storage.getTotalCount();
	}
	

}
