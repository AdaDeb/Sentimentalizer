package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.List;

public interface FeatureExtractor {
	public List<Feature> extractFeatures(ArrayList<String> tokens);
}
