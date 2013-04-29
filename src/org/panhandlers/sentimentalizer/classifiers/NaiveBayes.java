package org.panhandlers.sentimentalizer.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.panhandlers.sentimentalizer.GlobalConfig;
import org.panhandlers.sentimentalizer.features.Feature;
import org.panhandlers.sentimentalizer.redis.RedisStorage;
import org.panhandlers.sentimentalizer.storage.ClassifierStorage;

public class NaiveBayes implements Classifier {
	private ClassifierStorage storage;
	private HashMap<String, Integer> emptyFeatureCounts;
	
	public NaiveBayes() {
		storage = new RedisStorage();
	}
	
	public NaiveBayes(ClassifierStorage storage) {
		this.storage = storage;
		if (GlobalConfig.DEBUG) {
			emptyFeatureCounts = new HashMap<String, Integer>();
		}
	}
	
	@Override
	public void train(String category, List<Feature> features) {
		storage.addItem(category, features);
	}

	@Override
	public ClassificationResult classify(List<Feature> features) {
		Set<String> categories = storage.getCategories();
		ArrayList<ClassificationResult> results = new ArrayList<ClassificationResult>(categories.size());
		ClassificationResult result;
		double prob;
		for (String category : categories)
		{
			prob = pOfFeaturesGivenCategory(category, features) * pOfCategory(category);
			result = new ClassificationResult(category, prob);
			results.add(result);
		}
		Collections.sort(results, Collections.reverseOrder());
		if (GlobalConfig.DEBUG){
			System.out.println("Result vector:");
			for(ClassificationResult r : results) {
				System.out.println(r);
			}
			System.out.println("Empty features per category: ");
			for(Entry<String, Integer> entry : emptyFeatureCounts.entrySet()) {
				System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		}
		return results.get(0);
	}

	private double pOfFeatureGivenCategory(Feature feature, String category) {
		double featureCount = (double) storage.getFeatureCount(category, feature);
		if (featureCount == 0.0d) {
			if (GlobalConfig.DEBUG) {
				System.out.println("Feature count is zero for feature: " + feature + " and category: " + category);
				incrementEmptyFeatureCount(category);
			}
			return defaultProbability();
		} else {
			double categoryCount = (double) storage.getTotalFeaturesInCategoryCount(category);
			return featureCount/categoryCount;
		}
	}
	
private void incrementEmptyFeatureCount(String category) {
		Integer n = emptyFeatureCounts.get(category);
		if (n == null) {
			emptyFeatureCounts.put(category, 1);
		} else {
			emptyFeatureCounts.put(category, n + 1);
		}
	}

	private double defaultProbability() {
		return 0.5d / ((double) storage.getTotalCount() / 2);
	}
	
	private double pOfFeaturesGivenCategory(String category, List<Feature> features) {
		double sum = 1f;
		double probability;
		for (Feature feature : features) {
			probability = pOfFeatureGivenCategory(feature, category);
			sum += Math.log(probability);
			// System.out.println("Sum is" + sum);
		}
		return sum;
	}
	
	private double pOfCategory(String category) {
		double categoryCount = (double) storage.getItemsInCategoryCount(category);
		double totalCount = (double) storage.getTotalItemsCount();
		return categoryCount/totalCount;
	}

	@Override
	public void multipleTrain(HashMap<String, List<List<Feature>>> trainingSet, Set<String> dictionary) {
		for(Entry<String, List<List<Feature>>> entry : trainingSet.entrySet()) {
			for(List<Feature> item : entry.getValue()) {
				train(entry.getKey(), item);
			}
		}
	}
	
	@Override
	public String toString() {
		return "NaiveBayes";
	}
	
	@Override
	public String getName() {
		return "NaiveBayes";
	}

}
