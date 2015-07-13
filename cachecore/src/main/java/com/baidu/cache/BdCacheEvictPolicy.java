package com.baidu.cache;

/**
 * 
 * 针对单个NameSpace内元素的缓存清理策略。
 * 
 * <p>
 * 注意：{@link BdCacheItem#timeToExpire} 设置的过期元素，可能会不经过调用policy判定就会直接被删掉。
 * </p>
 * 
 * @author liukaixuan@baidu.com
 */
public interface BdCacheEvictPolicy {

	String getName();

	/**
	 * 缓存允许的最大尺寸。
	 */
	int getMaxSize();

	/**
	 * 缓存命中时，是否应该更新最后命中的时间戳。
	 */
	boolean shouldUpdateLastHitTime();

	interface EvictOnCountSupport extends BdCacheEvictPolicy {
		/**
		 * 开始cache淘汰
		 */
		void startEvict();

		/**
		 * 计算出应该被删除的key。此方法会循环调用，遍历1个namespace下的所有缓存元素。
		 * 
		 * @param cachedItem
		 *            缓存元素
		 * @return null 跳过，不需要驱逐
		 */
		String getEvictedKey(BdCacheItem<?> cachedItem);

		/**
		 * 结束cache淘汰，清空缓存什么的临时元素。
		 */
		void finishEvict();
	}

	interface EvictOnInsertSupport extends BdCacheEvictPolicy {

		/**
		 * 通知一个新元素加入或老元素更新，返回需要删掉的老元素的key。
		 * 
		 * @return null 跳过，不需要驱逐
		 */
		String onItemJoined(BdCacheItem<?> cachedItem);

		/**
		 * 开始初始化，在cache创建时进行。
		 */
		void startInit();

		/**
		 * 准备淘汰参考数据。此方法会循环调用，遍历1个namespace下的所有缓存元素。
		 * 
		 * @param cachedItem
		 *            缓存元素
		 * @return 如果返回的key不是null，直接删除此元素。
		 */
		String prepareForOldData(BdCacheItem<?> cachedItem);

		/**
		 * 结束cache淘汰，清空缓存什么的临时元素。
		 */
		void finishInit();

		/**
		 * cache销毁时调用。
		 */
		void release();

	}
}
