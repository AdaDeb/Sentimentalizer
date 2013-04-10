package org.panhandlers.sentimentalizer;

public interface Classifier {
	public void train(String category, String text);
	public ClassificationResult classify(String text);
}
