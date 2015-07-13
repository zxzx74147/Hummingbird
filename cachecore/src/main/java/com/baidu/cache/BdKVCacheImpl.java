package com.baidu.cache;

/**
 * 
 * 
 * 
 * @author liukaixuan@baidu.com
 */
public class BdKVCacheImpl<T> implements BdKVCache.MXSupportedCache<T> {

	protected final BdCacheStorage<T> storage;

	protected final String nameSpace;

	public BdKVCacheImpl(String nameSpace, BdCacheStorage<T> storage) {
		this.nameSpace = nameSpace;
		this.storage = storage;
	}

	@Override
	public T get(String key) {
		return storage.get(nameSpace, key);
	}

	public CacheElement<T> getForDetail(String key) {
		return storage.getForDetail(nameSpace, key);
	}

	@Override
	public void set(String key, T value, long expiredTimeInMills) {
		if (expiredTimeInMills <= MILLS_10Years) {
			expiredTimeInMills += System.currentTimeMillis();
		}

		if (expiredTimeInMills <= System.currentTimeMillis()) {
			// 设置过期时间到过去，表示删除。
			this.remove(key);

			return;
		}

		this.storage.set(nameSpace, key, value, expiredTimeInMills);
	}

	@Override
	public void setForever(String key, T value) {
		this.set(key, value, MILLS_10Years);
	}

	@Override
	public void remove(String key) {
		this.storage.remove(nameSpace, key);
	}

	@Override
	public void asyncGet(final String key, final BdCacheGetCallback<T> callback) {
		ThreadService.sharedInstance().submitTask(new Runnable() {

			@Override
			public void run() {
				T result = BdKVCacheImpl.this.get(key);

				callback.onItemGet(key, result);
			}
		});
	}

	@Override
	public void asyncGetForDetail(final String key, final BdCacheGetDetailCallback<T> callback) {
		ThreadService.sharedInstance().submitTask(new Runnable() {

			@Override
			public void run() {
				CacheElement<T> result = BdKVCacheImpl.this.getForDetail(key);

				callback.onItemGet(key, result);
			}
		});
	}

	@Override
	public void asyncSet(final String key, final T value, final long expiredTimeInMills) {
		ThreadService.sharedInstance().submitTask(new Runnable() {

			@Override
			public void run() {
				BdKVCacheImpl.this.set(key, value, expiredTimeInMills);
			}
		});
	}

	@Override
	public void asyncSetForever(final String key, final T value) {
		this.asyncSet(key, value, MILLS_10Years);
	}

	@Override
	public void asyncRemove(final String key) {
		ThreadService.sharedInstance().submitTask(new Runnable() {

			@Override
			public void run() {
				BdKVCacheImpl.this.remove(key);
			}
		});
	}

	public String getNameSpace() {
		return this.nameSpace;
	}

	public BdCacheStorage<T> getCacheStorage() {
		return this.storage;
	}

	@Override
	public void onCacheCreated() {
		this.storage.startup(nameSpace);
	}

	protected void releaseCacheData() {
		BdCacheEvictPolicy cachePolicy = getCacheStorage().getCachePolicy();

		if (cachePolicy instanceof BdCacheEvictPolicy.EvictOnInsertSupport) {
			((BdCacheEvictPolicy.EvictOnInsertSupport) cachePolicy).release();
		}
	}

	@Override
	public void flushAndClose() {
		this.storage.flushAndClose(nameSpace);

		releaseCacheData();
	}

	@Override
	public void clearAndClose() {
		this.storage.clearAndClose(nameSpace);

		releaseCacheData();
	}

}
