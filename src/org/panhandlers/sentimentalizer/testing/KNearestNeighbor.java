package org.panhandlers.sentimentalizer.testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.panhandlers.sentimentalizer.features.ExistenceFeature;
import org.panhandlers.sentimentalizer.features.ExistenceFeatureExtractor;
import org.panhandlers.sentimentalizer.features.Feature;
import org.panhandlers.sentimentalizer.testing.Test;
import org.panhandlers.sentimentalizer.testing.TestEnvironment;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class KNearestNeighbor extends Test{
	
	/**
	 * This class is responsible for running the K-Nearest Neighbors algorithm
	 * It uses the Java-ML library to run the actual tests, we only supply the features
	 * in 2 files (one for training and one for testing).
	 */
	
    private Classifier knn;
    private Dataset dataForClassification = null;
	private HashMap<String, List<List<String>>> testData;
	private HashMap<String, List<List<String>>> trainingData;
	private ExistenceFeatureExtractor extractor;
	private Set<String> dictionary;
	private String testCategory;
	private String trainingCategory;
	private Test.Type type;
	private int offset;
	private int neighbors;
	
	/*
	 * Used for In domain sentiment test on a single category
	 */
	public KNearestNeighbor(TestEnvironment env, int ratio,
			int dictSize, String category, int neighbors) {
		super(env, null, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.testCategory = this.trainingCategory = category;
		this.type = Test.Type.IN_DOMAIN;
		this.offset = 0;
		this.neighbors = neighbors;
		knn = new KNearestNeighbors(neighbors);
		loadData();
	}
	
	/*
	 * Used for out of domain sentiment test
	 */
	public KNearestNeighbor(TestEnvironment env, int ratio,
			int dictSize, String trainingCategory, String testCategory, int neighbors) {
		super(env, null, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.testCategory = testCategory;
		this.trainingCategory = trainingCategory;
		this.type = Test.Type.OUT_OF_DOMAIN;
		knn = new KNearestNeighbors(neighbors);
		loadData();
	}
	
	/*
	 * Used for running category tests
	 */
	public KNearestNeighbor(TestEnvironment env, int ratio,
			int dictSize, int neighbors) {
		super(env, null, ratio, dictSize);
		this.extractor = new ExistenceFeatureExtractor();
		this.type = Test.Type.CATEGORY;
		this.offset = 0;
		this.neighbors = neighbors;
		knn = new KNearestNeighbors(neighbors);
		loadData();
	}
	
	/*
	 * This method is used to write features to files and
	 * save them on the local hdd. The outputted contains a feature vector per line
	 * where each value is seperated by a comma and the last value is the 
	 * expected output.  
	 */
	private void writeData(String fileName, HashMap<String, List<List<String>>> dataSet) {
			List<List<Feature>> features;
			HashMap<String, List<List<Feature>>> featureMap = new HashMap<String, List<List<Feature>>>(); // contains all features
			File file = new File(fileName);
			BufferedWriter output = null;
			try {
				output = new BufferedWriter(new FileWriter(file));
				for (Entry<String, List<List<String>>> cat : dataSet.entrySet()) { // looping through all features vectors
					features = new ArrayList<List<Feature>>();
					for (List<String> item : cat.getValue()) {
						features.add(extractor.extractFeatures(item)); // extract feature element
					}
					String line = "";
					// start looping through all features and construct line
					// in each iteration
					for (int j = 0; j < features.size(); j++) { // loop for features 
						for (int i = 0; i < features.get(j).size(); i++) { //loop for each feature element
							ExistenceFeature feat = (ExistenceFeature) features
									.get(j).get(i);
							line = line.concat(Integer.toString(feat.getValue())
									+ ","); // add value to current line
						}
						line = line.concat(cat.getKey()); // add category name (expected output) to current line
						output.write(line); // write line to file
						output.write("\n"); 
						line = ""; // reset line for next iteration
					}
					featureMap.put(cat.getKey(), features);
				}
				output.close(); // close stream of output file
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	
	/*
	 * This method is similar to other loadData methods in SentimentTest class and CategoryTest class
	 * It divides the data into testing and training data depending on what kind of test
	 * we are running
	 */
	private void loadData() {
		/*
		 * Load data
		 */
		TestEnvironment env = getEnv();
		env.getStorage().reset(); //reset redis storage

		List<List<String>> positive = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "pos");
		List<List<String>> negative = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "neg");

		if (this.type == Test.Type.IN_DOMAIN) { // In domain sentiment testing
			HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
			data.put("pos", positive); // positive reviews
			data.put("neg", negative); // negative reviews
			// divide data
			getDivider().setOffset(this.offset);
			getDivider().divide(data);
			testData = getDivider().getTestData();
			trainingData = getDivider().getTrainingData();
		} else if (this.type == Test.Type.OUT_OF_DOMAIN) { // out of domain test type
			List<List<String>> positiveTestData = env.getReader()
					.getItemsByCategoryAndSentiment(testCategory, "pos");
			List<List<String>> negativeTestData = env.getReader()
					.getItemsByCategoryAndSentiment(testCategory, "neg");
			trainingData = new HashMap<String, List<List<String>>>();
			testData = new HashMap<String, List<List<String>>>();
			trainingData.put("pos", positive);
			trainingData.put("neg", negative);
			testData.put("pos", positiveTestData);
			testData.put("neg", negativeTestData);
		} else { // Category test type
			// load all categories from data reader
			List<List<String>> musicCategory = env.getReader().getItemsByCategory("music");
			List<List<String>> dvdCategory = env.getReader().getItemsByCategory("dvd");
			List<List<String>> softwareCategory = env.getReader().getItemsByCategory("software");
			List<List<String>> booksCategory = env.getReader().getItemsByCategory("books");
			List<List<String>> healthCategory = env.getReader().getItemsByCategory("health");
			List<List<String>> cameraCategory = env.getReader().getItemsByCategory("camera");

			HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
			// add all categories to the data hash map
			data.put("music", musicCategory);
			data.put("dvd", dvdCategory);
			data.put("software", softwareCategory);
			data.put("books", booksCategory);
			data.put("health", healthCategory);
			data.put("camera", cameraCategory);
			
			// Set offset and divide data into training and testing sets
			getDivider().setOffset(offset);
			getDivider().divide(data);
			testData = getDivider().getTestData();
			trainingData = getDivider().getTrainingData();		
		}
		
		/*
		 * Construct dictionary
		 */
		dictionary = getDictionaryBuilder().buildDictionary(trainingData);
		System.out
				.println("Dictionary built with length: " + dictionary.size());
		extractor.setDictionary(dictionary);
		
		// write test and train data to files for the KNN algorithm to use
		writeData("train.data", trainingData);
		writeData("test.data", testData);
	}

	@Override
	public String getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * The code in this method was not written by us so we take no credit for it,
	 * we use the same code found on the guides on the Java-ML website
	 * http://java-ml.sourceforge.net/content/evaluate-classifier-dataset
	 */
	@Override
	public void test() {
	    /*
         * Load a data set for evaluation
         */
		try {
			dataForClassification = FileHandler.loadDataset(new File("test.data"), dictionary.size(), ",");
		} catch (IOException e) {
			e.printStackTrace();
		}
      
        Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(knn, dataForClassification);
        for (Object o : pm.keySet())
            System.out.println(o + ": " + pm.get(o).getAccuracy());
	
	}

	/*
	 * The code in this method was not written by us so we take no credit for it,
	 * we use the same code found on the guides on the Java-ML website
	 * http://java-ml.sourceforge.net/content/load-data-file
	 */
	@Override
	public void train() {
		Dataset data = null;
		try {
			data = FileHandler.loadDataset(new File("train.data"), dictionary.size(), ",");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        /*
         * Contruct a KNN classifier 
         */
        knn.buildClassifier(data);
		
		
	}
}

