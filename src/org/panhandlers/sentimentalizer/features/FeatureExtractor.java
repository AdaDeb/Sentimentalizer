package org.panhandlers.sentimentalizer.features;

import java.util.List;

public interface FeatureExtractor {
	public List<Feature> extractFeatures(List<String> tokens);
}
