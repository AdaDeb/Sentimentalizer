package org.panhandlers.sentimentalizer;

import java.util.List;

public interface FeatureExtractor {
	public List<Feature> extractFeatures(List<String> tokens);
}
