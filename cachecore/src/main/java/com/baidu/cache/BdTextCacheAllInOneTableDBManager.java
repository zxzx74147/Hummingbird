/**
 * 
 */
package com.baidu.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * Text Cache使用的SQLite Database封装【所有name space的cache存储在1张表里】
 * 
 * @author liukaixuan@baidu.com
 */
public class BdTextCacheAllInOneTableDBManager extends BdCacheBaseDBManager<String> {

	private String sharedTableName;

	public BdTextCacheAllInOneTableDBManager(BdSQLiteHelper helper, String sharedTableName) {
		super(helper);

		this.sharedTableName = sharedTableName;
	}

	public String onNewNameSpaceCreated(String nameSpace) {
		String sql1 = "CREATE TABLE IF NOT EXISTS "
				+ sharedTableName
				+ "(m_key VARCHAR(64) PRIMARY KEY, m_ns varchar(128), saveTime bigint(21) default 0, lastHitTime bigint(21) default 0, timeToExpire bigint(21) default 0, m_value text)";

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
	protected BdCacheItem<String> getFromDB(SQLiteDatabase db, String uniqueKey) throws Throwable {
		Cursor c = null;

		try {
			c = db.rawQuery("SELECT m_key, m_ns, saveTime, lastHitTime, timeToExpire, m_value  FROM " + tableName
					+ " where m_key = ?", new String[] { uniqueKey });
			BdCacheItem<String> item = null;

			if (c.moveToNext()) {
				item = new BdCacheItem<String>();
				item.uniqueKey = c.getString(0);
				item.nameSpace = c.getString(1);
				item.saveTime = c.getLong(2);
				item.lastHitTime = c.getLong(3);
				item.timeToExpire = c.getLong(4);
				item.value = c.getString(5);

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

	}

}
