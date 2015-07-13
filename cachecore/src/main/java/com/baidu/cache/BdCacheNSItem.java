package com.baidu.cache;

public class BdCacheNSItem {

	public static final String CACHE_TYPE_TEXT = "text";
	public static final String CACHE_TYPE_BLOB = "blob";

	public String nameSpace;

	public String tableName;

	public int maxSize;

	public String cacheType;

	public int cacheVersion;

	public long lastActiveTime;

}