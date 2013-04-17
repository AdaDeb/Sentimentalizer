package org.panhandlers.sentimentalizer.redis;

public abstract class Test {
	private TestEnvironment env;
	
	public abstract void run();
	public abstract String getResults();
	
	public TestEnvironment getEnv() {
		return env;
	}
	
	public void setEnv(TestEnvironment env) {
		this.env = env;
	}
}
