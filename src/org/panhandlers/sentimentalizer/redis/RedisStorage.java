package org.panhandlers.sentimentalizer.redis;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.panhandlers.sentimentalizer.ClassifierStorage;
import org.panhandlers.sentimentalizer.Feature;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisStorage implements ClassifierStorage {
	private static final String FEATURE = "bayes:feature:";
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
			p.hincrBy(CATEGORY_FEATURE_COUNTS, category, 1);
			p.hincrBy(getFeatureCountKey(category), feature.toString(), 1);
			p.sadd(ALL_FEATURES, feature.toString());
		}
		p.incr(TOTAL_ITEM_COUNT);
		p.hincrBy(CATEGORY_ITEM_COUNTS, category, 1);
		p.sync();
	}
	
	private String getFeatureCountKey(String category) {
		return FEATURE_COUNTS + category;
	}

	@Override
	public int getTotalFeaturesInCategoryCount(String category) {
		return Integer.parseInt(jedis.hget(CATEGORY_FEATURE_COUNTS, category));
	}

	@Override
	public int getFeatureCount(String category, Feature feature) {
		String output = jedis.hget(getFeatureCountKey(category), feature.toString());
		if (output == null) {
			return 0;
		} else {
			return Integer.parseInt(output);
		}
	}

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
