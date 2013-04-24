package org.panhandlers.sentimentalizer.testing;

import org.panhandlers.sentimentalizer.DataDivider;
import org.panhandlers.sentimentalizer.classifiers.Classifier;
import org.panhandlers.sentimentalizer.storage.DictionaryBuilder;

public abstract class Test {
	private TestEnvironment env;
	private Classifier classifier;
	private DataDivider divider;
	private DictionaryBuilder dictionaryBuilder;
	private double successRate;
	
	public void run() {
		train();
		test();
	}
	abstract void test();
	abstract void train();
	
	public abstract String getResults();
	
	public Test(TestEnvironment env, Classifier classifier, int ratio, int dictionarySize) {
		this.env = env;
		this.divider = new DataDivider(ratio);
		this.dictionaryBuilder = new DictionaryBuilder(dictionarySize);
		this.setClassifier(classifier);
	}
	
	public TestEnvironment getEnv() {
		return env;
	}
	
	public void setEnv(TestEnvironment env) {
		this.env = env;
	}
	
	public Classifier getClassifier() {
		return classifier;
	}
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public DictionaryBuilder getDictionaryBuilder() {
		return dictionaryBuilder;
	}
	public void setDictionaryBuilder(DictionaryBuilder dictionaryBuilder) {
		this.dictionaryBuilder = dictionaryBuilder;
	}

	public DataDivider getDivider() {
		return divider;
	}
	public void setDivider(DataDivider divider) {
		this.divider = divider;
	}

	public double getSuccessRate() {
		return successRate;
	}
	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}

	public enum Type {
		IN_DOMAIN, OUT_OF_DOMAIN, CATEGORY;
	}
}
