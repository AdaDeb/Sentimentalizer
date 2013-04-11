package org.panhandlers.sentimentalizer;

public interface FeatureExtractor {
	public Feature[] extractFeatures(String input);
}
