package com.baidu.cache;

public class BdCacheItem<T> {

	public String uniqueKey;

	public T value;

	public String nameSpace;

	public long saveTime;

	public long lastHitTime;

	//
	public long timeToExpire;

}