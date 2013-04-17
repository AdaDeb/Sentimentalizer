package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * The DataDivider divides data into to training data and test data.
 * @author jesjos
 *
 */
public class DataDivider {
	private HashMap<String, List<List<String>>> trainingData;
	private HashMap<String, List<List<String>>> testData;
	private int ratio;
	private int offset;
	
	public DataDivider(int ratio) {
		this.ratio = ratio;
		this.offset = 0;
		init();
	}
	
	public DataDivider(int ratio, int offset) {
		this.offset = offset;
		this.ratio = ratio;
		init();
	}
	
	public DataDivider(int ratio, HashMap<String, List<List<String>>> data) {
		this.ratio = ratio;
		this.offset = 0;
		init();
		divide(data);
	}
	
	public DataDivider(int ratio, int offset, HashMap<String, List<List<String>>> data) {
		this.ratio = ratio;
		this.offset = offset;
		init();
		divide(data);
	}
	
	private void init() {
		trainingData = new HashMap<String, List<List<String>>>();
		testData = new HashMap<String, List<List<String>>>();
	}
	
	/**
	 * Divides each category into two slices according to the given ratio
	 * @param data
	 */
	public void divide(HashMap<String, List<List<String>>> data) {
		int i, testSize, trainingSize;
		ArrayList<List<String>> testDataList;
		ArrayList<List<String>> trainingDataList;
		for (Entry<String, List<List<String>>> category : data.entrySet()) {
			// Establishing sane list sizes from the outset should give us some performance gains
			testSize = category.getValue().size() / ratio;
			trainingSize = category.getValue().size();
			testDataList = new ArrayList<List<String>>(testSize);
			trainingDataList = new ArrayList<List<String>>(trainingSize);
			testData.put(category.getKey(), testDataList);
			trainingData.put(category.getKey(), trainingDataList);
			for(i = 0; i < category.getValue().size(); i++) {
				if((i % ratio) - offset == 0) {
					testDataList.add(category.getValue().get(i));
				} else {
					trainingDataList.add(category.getValue().get(i));
				}
			}
		}
	}

	public HashMap<String, List<List<String>>> getTrainingData() {
		return trainingData;
	}
	
	public HashMap<String, List<List<String>>> getTestData() {
		return testData;
	}
}
