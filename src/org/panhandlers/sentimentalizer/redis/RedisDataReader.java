package org.panhandlers.sentimentalizer.redis;

import org.panhandlers.sentimentalizer.GeneralTokenizer;
import org.panhandlers.sentimentalizer.LuceneTokenizer;

import redis.clients.jedis.Jedis;

public class RedisDataReader {
	private Jedis jedis;
	private GeneralTokenizer tokenizer;
	private String path;
	
	public RedisDataReader() {
		init();
	}
	
	public RedisDataReader(String path) {
		this.path = path;
		read();
	}
	
	private void init() {
		tokenizer = new LuceneTokenizer();
		jedis = RedisConfig.getJedisPool().getResource();
	}
	
	public void read() {
		
	}
}
