package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.GlobalConfig;
import org.panhandlers.sentimentalizer.Pair;
import org.panhandlers.sentimentalizer.Utilities;
import org.panhandlers.sentimentalizer.classifiers.ClassificationResult;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.features.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.features.Feature;

public class NewSentimentAnalyzer extends Test {
	private Classifier classifier;
	private String[] categories;
	private String trainingCategory;
	private HashMap<String, List<List<String>>> trainingData;
	private Set<String> dictionary;
	private ExistenceFeatureExtractor extractor;
	private HashMap<String, HashMap<String, List<List<String>>>> testData;
	private List<SentimentAnalysisResult> results;
	
	public NewSentimentAnalyzer(TestEnvironment env,
			int ratio, int dictionarySize, Classifier classifier, String[] categories, String trainingCategory) {
		super(env, null, ratio, dictionarySize);
		this.classifier = classifier;
		this.categories = categories;
		this.extractor = new ExistenceFeatureExtractor();
		this.trainingCategory = trainingCategory;
		this.testData = new HashMap<String, HashMap<String, List<List<String>>>>();
		results = new ArrayList<SentimentAnalysisResult>();
	}

	private void loadData() {
		/*
		 * Load data
		 */
		TestEnvironment env = getEnv();
		env.getStorage().reset();

		/*
		 * Get all data for the training category
		 */
		List<List<String>> positive = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "pos");
		List<List<String>> negative = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "neg");
		
		/*
		 * Divide the training category into training and test data set, 
		 * for simultaneous in-domain testing
		 */
		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("pos", positive);
		data.put("neg", negative);
		getDivider().divide(data);
		HashMap<String, List<List<String>>> inDomainTestData = getDivider().getTestData();
		trainingData = getDivider().getTrainingData();
		testData.put(trainingCategory, inDomainTestData);
		
		/*
		 * Load the data for all the other categories
		 */
		HashMap<String, List<List<String>>> dataForCategory;
		List<List<String>> tempData;
		for (String category : categories) {
			// Don't load data for the training category again...
			if(!category.equals(trainingCategory)) {
				dataForCategory = new HashMap<String, List<List<String>>>();
				tempData = env.getReader().getItemsByCategoryAndSentiment(category, "pos");
				dataForCategory.put("pos", tempData);
				tempData = env.getReader().getItemsByCategoryAndSentiment(category, "neg");
				dataForCategory.put("neg", tempData);
				testData.put(category,dataForCategory);
			}

		}
		/*
		 * Construct dictionary
		 */
		dictionary = getDictionaryBuilder().buildDictionary(trainingData);
		if (GlobalConfig.DEBUG)
			debug("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
	}
	
	@Override
	public void train() {
		loadData();
		debug("Data loaded");
		HashMap<String, List<List<Feature>>> featureMap = extractFeatures(trainingData);
		debug("Training " + classifier.getName());
		classifier.multipleTrain(featureMap, dictionary);
	}
	
	private void debug(String string) {
		if (GlobalConfig.DEBUG) 
			System.out.println("NewSentimentAnalyzer debug: " + string);
	}

	private HashMap<String, List<List<Feature>>> extractFeatures(HashMap<String, List<List<String>>> inputData) {
		List<List<Feature>> features;
		HashMap<String, List<List<Feature>>> featureMap = new HashMap<String, List<List<Feature>>>();
		for (Entry<String, List<List<String>>> cat : inputData.entrySet()) {
			features = new ArrayList<List<Feature>>();
			for(List<String> item : cat.getValue()) {
				features.add(extractor.extractFeatures(item));
			}
			featureMap.put(cat.getKey(), features);
		}
		return featureMap;
	}
	
//	private void performMcNemar(McNemar mcnemar) {
//		HashMap<String, List<List<Feature>>> features = extractFeatures(testData.get(mcnemar.getTestCategory()));
//		ClassificationResult first;
//		ClassificationResult second;
//		boolean firstCorrect;
//		boolean secondCorrect;
//		int firstResult;
//		int secondResult;
//		for(Entry<String, List<List<Feature>>> itemsInCategory : features.entrySet()) {
//			for(List<Feature> item : itemsInCategory.getValue()) {
//				first = mcnemar.getFirstClassifier().classify(item);
//				second = mcnemar.getSecondClassifier().classify(item);
//				firstCorrect = first.getCategory().equals(itemsInCategory.getKey());
//				secondCorrect = second.getCategory().equals(itemsInCategory.getKey());
//				firstResult = firstCorrect ? 1 : 0;
//				secondResult = secondCorrect ? 1 : 0;
//				mcnemar.setPairResult(firstResult, secondResult);
//			}
//		}
//		
//	}
	@Override
	public void test() {
		HashMap<String, List<List<Feature>>> itemsAsFeaturesBySentiment;
		ClassificationResult classificationResult;
		String intendedResult;
		SentimentAnalysisResult result;
		for (String category : categories) {
			itemsAsFeaturesBySentiment = extractFeatures(testData.get(category));
			result = new SentimentAnalysisResult();
			result.trainingCategory = trainingCategory;
			result.testingCategory = category;
			for (Entry<String, List<List<Feature>>> itemsForSentiment : itemsAsFeaturesBySentiment.entrySet()) {
				intendedResult = itemsForSentiment.getKey();
				for(List<Feature> item : itemsForSentiment.getValue()) {
					classificationResult = classifier.classify(item);
					if (classificationResult.getCategory().equals(intendedResult)) {
						result.successes++;
					} else {
						result.failures++;
					}
				}
			}
			results.add(result);
		}
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Combined Sentiment tester for classifier " + classifier.getName());
		for (SentimentAnalysisResult r : results) {
			b.append(r);
		}
		return b.toString();
	}

	@Override
	public String getResults() {
		return toString();
	}
	private class SentimentAnalysisResult {
		public String classifierName;
		public String trainingCategory;
		public String testingCategory;
		public int successes;
		public int failures;
		
		public SentimentAnalysisResult() {
			successes = 0;
			failures = 0;
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			if (testingCategory.equals(trainingCategory)) {
				b.append("\nIn-domain testing on " + trainingCategory);
			} else {
				b.append("\nTraining on " + trainingCategory);
				b.append(" testing on " + testingCategory);
			}
			b.append("\nSuccesses: " + successes);
			b.append(" failures: " + failures);
			b.append("\nSuccess rate: " + successRate());
			return b.toString();
		}
		
		public Double successRate() {
			return (double) successes / ((double) successes + failures);
		}
		
	}

}
