package org.panhandlers.sentimentalizer.testing;

import org.panhandlers.sentimentalizer.redis.RedisDataReader;
import org.panhandlers.sentimentalizer.redis.RedisStorage;
import org.panhandlers.sentimentalizer.storage.DataDivider;
import org.panhandlers.sentimentalizer.storage.DictionaryBuilder;

public class TestEnvironment {
	
	/*
	 * This class sets up the test environment for 
	 * running the tests 
	 */
	
	private RedisDataReader reader;
	private RedisStorage storage;
	
	public TestEnvironment() {
		reader = new RedisDataReader("amazon-balanced-6cats");
		storage = new RedisStorage();
	}
	public RedisDataReader getReader() {
		return reader;
	}
	public void setReader(RedisDataReader reader) {
		this.reader = reader;
	}

	public RedisStorage getStorage() {
		return storage;
	}
	public void setStorage(RedisStorage storage) {
		this.storage = storage;
	}
}
