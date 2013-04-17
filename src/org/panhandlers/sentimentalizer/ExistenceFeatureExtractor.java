package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ExistenceFeatureExtractor implements FeatureExtractor {
	private Set<String> dictionary;
	private HashMap<String, ExistenceFeature> tokensToFeatures;
	public ExistenceFeatureExtractor() {
		this.tokensToFeatures = new HashMap<String, ExistenceFeature>();
	}
	public ExistenceFeatureExtractor(Set<String> dictionary) {
		this.tokensToFeatures = new HashMap<String, ExistenceFeature>();
		this.dictionary = dictionary;
	}
	@Override
	public List<Feature> extractFeatures(List<String> tokens) {
		List<Feature> features = new ArrayList<Feature>(dictionary.size());
		ExistenceFeature feature;
		for (String token : dictionary) {
			feature = new ExistenceFeature(token);
			tokensToFeatures.put(token, feature);
			features.add(feature);
		}
		for (String token : tokens) {
			feature = tokensToFeatures.get(token);
			if (feature != null) {
				feature.setExists(true);
			}
		}
//		for (String token : tokens) {
//			feature = tokensToFeatures.get(token);
//			if(feature == null) {
//				feature = new ExistenceFeature(token);
//				feature.setExists(true);
//				features.add(feature);
//			}
//		}
		return features;
	}
	public Set<String> getDictionary() {
		return dictionary;
	}
	public void setDictionary(Set<String> dictionary) {
		this.dictionary = dictionary;
	}

}
