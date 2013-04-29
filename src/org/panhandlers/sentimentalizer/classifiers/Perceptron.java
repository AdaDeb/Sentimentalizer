package org.panhandlers.sentimentalizer.classifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.panhandlers.sentimentalizer.features.Feature;
import org.panhandlers.sentimentalizer.features.TokenFeature;

public class Perceptron implements Classifier {

	/**
	 * In this class we implement the perceptron algorithm
	 * it is also responsible for building the input HashMaps used
	 * by different methods
	 */
	
	private HashMap<String, Integer> positionMap;
	private HashMap<String, Integer> categoryToKey;
	private HashMap<Integer, String> keyToCategory;
	private HashMap<Integer, double[]> categoryToWeights = new HashMap<Integer, double[]>();
	private ArrayList<TrainingItem> inputSet;

	/*
	 * Variables: bias, learning rate, initial weights...
	 */
	private double bias = 0.4;
	private double learningRate = 0.0001;// 0.00001;
	private double errorRate;
	private Set<String> dictionary;


	/*
	 * Training algorithm
	 */
	public void doTrain() {
		double actualOutput = 0;
		double correction = 0;
		int errors;
		int i = 0;
		int expectedOutput = 0;
		double[] currentWeightVector;
		double N = 0;
		while (true) {
			errors = 0;
			for (TrainingItem item : inputSet) { // loop over training set
				for (Entry<Integer, double[]> weightVector : categoryToWeights // find concerned weight vector
						.entrySet()) {
					currentWeightVector = weightVector.getValue();
					expectedOutput = weightVector.getKey() == item.category ? 1 // get expected output
							: 0;
					actualOutput = dotProduct(item.vector, currentWeightVector); //predict actual output
					errorRate = expectedOutput - actualOutput;
					correction = learningRate * errorRate;
					if (errorRate != 0.0) { // predicted output was wrong, updating weights 
						N = 0;
						for (int j = 0; j < currentWeightVector.length; j++) {
							currentWeightVector[j] += correction
									* item.vector[j];
							N += Math.pow(currentWeightVector[j], 2);
						}
						// Normalizing vector to avoid NaN errors
						for (int j = 0; j < currentWeightVector.length; j++) {
							currentWeightVector[j] /= Math.sqrt(N);
						}
					}
				}
				errors++;
			}
			if (errors == 0 || i > 350)
				break;
			i++;
		}
	}


	/*
	 * Calculate dot product of two vectors
	 */
	private double dotProduct(double[] input, double[] weight) {
		double product = 0;
		for (int i = 0; i < input.length; i++) {
			product += input[i] * weight[i];
		}
		return product;
	}


	/*
	 * Print out the weight vectors and their elements
	 */
	public void print() {
		StringBuilder b = new StringBuilder();
		b.append("Weight-vector(s):\n");
		for (Entry<Integer, double[]> weightVector : categoryToWeights
				.entrySet()) {
			for (int i = 0; i < weightVector.getValue().length; i++) {
				b.append(weightVector.getValue()[i]);
				b.append(", ");
			}
		}
		System.out.println(b);
	}
	

	/*
	 * Prints any vector
	 */
	public void printVector(double[] vector) {
		StringBuilder b = new StringBuilder();
		b.append("Vector:\n");
		for (int i = 0; i < vector.length; i++) {
			b.append(vector[i]);
			b.append(", ");
		}
		System.out.println(b);
	}


	/*
	 * Constructs the position map so that we know
	 * where each token belongs
	 */
	private void constructPositionMap(Set<String> dictionary) {
		int i = 0;
		positionMap = new HashMap<String, Integer>();
		for (String s : dictionary) {
			positionMap.put(s, i);
			i++;
		}
	}


	/*
	 * Classification algorithm used after training, we simply calculate
	 * the dot product for all available weight vectors and chose the highest 
	 * result. Finally, the category corresponding to the highest result found
	 * is looked up and set in the result object. 
	 */
	@Override
	public ClassificationResult classify(List<Feature> features) {
		double[] inputVector = new double[dictionary.size()];
		ArrayList<Double> resultArray = new ArrayList<Double>();
		zeroVector(inputVector);
		TokenFeature feature;
		for (Feature inputFeature : features) {
			feature = (TokenFeature) inputFeature;
			inputVector[positionMap.get(feature.getToken())] += feature.getValue();
		}
		for (Entry<Integer, double[]> weightVector : categoryToWeights.entrySet()) {
			resultArray.add(dotProduct(weightVector.getValue(), inputVector)); //Calculate dot product results
		}
		int posResult = getMaxPos(resultArray); // get position of highest result
		ClassificationResult resultObj = new ClassificationResult();
		resultObj.setCategory(keyToCategory.get(posResult)); // lookup category of result
		return resultObj;
	}


	/*
	 * Returns the position of the highest number in an array
	 */
	private int getMaxPos(ArrayList<Double> array) {
		int pos = 0;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) > array.get(pos))
				pos = i;
		}
		return pos;
	}

	@Override
	public void train(String category, List<Feature> features) {
		// TODO Auto-generated method stub

	}


	/*
	 * Converts the training set sent in and dictionary into hash maps
	 * used by the main train method
	 */
	@Override
	public void multipleTrain(HashMap<String, List<List<Feature>>> trainingSet,
			Set<String> dictionary) {
		this.dictionary = dictionary;
		constructPositionMap(dictionary);
		constructCategoryMap(trainingSet);
		constructInputSet(trainingSet);
		initMultipleWeights(trainingSet, dictionary.size());
		doTrain();

		// Memory optimizations
		inputSet = null;
	}
	

	/*
	 * Creates the needed number of weight vectors depending on number
	 * of categories (two in case of sentiment analysis and six for 
	 * category test)
	 */
	private void initMultipleWeights(
			HashMap<String, List<List<Feature>>> trainingData, int size) {
		for (Entry<String, List<List<Feature>>> cat : trainingData.entrySet()) {
			categoryToWeights.put(categoryToKey.get(cat.getKey()),
					new double[size]);
		}
	}
	

	/*
	 * Initiate a vector's elements to 0s
	 */
	private void zeroVector(double[] v) {
		for (int i = 0; i < v.length; i++) {
			v[i] = 0d;
		}
	}


	/*
	 * Responsible for building the Input set for different categories
	 */
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


	/*
	 * Initiates the input vector to 0s
	 */
	private double[] initInputVector() {
		double[] vector = new double[dictionary.size()];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}
		return vector;
	}


	/*
	 * Helper method for constructing the hash map that maps 
	 * the category to the keys.
	 */
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

	@Override
	public String toString() {
		return "Non-averaged Perceptron ";
	}
	
	@Override
	public String getName() {
		return "Non-averaged Perceptron";
	}

}
