package org.panhandlers.sentimentalizer.redis;

import java.util.HashMap;
import java.util.List;

import org.panhandlers.sentimentalizer.CategoryDataReader;
import org.panhandlers.sentimentalizer.DataReader;

import redis.clients.jedis.Jedis;

public class RedisDictionaryBuilder {
	private static final String JedisKey = "dictionary";
	private Jedis jedis;
	public RedisDictionaryBuilder() {
		jedis = RedisConfig.getJedisPool().getResource();
	}
	
	public void buildDictionary(HashMap<String, List<List<String>>> data) {
		jedis.del(JedisKey);
		for (List<List<String>> categoryItems : data.values()) {
			for(List<String> itemTokens : categoryItems) {
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
