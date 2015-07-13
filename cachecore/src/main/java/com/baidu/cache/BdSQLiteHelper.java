/**
 * 
 */
package com.baidu.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 
 * ADP库内部使用的SQLite服务
 * 
 * @author liukaixuan@baidu.com
 */
public class BdSQLiteHelper extends SQLiteOpenHelper {

	/**
	 * 用来存储每个name space对应的存储表信息。
	 */
	public static final String TABLE_CACHE_META_INFO = "cache_meta_info";

	private static final int DATABASE_VERSION = 1;

	public BdSQLiteHelper(Context context, String databaseFile) {
		// CursorFactory设置为null,使用默认值
		super(context, databaseFile, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.prepareTableForVersion1(db);
	}

	public boolean executeDDLSqlIgnoreAnyErrors(SQLiteDatabase db, String sql) {
		try {
			db.execSQL(sql);
		} catch (Throwable t) {
			BdLog.e(this.getClass(), sql, t);

			return false;
		}

		return true;
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 1) {
			this.prepareTableForVersion1(db);
		}
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 防止降级发生crash
	}

	protected void prepareTableForVersion1(SQLiteDatabase db) {
		String cacheMetaInfo = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_CACHE_META_INFO
				+ "(nameSpace VARCHAR(128) PRIMARY KEY, tableName varchar(64), maxSize int(11) default 0, cacheType varchar(32) not null, cacheVersion int(11) default 0, lastActiveTime bigint(21) default 0)";

		this.executeDDLSqlIgnoreAnyErrors(db, cacheMetaInfo);
	}

	// shared database
	private static SQLiteDatabase sharedDB;

	private static Object lockForDBOpen = new Object();

	public SQLiteDatabase getSharedWritableDB() {
		if (sharedDB == null || !sharedDB.isOpen()) {
			synchronized (lockForDBOpen) {
				if (sharedDB == null) {
					sharedDB = this.getWritableDatabase();
				}
			}
		}

		return sharedDB;
	}

	/**
	 * re-open
	 */
	public void notifyExceptionOnSharedDB(Throwable t) {
		this.close();
	}

	public void close() {
		synchronized (lockForDBOpen) {
			try {
				sharedDB.close();
			}catch (Exception e){
				e.printStackTrace();
			}

			sharedDB = null;
		}
	}

}
