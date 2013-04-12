package org.panhandlers.sentimentalizer.redis;

import java.util.ArrayList;
import java.util.HashMap;

import org.panhandlers.sentimentalizer.DataReader;
import org.panhandlers.sentimentalizer.CategoryDataReader;

import redis.clients.jedis.Jedis;

public class RedisDictionaryBuilder {
	private static final String JedisKey = "dictionary";
	private Jedis jedis;
	public RedisDictionaryBuilder() {
		jedis = RedisConfig.getJedisPool().getResource();
	}
	
	public void buildDictionary(HashMap<String, ArrayList<ArrayList<String>>> data) {
		jedis.del(JedisKey);
		for (ArrayList<ArrayList<String>> categoryItems : data.values()) {
			for(ArrayList<String> itemTokens : categoryItems) {
				for(String token : itemTokens) {
					jedis.sadd(JedisKey, token);
				}
			}
		}
	}
	
	public static void main (String[] args) {
		RedisDictionaryBuilder b = new RedisDictionaryBuilder();
		DataReader reader = new CategoryDataReader("amazon-balanced-6cats");
		b.buildDictionary(reader.getData());
	}
}
