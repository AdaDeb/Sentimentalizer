package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.Pair;
import org.panhandlers.sentimentalizer.Utilities;
import org.panhandlers.sentimentalizer.classifiers.ClassificationResult;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.features.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.features.Feature;

public class McNemarSentimentTester extends Test {
	private Classifier[] classifiers;
	private String[] categories;
	private String trainingCategory;
	private HashMap<String, List<List<String>>> trainingData;
	private Set<String> dictionary;
	private ExistenceFeatureExtractor extractor;
	private HashMap<String, HashMap<String, List<List<String>>>> testData;
	private List<McNemar> mcNemars;
	
	public McNemarSentimentTester(TestEnvironment env,
			int ratio, int dictionarySize, Classifier[] classifiers, String[] categories, String trainingCategory) {
		super(env, null, ratio, dictionarySize);
		this.classifiers = classifiers;
		this.categories = categories;
		this.extractor = new ExistenceFeatureExtractor();
		this.trainingCategory = trainingCategory;
		this.mcNemars = new ArrayList<McNemar>(36);
		this.testData = new HashMap<String, HashMap<String, List<List<String>>>>();
	}

	private void loadData() {
		/*
		 * Load data
		 */
		TestEnvironment env = getEnv();
		env.getStorage().reset();

		List<List<String>> positive = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "pos");
		List<List<String>> negative = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "neg");
		
		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("pos", positive);
		data.put("neg", negative);
		getDivider().divide(data);
		HashMap<String, List<List<String>>> inDomainTestData = getDivider().getTestData();
		trainingData = getDivider().getTrainingData();
		testData.put(trainingCategory, inDomainTestData);
		
		HashMap<String, List<List<String>>> dataForCategory;
		List<List<String>> tempData;
		for (String category : categories) {
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
		System.out
				.println("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
	}
	
	@Override
	public void train() {
		loadData();
		debug("Data loaded");
		HashMap<String, List<List<Feature>>> featureMap = extractFeatures(trainingData);
		for (Classifier classifier : classifiers) {
			debug("Training " + classifier.getName());
			classifier.multipleTrain(featureMap, dictionary);
		}
	}
	
	private void debug(String string) {
		System.out.println("MultipleSentimentTester debug: " + string);
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

	@Override
	void test() {
		/*
		 * Create out-of-domain mcnemars
		 */
		for (String testCategory : categories) {
//			for (Classifier firstClassifier : classifiers) {
//				for (Classifier secondClassifier : classifiers) {
//					if (firstClassifier != secondClassifier) {
//						McNemar mcnemar = new McNemar(trainingCategory, testCategory, firstClassifier, secondClassifier);
//						mcNemars.add(mcnemar);
//					}
//				}
//			}
			debug("Performing tests for " + testCategory);
			for (Pair<Classifier> pair : Utilities.combinations(Arrays.asList(classifiers))) {
				McNemar mcnemar = new McNemar(trainingCategory, testCategory, pair.first, pair.other);
				mcNemars.add(mcnemar);
			}
		}
		for (McNemar nemar : mcNemars) {
			performMcNemar(nemar);
		}
	}
	
	private void performMcNemar(McNemar mcnemar) {
		HashMap<String, List<List<Feature>>> features = extractFeatures(testData.get(mcnemar.getTestCategory()));
		ClassificationResult first;
		ClassificationResult second;
		boolean firstCorrect;
		boolean secondCorrect;
		int firstResult;
		int secondResult;
		for(Entry<String, List<List<Feature>>> itemsInCategory : features.entrySet()) {
			for(List<Feature> item : itemsInCategory.getValue()) {
				first = mcnemar.getFirstClassifier().classify(item);
				second = mcnemar.getSecondClassifier().classify(item);
				firstCorrect = first.getCategory().equals(itemsInCategory.getKey());
				secondCorrect = second.getCategory().equals(itemsInCategory.getKey());
				firstResult = firstCorrect ? 1 : 0;
				secondResult = secondCorrect ? 1 : 0;
				mcnemar.setPairResult(firstResult, secondResult);
			}
		}
		
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Pairwise sentiment tester results: \n");
		for(McNemar nemar : mcNemars) {
			b.append(nemar.toString());
		}
		return b.toString();
	}

	@Override
	public String getResults() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class McNemar {
		private String trainingCategory;
		private String testCategory;
		private Classifier firstClassifier;
		private Classifier secondClassifier;
		int[][] result;
		
		public McNemar(String trainingCategory, String testCategory, Classifier firstClassifier, Classifier secondClassifier) {
			this.setTrainingCategory(trainingCategory);
			this.setTestCategory(testCategory);
			this.setFirstClassifier(firstClassifier);
			this.setSecondClassifier(secondClassifier);
			result = new int[][]{{0,0}, {0,0}};
		}
		/*
		 * 		The square looks like this
		 * 			Second Classifier
		 * 			-	+
		 * 		-
		 * 		+
		 * First classifier
		 */
		public void setPairResult(int firstClassifierResult, int secondClassifierResult) {
			result[firstClassifierResult][secondClassifierResult]++;
		}
		public String getTestCategory() {
			return testCategory;
		}
		public void setTestCategory(String testCategory) {
			this.testCategory = testCategory;
		}
		public String getTrainingCategory() {
			return trainingCategory;
		}
		public void setTrainingCategory(String trainingCategory) {
			this.trainingCategory = trainingCategory;
		}
		public Classifier getFirstClassifier() {
			return firstClassifier;
		}
		public void setFirstClassifier(Classifier firstClassifier) {
			this.firstClassifier = firstClassifier;
		}
		public Classifier getSecondClassifier() {
			return secondClassifier;
		}
		public void setSecondClassifier(Classifier secondClassifier) {
			this.secondClassifier = secondClassifier;
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("McNemar for training cat: ");
			b.append(trainingCategory);
			b.append(" and test category: ");
			b.append(testCategory);
			b.append("\nFirst classifier: " + firstClassifier.getName());
			b.append(" second classifier: " + secondClassifier.getName());
			b.append("\n");
			for(int[] row : result) {
				String s = "[";
				for (int i : row) {
					s += i + " ";
				}
				s += "]\n";
				b.append(s);
			}
			return b.toString();
		}
	}

}
