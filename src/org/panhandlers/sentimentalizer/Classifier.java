package org.panhandlers.sentimentalizer;

public interface Classifier {
	public void train(String category, String text);
	public void classify(String text);
}
