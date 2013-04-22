package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class AlternatePerceptron implements Classifier {
	private HashMap<String, Integer> positionMap;
	private HashMap<String, Integer> categoryToKey;
	private HashMap<Integer, String> keyToCategory;
	private ArrayList<TrainingItem> inputSet;
	private double[] weights;
	int previousErrors;
	
	
	/*
	 * Variables: bias, learning rate, initial weights...
	 */
	private double bias = 0.4;
	private double learningRate = 0.00001;
	private double errorRate;
	private Set<String> dictionary;

	public void doTrain() {
		previousErrors = Integer.MAX_VALUE;
		double actualOutput = 0;
		@SuppressWarnings("unused")
		double correction = 0;
		int errors;
		int i = 0;
		while (true) {
			errors = 0;
			for (TrainingItem item : inputSet) {
				errorRate = 0;
				actualOutput = trimOutput(dotProduct(item.vector, weights));
				// System.out.println("Category: " + item.category);
				errorRate = item.category - actualOutput;
				correction = (learningRate * errorRate);
				if (Double.isNaN(correction))
					throw new RuntimeException("SHIT WENT DOWN");
				if (errorRate != 0.0) {
					errors++;
					for (int j = 0; j < weights.length; j++) {
						weights[j] += correction * item.vector[j];
					}
				}
			}
			System.err.println("Errors: " + errors);
			if (errors == 0 || i > 2000 )
				break;
			i++;
			// if(errors > previousErrors) break;
			previousErrors = errors;

		}
		System.err.println("Errors: " + errors);

	}

	// THIS IS VERY VERY BAD HACK TO SOLVE NAN ERRORS
	private double trimOutput(double value) {
		return Double.parseDouble(String.valueOf(value).substring(0, 3)) + bias;
	}

	private double dotProduct(double[] input, double[] weight) {
		double product = 0;
		for (int i = 0; i < input.length; i++) {
			product += input[i] * weight[i];
		}
		return product;

	}

	public void print() {
		StringBuilder b = new StringBuilder();
		b.append("Weight-vector:\n");
		for (int i = 0; i < weights.length; i++) {
			b.append(weights[i]);
			b.append(", ");
		}
		System.out.println(b);
	}

	public void printVector(double[] vector) {
		StringBuilder b = new StringBuilder();
		b.append("Vector:\n");
		for (int i = 0; i < vector.length; i++) {
			b.append(vector[i]);
			b.append(", ");
		}
		System.out.println(b);
	}

	private void constructPositionMap(Set<String> dictionary) {
		int i = 0;
		positionMap = new HashMap<String, Integer>();
		for (String s : dictionary) {
			positionMap.put(s, i);
			i++;
		}
	}

	@Override
	public ClassificationResult classify(List<Feature> features) {
		double[] inputVector = new double[dictionary.size()];
		zeroVector(inputVector);
		TokenFeature feature;
		for (Feature inputFeature : features) {
			feature = (TokenFeature) inputFeature;
			inputVector[positionMap.get(feature.getToken())] += feature
					.getValue();
		}
		double result = dotProduct(weights, inputVector); // consider bias
															// here?!
		// System.out.println("Result value: " + result);
		int resultInt = result > 0 ? 1 : 0;
		ClassificationResult resultObj = new ClassificationResult();
		resultObj.setCategory(keyToCategory.get(resultInt));
		return resultObj;
	}

	@Override
	public void train(String category, List<Feature> features) {
		// TODO Auto-generated method stub

	}

	@Override
	public void multipleTrain(HashMap<String, List<List<Feature>>> trainingSet,
			Set<String> dictionary) {
		this.dictionary = dictionary;
		constructPositionMap(dictionary);
		constructCategoryMap(trainingSet);
		constructInputSet(trainingSet);
		initWeights(dictionary.size());
		doTrain();
	}

	private void initWeights(int size) {
		weights = new double[size];
		zeroWeights(weights);
	}

	private void zeroVector(double[] v) {
		for (int i = 0; i < v.length; i++) {
			v[i] = 0d;
		}
	}

	private void zeroWeights(double[] v) {
		for (int i = 0; i < v.length; i++) {
			v[i] = 0.001;
		}
	}

	private void constructInputSet(
			HashMap<String, List<List<Feature>>> trainingSet) {
		inputSet = new ArrayList<TrainingItem>();
		TrainingItem item;
		int categoryKey;
		TokenFeature tokenFeature;
		for (Entry<String, List<List<Feature>>> entry : trainingSet.entrySet()) {
			System.out.println("Constructing input set for category "
					+ entry.getKey());
			categoryKey = categoryToKey.get(entry.getKey());
			System.out.println("category key " + categoryKey);
			for (List<Feature> featureItem : entry.getValue()) {
				item = new TrainingItem();
				item.category = categoryKey;
				item.vector = initInputVector();
				for (Feature feature : featureItem) {
					tokenFeature = (TokenFeature) feature;
					item.vector[positionMap.get(tokenFeature.getToken())] += tokenFeature
							.getValue();
				}
				inputSet.add(item);
			}
		}
	}

	private double[] initInputVector() {
		double[] vector = new double[dictionary.size()];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}
		return vector;
	}

	private void constructCategoryMap(
			HashMap<String, List<List<Feature>>> trainingSet) {
		categoryToKey = new HashMap<String, Integer>();
		keyToCategory = new HashMap<Integer, String>();
		int i = 0;
		for (Entry<String, List<List<Feature>>> entry : trainingSet.entrySet()) {
			System.out.println("Register category: " + entry.getKey());
			categoryToKey.put(entry.getKey(), i);
			keyToCategory.put(i, entry.getKey());
			i++;
		}
	}

	private class TrainingItem {
		public double[] vector;
		public int category;
	}
	
	public String toString(){
		
		
		return "Perceptron ";
		
		
	}

}
