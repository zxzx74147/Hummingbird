package com.baidu.cache;

/**
 * 
 * 线程调度服务
 * 
 * @author liukaixuan@baidu.com
 */
public class ThreadService {

	private static ThreadService _service = new ThreadService();

	public static ThreadService sharedInstance() {
		return _service;
	}

	public void submitTask(Runnable task) {
		new Thread(task).start();
	}

}
