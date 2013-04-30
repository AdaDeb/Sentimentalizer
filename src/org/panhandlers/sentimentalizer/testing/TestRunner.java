package org.panhandlers.sentimentalizer.testing;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.panhandlers.sentimentalizer.classifiers.AveragedPerceptron;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.classifiers.Perceptron;
import org.panhandlers.sentimentalizer.classifiers.NaiveBayes;
import org.panhandlers.sentimentalizer.storage.HashStorage;

public class TestRunner {
	/*
	 * This acts as the main entry point to the program,
	 * it runs different types of tests (in domain, out of domain, category)
	 * and cross validation tests. It also runs the KNN tests. 
	 */
	
	private static final String[] CATEGORIES = new String[]{"books", "dvd", "health", "music", "camera", "software"};
	private static final int RATIO = 10;
	private static final int DICTIONARY_SIZE = 500;
	private static final int CROSS_VALIDATION_SLICES = 5;
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
		
		//Uncomment below to run category tests for KNN
//		KNearestNeighbor knn = new KNearestNeighbor(env, RATIO, DICTIONARY_SIZE, 4);
//		knn.train();
//		knn.test();
//		knn.categoryCrossValidation(); // // Use this to run cross validation but comment train and test above.

		// Uncomment below loop to run in domain for KNN
//		for(i = 0; i < CATEGORIES.length; i++) {
//			System.out.println("Testing category:" + CATEGORIES[i]);
//			KNearestNeighbor knn = new KNearestNeighbor(env, RATIO, DICTIONARY_SIZE, CATEGORIES[i], 4);
//			knn.train();
//			knn.test();
//			knn.inDomainCrossValidation(); // Use this to run cross validation but comment train and test above.
//		}
		
		//Uncomment the code below to run out of domain for KNN
//		for (i = 0; i < CATEGORIES.length; i++) {
//			String currentCategory = CATEGORIES[i];
//			for (int j = 0; j < CATEGORIES.length; j++) {
//				if (!currentCategory.equals(CATEGORIES[j])) {
//					System.out.println("Training on category:"
//							+ currentCategory + " and testing on "
//							+ CATEGORIES[j]);
//					KNearestNeighbor knn = new KNearestNeighbor(env, RATIO,
//							DICTIONARY_SIZE, currentCategory, CATEGORIES[j], 4);
//					knn.train();
//					knn.test();
//				}
//			}
//		}
		
		
		
		
		
		
		
//		knn.train(); //Train KNN
//		knn.test();  //Test KNN
		
		Classifier[] classifiers = new Classifier[] {new Perceptron(), new AveragedPerceptron()};//new NaiveBayes(new HashStorage())};
//		MultipleSentimentTester t = new MultipleSentimentTester(env,
//				RATIO, DICTIONARY_SIZE, classifiers, CATEGORIES, "dvd");
//		tests.add(t);
		for (Classifier classifier : classifiers) {
////			CategoryTest categoryTest = new CategoryTest(env, classifier, RATIO, DICTIONARY_SIZE);
//			CategoryCrossValidation categoryTest = new CategoryCrossValidation(env, classifier, RATIO, DICTIONARY_SIZE, CROSS_VALIDATION_SLICES);
//			categoryTest.setCategories(Arrays.asList(CATEGORIES));
//			tests.add(categoryTest);
//			/*
//			 * Run in-domain tests
//			 */
			for (String category : CATEGORIES) {
				MultiSentimentAnalyzer t = new MultiSentimentAnalyzer(env,
						RATIO, DICTIONARY_SIZE, classifier, CATEGORIES, category);
				tests.add(t);
//				Test t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category);
//				tests.add(t);
////				t = new SentimentCrossValidation(env, classifier, RATIO, DICTIONARY_SIZE, CROSS_VALIDATION_SLICES, category);
////				tests.add(t);
////				t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category);
////				tests.add(t);
////				for(; i < CATEGORIES.length; i++) {
////					t = new SentimentTest(env, classifier, RATIO, DICTIONARY_SIZE, category, CATEGORIES[i]);
////					tests.add(t);
////				}
			}
		}
	}

	/*
	 * Loops through the tests and runs each of them
	 */
	public void runTests() {
		for (Test t : tests) {
			t.run();
		}
		System.out.println("Test run complete");
		try {
		PrintWriter writer = new PrintWriter("TestResult" + new Date(), "UTF-8");
			for (Test t : tests) {
				writer.println(t.toString());
//				System.out.println(t.toString());
			}
			writer.close();
		} catch(Exception c) {
			System.out.println("Could not write result to file");
			for (Test t : tests) {
				System.out.println(t.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		runner.runTests();
	}
}
