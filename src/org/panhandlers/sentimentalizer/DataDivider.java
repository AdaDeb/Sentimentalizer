package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * The DataDivider divides data into to training data and test data.
 * @author jesjos
 *
 */
public class DataDivider {
	private HashMap<String, ArrayList<ArrayList<String>>> trainingData;
	private HashMap<String, ArrayList<ArrayList<String>>> testData;
	private int ratio;
	
	public DataDivider(int ratio, HashMap<String, ArrayList<ArrayList<String>>> data) {
		this.ratio = ratio;
		divide(data);
	}
	
	private void divide(HashMap<String, ArrayList<ArrayList<String>>> data) {
		int i;
		ArrayList<ArrayList<String>> testDataList;
		ArrayList<ArrayList<String>> trainingDataList;
		for (Entry<String, ArrayList<ArrayList<String>>> category : data.entrySet()) {
			testDataList = new ArrayList<ArrayList<String>>();
			trainingDataList = new ArrayList<ArrayList<String>>();
			testData.put(category.getKey(), testDataList);
			trainingData.put(category.getKey(), trainingDataList);
			for(i = 0; i < category.getValue().size(); i++) {
				if(i % ratio == 0) {
					testDataList.add(category.getValue().get(i));
				} else {
					trainingDataList.add(category.getValue().get(i));
				}
			}
		}
	}

	public HashMap<String, ArrayList<ArrayList<String>>> getTrainingData() {
		return trainingData;
	}
	
	public HashMap<String, ArrayList<ArrayList<String>>> getTestData() {
		return testData;
	}
}
