package org.panhandlers.sentimentalizer;

public class NaiveBayes implements Classifier {
	private ClassifierStorage storage;
	
	public NaiveBayes() {
		storage = new HashStorage();
	}
	
	@Override
	public void train(String category, String text) {
		// TODO: need to split strings and maybe stemming and stuff like that...
		storage.addFeature(category, text);
	}

	@Override
	public ClassificationResult classify(String text) {
		return null;
	}
	
	private float pOfFeatureGivenCategory(String feature, String category) {
		return 0.0f;
	}

}
