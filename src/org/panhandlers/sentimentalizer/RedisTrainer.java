package org.panhandlers.sentimentalizer;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTrainer implements Trainer {
	private JedisPool jedisPool;
	public RedisTrainer() {
		jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
	}
	@Override
	public void train(String path) {
		
	}

}
