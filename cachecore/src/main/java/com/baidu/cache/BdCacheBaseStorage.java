package com.baidu.cache;


/**
 * 
 * 所有缓存元素全部存储同在一张KV对应表的存储系统。
 * 
 * @author liukaixuan@baidu.com
 */
public abstract class BdCacheBaseStorage<T> implements BdCacheStorage<T> {

	protected final boolean prefixNameSpaceToKey;

	protected final BdCacheEvictPolicy cachePolicy;

	public abstract BdCacheItem<T> getByUniqueKey(String uniqueKey);

	public abstract void insertOrUpdate(BdCacheItem<T> item);

	public abstract void removeByUniqueKey(String uniqueKey);

	protected abstract void removeExpiredItem(String uniqueKey);

	public BdCacheBaseStorage(BdCacheEvictPolicy cachePolicy, boolean prefixNameSpaceToKey) {
		this.cachePolicy = cachePolicy;
		this.prefixNameSpaceToKey = prefixNameSpaceToKey;
	}

	protected String buildUniqueKey(String nameSpace, String key) {
		if (this.prefixNameSpaceToKey) {
			return nameSpace + "@" + key;
		} else {
			return key;
		}
	}

	protected BdCacheItem<T> internalGetWithCachePolicy(String nameSpace, String key) {
		String uniqueKey = buildUniqueKey(nameSpace, key);
		BdCacheItem<T> item = this.getByUniqueKey(uniqueKey);

		if (item == null) {
			if (BdLog.isDebugMode()) {
				BdLog.d("cache", "get", "cache miss:" + uniqueKey);
			}

			return null;
		}

		if (item.timeToExpire < System.currentTimeMillis()) {
			// 过期了，删除掉。
			this.removeExpiredItem(uniqueKey);

			if (BdLog.isDebugMode()) {
				BdLog.d("cache", "get", "cache miss on expired:" + uniqueKey);
			}

			return null;
		} else if (cachePolicy.shouldUpdateLastHitTime()) {
			// LRU cache
			item.lastHitTime = System.currentTimeMillis();
			this.insertOrUpdate(item);
		}

		if (BdLog.isDebugMode()) {
			BdLog.d("cache", "get", "cache hit:" + uniqueKey);
		}

		return item;
	}

	@Override
	public T get(String nameSpace, String key) {
		BdCacheItem<T> item = this.internalGetWithCachePolicy(nameSpace, key);

		if (item == null) {
			return null;
		}

		return item.value;
	}

	@Override
	public BdKVCache.CacheElement<T> getForDetail(String nameSpace, String key) {
		BdCacheItem<T> item = this.internalGetWithCachePolicy(nameSpace, key);

		if (item == null) {
			return null;
		}

		BdKVCache.CacheElement<T> element = new BdKVCache.CacheElement<T>();
		element.key = key;
		element.value = item.value;
		element.timeToExpire = item.timeToExpire;
		element.lastSaveTime = item.saveTime;

		return element;
	}

	@Override
	public void set(String nameSpace, String key, T value, long expiredTimeInMills) {
		BdCacheItem<T> item = new BdCacheItem<T>();

		item.uniqueKey = this.buildUniqueKey(nameSpace, key);
		item.nameSpace = nameSpace;
		item.timeToExpire = expiredTimeInMills;
		item.value = value;
		item.lastHitTime = System.currentTimeMillis();
		item.saveTime = System.currentTimeMillis();

		this.insertOrUpdate(item);
	}

	@Override
	public void remove(String nameSpace, String key) {
		this.removeByUniqueKey(this.buildUniqueKey(nameSpace, key));
	}

	public BdCacheEvictPolicy getCachePolicy() {
		return cachePolicy;
	}

}
