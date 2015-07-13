package com.baidu.cache;

/**
 * 
 * 
 * 
 * @author liukaixuan@baidu.com
 */
public class BdKVEmptyCacheImpl<T> implements BdKVCache<T> {

	public BdKVEmptyCacheImpl() {
	}

	@Override
	public T get(String key) {
		return null;
	}

	public CacheElement<T> getForDetail(String key) {
		return null;
	}

	@Override
	public void set(String key, T value, long expiredTimeInMills) {
	}

	@Override
	public void setForever(String key, T value) {
	}

	@Override
	public void remove(String key) {
	}

	@Override
	public void asyncGet(final String key, final BdCacheGetCallback<T> callback) {
	}

	@Override
	public void asyncGetForDetail(final String key, final BdCacheGetDetailCallback<T> callback) {
	}

	@Override
	public void asyncSet(final String key, final T value, final long expiredTimeInMills) {
	}

	@Override
	public void asyncSetForever(final String key, final T value) {
	}

	@Override
	public void asyncRemove(final String key) {
	}

	protected void releaseCacheData() {
	}

}
