package org.panhandlers.sentimentalizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class CategoryDataReader implements DataReader {
	private String path;
	private HashMap<String, List<List<String>>> data;
	private GeneralTokenizer tokenizer;
	public CategoryDataReader() {}
	public CategoryDataReader(String path) {
		this.data = new HashMap<String, List<List<String>>>();
		this.path = path;
		this.tokenizer = new LuceneTokenizer();
		read();
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
			List<List<String>> texts = data.get(category);
			if (texts == null) {
				texts = new ArrayList<List<String>>();
				data.put(category, texts);
			}
			for (File sentimentDir : categoryDir.listFiles()) {
				readSentimentDir(sentimentDir, texts);
			}
		}
	}
	
	private void readSentimentDir(File sentimentDir, List<List<String>> texts) {
		if (sentimentDir.listFiles() != null) {
			FileInputStream fileStream = null;
			ArrayList<String> tokenizedText;
			for(File file : sentimentDir.listFiles()) {
				try {
					fileStream = new FileInputStream(file);
					String text = IOUtils.toString(fileStream);
					tokenizedText = tokenizer.tokenize(text);
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
	public HashMap<String, List<List<String>>> getData() {
		return data;
	}
	
	/**
	 * Quick and dirty test method.
	 * @param args
	 */
	public static void main (String[] args) {
		DataReader r = new CategoryDataReader("amazon-balanced-6cats");
		r.read();
		for (List<List<String>> lists : r.getData().values()) {
			for (List<String> list : lists) {
				for (String s : list) 
					System.out.println(s);
			}
		}
	}
	
}
