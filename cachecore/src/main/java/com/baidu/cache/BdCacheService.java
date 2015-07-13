package com.baidu.cache;

import android.content.Context;

import java.util.HashMap;


/**
 * 
 * Cache服务，从这里申请对cache的使用。
 * 
 * 使用方式：
 * 
 * <pre>
 * BdCacheService service = BdCacheService.sharedInstance();
 * 
 * BdKVCache<String> kv_cache = service.getAndStartTextCache("tb.global", CacheStorage.SQLite_CACHE_PER_TABLE, CacheEvictPolicy.NO_EVICT, 1);
 * 
 * String key = "mykey";
 * String content = "mycontent";
 * 
 * kv_cache.set(key, content, 3000);
 * assertEquals(kv_cache.get(key), content);
 * if (this.frs_cache.get(key) != null) {
 * 	fail("fail");
 * }
 * 
 * synchronized (this) {
 * 	try {
 * 		this.wait(3000);
 * 	} catch (InterruptedException e) {
 * 		fail(e.getMessage());
 * 	}
 * }
 * 
 * if (kv_cache.get(key) != null) {
 * 	fail("should expire");
 * }
 * 
 * 
 * ...long long run....
 * 
 * 
 * service.returnAndCloseTextCache(kv_cache);
 * 
 * ...
 * 
 * cache service完全不再使用了：service.shutdown()
 * </pre>
 * 
 * <p>
 * <b>使用技巧：</b> <br/>
 * 1. 表分裂后，也就是使用 {@link CacheStorage#SQLite_CACHE_PER_TABLE} 进行cache清空时， 性能比
 * {@link CacheStorage#SQLite_CACHE_All_IN_ONE_TABLE}
 * 快40%左右。其他性能类似，查询时All_IN_ONE略高。 <br/>
 * <br/>
 * 2. 对于频繁有数据淘汰的表，也就是LRU算法cache或是手工调用remove操作的表。使用 {@link #newInstance(String)}
 * 将cache分配到单独的数据库中，可以获得更好的性能。
 * SQLite的删除性能与表大小关系不大，主要是库大小，也就是要删除的记录后面有多少记录。后面的记录越多
 * ，性能越差。分配到单独的数据库将会分配单独的存储文件，存储文件越小，性能越高。
 * </p>
 * 
 * @author liukaixuan@baidu.com
 */
public class BdCacheService {

	private static final String SHARED_DATABASE_NAME = "baidu_adp.db";

	public static final String CACHE_TABLE_PREFIX = "cache_kv_";

	public static final String SHARED_TEXT_TABLE = CACHE_TABLE_PREFIX + "tshare";
	public static final String SHARED_BLOB_TABLE = CACHE_TABLE_PREFIX + "bshare";

	private static BdCacheService _instance;

	private BdNameSpaceDBManager nameSpaceManager;

	private Context context;

	private BdSQLiteHelper sqlLiteHelper;

	private final String databaseFile;

	private boolean debugMode;

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public enum CacheEvictPolicy {
		/** 不限制最大容量 */
		NO_EVICT,

		/** 当新增加次数超过总容量一定数量后【由后端存储引擎决定数量阀值】，进行1次后台LRU淘汰。 */
		LRU_ON_COUNT,

		/** 到达最大容量后，每插入1条新记录，按照LRU算法淘汰1条旧数据。 */
		LRU_ON_INSERT
	}

	public enum CacheStorage {
		/** 基于SQLite数据库的存储，每个name space存储在单独一张表中 */
		SQLite_CACHE_PER_TABLE,

		/** 基于SQLite数据库的存储，所有name space存储在一张表中 */
		SQLite_CACHE_All_IN_ONE_TABLE
	}

