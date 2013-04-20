package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.ClassificationResult;
import org.panhandlers.sentimentalizer.Classifier;
import org.panhandlers.sentimentalizer.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.Feature;

/**
 * Conducts a test on one classifier on sentiment.
 * @author jesjos
 *
 */
public class SentimentTest extends Test {
	
	private String report;
	private String testCategory;
	private String trainingCategory;
	private ExistenceFeatureExtractor extractor;
	private HashMap<String, List<List<String>>> testData;
	private HashMap<String, List<List<String>>> trainingData;
	private Test.Type type;
	private Set<String> dictionary;

	public SentimentTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize, String category) {
		super(env, classifier, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.testCategory = this.trainingCategory = category;
		this.report = "";
		this.type = Test.Type.IN_DOMAIN;
	}
	
	public SentimentTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize, String trainingCategory, String testCategory) {
		super(env, classifier, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.testCategory = testCategory; 
		this.trainingCategory = trainingCategory;
		this.report = "";
		this.type = Test.Type.OUT_OF_DOMAIN;
	}

	@Override
	void test() {
		ClassificationResult result;
		List<Feature> features;
		int successes = 0;
		int failures = 0;
		for (Entry<String, List<List <String>>> cat : testData.entrySet()) {
			for (List<String> item : cat.getValue()) {
				features = extractor.extractFeatures(item);
				result = getClassifier().classify(features);
				if (result.getCategory().equals(cat.getKey())) {
					successes++;
				} else {
					failures++;
				}
			}
		}
		report += ("Successes: " + successes + " Failures: " + failures);
		double percentage = (double) successes / ((double) successes + failures);
		report += (" Percentage: " + percentage * 100);	
		
		/*
		 * Unload data
		 */
		testData = trainingData = null;
		extractor = null;
		dictionary = null;
	}
	
	private void loadData() {
		/*
		 * Load data
		 */
		TestEnvironment env = getEnv();
		env.getStorage().reset();
		
		List<List<String>> positive = env.getReader().getItemsByCategoryAndSentiment(trainingCategory, "pos");
		List<List<String>> negative = env.getReader().getItemsByCategoryAndSentiment(trainingCategory, "neg");
		
		if (this.type == Test.Type.IN_DOMAIN) {
			HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
			data.put("pos", positive);
			data.put("neg", negative);
			
			/*
			 * Divide data
			 */
			getDivider().divide(data);
			testData = getDivider().getTestData();
			trainingData = getDivider().getTrainingData();
		} else {
			List<List<String>> positiveTestData = env.getReader().getItemsByCategoryAndSentiment(testCategory, "pos");
			List<List<String>> negativeTestData = env.getReader().getItemsByCategoryAndSentiment(testCategory, "neg");
			trainingData = new HashMap<String, List<List<String>>>();
			testData = new HashMap<String, List<List<String>>>();
			trainingData.put("pos", positive);
			trainingData.put("neg", negative);
			testData.put("pos", positiveTestData);
			testData.put("neg", negativeTestData);
		}
		
		/*
		 * Construct dictionary
		 */
		dictionary = getDictionaryBuilder().buildDictionary(trainingData);
		System.out.println("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
	}

	@Override
	void train() {
		System.out.println("Starting test: " + toString());
		loadData();
		List<List<Feature>> features;
		HashMap<String, List<List<Feature>>> featureMap = new HashMap<String, List<List<Feature>>>();
		for (Entry<String, List<List<String>>> cat : trainingData.entrySet()) {
			features = new ArrayList<List<Feature>>();
			for(List<String> item : cat.getValue()) {
				features.add(extractor.extractFeatures(item));
			}
			featureMap.put(cat.getKey(), features);
		}
		getClassifier().multipleTrain(featureMap, dictionary);
	}
	@Override
	public String getResults() {
		return toString();
	}
	
	@Override
	public String toString() {
		if (type == Type.IN_DOMAIN) {
			return "InDomain SentimentTester for category " + trainingCategory + "\n" + report;
		} else {
			return "OutOfDomain SentimentTester for training category " + trainingCategory + " tested on " + testCategory + "\n" + report;
		}
	}

}
