/**
 * 
 */
package com.baidu.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

/**
 * 
 * Cache使用的SQLite Database封装
 * 
 * @author liukaixuan@baidu.com
 */
public abstract class BdCacheBaseDBManager<T> {

	protected final BdSQLiteHelper helper;

	protected String tableName;

	protected BdCacheEvictPolicy.EvictOnInsertSupport insertCachePolicy;
	protected BdCacheEvictPolicy.EvictOnCountSupport countCachePolicy;

	protected int dirtyCount;

	/**
	 * 待删除列表。
	 */
	protected LinkedList<String> idsToDelete = new LinkedList<String>();

	private Object lockForIdsToDelete = new Object();

	/**
	 * 通知有一个新的name space创建。
	 * 
	 * @param nameSpace
	 * 
	 * @return 将要存到的表名称
	 */
	public abstract String onNewNameSpaceCreated(String nameSpace);

	public abstract void onNameSpaceUpgraded(String nameSpace, String tableName, int oldVersion, int newVersion);

	public abstract int getCacheVersion();

	public abstract Cursor queryAllForNameSpace(SQLiteDatabase db, String nameSpace);

	protected abstract BdCacheItem<T> getFromDB(SQLiteDatabase db, String uniqueKey) throws Throwable;

	protected abstract ContentValues prepareForAddOrUpdate(BdCacheItem<T> item);

	protected abstract Cursor countForNameSpace(SQLiteDatabase db, String nameSpace);

	public BdCacheBaseDBManager(BdSQLiteHelper helper) {
		this.helper = helper;
	}

	public void startup(BdCacheEvictPolicy cachePolicy, String tableName) {
		this.tableName = tableName;

		if (cachePolicy instanceof BdCacheEvictPolicy.EvictOnInsertSupport) {
			insertCachePolicy = (BdCacheEvictPolicy.EvictOnInsertSupport) cachePolicy;
		}

		if (cachePolicy instanceof BdCacheEvictPolicy.EvictOnCountSupport) {
			countCachePolicy = (BdCacheEvictPolicy.EvictOnCountSupport) cachePolicy;
		}
	}

	public BdCacheItem<T> get(String uniqueKey) {
		try {
			BdCacheItem<T> item = this.getFromDB(helper.getSharedWritableDB(), uniqueKey);

			return item;
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);

			BdLog.e(this.getClass(), uniqueKey, t);
		}

