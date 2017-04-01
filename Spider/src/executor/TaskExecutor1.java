/**
 * @date 2017年3月25日
 * @version 1.0
 */
package executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import task.SpiderTask1;
import test.LayeringWithThreadPool;

public class TaskExecutor1 {

	private ExecutorService threadPool;                  //thread Pool
	private Thread queueThread;                          //get task thread
	public static LinkedBlockingQueue<SpiderTask1> taskQueue;   //task tread
	
	private String rootUrl = "http://www.bjtu.edu.cn";
	
	private int maxThread = 5;
	
	public TaskExecutor1(int maxThread) {
		this.maxThread = maxThread;
	}
	
	public TaskExecutor1() {
		queueThread = new Thread() {

			@Override
			public void run() {
				while(!interrupted()) {
					if(!taskQueue.isEmpty()) {
						SpiderTask1 task = take();
						if(task != null) {
							threadPool.execute(task);
						}
					}									
				}
			}			
		};
		
		SpiderTask1 rootTask = new SpiderTask1(rootUrl, 0, 3000);
		LayeringWithThreadPool.urlLinkHashmap.put(rootUrl.hashCode(), rootUrl);
		threadPool = Executors.newFixedThreadPool(maxThread);
		taskQueue = new LinkedBlockingQueue<SpiderTask1>();
		addTask(rootTask);
		queueThread.start();
	}
	
	public static SpiderTask1 take() {
		
		synchronized (taskQueue) {
			try {
				return taskQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	public static void addTask(SpiderTask1 task) {
		
		synchronized (taskQueue) {
			try {
				taskQueue.put(task);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
}
