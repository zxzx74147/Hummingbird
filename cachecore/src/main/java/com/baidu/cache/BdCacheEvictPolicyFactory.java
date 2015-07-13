package com.baidu.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * 
 * 
 * 
 * @author liukaixuan@baidu.com
 */
public class BdCacheEvictPolicyFactory {

	/**
	 * 
	 * @param maxSize
	 * @param evictOnInsert
	 *            插入新元素时间检查老元素淘汰。适合元素数量较小，但淘汰频繁的场景，可以分散淘汰操作，减少淘汰过程对性能的冲击。
	 */
	public static BdCacheEvictPolicy newLRUCachePolicy(int maxSize, boolean evictOnInsert) {
		if (evictOnInsert) {
			return new EvictOnInsertLRUCachePolicy(maxSize);
		} else {
			return new EvictOnCountLRUCachePolicy(maxSize);
		}
	}

	/**
	 * 不主动驱逐元素。一般用于只缓存1个元素的namespace
	 */
	public static BdCacheEvictPolicy newNoEvictCachePolicy() {
		return new NoEvictCachePolicy();
	}

	static class NoEvictCachePolicy implements BdCacheEvictPolicy {

		@Override
		public String getName() {
			return "Noop";
		}

		@Override
		public int getMaxSize() {
			return 1;
		}

		@Override
		public boolean shouldUpdateLastHitTime() {
			return false;
		}

	}

	static class EvictOnCountLRUCachePolicy implements BdCacheEvictPolicy.EvictOnCountSupport {

		private final int maxSize;

		private LinkedList<BdCacheItem<?>> tempItems;

		public EvictOnCountLRUCachePolicy(int maxSize) {
			this.maxSize = maxSize;
		}

		@Override
		public String getName() {
			return "LRU_EVICT_ON_COUNT";
		}

		@Override
		public int getMaxSize() {
			return this.maxSize;
		}

		@Override
		public void startEvict() {
			tempItems = new LinkedList<BdCacheItem<?>>();
		}

		@Override
		public String getEvictedKey(BdCacheItem<?> cachedItem) {
			if (cachedItem.timeToExpire < System.currentTimeMillis()) {
				return cachedItem.uniqueKey;
			}

			tempItems.add(cachedItem);

			if (tempItems.size() > this.getMaxSize()) {

				int toEvictPos = -1;
				long lastHitTime = 0;

				String keyToEvict = null;

				for (int i = 0; i < this.tempItems.size(); i++) {
					BdCacheItem<?> m_item = this.tempItems.get(i);

					if (toEvictPos == -1 || m_item.lastHitTime < lastHitTime) {
						toEvictPos = i;
						keyToEvict = m_item.uniqueKey;
						lastHitTime = m_item.lastHitTime;
					}
				}

				this.tempItems.remove(toEvictPos);

				return keyToEvict;
			}

			return null;
		}

		@Override
		public void finishEvict() {
			tempItems.clear();

			tempItems = null;
		}

		@Override
		public boolean shouldUpdateLastHitTime() {
			return true;
		}

	}

	static class EvictOnInsertLRUCachePolicy implements BdCacheEvictPolicy.EvictOnInsertSupport {

		private final int maxSize;

		/**
		 * 大部分的key命中都是老元素的更新，相比排序开销，查询与更新更加有效，因为用HashMap而不使用自动排序的数据结构如TreeMap。 <br/>
		 * key~lastHitTime
		 */
		private HashMap<String, Long> items = new HashMap<String, Long>();

		public EvictOnInsertLRUCachePolicy(int maxSize) {
			this.maxSize = maxSize;
		}

		@Override
		public String getName() {
			return "LRU_EVICT_ON_INSERT";
		}

		@Override
		public int getMaxSize() {
			return this.maxSize;
		}

		@Override
		public boolean shouldUpdateLastHitTime() {
			return true;
		}

		public String keyToEvictedOnNewItemJoined(String keyToJoin) {
			if (this.items.containsKey(keyToJoin)) {
				return null;
			}

			if (this.items.size() < this.maxSize) {
				return null;
			}

			String key = null;
			long hit = -1;

			synchronized (this) {
				for (Entry<String, Long> e : this.items.entrySet()) {
					long m_hit = e.getValue().longValue();

					if (hit == -1 || hit > m_hit) {
						hit = m_hit;
						key = e.getKey();
					}
				}

				if (key != null) {
					this.items.remove(key);
				}
			}

			return key;
		}

		@Override
		public String onItemJoined(BdCacheItem<?> cachedItem) {
			String key = keyToEvictedOnNewItemJoined(cachedItem.uniqueKey);

			synchronized (this) {
				this.items.put(cachedItem.uniqueKey, cachedItem.lastHitTime);
			}

			return key;
		}

		@Override
		public void startInit() {
		}

		@Override
		public String prepareForOldData(BdCacheItem<?> cachedItem) {
			if (cachedItem.timeToExpire < System.currentTimeMillis()) {
				return cachedItem.uniqueKey;
			}

			return onItemJoined(cachedItem);
		}

		@Override
		public void finishInit() {
		}

		@Override
		public void release() {
			synchronized (this) {
				this.items.clear();
			}
		}

	}

}
