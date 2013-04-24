package org.panhandlers.sentimentalizer.storage;

import java.util.HashMap;
import java.util.List;

public interface DataReader {

	public abstract void read();

	public abstract void read(String path);
	/**
	 * @return Map Category => List of Tokenized Items 
	 */
	public abstract HashMap<String, List<List<String>>> getData();

}