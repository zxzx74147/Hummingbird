package com.baidu.cache;

/**
 * 
 * cache API
 * 
 * @author liukaixuan@baidu.com
 */
public interface BdKVCache<T> {

	long MILLS_1Hour = 1000 * 60 * 60L;

	long MILLS_24Hours = MILLS_1Hour * 24;

	long MILLS_30Days = MILLS_24Hours * 30;

	long MILLS_1YEAR = MILLS_24Hours * 365;

	long MILLS_10Years = MILLS_24Hours * 3652;

    long MILLS_FOEVER = MILLS_24Hours * 3652 * 10;

	class CacheElement<T> {

		public String key;

		/**
		 * cache内容
		 */
		public T value;

		/**
		 * cache最后1次保存的时间。
		 */
		public long lastSaveTime;

		/**
		 * cache到期时间。
		 */
		public long timeToExpire;

	}

	/**
	 * 同步获取1个缓存项，如果不存在返回null。
	 * 
	 * <p>
	 * 如果需要同时获取cache最后一次保存的时间，使用 {@link #getForDetail(String)}
	 * </p>
	 * 
	 * <p>
	 * <b>SQLite存储引擎时性能：</b> 【cache value
	 * 100K】。10000次随机查询，在ZET普通手机上测试，每次查询平均24ms； 在小米2S上测试，每次查询3.4~17ms。 <br/>
	 * 对记录数为15条与1000条的表查询，性能无明显变化。多name space连续随机查询，小表性能略低于大表。
	 * </p>
	 */
	T get(String key);

	/**
	 * 同步获取1个缓存项的详细信息，如果不存在返回null。
	 * 
	 * <p>
	 * <b>SQLite存储引擎时性能：</b> 同 {@link #get(String)}。
	 * </p>
	 */
	CacheElement<T> getForDetail(String key);

	/**
	 * 同步保存1个cache，如果之前的key存在直接覆盖。
	 * 
	 * <p>
	 * <b>SQLite存储引擎时性能：</b> 【cache value 100K】。在ZET普通手机上测试，每次查询平均64ms到99ms； <br/>
	 * 在小米2S上测试，30ms到54ms。 <br/>
	 * 对记录数为15条与1000条的表更新，小米性能无明显变化，ZET小表速度更快。
	 * </p>
	 * 
	 * @param key
	 * @param value
	 * @param expiredTimeInMills
	 *            缓存有效期，单位毫秒，如缓存30秒可以传入30*1000。
	 *            如果缓存时间超过10年，则此参数当做是要缓存的绝对到期时间，如缓存30秒也传入
	 *            {@link System#currentTimeMillis()} + 30*1000
	 * 
	 * @see #MILLS_10Years
	 * @throws NullPointerException
	 *             key或是value为null抛出NPE
	 */
	void set(String key, T value, long expiredTimeInMills);

	/**
	 * 永久保存1个key，永不过期。
	 */
	void setForever(String key, T value);

	/**
	 * 同步删除。在SQLite中，删除性能与表大小关系不大，主要影响点是数据库的大小，以及要删除的记录所在文件的位置。
	 * 如果要删除的记录在文件尾部【后插入的】，性能非常好；记录后面的数据越多，性能越差。
	 * 
	 * <p>
	 * <b>SQLite存储引擎时性能：</b> 【cache value 100K】。
	 * 在1000记录的库中，小米2S测试1000条记录，批量删除平均耗时24ms；单条删除平均52ms；drop表删除平均17ms。 <br/>
	 * 在10条记录的库中，小米2S测试，批量删除平均耗时10ms；单条删除平均耗时32ms；drop表删除平均10ms。
	 * </p>
	 */
	void remove(String key);

	void asyncGet(String key, BdCacheGetCallback<T> callback);

	void asyncGetForDetail(String key, BdCacheGetDetailCallback<T> callback);

	void asyncSet(String key, T value, long expiredTimeInMills);

	void asyncSetForever(String key, T value);

	void asyncRemove(String key);

	interface MXSupportedCache<T> extends BdKVCache<T> {

		// mx methods
		String getNameSpace();

		BdCacheStorage<T> getCacheStorage();

		void onCacheCreated();

		void flushAndClose();

		/**
		 * 删除此name space下的所有数据。
		 */
		void clearAndClose();

	}

	interface BdCacheGetCallback<T> {

		/**
		 * @param key
		 * @param value
		 *            如果不存在传入null。
		 */
		void onItemGet(String key, T value);

	}

	interface BdCacheGetDetailCallback<T> {

		/**
		 * @param key
		 * @param value
		 *            如果不存在传入null。
		 */
		void onItemGet(String key, CacheElement<T> element);

	}

}
