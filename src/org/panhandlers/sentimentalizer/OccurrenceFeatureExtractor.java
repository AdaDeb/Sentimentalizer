package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OccurrenceFeatureExtractor implements FeatureExtractor {
	private Set<String> dictionary;
	private HashMap<String, OccurrenceFeature> tokensToFeatures;
	private ArrayList<Feature> features;
	
	public OccurrenceFeatureExtractor (Set<String> dictionary) {
		this.setDictionary(dictionary);
	}
	
	public OccurrenceFeatureExtractor() {}
	
	@Override
	public List<Feature> extractFeatures(List<String> input) {
		tokensToFeatures = new HashMap<String, OccurrenceFeature>();
		features = new ArrayList<Feature>();
		// Initialize with 0 occurrences for each token in dictionary
		OccurrenceFeature feature;
		for (String token : getDictionary()) {
			feature = new OccurrenceFeature(token);
			features.add(feature);
			tokensToFeatures.put(token, feature);
		}
		for (String token : input) {
			if (tokensToFeatures.containsKey(token)) {
				tokensToFeatures.get(token).increment();
			}
		}
		return features;
	}

	public Set<String> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Set<String> dictionary) {
		this.dictionary = dictionary;
	}

}
