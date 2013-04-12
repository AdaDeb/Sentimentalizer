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
		this.dictionary = dictionary;
	}
	@Override
	public List<Feature> extractFeatures(ArrayList<String> input) {
		tokensToFeatures = new HashMap<String, OccurrenceFeature>();
		features = new ArrayList<Feature>();
		// Initialize with 0 occurrences for each token in dictionary
		OccurrenceFeature feature;
		for (String token : dictionary) {
			feature = new OccurrenceFeature();
			features.add(feature);
			tokensToFeatures.put(token, new OccurrenceFeature());
		}
		for (String token : input) {
			if (tokensToFeatures.containsKey(token)) {
				tokensToFeatures.get(token).increment();
			}
		}
		return features;
	}

}
