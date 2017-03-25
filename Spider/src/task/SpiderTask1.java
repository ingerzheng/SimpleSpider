/**
 * @date 2017年3月25日
 * @version 1.0
 */
package task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import executor.TaskExecutor1;
import pojo.Link;
import pojo.TaskList;
import test.LayeringWithThreadPool;
import util.KeyUtils;
import util.OutFormatUtils;
import util.UrlUtils;

public class SpiderTask1 implements Runnable{

	private String url;
	private int upLayerId;
	
	private int timeOut;
	private String emptyPath = "";

	public String getUrl() {
		return url;
	}

	public int getUpLayerId() {
		return upLayerId;
	}

	/**
	 * @param url
	 * @param upLayerId
	 * @param timeOut
	 */
	public SpiderTask1(String url, int upLayerId, int timeOut) {
		super();
		this.url = url;
		this.upLayerId = upLayerId;
		this.timeOut = timeOut;
	}


	public void run() {

		System.out.println(OutFormatUtils.print("Fetching url: %s...", url));
		Document doc;
		try {
			doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)").timeout(timeOut).get();
		} catch (IOException e) {
			System.err.printf(url + " 页面无效。");
			return;
		}
		Elements links = doc.select("a[href]");
		for(Element link : links) {
			
			String urlLink = link.attr("abs:href");
			String urlName = link.text();
			if(urlLink.isEmpty()) {
				continue;
			} else if(urlLink.startsWith("http://") && (urlLink.endsWith("/") || urlLink.endsWith("index.htm"))&& !urlLink.endsWith("shtml") 
					&& UrlUtils.isBJUTWebsite(urlLink)) {	
				if(!LayeringWithThreadPool.urlLinkHashmap.containsKey(urlLink.hashCode())) {
					Link tempLink = new Link(KeyUtils.getUrlId(), urlName, urlLink, upLayerId, emptyPath);
					LayeringWithThreadPool.urlLinkHashmap.put(urlLink.hashCode(), urlLink);			
					
					//add Task
					SpiderTask1 spiderTask = new SpiderTask1(urlLink, tempLink.getId(), 3000);
					TaskExecutor1.addTask(spiderTask);
					//insert sql;					
					//dbUtil.insert(jsoup.getInsertSql(tempLink));
					System.out.println(OutFormatUtils.print("Thread:%d %s %s %d", Thread.currentThread().getId(), link.attr("abs:href"), tempLink.getUrlName(), upLayerId));
				}
			} else {
				if(UrlUtils.isBJUTWebsite(urlLink)) {					
					if(!LayeringWithThreadPool.urlLinkHashmap.containsKey(urlLink.hashCode())) {
						Link tempLink = new Link(KeyUtils.getUrlId(), urlName, urlLink, upLayerId, emptyPath);		
						LayeringWithThreadPool.urlLinkHashmap.put(urlLink.hashCode(), urlLink);		
						System.out.println(OutFormatUtils.print("Thread:%d %s %s %d", Thread.currentThread().getId(), link.attr("abs:href"), link.text(), upLayerId));
						//dbUtil.insert(jsoup.getInsertSql(tempLink));
						//saveFile(tempLink);
					}
				}			
			}
		}
	}


}