		return null;
	}

	public void addOrUpdateTextCacheItem(BdCacheItem<T> item) {
		try {
			// Cancel delay-delete task if existed.
			synchronized (this.lockForIdsToDelete) {
				this.idsToDelete.remove(item.uniqueKey);
			}

			ContentValues cv = this.prepareForAddOrUpdate(item);

			int affectedRows = helper.getSharedWritableDB().update(tableName, cv, "m_key = ?", new String[] { item.uniqueKey });

			if (affectedRows == 0) {
				// insert new record
				helper.getSharedWritableDB().insert(tableName, null, cv);

				if (this.countCachePolicy != null) {
					this.notifyDirtyCountAdded();
				}
			}

			if (insertCachePolicy != null) {
				final String keyToEvict = insertCachePolicy.onItemJoined(item);
				if (keyToEvict != null) {
					this.deleteCacheItem(keyToEvict);
				}
			}

		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			BdLog.e(this.getClass(), "failed to insert " + item.uniqueKey + " to db.", t);
		}
	}

	protected void notifyDirtyCountAdded() {
		if (countCachePolicy != null) {
			this.dirtyCount++;

			// 最多不超过5条就进行一次清理，减少GC对性能的影响。
			int maxDirtyCount = (int) Math.min(countCachePolicy.getMaxSize() * 0.2, 5);

			// 如果dirty count没有到达限定值，应用退出了。clean操作在下次启动时会初始化性执行。
			if (this.dirtyCount >= maxDirtyCount) {
				// 先标记，免得下面的线程池排队时重复执行。
				dirtyCount = 0;

				ThreadService.sharedInstance().submitTask(new Runnable() {

					@Override
					public void run() {
						performCleanup();
					}
				});
			}
		}
	}

	public int deleteCacheItem(String uniqueKey) {
		try {
			return helper.getSharedWritableDB().delete(tableName, "m_key = ?", new String[] { uniqueKey });
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			BdLog.e(this.getClass(), "failed to delete " + uniqueKey + " from db.", t);
		}

		return 0;
	}

	public void clearAllForNameSpace(String nameSpace) {
		this.dirtyCount = 0;

		synchronized (lockForIdsToDelete) {
			this.idsToDelete.clear();
		}

		if (this.clearData(nameSpace)) {
			BdCacheService.sharedInstance().getNameSpaceManager().delete(nameSpace);
		}
	}

	protected abstract boolean clearData(String nameSpace);

	// public int clearSpace(String nameSpace){
	// // try{
	// // if(nameSpace != null){
	// // return helper.getSharedWritableDB().delete(tableName, "m_ns = ?", new
	// String[] { nameSpace });
	// // }else{
	// // return helper.getSharedWritableDB().delete(tableName, null, new
	// String[0]);
	// // }
	// // }catch(Throwable t){
	// // helper.notifyExceptionOnSharedDB(t) ;
	// // Log.e(tag, "failed to clear from " + nameSpace + ".", t) ;
	// // }
	//
	// Cursor c = null ;
	//
	// try{
	// if(nameSpace != null){
	// c=
	// helper.getSharedWritableDB().rawQuery("SELECT m_key, m_ns, lastHitTime, timeToExpire, m_value  FROM "
	// + tableName + " where m_ns = ?", new String[]{nameSpace}) ;
	// }else{
	// c=
	// helper.getSharedWritableDB().rawQuery("SELECT m_key, lastHitTime, timeToExpire, m_value  FROM "
	// + tableName , new String[0]) ;
	// }
	//
	// while (c.moveToNext()) {
	// this.addItemIdToDeleteList(c.getString(0)) ;
	// }
	//
	// this.performCleanup() ;
	// }catch(Throwable t){
	// helper.notifyExceptionOnSharedDB(t) ;
	//
	// BdLog.e(tag, nameSpace, t.getMessage()) ;
	// }finally{
	// BdCloseHelper.close(c) ;
	// }
	//
	// return 0 ;
	// }

	public int count(String nameSpace) {
		Cursor c = null;

		try {
			c = this.countForNameSpace(helper.getSharedWritableDB(), nameSpace);

			if (c.moveToNext()) {
				return c.getInt(0);
			}
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			BdLog.e(this.getClass(), "count", t);
		} finally {
			BdCloseHelper.close(c);
		}

		return 0;
	}

	public synchronized void addItemIdToDeleteList(String uniqueId, boolean notifyDirty) {
		synchronized (lockForIdsToDelete) {
			if (this.idsToDelete.contains(uniqueId)) return;

			this.idsToDelete.addLast(uniqueId);
		}

		if (notifyDirty) {
			this.notifyDirtyCountAdded();
		}
	}

	public void performEvict(String nameSpace) {
		if (countCachePolicy == null) {
			return;
		}

		Cursor c = null;

		try {
			countCachePolicy.startEvict();

			c = this.queryAllForNameSpace(helper.getSharedWritableDB(), nameSpace);

			while (c.moveToNext()) {
				try {
					BdCacheItem<Object> item = new BdCacheItem<Object>();
					item.uniqueKey = c.getString(c.getColumnIndex("m_key"));
					item.saveTime = c.getLong(c.getColumnIndex("saveTime"));
					item.lastHitTime = c.getLong(c.getColumnIndex("lastHitTime"));
					item.timeToExpire = c.getLong(c.getColumnIndex("timeToExpire"));

					String m_evictId = countCachePolicy.getEvictedKey(item);
					if (m_evictId != null) {
						addItemIdToDeleteList(m_evictId, false);
					}
				} catch (Throwable t) {
					BdLog.e(this.getClass(), "performEvict", t);
				}
			}

			this.performCleanup();
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			BdLog.e(this.getClass(), "performEvict", t);
		} finally {
			BdCloseHelper.close(c);

			countCachePolicy.finishEvict();
		}
	}

	public void performPump(String nameSpace) {
		if (insertCachePolicy == null) {
			return;
		}

		Cursor c = null;

		try {
			insertCachePolicy.startInit();

			c = this.queryAllForNameSpace(helper.getSharedWritableDB(), nameSpace);

			while (c.moveToNext()) {
				try {
					BdCacheItem<Object> item = new BdCacheItem<Object>();
					item.uniqueKey = c.getString(c.getColumnIndex("m_key"));
					item.saveTime = c.getLong(c.getColumnIndex("saveTime"));
					item.lastHitTime = c.getLong(c.getColumnIndex("lastHitTime"));
					item.timeToExpire = c.getLong(c.getColumnIndex("timeToExpire"));

					String m_evictId = insertCachePolicy.prepareForOldData(item);
					if (m_evictId != null) {
						addItemIdToDeleteList(m_evictId, false);
					}
				} catch (Throwable t) {
					BdLog.e(this.getClass(), "performPump", t);
				}
			}

			this.performCleanup();
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			BdLog.e(this.getClass(), "performPump", t);
		} finally {
			BdCloseHelper.close(c);

			insertCachePolicy.finishInit();
		}
	}

	/**
	 * 批量删除。
	 * 
	 * SQLLite在进行删除操作时，会挪动存储文件数据。记录越多，删除性能越差。
	 * 
	 * FIXME: 重新测试下删除性能
	 * 
	 * <p>
	 * 在ZET的一台普通手机上测试数据如下： <br>
	 * 在1000条记录的表中逐个删除1000条，耗时171876ms，平均单条172ms； <br>
	 * 在282条记录的表中逐个删除282条，耗时59232ms，平均单条210ms； <br>
	 * 在14条记录的表中逐个删除14条，耗时138ms，平均单条10ms；
	 * </p>
	 */
	protected void performCleanup() {
		if (this.idsToDelete.isEmpty()) return;

		SQLiteDatabase db = helper.getSharedWritableDB();

		db.beginTransaction(); // 开始事务
		try {
			while (true) {
				String pk = null;

				synchronized (lockForIdsToDelete) {

					if (this.idsToDelete.isEmpty()) {
						break;
					}

					pk = this.idsToDelete.removeFirst();
				}

				try {
					db.delete(tableName, "m_key = ?", new String[] { String.valueOf(pk) });
				} catch (Throwable t) {
					BdLog.e(this.getClass(), "performCleanup", t);
				}
			}

			db.setTransactionSuccessful(); // 设置事务成功完成
			this.dirtyCount = 0;
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public BdSQLiteHelper getSQLiteHelper() {
		return helper;
	}

}
