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
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

public class KNearestNeighbor extends Test{
	
    Classifier knn;

	private HashMap<String, List<List<String>>> testData;
	private HashMap<String, List<List<String>>> trainingData;
	private ExistenceFeatureExtractor extractor;
	private Set<String> dictionary;
	private String testCategory;
	private String trainingCategory;
	private Test.Type type;
	private int offset;
	private int neighbors;
	
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
	
	
	
	private void writeData(String fileName, HashMap<String, List<List<String>>> dataSet) {
			List<List<Feature>> features;
			HashMap<String, List<List<Feature>>> featureMap = new HashMap<String, List<List<Feature>>>();
			File file = new File(fileName);
			BufferedWriter output = null;
			try {
				output = new BufferedWriter(new FileWriter(file));
				for (Entry<String, List<List<String>>> cat : dataSet.entrySet()) {
					features = new ArrayList<List<Feature>>();
					for (List<String> item : cat.getValue()) {
						features.add(extractor.extractFeatures(item));
					}
					String line = "";
					for (int j = 0; j < features.size(); j++) {
						for (int i = 0; i < features.get(j).size(); i++) {
							ExistenceFeature feat = (ExistenceFeature) features
									.get(j).get(i);
							line = line.concat(Integer.toString(feat.getValue())
									+ ",");
						}
						line = line.concat(cat.getKey());
						output.write(line);
						output.write("\n");
						line = "";
					}
					featureMap.put(cat.getKey(), features);
				}
				output.close();
				

			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	
	
	
	private void loadData() {
		/*
		 * Load data
		 */
		TestEnvironment env = getEnv();
		env.getStorage().reset();

		List<List<String>> positive = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "pos");
		List<List<String>> negative = env.getReader()
				.getItemsByCategoryAndSentiment(trainingCategory, "neg");

		if (this.type == Test.Type.IN_DOMAIN) {
			HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
			data.put("pos", positive);
			data.put("neg", negative);

			/*
			 * Divide data
			 */
			getDivider().setOffset(this.offset);
			getDivider().divide(data);
			testData = getDivider().getTestData();
			trainingData = getDivider().getTrainingData();
		} else if (this.type == Test.Type.OUT_OF_DOMAIN) {
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
		} else {
			List<List<String>> musicCategory = env.getReader().getItemsByCategory("music");
			List<List<String>> dvdCategory = env.getReader().getItemsByCategory("dvd");
			List<List<String>> softwareCategory = env.getReader().getItemsByCategory("software");
			List<List<String>> booksCategory = env.getReader().getItemsByCategory("books");
			List<List<String>> healthCategory = env.getReader().getItemsByCategory("health");
			List<List<String>> cameraCategory = env.getReader().getItemsByCategory("camera");

			System.out.println("music length " + musicCategory.size());
			HashMap<String, List<List<String>>> data = new HashMap<String, List<List<String>>>();
			data.put("music", musicCategory);
			data.put("dvd", dvdCategory);
			data.put("software", softwareCategory);
			data.put("books", booksCategory);
			data.put("health", healthCategory);
			data.put("camera", cameraCategory);
			
			// Set offset
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
		
		
		writeData("train.data", trainingData);
		writeData("test.data", testData);
	}

	@Override
	public String getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void test() {
		  /*
         * Load a data set for evaluation, this can be a different one, but we
         * will use the same one.
         */
        Dataset dataForClassification = null;
		try {
			dataForClassification = FileHandler.loadDataset(new File("test.data"), dictionary.size(), ",");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
		
		//dataForClassification = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");

        Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(knn, dataForClassification);
        for (Object o : pm.keySet())
            System.out.println(o + ": " + pm.get(o).getAccuracy());
		
		
//		/* Counters for correct and wrong predictions. */
//        int correct = 0, wrong = 0;
//        /* Classify all instances and check with the correct class values */
//        for (Instance inst : dataForClassification) {
//            Object predictedClassValue = knn.classify(inst);
//            Object realClassValue = inst.classValue();
//            if (predictedClassValue.equals(realClassValue))
//                correct++;
//            else
//                wrong++;
//        }
//        System.out.println("Correct predictions  " + correct);
//        System.out.println("Wrong predictions " + wrong);
	
	}

	@Override
	public void train() {
		Dataset data = null;
		try {
			data = FileHandler.loadDataset(new File("train.data"), dictionary.size(), ",");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        /*
         * Contruct a KNN classifier that uses 5 neighbors to make a decision.
         */
        knn.buildClassifier(data);
		
		
	}

	
	

}

