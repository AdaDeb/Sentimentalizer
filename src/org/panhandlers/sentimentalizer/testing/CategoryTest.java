package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.GlobalConfig;
import org.panhandlers.sentimentalizer.classifiers.ClassificationResult;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.features.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.features.Feature;

public class CategoryTest extends Test{
	
	/*
	 * This class is used to run category tests for
	 * different algorithms (except KNN)
	 */
	
	private List<String> categories;
	private String report;
	private ExistenceFeatureExtractor extractor;
	private HashMap<String, List<List<String>>> testData;
	private HashMap<String, List<List<String>>> trainingData;
	private HashMap<String, Integer> successesForCategory;
	private HashMap<String, Integer> failureForCategory;
	private Set<String> dictionary;
	private int offset;

	public CategoryTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize) {
		super(env, classifier, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.report = "";
		this.offset = 0;
		this.successesForCategory = new HashMap<String, Integer>();
		this.failureForCategory = new HashMap<String, Integer>();
	}
	
	public CategoryTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize, int offset) {
		this(env, classifier, ratio, dictSize);
		this.offset = offset;
	}

	/*
	 * Test method used to classify the test data, each classification
	 * is checked with the expected output. If the classification is correct
	 * then we increment the number of successes otherwise we increment the
	 * failures
	 */
	@Override
	void test() {
		ClassificationResult result;
		List<Feature> features;
		int successes = 0;
		int failures = 0;
		for (Entry<String, List<List <String>>> cat : testData.entrySet()) { // loop through test data
			for (List<String> item : cat.getValue()) {
				features = extractor.extractFeatures(item);
				result = getClassifier().classify(features); 
				if (result.getCategory().equals(cat.getKey())) { // check if classification is correct	
					System.out.println("Succeeded for category " + cat.getKey());
					registerSuccess(cat.getKey());
					successes++;
				} else {
					System.out.println("Failed for category: " + cat.getKey());
					registerFailure(cat.getKey());
					failures++;
				}
			}
		}
		report += ("Successes: " + successes + " Failures: " + failures);
		double percentage = (double) successes / ((double) successes + failures);
		report += (" Percentage: " + percentage * 100);	
		setSuccessRate(percentage);
		// Print individual success rates
		if (categories != null) {
			int failure;
			int success;
			double rate;
			for (String category : getCategories()) {
				if (failureForCategory.get(category) != null) {
					try {
						failure = failureForCategory.get(category);
						success = successesForCategory.get(category);
						rate = (double) success / ((double) success + failure);
						report += "\n";
						report += "Success rate for " + category + ": " + rate + "\n";
					} catch (Exception e) {
						report += "\n Error printing result for category " + category;
					}
				}
			}
		}
		
		/*
		 * Unload data
		 */
		testData = trainingData = null;
		extractor = null;
		dictionary = null;
	}
	
	/*
	 * Calculates the success rate for a specific category
	 */
	public Double successRateForCategory(String category) {
		Integer successes = successesForCategory.get(category);
		Integer failures = failureForCategory.get(category);
		if (successes != null && failures != null) {
			return ((double) successes / ((double) successes + (double) failures));
		} else {
			return 0d;
		}
	}
	
	/*
	 * Record the success of a classification test
	 */
	private void registerSuccess(String key) {
		Integer n = successesForCategory.get(key);
		if(n == null) {
			successesForCategory.put(key, 1);
		} else {
			successesForCategory.put(key, n + 1);
		}
	}

	/*
	 * Record the failure of a classification test
	 */
	private void registerFailure(String key) {
		Integer n = failureForCategory.get(key);
		if(n == null) {
			failureForCategory.put(key, 1);
		} else {
			failureForCategory.put(key, n + 1);
		}
	}
	
	/*
	 * This method loads the data and divides it to training and 
	 * testing data
	 */
	private void loadData() {
		/*
		 * Load data
		 */
		TestEnvironment env = getEnv();
		env.getStorage().reset();
		
		// Load data from each category without considering pos/neg
		List<List<String>> musicCategory = env.getReader().getItemsByCategory("music");
		List<List<String>> dvdCategory = env.getReader().getItemsByCategory("dvd");
		List<List<String>> softwareCategory = env.getReader().getItemsByCategory("software");
		List<List<String>> booksCategory = env.getReader().getItemsByCategory("books");
		List<List<String>> healthCategory = env.getReader().getItemsByCategory("health");
		List<List<String>> cameraCategory = env.getReader().getItemsByCategory("camera");

		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("music", musicCategory);
		data.put("dvd", dvdCategory);
		data.put("software", softwareCategory);
		data.put("books", booksCategory);
		data.put("health", healthCategory);
		data.put("camera", cameraCategory);
		
		// Set offset
		getDivider().setOffset(offset);
		// Divide data
		getDivider().divide(data);
		testData = getDivider().getTestData();
		trainingData = getDivider().getTrainingData();
		
		/*
		 * Construct dictionary
		 */
		dictionary = getDictionaryBuilder().buildDictionary(trainingData);
		System.out.println("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
	}

	@Override
	/*
	 * Main method that train the algorithm using the training data
	 * Here we extract the existence features of before sending them in to 
	 * the classifier algorithm 
	 */
	void train() {
		System.out.println("Starting test: " + toString());
		loadData();
		List<List<Feature>> features;
		HashMap<String, List<List<Feature>>> featureMap = new HashMap<String, List<List<Feature>>>();
		for (Entry<String, List<List<String>>> cat : trainingData.entrySet()) { // loop through training data
			features = new ArrayList<List<Feature>>();
			for(List<String> item : cat.getValue()) {
				features.add(extractor.extractFeatures(item)); // extract feature
			}
			featureMap.put(cat.getKey(), features);
		}
		// send input to the classifier to train
		getClassifier().multipleTrain(featureMap, dictionary); 
	}
	@Override
	public String getResults() {
		return toString();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getClassifier().toString());
		b.append("Category Tester  ");
		b.append("\n");
		b.append(report);
		return b.toString();
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories2) {
		this.categories = categories2;
	}


}
