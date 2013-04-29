package org.panhandlers.sentimentalizer.classifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.panhandlers.sentimentalizer.features.Feature;

public interface Classifier {
	/**
	 * Train the Classifier for one item. 
	 * @param category
	 * @param features A list of features that make up one item.
	 */
	public void train(String category, List<Feature> features);
	
	/**
	 * Classify an item, represented by a list of features.
	 * @param features
	 * @return
	 */
	public ClassificationResult classify(List<Feature> features);
	
	/**
	 * Train the classifier on multiple items
	 */
	public void multipleTrain(HashMap<String, List<List<Feature>>> trainingSet, Set<String> dictionary);

	public String getName();
}
