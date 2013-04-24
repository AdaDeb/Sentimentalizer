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
	
	private String report;
	private ExistenceFeatureExtractor extractor;
	private HashMap<String, List<List<String>>> testData;
	private HashMap<String, List<List<String>>> trainingData;
	private Set<String> dictionary;
	private int offset;

	public CategoryTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize) {
		super(env, classifier, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.report = "";
		this.offset = 0;
	}
	
	public CategoryTest(TestEnvironment env, Classifier classifier, int ratio, int dictSize, int offset) {
		this(env, classifier, ratio, dictSize);
		this.offset = offset;
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
					
					System.out.println("Succeeded for category " + cat.getKey());
					successes++;
				} else {
					System.out.println("Failed for category: " + cat.getKey());
					failures++;
				}
			}
		}
		report += ("Successes: " + successes + " Failures: " + failures);
		double percentage = (double) successes / ((double) successes + failures);
		report += (" Percentage: " + percentage * 100);	
		setSuccessRate(percentage);
		
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
		
		List<List<String>> musicCategory = env.getReader().getItemsByCategory("music");
		List<List<String>> dvdCategory = env.getReader().getItemsByCategory("dvd");
		List<List<String>> softwareCategory = env.getReader().getItemsByCategory("software");
		List<List<String>> booksCategory = env.getReader().getItemsByCategory("books");
		List<List<String>> healthCategory = env.getReader().getItemsByCategory("health");
		List<List<String>> cameraCategory = env.getReader().getItemsByCategory("camera");

		System.out.println("music length " + musicCategory.size());
		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("music", musicCategory);
		data.put("dvd", dvdCategory);
		data.put("software", softwareCategory);
		data.put("books", booksCategory);
		data.put("health", healthCategory);
		data.put("camera", cameraCategory);
		
		// Set offset
		getDivider().setOffset(offset);
		getDivider().divide(data);
		testData = getDivider().getTestData();
		trainingData = getDivider().getTrainingData();
		
		
		
		
		
//		System.out.println("Length music " + trainingData.get("music").size());
//		System.out.println("Length dvd" + trainingData.get("dvd").size());
//		System.out.println("Length software " + trainingData.get("software").size());
//		System.out.println("Length books " + trainingData.get("books").size());
//		System.out.println("Length health " + trainingData.get("health").size());
//		System.out.println("Length camera " + trainingData.get("camera").size());
		
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
		StringBuilder b = new StringBuilder();
		b.append(getClassifier().toString());
		b.append("Category Tester  ");
		b.append("\n");
		b.append(report);
		return b.toString();
	}


}
