package org.panhandlers.sentimentalizer;

public class AveragedPerceptron {

	private double threshold = 0.5;
	private double learningRate = 0.1;
	private double errorRate;

	public AveragedPerceptron() {

	}

	
	/*
	 * Input data structure will be improved!
	 * To test: create a new AveragedPerceprtron then run train and print 
	 */
	double[] output = { 1, 1, 1, 0 };

	double[][] trainingDataSet = { { 1, 0, 0 }, { 1, 0, 1 }, { 1, 1, 0 },
			{ 1, 1, 1 }, };

	double[] weights = { 0, 0, 0 };

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

}
