package com.baidu.cache;

/**
 * 
 * 
 * 
 * @author liukaixuan@baidu.com
 */
public class BdCacheSQLiteStorage<T> extends BdCacheBaseStorage<T> {

	protected final BdCacheBaseDBManager<T> dbManager;

	public BdCacheSQLiteStorage(BdCacheBaseDBManager<T> dbManager, BdCacheEvictPolicy cachePolicy,
			boolean prefixNameSpaceToKey) {
		super(cachePolicy, prefixNameSpaceToKey);

		this.dbManager = dbManager;
	}

	@Override
	public BdCacheItem<T> getByUniqueKey(String uniqueKey) {
		return this.dbManager.get(uniqueKey);
	}

	@Override
	public void insertOrUpdate(BdCacheItem<T> item) {
		this.dbManager.addOrUpdateTextCacheItem(item);
	}

	@Override
	public void removeByUniqueKey(String uniqueKey) {
		this.dbManager.deleteCacheItem(uniqueKey);
	}

	@Override
	protected void removeExpiredItem(String uniqueKey) {
		// async delete
		this.dbManager.addItemIdToDeleteList(uniqueKey, true);
	}

	@Override
	public void clearAndClose(String nameSpace) {
		this.dbManager.clearAllForNameSpace(nameSpace);
	}

	@Override
	public void flushAndClose(String nameSpace) {
		this.dbManager.performCleanup();
	}

	public void startup(final String nameSpace) {
		if (cachePolicy instanceof BdCacheEvictPolicy.EvictOnInsertSupport) {

			ThreadService.sharedInstance().submitTask(new Runnable() {

				@Override
				public void run() {
					dbManager.performPump(nameSpace);
				}
			});
		}

		if (cachePolicy instanceof BdCacheEvictPolicy.EvictOnCountSupport) {

			ThreadService.sharedInstance().submitTask(new Runnable() {

				@Override
				public void run() {
					dbManager.performEvict(nameSpace);
				}
			});
		}
	}

	public BdCacheBaseDBManager<T> getDbManager() {
		return dbManager;
	}

}
