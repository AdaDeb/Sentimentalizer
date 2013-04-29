package org.panhandlers.sentimentalizer.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {
	
	/*
	 * Initializes Jedis objects
	 */
	
	private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");

	public static JedisPool getJedisPool() {
		return jedisPool;
	}

	public static void setJedisPool(JedisPool jedisPool) {
		RedisConfig.jedisPool = jedisPool;
	}
}
