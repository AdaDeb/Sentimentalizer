package org.panhandlers.sentimentalizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

public class SentimentTestDataReader implements TestDataReader {
	private String path;
	private HashMap<String, ArrayList<String>> data;
	public SentimentTestDataReader() {}
	public SentimentTestDataReader(String path) {
		this.data = new HashMap<String, ArrayList<String>>();
		this.path = path;
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
			ArrayList<String> texts = data.get(category);
			if (texts == null) {
				texts = new ArrayList<String>();
				data.put(category, texts);
			}
			for (File sentimentDir : categoryDir.listFiles()) {
				readSentimentDir(sentimentDir, texts);
			}
		}
	}
	
	private void readSentimentDir(File sentimentDir, ArrayList<String> texts) {
		if (sentimentDir.listFiles() != null) {
			FileInputStream fileStream = null;
			for(File file : sentimentDir.listFiles()) {
				try {
					fileStream = new FileInputStream(file);
					String text = IOUtils.toString(fileStream);
					texts.add(text);
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
	public HashMap<String, ArrayList<String>> getData() {
		return data;
	}
	
	/**
	 * Quick and dirty test method.
	 * @param args
	 */
	public static void main (String[] args) {
		TestDataReader r = new SentimentTestDataReader("amazon-balanced-6cats");
		r.read();
		for (ArrayList<String> list : r.getData().values()) {
			for (String s : list) {
				System.out.println(s);
			}
		}
	}
	
}
