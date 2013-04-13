package org.panhandlers.sentimentalizer.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.panhandlers.sentimentalizer.ClassificationResult;
import org.panhandlers.sentimentalizer.DataDivider;
import org.panhandlers.sentimentalizer.DictionaryBuilder;
import org.panhandlers.sentimentalizer.Feature;
import org.panhandlers.sentimentalizer.NaiveBayes;
import org.panhandlers.sentimentalizer.OccurrenceFeatureExtractor;

public class RedisBayesTester {
	private RedisDataReader reader;
	private DataDivider divider;
	private OccurrenceFeatureExtractor extractor;
	private NaiveBayes classifier;
	RedisBayesTester() {
		classifier = new NaiveBayes(new RedisStorage());
		reader = new RedisDataReader();
		divider = new DataDivider(9);
		extractor = new OccurrenceFeatureExtractor();
	}
	
	private void run() {
		train();
		test();
	}
	
	private void test() {
		ClassificationResult result;
		List<Feature> features;
		int successes = 0;
		int failures = 0;
		for (Entry<String, List<List <String>>> cat : divider.getTestData().entrySet()) {
			for (List<String> item : cat.getValue()) {
				features = extractor.extractFeatures(item);
				result = classifier.classify(features);
				if (result.getCategory().equals(cat.getKey())) {
					successes++;
				} else {
					failures++;
				}
			}
		}
		System.out.println("Successes: " + successes + " Failures: " + failures);
	}

	private void train() {
		List<List<String>> positive = reader.getItemsByCategoryAndSentiment("software", "pos");
		List<List<String>> negative = reader.getItemsByCategoryAndSentiment("software", "neg");
		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("pos", positive);
		data.put("neg", negative);
		divider.divide(data);
		Set<String> dictionary = DictionaryBuilder.buildDictionary(divider.getTrainingData());
		extractor.setDictionary(dictionary);
		List<Feature> features;
		for (Entry<String, List<List<String>>> cat: divider.getTrainingData().entrySet()) {
			for(List<String> item : cat.getValue()) {
				features = extractor.extractFeatures(item);
				classifier.train(cat.getKey(), features);
			}
		}
	}

	public static void main(String[] args) {
		RedisBayesTester t = new RedisBayesTester();
		t.run();
	}
}
