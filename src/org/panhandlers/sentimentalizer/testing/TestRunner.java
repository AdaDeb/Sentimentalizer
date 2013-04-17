package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;

import org.panhandlers.sentimentalizer.Classifier;
import org.panhandlers.sentimentalizer.NaiveBayes;



public class TestRunner {
	private static final String[] CATEGORIES = new String[]{"books", "camera", "dvd", "health", "music", "software"};
	private static final int RATIO = 10;
	private static final int DICTIONARY_SIZE = 500;
	private ArrayList<Test> tests;
	private TestEnvironment env;
	
	public TestRunner(){
		tests = new ArrayList<Test>();
		env = new TestEnvironment();
		createTests();
	}
	
	private void createTests() {
		Test t;
		Classifier[] classifiers = new Classifier[]{new NaiveBayes(env.getStorage())};
		for (Classifier classifier : classifiers) {
			for (String category : CATEGORIES) {
				t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category);
				tests.add(t);
			}
		}
	}

	public void runTests() {
		for (Test t : tests) {
			t.run();
		}
		for (Test t : tests) {
			System.out.println(t.toString());
		}
	}
	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		runner.runTests();
	}
}
