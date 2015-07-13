package com.baidu.cache;


/**
 * 
 * 
 * 
 * @author liukaixuan@baidu.com
 */
public interface BdCacheStorage<T> {

	/**
	 * 同步获取1个缓存项，如果不存在返回null。
	 */
	T get(String nameSpace, String key);

	/**
	 * 同步获取1个缓存项，如果不存在返回null。
	 */
	BdKVCache.CacheElement<T> getForDetail(String nameSpace, String key);

	/**
	 * 同步保存1个cache，如果之前的key存在直接覆盖。
	 * 
	 * @param key
	 * @param value
	 * @param expiredTimeInMills
	 *            要缓存到的绝对到期时间
	 */
	void set(String nameSpace, String key, T value, long expiredTimeInMills);

	void remove(String nameSpace, String key);

	BdCacheEvictPolicy getCachePolicy();

	/**
	 * Cache创建完毕，初始化storage系统。
	 */
	void startup(String nameSpace);

	void clearAndClose(String nameSpace);

	void flushAndClose(String nameSpace);

}
