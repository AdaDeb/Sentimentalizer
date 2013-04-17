package org.panhandlers.sentimentalizer.testing;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.ClassificationResult;
import org.panhandlers.sentimentalizer.Classifier;
import org.panhandlers.sentimentalizer.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.Feature;

public class SentimentTest extends Test {
	
	private String report;
	private String category;
	private ExistenceFeatureExtractor extractor;

	public SentimentTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize, String category) {
		super(env, classifier, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.category = category;
		this.report = "";
	}

	@Override
	void test() {
		ClassificationResult result;
		List<Feature> features;
		int successes = 0;
		int failures = 0;
		for (Entry<String, List<List <String>>> cat : getDivider().getTestData().entrySet()) {
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
		report += ("Percentage: " + percentage * 100);	
	}
	
	private void loadData() {
		TestEnvironment env = getEnv();
		env.getStorage().reset();
		List<List<String>> positive = env.getReader().getItemsByCategoryAndSentiment(category, "pos");
		List<List<String>> negative = env.getReader().getItemsByCategoryAndSentiment(category, "neg");
		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("pos", positive);
		data.put("neg", negative);
		getDivider().divide(data);
		Set<String> dictionary = getDictionaryBuilder().buildDictionary(getDivider().getTrainingData());
		System.out.println("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
	}

	@Override
	void train() {
		loadData();
		List<Feature> features;
		for (Entry<String, List<List<String>>> cat: getDivider().getTrainingData().entrySet()) {
			System.out.println("Training for: " + cat.getKey() + " with " + cat.getValue().size() + " items");
			for(List<String> item : cat.getValue()) {	    
//				System.out.println("Training item with length: " + item.size());
				features = extractor.extractFeatures(item);
			    long startTime = System.currentTimeMillis();
//				System.out.println("Extraction complete with length: " + features.size());
				getClassifier().train(cat.getKey(), features);
				long stopTime = System.currentTimeMillis();
			    long elapsedTime = stopTime - startTime;
//			    System.out.println("Training  completed in " + elapsedTime);
			}
		}
	}
	@Override
	public String getResults() {
		return toString();
	}
	
	@Override
	public String toString() {
		return "SentimentTester for category " + category + "\n" + report;
	}

}
