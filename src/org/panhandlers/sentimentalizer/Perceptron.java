package org.panhandlers.sentimentalizer;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Perceptron implements Classifier {
	
	private ClassifierStorage storage;

	
	private double threshold = 0.5;
	private double learningRate = 0.1;
	private double errorRate;

	public Perceptron(ClassifierStorage storage) {
		this.storage = storage;
	}

	/*
	 * Input data structure will be improved!
	 * To test: create a new AveragedPerceprtron then run train and print 
	 */
	//double[] output = { 1, 1, 1, 0 };
	
	double input[][];
	double output[];

	//double[][] trainingDataSet = { { 1, 0, 0 }, { 1, 0, 1 }, { 1, 1, 0 },
			//{ 1, 1, 1 }, };

	double[][] trainingDataSet;
	//double[] weights = { 0, 0, 0 };
	double[] weights = {0,0};
	double[] averagedWeights = { 0, 0, 0 };

	
	public void createInput(HashMap<String, Integer> dictHash, List<Feature> features){
		
		this.trainingDataSet = new double[dictHash.size()][2];
		int i=0;
		for (Feature feature : features) {
			OccurrenceFeature feat = (OccurrenceFeature) feature;
			double[] tuple = {dictHash.get(feat.getToken()), feat.getOccurrences()};
//			System.out.println("Value" + feat.getOccurrences());
//			System.out.println();
			trainingDataSet[i] = tuple;			
		}
		System.out.println("Training set size is: " + trainingDataSet.length);
		//this.input = array;
		this.output = new double[dictHash.size()];
		//this.weights = {0,0};
		//this.trainingDataSet = input;
		for(int j=0; j < output.length; j++){
			output[j] = 1; //negative doc/ output
			//weights[j] = 0;
		}
		System.out.println("===============>>>>> EXIEXITISIS!!!");
		
	//	System.out.println("Weights array size: " + weights.length);
	//	for(int k=0; k < trainingDataSet.length; k++){
//			System.out.println("Weight " + j  + " is: " + weights[j]);
//		
		//System.out.println("Output array size: " + output.length);
		//for(int j=0; j < trainingDataSet[k].length; j++){
	//		System.out.println("Pos: " + trainingDataSet[k][0]  + " is: " + trainingDataSet[k][1]);
		//}
	//	}
		
	}
	
	
	
	public void train() {
		double actualOutput = 0;
		double out = 0;
		@SuppressWarnings("unused")
		double correction = 0;
		int errors;
		while (true) {
			errors = 0;
			for (int i = 0; i < trainingDataSet.length; i++) {
				actualOutput = dotProduct(trainingDataSet[i], weights);
				if (actualOutput > threshold)
					out = 1;
				else
					out = 0;
				errorRate = output[i] - out;
				correction = learningRate * errorRate;
				if (errorRate != 0) {
					errors++;
					for (int j = 0; j < weights.length; j++) {
						weights[j] += learningRate * errorRate
								* trainingDataSet[i][j];
					}
				}
			}
			if (errors == 0)
				break;
		}
	}

	private double dotProduct(double[] input, double[] weight) {
		double product = 0;
		for (int i = 0; i < input.length; i++) {
			product += input[i] * weight[i];
		}
		return product;

	}

	public void print() {
		for (int i = 0; i < weights.length; i++) {
			System.out.print(weights[i] + " ");
			System.out.print("\n");
		}

	}

	@Override
	public void train(String category, List<Feature> features) {
		storage.addItem(category, features);
	}

	@Override
	public ClassificationResult classify(List<Feature> features) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void multipleTrain(HashMap<String, List<List<Feature>>> trainingSet,
			Set<String> dictionary) {
		// TODO Auto-generated method stub
		
	}

}
