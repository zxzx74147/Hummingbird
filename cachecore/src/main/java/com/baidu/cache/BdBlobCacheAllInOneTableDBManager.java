/**
 * 
 */
package com.baidu.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * Blob Cache使用的SQLite Database封装【所有name space的cache存储在1张表里】
 * 
 * @author liukaixuan@baidu.com
 */
public class BdBlobCacheAllInOneTableDBManager extends BdCacheBaseDBManager<byte[]> {

	private String sharedTableName;

	public BdBlobCacheAllInOneTableDBManager(BdSQLiteHelper helper, String sharedTableName) {
		super(helper);

		this.sharedTableName = sharedTableName;
	}

	public String onNewNameSpaceCreated(String nameSpace) {
		String sql1 = "CREATE TABLE IF NOT EXISTS "
				+ sharedTableName
				+ "(m_key VARCHAR(64) PRIMARY KEY, m_ns varchar(128), saveTime bigint(21) default 0, lastHitTime bigint(21) default 0, timeToExpire bigint(21) default 0, m_value blob)";

		String sql2 = "CREATE INDEX if not exists idx_mi_ns ON " + sharedTableName + "(m_ns)";

		helper.executeDDLSqlIgnoreAnyErrors(helper.getSharedWritableDB(), sql1);
		helper.executeDDLSqlIgnoreAnyErrors(helper.getSharedWritableDB(), sql2);

		return this.sharedTableName;
	}

	public void onNameSpaceUpgraded(String nameSpace, String tableName, int oldVersion, int newVersion) {
	}

	public int getCacheVersion() {
		return 1;
	}

	@Override
	protected BdCacheItem<byte[]> getFromDB(SQLiteDatabase db, String uniqueKey) throws Throwable {
		Cursor c = null;

		try {
			c = db.rawQuery("SELECT m_key, m_ns, saveTime, lastHitTime, timeToExpire, m_value  FROM " + tableName
					+ " where m_key = ?", new String[] { uniqueKey });

			if (c.moveToNext()) {
				BdCacheItem<byte[]> item = new BdCacheItem<byte[]>();
				item.uniqueKey = c.getString(0);
				item.nameSpace = c.getString(1);
				item.saveTime = c.getLong(2);
				item.lastHitTime = c.getLong(3);
				item.timeToExpire = c.getLong(4);
				item.value = c.getBlob(5);

				return item;
			}
		} finally {
			BdCloseHelper.close(c);
		}

		return null;
	}

	@Override
	protected ContentValues prepareForAddOrUpdate(BdCacheItem<byte[]> item) {
		ContentValues cv = new ContentValues();
		cv.put("m_key", item.uniqueKey);
		cv.put("m_ns", item.nameSpace);
		cv.put("m_value", item.value);
		cv.put("saveTime", item.saveTime);
		cv.put("lastHitTime", item.lastHitTime);
		cv.put("timeToExpire", item.timeToExpire);

		return cv;
	}

	public Cursor queryAllForNameSpace(SQLiteDatabase db, String nameSpace) {
		return db.rawQuery("select * from " + tableName + " where m_ns = ?", new String[] { nameSpace });
	}

	protected Cursor countForNameSpace(SQLiteDatabase db, String nameSpace) {
		return db.rawQuery("select count(*) from " + tableName + " where m_ns = ?", new String[] { nameSpace });
	}

	@Override
	protected boolean clearData(String nameSpace) {
		try {
			helper.getSharedWritableDB().delete(tableName, "m_ns = ?", new String[] { nameSpace });
		} catch (Throwable t) {
			helper.notifyExceptionOnSharedDB(t);

			BdLog.e(this.getClass(), "failed to clear from " + nameSpace, t);
			return false;
		}

		return true;
	}

}
