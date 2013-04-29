package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;
import java.util.Arrays;

import org.panhandlers.sentimentalizer.classifiers.AveragedPerceptron;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.classifiers.Perceptron;
import org.panhandlers.sentimentalizer.classifiers.NaiveBayes;

public class TestRunner {
	
	/*
	 * This acts as the main entry point to the program,
	 * it runs different types of tests (in domain, out of domain, category)
	 * and cross validation tests. It also runs the KNN tests. 
	 */
	
	private static final String[] CATEGORIES = new String[]{"books", "software", "dvd", "health", "music", "camera"};
	private static final int RATIO = 10;
	private static final int DICTIONARY_SIZE = 500;
	private static final int CROSS_VALIDATION_SLICES = 10;
	private ArrayList<Test> tests;
	private TestEnvironment env;
	
	public TestRunner(){
		tests = new ArrayList<Test>();
		env = new TestEnvironment();
		createTests();
	}
	
	/*
	 * This method constructs the tests for each classifier
	 * that we want to run
	 */
	private void createTests() {
//		Test t;
		int i = 1;
		
		// KNN INDOMAIN Sentiment Test
		//KNearestNeighbor knn = new KNearestNeighbor(env, RATIO, DICTIONARY_SIZE, "dvd", 7);
		
		// KNN OUTOFDOMAIN Sentiment Test
		//KNearestNeighbor knn = new KNearestNeighbor(env, RATIO, DICTIONARY_SIZE, "music", "books", 4);
		
		// KNN CATEGORY Test
//		KNearestNeighbor knn = new KNearestNeighbor(env, RATIO, DICTIONARY_SIZE, 4); // takes time
//
//		knn.train(); //Train KNN
//		knn.test();  //Test KNN
		
		Classifier[] classifiers = new Classifier[] {new Perceptron(), new AveragedPerceptron(), new NaiveBayes(env.getStorage())};
		MultipleSentimentTester t = new MultipleSentimentTester(env,
				RATIO, DICTIONARY_SIZE, classifiers, CATEGORIES, "dvd");
		tests.add(t);
//		for (Classifier classifier : classifiers) {
////			CategoryTest categoryTest = new CategoryTest(env, classifier, RATIO, DICTIONARY_SIZE);
//			CategoryCrossValidation categoryTest = new CategoryCrossValidation(env, classifier, RATIO, DICTIONARY_SIZE, CROSS_VALIDATION_SLICES);
//			categoryTest.setCategories(Arrays.asList(CATEGORIES));
//			tests.add(categoryTest);
//			/*
//			 * Run in-domain tests
//			 */
//			for (String category : CATEGORIES) {
//				//t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category);
//				//tests.add(t);
////				t = new SentimentCrossValidation(env, classifier, RATIO, DICTIONARY_SIZE, CROSS_VALIDATION_SLICES, category);
////				tests.add(t);
////				t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category);
////				tests.add(t);
////				for(; i < CATEGORIES.length; i++) {
////					t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category, CATEGORIES[i]);
////					tests.add(t);
////				}
//			}
//		}
	}

	/*
	 * Loops through the tests and runs each of them
	 */
	public void runTests() {
		for (Test t : tests) {
			t.run();
		}
		System.out.println("Test run complete");
		for (Test t : tests) {
			System.out.println(t.toString());
		}
	}
	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		runner.runTests();
	}
}
