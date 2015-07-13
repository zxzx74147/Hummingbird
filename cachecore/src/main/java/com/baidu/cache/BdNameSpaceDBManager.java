/**
 * 
 */
package com.baidu.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * 
 * 存储name space使用的SQLite Database封装
 * 
 * @author liukaixuan@baidu.com
 */
public class BdNameSpaceDBManager {

	private final BdSQLiteHelper helper;

	private static final String tag = "BdNameSpaceDBManager";

	public BdNameSpaceDBManager(Context context, BdSQLiteHelper helper) {
		this.helper = helper;
	}

	public BdCacheNSItem get(String nameSpace) {
		Cursor c = null;

		try {
			c = helper.getSharedWritableDB().rawQuery("SELECT nameSpace, tableName, maxSize, cacheType, cacheVersion, lastActiveTime FROM "
					+ BdSQLiteHelper.TABLE_CACHE_META_INFO + " where nameSpace = ?", new String[] { nameSpace });

			if (c.moveToNext()) {
				BdCacheNSItem item = new BdCacheNSItem();

				item.nameSpace = c.getString(0);
				item.tableName = c.getString(1);
				item.maxSize = c.getInt(2);
				item.cacheType = c.getString(3);
				item.cacheVersion = c.getInt(4);
				item.lastActiveTime = c.getLong(5);

				return item;
			}
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);

			BdLog.e(tag, nameSpace, t.getMessage());
		} finally {
			BdCloseHelper.close(c);
		}

		return null;
	}

	public void addOrUpdate(BdCacheNSItem item) {
		try {
			ContentValues cv = new ContentValues();
			cv.put("nameSpace", item.nameSpace);
			cv.put("tableName", item.tableName);
			cv.put("maxSize", item.maxSize);
			cv.put("cacheVersion", item.cacheVersion);
			cv.put("cacheType", item.cacheType);
			cv.put("lastActiveTime", item.lastActiveTime);

			int affectedRows = helper.getSharedWritableDB().update(BdSQLiteHelper.TABLE_CACHE_META_INFO, cv, "nameSpace = ?", new String[] { item.nameSpace });

			if (affectedRows == 0) {
				// insert new record
				helper.getSharedWritableDB().insert(BdSQLiteHelper.TABLE_CACHE_META_INFO, null, cv);
			}

		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			BdLog.e(tag, "failed to insert " + item.nameSpace + " to db.", t);
		}
	}

	public int delete(String nameSpace) {
		try {
			BdCacheNSItem item = this.get(nameSpace);
			if (item == null) {
				return 0;
			}

			return helper.getSharedWritableDB().delete(BdSQLiteHelper.TABLE_CACHE_META_INFO, "nameSpace = ?", new String[] { nameSpace });
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);
			Log.e(tag, "failed to delete " + nameSpace + " from db.", t);
		}

		return 0;
	}

}
