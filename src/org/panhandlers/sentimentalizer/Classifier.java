package org.panhandlers.sentimentalizer;

import java.util.List;

public interface Classifier {
	/**
	 * Train the Classifier for one item. 
	 * @param category
	 * @param features A list of features that make up one item.
	 */
	public void train(String category, List<Feature> features);
	public ClassificationResult classify(List<Feature> features);
}
