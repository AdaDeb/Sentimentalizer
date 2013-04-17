package org.panhandlers.sentimentalizer.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.ClassificationResult;
import org.panhandlers.sentimentalizer.DataDivider;
import org.panhandlers.sentimentalizer.DictionaryBuilder;
import org.panhandlers.sentimentalizer.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.Feature;
import org.panhandlers.sentimentalizer.NaiveBayes;
import org.panhandlers.sentimentalizer.OccurrenceFeature;
import org.panhandlers.sentimentalizer.OccurrenceFeatureExtractor;
import org.panhandlers.sentimentalizer.Perceptron;

public class RedisPerceptronTester {

	private RedisDataReader reader;
	private DataDivider divider;
	private OccurrenceFeatureExtractor extractor;
	private Perceptron classifier;
	private RedisStorage storage;
	
	public RedisPerceptronTester(){
		storage = new RedisStorage();
		classifier = new Perceptron(storage);
		reader = new RedisDataReader("amazon-balanced-6cats");
		divider = new DataDivider(9);
		extractor = new OccurrenceFeatureExtractor();
		
	}
	

	private void run() {
		train();
		//test();
	}
	
	private void test(){
//		ClassificationResult result;
//		List<OccurrenceFeature> features;
//		int successes = 0;
//		int failures = 0;
//		for (Entry<String, List<List <String>>> cat : divider.getTestData().entrySet()) {
//			for (List<String> item : cat.getValue()) {
//				features = extractor.extractFeatures(item);
//				//result = classifier.classify(features);
//				if (result.getCategory().equals(cat.getKey())) {
//					System.out.println("Succeeded for: " + detokenize(item) +"\n which was supposed to be " + cat.getKey());
//					successes++;
//				} else {
//					System.out.println("Failed for: " + detokenize(item));
//					failures++;
//				}
//			}
//		}
//		System.out.println("Successes: " + successes + " Failures: " + failures);
	}
	
	private String detokenize(List<String> item) {
		StringBuilder b = new StringBuilder();
		for (String s : item) {
			b.append(s);
		}
		return b.toString();
	}
	
	
	private HashMap<String, Integer> percepInput(Set<String> dict){
		HashMap<String, Integer> dictHash = new HashMap<>();
		int pos = 0;
		for (String item : dict) {
			//System.out.println(item);
			dictHash.put(item, pos);
			pos++;
		}
		
		return dictHash;

	}
	
	
	
	private void train(){
		storage.reset();
		List<List<String>> positive = reader.getItemsByCategoryAndSentiment("music", "pos");
		List<List<String>> negative = reader.getItemsByCategoryAndSentiment("music", "neg");
		HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
		data.put("pos", positive);
		data.put("neg", negative);
		divider.divide(data);
		DictionaryBuilder build = new DictionaryBuilder(500);
		Set<String> dictionary = build.buildDictionary(divider.getTrainingData());
	   System.out.println();
		HashMap<String, Integer> dictHash = percepInput(dictionary);
		//percepInput(dictionary);
		System.out.println("New hash is: " + dictHash.size());
		//classifier.createInput(dictHash, features);
		System.out.println("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
		List<Feature> features;
		for (Entry<String, List<List<String>>> cat: divider.getTrainingData().entrySet()) {
			System.out.println("Training for: " + cat.getKey() + " with " + cat.getValue().size() + " items");
			for(List<String> item : cat.getValue()) {	    
				System.out.println("Training item with length: " + item.size());
				features = extractor.extractFeatures(item);
//				for (OccurrenceFeature feat : features){
//					System.out.println("Occurences: " + feat.getToken());
//				}
			    long startTime = System.currentTimeMillis();
			    System.out.println();
				System.out.println("Extraction complete with length: " + features.size());
				classifier.createInput(dictHash, features);
				
				classifier.train();
				classifier.print();
				long stopTime = System.currentTimeMillis();
			    long elapsedTime = stopTime - startTime;
			    System.out.println("Training  completed in " + elapsedTime);
			    break;
			}
		}
		System.out.println("Training complete");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RedisPerceptronTester percepTester = new RedisPerceptronTester();
		percepTester.run();
	}

}
