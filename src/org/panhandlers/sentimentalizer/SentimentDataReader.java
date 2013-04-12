package org.panhandlers.sentimentalizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

public class SentimentDataReader implements DataReader {
	private String path;
	private HashMap<String, ArrayList<ArrayList<String>>> data;
	private GeneralTokenizer tokenizer;
	public SentimentDataReader() {}
	public SentimentDataReader(String path) {
		this.data = new HashMap<String, ArrayList<ArrayList<String>>>();
		this.path = path;
		this.tokenizer = new LuceneTokenizer();
	}
	
	@Override
	public void read() {
		File folder = new File(path);
		File[] categoryList = folder.listFiles();
		for (File categoryDir: categoryList) {
			readCategoryDirectory(categoryDir);
		}
	}
	
	private void readCategoryDirectory(File categoryDir) {
		if(categoryDir.listFiles() != null) {
			String category = categoryDir.getName();
			ArrayList<ArrayList<String>> texts = data.get(category);
			if (texts == null) {
				texts = new ArrayList<ArrayList<String>>();
				data.put(category, texts);
			}
			for (File sentimentDir : categoryDir.listFiles()) {
				readSentimentDir(sentimentDir, texts);
			}
		}
	}
	
	private void readSentimentDir(File sentimentDir, ArrayList<ArrayList<String>> texts) {
		if (sentimentDir.listFiles() != null) {
			FileInputStream fileStream = null;
			for(File file : sentimentDir.listFiles()) {
				try {
					fileStream = new FileInputStream(file);
					String text = IOUtils.toString(fileStream);
					ArrayList<String> tokenizedText = tokenizer.tokenize(text);
					texts.add(tokenizedText);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (fileStream != null) {
						try {
							fileStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	@Override
	public void read(String path) {
		this.path = path;
		read();
	}
	@Override
	public HashMap<String, ArrayList<ArrayList<String>>> getData() {
		return data;
	}
	
	/**
	 * Quick and dirty test method.
	 * @param args
	 */
	public static void main (String[] args) {
		DataReader r = new SentimentDataReader("amazon-balanced-6cats");
		r.read();
		for (ArrayList<ArrayList<String>> lists : r.getData().values()) {
			for (ArrayList<String> list : lists) {
				for (String s : list) 
					System.out.println(s);
			}
		}
	}
	
}
