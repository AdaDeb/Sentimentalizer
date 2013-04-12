package org.panhandlers.sentimentalizer;

import java.util.ArrayList;
import java.util.HashMap;

public interface TestDataReader {

	public abstract void read();

	public abstract void read(String path);
	
	public abstract HashMap<String, ArrayList<String>> getData();

}