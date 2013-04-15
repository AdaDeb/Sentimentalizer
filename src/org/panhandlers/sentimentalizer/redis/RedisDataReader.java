package org.panhandlers.sentimentalizer.redis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.panhandlers.sentimentalizer.GeneralTokenizer;
import org.panhandlers.sentimentalizer.LuceneTokenizer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class RedisDataReader {
	private static final String DICTIONARY_KEY = "dictionary";
	private static final String CATEGORIES_KEY = "categories";
	private static final String FILES_KEY = "items";
	private static final String CATEGORIES_ITEMS = "items:by_category:";
	private Jedis jedis;
	private GeneralTokenizer tokenizer;
	private String path;
	
	public RedisDataReader() {
		init();
	}
	
	public RedisDataReader(String path) {
		this.path = path;
		init();
		read();
	}
	
	
	private void init() {
		tokenizer = new LuceneTokenizer();
		jedis = RedisConfig.getJedisPool().getResource();
	}
	
	public void read() {
		String loaded = jedis.get("data_loaded");
		if(loaded == null || (loaded != null && loaded.equals("false"))) {
			// Purge dictionary
			jedis.del(DICTIONARY_KEY);
			File folder = new File(path);
			File[] categoryList = folder.listFiles();
			for (File categoryDir: categoryList) {
				readCategoryDirectory(categoryDir);
			}
			jedis.set("data_loaded", "true");
			System.out.println("DataReader: loaded data");
		} else {
			System.out.println("DataReader: data already loaded");
		}
	}
	
	public List<List<String>> getItemsByCategory(String category) {
		List<String> keys = jedis.lrange(itemsByCategoryKey(category), 0, -1);
		return getLists(keys);
	}
	
	public List<List<String>> getItemsByCategoryAndSentiment(String category, String sentiment) {
		List<String> keys = jedis.lrange(itemsByCategoryAndSentimentKey(category, sentiment), 0, -1);
		return getLists(keys);
	}
	
	private List<List<String>> getLists(List<String> keys) {
		List<List<String>> items = new LinkedList<List<String>>();
		Pipeline p = jedis.pipelined();
		List<Response<List<String>>> responses = new LinkedList<Response<List<String>>>(); 
		for(String key : keys) {
			responses.add(p.lrange(key, 0, -1));	
		}
		p.sync();
		for (Response<List<String>> resp : responses) {
			items.add(resp.get());
		}
		return items;
	}
	
	private String itemKey(String category, String sentiment, String id) {
		return category + ":" + sentiment + ":" + id;
	}
	
	private String itemsByCategoryKey(String category) {
		return CATEGORIES_ITEMS + category;
	}
	
	private String itemsByCategoryAndSentimentKey(String category, String sentiment) {
		return category + ":" + sentiment;
	}
	
	private void readCategoryDirectory(File categoryDir) {
		if(categoryDir.listFiles() != null) {
			String category = categoryDir.getName();
			for (File sentimentDir : categoryDir.listFiles()) {
				readSentimentDir(category, sentimentDir);
			}
		}
	}
	
	private void readSentimentDir(String category, File sentimentDir) {
		if (sentimentDir.listFiles() != null) {
			// Declarations
			FileInputStream fileStream = null;
			String sentiment = sentimentDir.getName();
			String itemKey;
			String text;
			ArrayList<String> tokenizedText;
			
			for(File file : sentimentDir.listFiles()) {
				try {
					fileStream = new FileInputStream(file);
					text = IOUtils.toString(fileStream);
					tokenizedText = tokenizer.tokenize(text);
					itemKey = itemKey(category, sentiment, file.getName());
					Pipeline p = jedis.pipelined();
					// Keep a set of all categories
					p.sadd(CATEGORIES_KEY, category);
					// Keep a set of file names, just in case
					p.sadd(FILES_KEY, file.getName());
					// Keep lists that connect categories to their items according to sentiment
					p.lpush(itemsByCategoryAndSentimentKey(category, sentiment), itemKey);
					// Keep lists that connect sentiments to items
					p.lpush(itemsByCategoryKey(category), itemKey);
					// Save the token to the item
					for (String token : tokenizedText) {
						// Keep a dictionary of all words
						p.sadd(DICTIONARY_KEY, token);
						p.lpush(itemKey, token);	
					}
					p.sync();
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
	
	public static void main(String[] args) {
		RedisDataReader reader = new RedisDataReader("amazon-balanced-6cats");
		System.out.println("Done!");
		for(List<String> item : reader.getItemsByCategory("software")) {
			for (String token : item) {
				System.out.println(token );
			}
		}
	}
}
