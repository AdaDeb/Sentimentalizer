package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.panhandlers.sentimentalizer.Utilities;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.testing.CategoryTest;
import org.panhandlers.sentimentalizer.testing.Test.Type;

public class CategoryCrossValidation extends Test {
	
	/*
	 * Responsible with running cross validation 
	 * tests for category tests
	 */
	
	private int numberOfSlices;
	private ArrayList<CategoryTest> tests;
	private int ratio;
	private int dictionarySize;
	private Double averageSuccessRate;
	private Double standardDeviation;
	private List<String> categories;
	private HashMap<String, Test> testsPerCategory;
	
	public CategoryCrossValidation(TestEnvironment env, Classifier classifier, int ratio, int dictionarySize, int numberOfSlices) {
		super(env, classifier, ratio, dictionarySize);
		this.dictionarySize = dictionarySize;
		this.ratio = ratio;
		this.numberOfSlices = numberOfSlices;
		testsPerCategory = new HashMap<String, Test>();
	}

	@Override
	void test() {
		buildTest();
		runTest();
	}

	/*
	 * Builds a list of tests to run depending on the
	 * number of slices desired
	 */
	private void buildTest() {
		CategoryTest test;
		tests = new ArrayList<CategoryTest>(numberOfSlices);
		for(int i = 0; i< numberOfSlices; i++) {
			test = new CategoryTest(getEnv(), getClassifier(), this.ratio, this.dictionarySize, i, true);
			if(categories != null) {
				test.setCategories(categories);
			}
			tests.add(test);
		}
	}
	
	/*
	 * Runs each test that was previously built and 
	 * records the results and success rate
	 */
	private void runTest() {
		ArrayList<Double> successRates = new ArrayList<Double>();
		Double sumOfSuccessRates= 0d;
		for (CategoryTest test : tests) {
			test.run();
			successRates.add(test.getSuccessRate());
			sumOfSuccessRates += test.getSuccessRate();
		}
		averageSuccessRate = sumOfSuccessRates / successRates.size();
		standardDeviation = Utilities.standardDeviation(successRates);
	}

	@Override
	void train() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getResults() {
		return toString();
	}
	
	/*
	 * Prints out the results of cross validation tests
	 * in an easy to read manner
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getClassifier().toString());
		b.append("CrossValidation of ");
		b.append(tests.get(0).toString());
		b.append(", divided data into ");
		b.append(numberOfSlices);
		b.append(" slices.");
		b.append("\nAverage success rate is ");
		b.append(averageSuccessRate);
		b.append("\nStandard deviation is ");
		b.append(standardDeviation);
		b.append("\nIndividual results:\n");
		for (String category : categories) {
			b.append(averageSuccessRateForCategory(category));
		}
		for (String category : categories) {
			b.append(standardDevationForCategory(category));
		}
		return b.toString();
	}

	private String standardDevationForCategory(String category) {
		ArrayList<Double> successRates = new ArrayList<Double>();
		for (CategoryTest test : tests) {
			successRates.add(test.successRateForCategory(category));
		}
		Double sd = Utilities.standardDeviation(successRates);
		return "Standard deviation for " + category + " is " + sd + "\n";
	}

	/*
	 * Calculates the average success rate of a cross validation 
	 * test for a specific category
	 */
	private String averageSuccessRateForCategory(String category) {
		Double total = 0d;
		for (CategoryTest test : tests) {
			total += test.successRateForCategory(category);
		}
		Double average = total / (double) tests.size();
		return "Average success rate for " + category + " is " + average + "\n";
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> list) {
		this.categories = list;
	}

}