	private BdCacheService(String databaseFile) {
		this.databaseFile = databaseFile;

		if (BdCacheManger.getApp() != null) {
			this.debugMode = BdLog.isDebugMode();
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		if (this.context == null) {
			return BdCacheManger.getApp();
		} else {
			return this.context;
		}
	}

	/**
	 * 获取共享的缓存服务。
	 */
	public static BdCacheService sharedInstance() {
		if (_instance == null) {
			_instance = new BdCacheService(SHARED_DATABASE_NAME);
		}

		return _instance;
	}

	/**
	 * 创建一个独立的缓存。返回的Service需要应用自己缓存。
	 */
	public static BdCacheService newInstance(String databaseFile) {
		return new BdCacheService(databaseFile);
	}

	public synchronized String initAndGetTableName(BdCacheBaseDBManager<?> dbManager, String nameSpace,
			String cacheType, int maxSize) {
		int oldVersion = dbManager.getCacheVersion();

		BdNameSpaceDBManager ndb = this.getNameSpaceManager();
		BdCacheNSItem item = ndb.get(nameSpace);
		if (item == null) {
			item = new BdCacheNSItem();

			item.nameSpace = nameSpace;
			item.cacheVersion = oldVersion;
			item.cacheType = cacheType;
			item.maxSize = maxSize;
			item.lastActiveTime = System.currentTimeMillis();

			// 新建
			item.tableName = dbManager.onNewNameSpaceCreated(nameSpace);

			ndb.addOrUpdate(item);
		} else {
			if (!cacheType.equalsIgnoreCase(item.cacheType)) {
				throw new IllegalArgumentException("nameSpace [" + nameSpace + "] is already taken by cacheType:"
						+ item.cacheType);
			}

			item.maxSize = maxSize;
			item.lastActiveTime = System.currentTimeMillis();

			// 升级或是降低
			if (oldVersion != item.cacheVersion) {
				dbManager.onNameSpaceUpgraded(nameSpace, item.tableName, oldVersion, item.cacheVersion);
			}

			ndb.addOrUpdate(item);
		}

		return item.tableName;
	}

	private HashMap<String, BdKVCache<String>> textCaches = new HashMap<String, BdKVCache<String>>();

	/**
	 * 创建比初始化缓存。如果此name space的缓存已经创建过，直接返回。如果需要重新创建，需要先返回旧的缓存，在调用此方法创建新的。
	 * 
	 * <p>
	 * <b>性能：</b> 在ZET普通手机上测试，第一次创建DB时间为146ms，每个Cache第一次初始化创建表的时间为2ms到17ms； <br/>
	 * 在小米2S上测试，第一次数据库创建时间为0ms，每个Cache第一次初始化创建表的时间为10ms到60ms。 <br/>
	 * 完成第一次表创建后，后续创建时间为0ms【不包括LRU策略预加载或是淘汰数据的时间--异步线程】。
	 * </p>
	 * 
	 * @param nameSpace
	 *            命名空间，缓存内容清理策略都是基于命名空间进行。不同命名空间之间的key可以重复。最多128个字符。
	 * @param storage
	 *            存储策略
	 * @param maxSize
	 *            最大保留的cache条数
	 *
	 */
	public synchronized BdKVCache<String> getAndStartTextCache(String nameSpace, CacheStorage storage,
			CacheEvictPolicy evictPolicy, int maxSize) {
		BdKVCache<String> m_old = textCaches.get(nameSpace);
		if (m_old != null) {
			return m_old;
		}

		BdCacheEvictPolicy cachePolicy = null;

		if (evictPolicy == CacheEvictPolicy.LRU_ON_COUNT) {
			cachePolicy = BdCacheEvictPolicyFactory.newLRUCachePolicy(maxSize, false);
		} else if (evictPolicy == CacheEvictPolicy.LRU_ON_INSERT) {
			cachePolicy = BdCacheEvictPolicyFactory.newLRUCachePolicy(maxSize, true);
		} else {
			cachePolicy = BdCacheEvictPolicyFactory.newNoEvictCachePolicy();
		}

		// Shared Database Connection is used in BdTextCachePerTableDBManager,
		// so BdTextCachePerTableDBManager can be created on fly.
		BdCacheBaseDBManager<String> dbManager;
		boolean prefixNameSpaceToKey;

		try {
			if (storage == CacheStorage.SQLite_CACHE_PER_TABLE) {
				dbManager = new BdTextCachePerTableDBManager(this.getSQLiteHelper());
				prefixNameSpaceToKey = false;
			} else {
				dbManager = new BdTextCacheAllInOneTableDBManager(this.getSQLiteHelper(), SHARED_TEXT_TABLE);
				prefixNameSpaceToKey = true;
			}

			String tableName = initAndGetTableName(dbManager, nameSpace, BdCacheNSItem.CACHE_TYPE_TEXT, maxSize);
			dbManager.startup(cachePolicy, tableName);

			BdCacheSQLiteStorage<String> m_storage = new BdCacheSQLiteStorage<String>(dbManager, cachePolicy, prefixNameSpaceToKey);

			return getAndStartTextCache(nameSpace, m_storage);

		} catch (Throwable t) {
			if (this.debugMode) {
				throw new RuntimeException(t);
			} else {
				return new BdKVEmptyCacheImpl<String>();
			}
		}
	}

	public synchronized BdKVCache<String> getAndStartTextCache(String nameSpace, BdCacheStorage<String> storageImpl) {
		BdKVCache<String> m_old = textCaches.get(nameSpace);

		if (m_old != null) {
			if (storageImpl != null && m_old instanceof BdKVCache.MXSupportedCache) {
				if (((BdKVCache.MXSupportedCache<String>) m_old).getCacheStorage() != storageImpl) {
					throw new IllegalStateException("nameSpace:[" + nameSpace + "] is already used for storage:["
							+ storageImpl + "]. Make sure to return the old cache before re-use the same namespace.");
				}
			}

			return m_old;
		}

		BdKVCacheImpl<String> m_cache = null;
		if (this.isDebugMode()) {
			m_cache = new BdKVCacheImpl<String>(nameSpace, storageImpl);
		} else {
			m_cache = new BdKVCacheSafeImpl<String>(nameSpace, storageImpl);
		}

		this.textCaches.put(nameSpace, m_cache);

		// 初始化。如启用淘汰算法的初始化淘汰和预加载数据等等。
		m_cache.onCacheCreated();

		return m_cache;
	}

	/**
	 * 退回并关闭缓存。同步调用方法。
	 *
	 */
	public void returnAndCloseTextCache(BdKVCache<String> cache) {
		if (cache instanceof BdKVCache.MXSupportedCache) {
			BdKVCache.MXSupportedCache<?> mxCache = (BdKVCache.MXSupportedCache<?>) cache;

			synchronized (mxCache) {
				String ns = mxCache.getNameSpace();

				mxCache.flushAndClose();

				this.textCaches.remove(ns);
			}
		}
	}

	private HashMap<String, BdKVCache<byte[]>> blobCaches = new HashMap<String, BdKVCache<byte[]>>();

	/**
	 * 创建比初始化缓存。如果此name space的缓存已经创建过，直接返回。如果需要重新创建，需要先返回旧的缓存，在调用此方法创建新的。
	 * 
	 * <p>
	 * <b>性能：</b> 在小米2S上测试，第一次数据库创建时间为0ms，每个Cache第一次初始化创建表的时间为16ms到28ms。 <br/>
	 * 完成第一次表创建后，后续创建时间为0ms【不包括LRU策略预加载或是淘汰数据的时间--异步线程】。
	 * </p>
	 * 
	 * @param nameSpace
	 *            命名空间，缓存内容清理策略都是基于命名空间进行。不同命名空间之间的key可以重复。最多128个字符。
	 * @param storage
	 *            存储策略
	 *            淘汰策略
	 * @param maxSize
	 *            最大保留的cache条数
	 *
	 */
	public synchronized BdKVCache<byte[]> getAndStartBlobCache(String nameSpace, CacheStorage storage,
			CacheEvictPolicy evictPolicy, int maxSize) {
		BdKVCache<byte[]> m_old = blobCaches.get(nameSpace);
		if (m_old != null) {
			return m_old;
		}

		BdCacheEvictPolicy cachePolicy = null;

		if (evictPolicy == CacheEvictPolicy.LRU_ON_COUNT) {
			cachePolicy = BdCacheEvictPolicyFactory.newLRUCachePolicy(maxSize, false);
		} else if (evictPolicy == CacheEvictPolicy.LRU_ON_INSERT) {
			cachePolicy = BdCacheEvictPolicyFactory.newLRUCachePolicy(maxSize, true);
		} else {
			cachePolicy = BdCacheEvictPolicyFactory.newNoEvictCachePolicy();
		}

		// Shared Database Connection is used in BdTextCachePerTableDBManager,
		// so BdTextCachePerTableDBManager can be created on fly.
		BdCacheBaseDBManager<byte[]> dbManager;
		boolean prefixNameSpaceToKey;

		try {
			if (storage == CacheStorage.SQLite_CACHE_PER_TABLE) {
				dbManager = new BdBlobCachePerTableDBManager(this.getSQLiteHelper());
				prefixNameSpaceToKey = false;
			} else {
				dbManager = new BdBlobCacheAllInOneTableDBManager(this.getSQLiteHelper(), SHARED_BLOB_TABLE);
				prefixNameSpaceToKey = true;
			}

			String tableName = initAndGetTableName(dbManager, nameSpace, BdCacheNSItem.CACHE_TYPE_BLOB, maxSize);
			dbManager.startup(cachePolicy, tableName);

			BdCacheSQLiteStorage<byte[]> m_storage = new BdCacheSQLiteStorage<byte[]>(dbManager, cachePolicy, prefixNameSpaceToKey);

			return getAndStartBlobCache(nameSpace, m_storage);
		} catch (Throwable t) {
			if (this.debugMode) {
				throw new RuntimeException(t);
			} else {
				return new BdKVEmptyCacheImpl<byte[]>();
			}
		}
	}

	public synchronized BdKVCache<byte[]> getAndStartBlobCache(String nameSpace, BdCacheStorage<byte[]> storageImpl) {
		BdKVCache<byte[]> m_old = blobCaches.get(nameSpace);

		if (m_old != null) {
			if (storageImpl != null && m_old instanceof BdKVCache.MXSupportedCache) {
				if (((BdKVCache.MXSupportedCache<byte[]>) m_old).getCacheStorage() != storageImpl) {
					throw new IllegalStateException("nameSpace:[" + nameSpace + "] is already used for storage:["
							+ storageImpl + "]. Make sure to return the old cache before re-use the same namespace.");
				}
			}

			return m_old;
		}

		BdKVCacheImpl<byte[]> m_cache = null;
		if (this.isDebugMode()) {
			m_cache = new BdKVCacheImpl<byte[]>(nameSpace, storageImpl);
		} else {
			m_cache = new BdKVCacheSafeImpl<byte[]>(nameSpace, storageImpl);
		}

		this.blobCaches.put(nameSpace, m_cache);

		// 初始化。如启用淘汰算法的初始化淘汰和预加载数据等等。
		m_cache.onCacheCreated();

		return m_cache;
	}

	/**
	 * 退回并关闭缓存。同步调用方法。
	 */
	public void returnAndCloseBlobCache(BdKVCache<byte[]> cache) {
		if (cache instanceof BdKVCache.MXSupportedCache) {
			BdKVCache.MXSupportedCache<?> mxCache = (BdKVCache.MXSupportedCache<?>) cache;

			synchronized (mxCache) {
				String ns = mxCache.getNameSpace();
				try {
					mxCache.flushAndClose();

					this.blobCaches.remove(ns);
				} catch (Throwable t) {
					BdLog.e("BdCacheService", "failed to close cache:" + ns, t);
				}
			}
		}
	}

	/**
	 * 归还并清空一个cache下的所有数据。调用此方法后，如果还需要继续使用Cache，需要重新获取。同步调用方法。
	 *
	 * @see #getAndStartTextCache(String, BdCacheStorage)
	 */
	public void returnAndClearCache(BdKVCache<?> cache) {
		if (cache instanceof BdKVCache.MXSupportedCache) {
			BdKVCache.MXSupportedCache<?> mxCache = (BdKVCache.MXSupportedCache<?>) cache;

			synchronized (mxCache) {
				String ns = mxCache.getNameSpace();

				try {
					mxCache.clearAndClose();

					this.textCaches.remove(ns);
				} catch (Throwable t) {
					BdLog.e("BdCacheService", "failed to close cache:" + ns, t);
				}
			}
		}
	}

	/**
	 * 关闭所有缓存以及相关的资源。同步调用方法。
	 */
	public void shutdown() {
		while (!this.textCaches.isEmpty()) {
			String ns = this.textCaches.keySet().iterator().next();

			BdKVCache<String> m_cache = this.textCaches.get(ns);
			this.returnAndCloseTextCache(m_cache);
		}

		// 关闭数据库
		if (this.sqlLiteHelper != null) {
			this.sqlLiteHelper.close();
		}
	}

	public BdNameSpaceDBManager getNameSpaceManager() {
		if (nameSpaceManager == null) {
			nameSpaceManager = new BdNameSpaceDBManager(this.getContext(), getSQLiteHelper());
		}

		return nameSpaceManager;
	}

	public BdSQLiteHelper getSQLiteHelper() {
		if (this.sqlLiteHelper == null) {
			sqlLiteHelper = new BdSQLiteHelper(this.getContext(), this.databaseFile);
		}

		return sqlLiteHelper;
	}

}
