package com.baidu.cache;

/**
 * 
 * 后端cache出错后，忽略get/set/remove操作异常的cache实现。
 * 
 * @author liukaixuan@baidu.com
 */
public class BdKVCacheSafeImpl<T> extends BdKVCacheImpl<T> {

	public BdKVCacheSafeImpl(String nameSpace, BdCacheStorage<T> storage) {
		super(nameSpace, storage);
	}

	@Override
	public T get(String key) {
		try {
			return super.get(key);
		} catch (Throwable t) {
			if (BdLog.isDebugMode()) {
				BdLog.e(this.getClass(), "failed to get cache:" + key, t);
			}

			return null;
		}
	}

	public CacheElement<T> getForDetail(String key) {
		try {
			return super.getForDetail(key);
		} catch (Throwable t) {
			if (BdLog.isDebugMode()) {
				BdLog.e(this.getClass(), "failed to get cache:" + key, t);
			}

			return null;
		}
	}

	@Override
	public void set(String key, T value, long expiredTimeInMills) {
		try {
			super.set(key, value, expiredTimeInMills);
		} catch (Throwable t) {
			if (BdLog.isDebugMode()) {
				BdLog.e(this.getClass(), "failed to store cache key:" + key + ", value:" + value, t);
			}
		}
	}

	@Override
	public void remove(String key) {
		try {
			super.remove(key);
		} catch (Throwable t) {
			if (BdLog.isDebugMode()) {
				BdLog.e(this.getClass(), "failed to remove cache:" + key, t);
			}
		}
	}

}
