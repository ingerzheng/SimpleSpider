/**
 * @date 2017年3月25日
 * @version 1.0
 */
package test;

import java.util.concurrent.ConcurrentHashMap;

import executor.TaskExecutor1;

public class LayeringWithThreadPool {
	
	public static ConcurrentHashMap<Integer, String> urlLinkHashmap = new ConcurrentHashMap<Integer, String>();
	
	public static void main(String[] args) {
		
		TaskExecutor1 te = new TaskExecutor1();
	}

}
