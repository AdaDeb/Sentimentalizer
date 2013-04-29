package org.panhandlers.sentimentalizer.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ExistenceFeatureExtractor implements FeatureExtractor {
	
	/*
	 * This class is responsible of extracting the existence features, 
	 * that is to say whether a token exists or not
	 */
	
	private Set<String> dictionary;
	private HashMap<String, ExistenceFeature> tokensToFeatures;
	public ExistenceFeatureExtractor() {
		this.tokensToFeatures = new HashMap<String, ExistenceFeature>();
	}
	
	public ExistenceFeatureExtractor(Set<String> dictionary) {
		this.tokensToFeatures = new HashMap<String, ExistenceFeature>();
		this.dictionary = dictionary;
	}
	
	/*
	 * This method loops through a list of tokens and creates
	 * an existence feature object for each, it also sets the 
	 * existence boolean if a token exists or not
	 */
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
		return features;
	}
	public Set<String> getDictionary() {
		return dictionary;
	}
	public void setDictionary(Set<String> dictionary) {
		this.dictionary = dictionary;
	}

}
