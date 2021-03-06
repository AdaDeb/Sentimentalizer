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
import org.panhandlers.sentimentalizer.storage.HashStorage;

/**
 * An implementation of NaiveBayes text classification.
 * @author jesjos
 *
 */
public class NaiveBayes implements Classifier {
	private ClassifierStorage storage;
	private HashMap<String, Integer> emptyFeatureCounts;
	
	/**
	 * The default constructor uses HashStorage
	 */
	public NaiveBayes() {
		storage = new HashStorage();
		// In debug mode we count occurrences of unknown features in the classification input
		if (GlobalConfig.DEBUG) {
			emptyFeatureCounts = new HashMap<String, Integer>();
		}
	}
	
	public NaiveBayes(ClassifierStorage storage) {
		this.storage = storage;
	}
	
	/**
	 * Train the algorithm for one item
	 */
	@Override
	public void train(String category, List<Feature> features) {
		storage.addItem(category, features);
	}

	/**
	 * Classification involves calculating the probability of
	 * the input set given all known categories and then choosing
	 * the category with the highest probability.
	 */
	@Override
	public ClassificationResult classify(List<Feature> features) {
		Set<String> categories = storage.getCategories();
		ArrayList<ClassificationResult> results = new ArrayList<ClassificationResult>(categories.size());
		ClassificationResult result;
		double prob;
		for (String category : categories)
		{
			prob = pOfFeaturesGivenCategory(category, features) + pOfCategory(category);
			result = new ClassificationResult(category, prob);
			results.add(result);
		}
		// Reverse order sorting is equal to descending order.
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
	
	/**
	 * Calculate the probability of a single feature occurring in category
	 * @param feature
	 * @param category
	 * @return the probability
	 */
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
			if (GlobalConfig.DEBUG) {
				if (featureCount == 1.0d) {
					System.out.println("Feature count is one for " + feature);
				}
			}
			return featureCount/categoryCount;
		}
	}
	
	/**
	 * Increment the counter of empty features in a certain category
	 * @param category
	 */
	private void incrementEmptyFeatureCount(String category) {
		Integer n = emptyFeatureCounts.get(category);
		if (n == null) {
			emptyFeatureCounts.put(category, 1);
		} else {
			emptyFeatureCounts.put(category, n + 1);
		}
	}
	
	/**
	 * Returns a default probability that is used when an encountered feature isn't present
	 * in the vocabulary
	 * @return
	 */
	private double defaultProbability() {
		return 0.5d / ((double) storage.getTotalCount() / 2);
	}
	
	/**
	 * Calculate the probability of a set of features given a category
	 * Values are logarithmized to avoid running out of precision
	 * @param category
	 * @param features
	 * @return
	 */
	private double pOfFeaturesGivenCategory(String category, List<Feature> features) {
		double sum = 0D;
		double probability;
		for (Feature feature : features) {
			probability = pOfFeatureGivenCategory(feature, category);
			sum += Math.log(probability);
		}
		return sum;
	}
	
	/**
	 * Calculate the probability of encountering an item from the given category
	 * @param category
	 * @return
	 */
	private double pOfCategory(String category) {
		double categoryCount = (double) storage.getItemsInCategoryCount(category);
		double totalCount = (double) storage.getTotalItemsCount();
		return Math.log(categoryCount/totalCount);
	}

	/**
	 * Train on multiple items sequentially.
	 */
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
