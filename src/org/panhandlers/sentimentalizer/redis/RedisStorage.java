package org.panhandlers.sentimentalizer.redis;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.panhandlers.sentimentalizer.features.Feature;
import org.panhandlers.sentimentalizer.storage.ClassifierStorage;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisStorage implements ClassifierStorage {
	private static final String FEATURE = "bayes:feature:";
	// Counts the total amount of features in a category. Is a hash.
	// Example: HGET bayes:category_feature_counts category
	// => 104
	private static final String CATEGORY_FEATURE_COUNTS = "bayes:category_feature_counts";
	private static final String CATEGORY_ITEM_COUNTS = "bayes:category_item_counts";
	private static final String TOTAL_ITEM_COUNT = "bayes:total_item_count";
	private static final String FEATURE_COUNTS = "bayes:feature_counts:";
	private static final String ALL_FEATURES = "bayes:all_features";
	private Jedis jedis;
	
	public RedisStorage() {
		jedis = RedisConfig.getJedisPool().getResource();
	}

	public void addItem(String category, List<Feature> features) {
		Pipeline p = jedis.pipelined();
		for (Feature feature : features) {
			// Increment the number of features in the category
			p.hincrBy(CATEGORY_FEATURE_COUNTS, category, 1);
			// Increment the count for this features in the category
			p.hincrBy(getFeatureCountKey(category), feature.toString(), 1);
			// Add feature to the set of all features
			p.sadd(ALL_FEATURES, feature.toString());
		}
		p.incr(TOTAL_ITEM_COUNT);
		p.hincrBy(CATEGORY_ITEM_COUNTS, category, 1);
		p.sync();
	}
	
	// Returns a key that can be used to fetch the counts of individual features a category
	// Example: getFeatureCountKey("music") => "bayes:category_feature_counts"
	// This can be used to get the number of items that contain a certain features:
	// HGET bayes:category_feature_counts existencegood1 => 10
	private String getFeatureCountKey(String category) {
		return FEATURE_COUNTS + category;
	}

	@Override
	public int getTotalFeaturesInCategoryCount(String category) {
		return Integer.parseInt(jedis.hget(CATEGORY_FEATURE_COUNTS, category));
	}

	/**
	 * Returns the number of items with a particular feature in a particular category
	 */
	@Override
	public int getFeatureCount(String category, Feature feature) {
		String output = jedis.hget(getFeatureCountKey(category), feature.toString());
		if (output == null) {
			return 0;
		} else {
			return Integer.parseInt(output);
		}
	}
	
	/**
	 * Get the total amount of registered feature occurrences.
	 */
	@Override
	public int getTotalCount() {
		return (int) new BigDecimal(jedis.scard(ALL_FEATURES)).intValueExact();
	}

	@Override
	public Set<String> getCategories() {
		return 	jedis.hkeys(CATEGORY_FEATURE_COUNTS);
	}
	
	public void reset() {
		Set<String> keys = jedis.keys("bayes*");
		Pipeline p = jedis.pipelined();
		for (String key : keys) {
			p.del(key);
		}
		p.sync();
		System.out.println("RedisStorage: did reset");
	}
	@Override
	public int getItemsInCategoryCount(String category) {
		return Integer.parseInt(jedis.hget(CATEGORY_ITEM_COUNTS, category));
	}
	@Override
	public int getTotalItemsCount() {
		return Integer.parseInt(jedis.get(TOTAL_ITEM_COUNT));
	}

}
