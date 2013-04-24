package org.panhandlers.sentimentalizer.testing;

import java.util.ArrayList;

import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.testing.Test.Type;

public class CategoryCrossValidation extends Test {
	private int numberOfSlices;
	private ArrayList<Test> tests;
	private int ratio;
	private int dictionarySize;
	private Double averageSuccessRate;
	
	public CategoryCrossValidation(TestEnvironment env, Classifier classifier, int ratio, int dictionarySize, int numberOfSlices) {
		super(env, classifier, ratio, dictionarySize);
		this.dictionarySize = dictionarySize;
		this.ratio = ratio;
		this.numberOfSlices = numberOfSlices;
	}

	@Override
	void test() {
		buildTest();
		runTest();
	}

	private void buildTest() {
		CategoryTest test;
		tests = new ArrayList<Test>(numberOfSlices);
		for(int i = 0; i< numberOfSlices; i++) {
			test = new CategoryTest(getEnv(), getClassifier(), this.ratio, this.dictionarySize, i);
			tests.add(test);
		}
	}

	private void runTest() {
		ArrayList<Double> successRates = new ArrayList<Double>();
		Double sumOfSuccessRates= 0d;
		for (Test test : tests) {
			test.run();
			successRates.add(test.getSuccessRate());
			sumOfSuccessRates += test.getSuccessRate();
		}
		averageSuccessRate = sumOfSuccessRates / successRates.size();
	}

	@Override
	void train() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getResults() {
		return toString();
	}
	
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
		return b.toString();
	}

}
