/**
 * 
 */
package com.baidu.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * Text Cache使用的SQLite Database封装
 * 
 * @author liukaixuan@baidu.com
 */
public class BdTextCachePerTableDBManager extends BdCacheBaseDBManager<String> {

	public BdTextCachePerTableDBManager(BdSQLiteHelper helper) {
		super(helper);
	}

	public String onNewNameSpaceCreated(String nameSpace) {
		String m_tableName = BdCacheService.CACHE_TABLE_PREFIX + "t" + Math.abs(nameSpace.hashCode());

		String sql1 = "CREATE TABLE IF NOT EXISTS "
				+ m_tableName
				+ "(m_key VARCHAR(64) PRIMARY KEY, saveTime bigint(21) default 0, lastHitTime bigint(21) default 0, timeToExpire bigint(21) default 0, m_value text)";

		helper.executeDDLSqlIgnoreAnyErrors(helper.getSharedWritableDB(), sql1);

		return m_tableName;
	}

	public void onNameSpaceUpgraded(String nameSpace, String tableName, int oldVersion, int newVersion) {
	}

	public int getCacheVersion() {
		return 1;
	}

	@Override
	protected BdCacheItem<String> getFromDB(SQLiteDatabase db, String uniqueKey) throws Throwable {
		Cursor c = null;

		try {
			c = db.rawQuery("SELECT m_key, saveTime, lastHitTime, timeToExpire, m_value  FROM " + tableName
					+ " where m_key = ?", new String[] { uniqueKey });
			BdCacheItem<String> item = null;

			if (c.moveToNext()) {
				item = new BdCacheItem<String>();
				item.uniqueKey = c.getString(0);
				item.saveTime = c.getLong(1);
				item.lastHitTime = c.getLong(2);
				item.timeToExpire = c.getLong(3);
				item.value = c.getString(4);

				return item;
			}
		} finally {
			BdCloseHelper.close(c);
		}

		return null;
	}

	@Override
	protected ContentValues prepareForAddOrUpdate(BdCacheItem<String> item) {
		ContentValues cv = new ContentValues();
		cv.put("m_key", item.uniqueKey);
		cv.put("m_value", item.value);
		cv.put("saveTime", item.saveTime);
		cv.put("lastHitTime", item.lastHitTime);
		cv.put("timeToExpire", item.timeToExpire);

		return cv;
	}

	public Cursor queryAllForNameSpace(SQLiteDatabase db, String nameSpace) {
		return db.rawQuery("select * from " + tableName, new String[0]);
	}

	protected Cursor countForNameSpace(SQLiteDatabase db, String nameSpace) {
		return db.rawQuery("select count(*) from " + tableName, new String[0]);
	}

	@Override
	protected boolean clearData(String nameSpace) {
		this.helper.executeDDLSqlIgnoreAnyErrors(helper.getSharedWritableDB(), "DROP TABLE IF EXISTS " + tableName);

		return true;
	}

}
