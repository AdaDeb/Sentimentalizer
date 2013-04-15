package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class BayesTest {
	private DataReader reader;
	private NaiveBayes classifier;
	private Set<String> dictionary;
	private OccurrenceFeatureExtractor extractor;
	private DataDivider dataDivider;
	
	public BayesTest() {
		setup();
		run();
	}
	
	private void run() {
		List<Feature> features;
		for(Entry<String, List<List<String>>> pair : dataDivider.getTrainingData().entrySet()) {
			for(List<String> item : pair.getValue()) {
				features = extractor.extractFeatures(item);
				classifier.train(pair.getKey(), features);
			}
		}
		System.out.println("Learning complete");
		List<String> testItem = dataDivider.getTestData().values().iterator().next().get(0);
		List<Feature> testItemFeatures = extractor.extractFeatures(testItem);
		ClassificationResult r = classifier.classify(testItemFeatures);
		System.out.println(r);
	}

	private void setup() {
		reader = new CategoryDataReader("amazon-balanced-6cats");
		System.out.println("Reader done");
		classifier = new NaiveBayes();
		dictionary = DictionaryBuilder.buildDictionary(reader.getData());
		System.out.println("Built dictionary");
		dataDivider = new DataDivider(9, reader.getData());
		extractor = new OccurrenceFeatureExtractor(dictionary);
	}

	public static void main (String[] args) {
		BayesTest test = new BayesTest();
		System.out.println("At least nothing crashed");
	}
}
