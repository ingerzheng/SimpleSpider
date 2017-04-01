/**
 * @date 2017骞�3鏈�25鏃�
 * @version 1.0
 */
package task;

import java.io.IOException;

import jsoup.JsoupUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pojo.Link;
import test.LayeringWithThreadPool;
import util.FileUtils;
import util.KeyUtils;
import util.OutFormatUtils;
import util.UrlUtils;
import executor.DBExecutor;
import executor.TaskExecutor1;

public class SpiderTask1 implements Runnable{

	private String url;
	private int upLayerId;
	
	private int timeOut;
	private String emptyPath = "";
	
	private final String charSet = "utf-8";
	private final String rootDir = "D:/spider/";

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
			System.err.printf(url + " 404 Error");
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
					DBExecutor.addTask(new DBTask(JsoupUtil.getInstance().getInsertSql(tempLink)));
					//System.out.println(OutFormatUtils.print("id:%d %s %s %d", tempLink.getId(), link.attr("abs:href"), tempLink.getUrlName(), upLayerId));
					saveFile(tempLink);
				}
			} else {
				if(UrlUtils.isBJUTWebsite(urlLink)) {					
					if(!LayeringWithThreadPool.urlLinkHashmap.containsKey(urlLink.hashCode())) {
						Link tempLink = new Link(KeyUtils.getUrlId(), urlName, urlLink, upLayerId, emptyPath);		
						LayeringWithThreadPool.urlLinkHashmap.put(urlLink.hashCode(), urlLink);		
						//System.out.println(OutFormatUtils.print("id:%d %s %s %d", tempLink.getId(), link.attr("abs:href"), link.text(), upLayerId));
						DBExecutor.addTask(new DBTask(JsoupUtil.getInstance().getInsertSql(tempLink)));
						//dbUtil.insert(jsoup.getInsertSql(tempLink));
						saveFile(tempLink);
					}
				}			
			}
		}
	}
	
	public void saveFile(Link spiderLink) {
		Document doc;
		try {
			doc = Jsoup.connect(spiderLink.getUrl())
					.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)").timeout(timeOut).get();
		} catch (IOException e) {
			System.err.printf(spiderLink.getUrl() + " 404Error\n");
			return;
		}
		
		String html = doc.html();
		
		String filePath = spiderLink.getUrl();
		if(filePath.indexOf("https://") != -1) {
			filePath = filePath.substring(filePath.indexOf("https://") + 8);
		} else {
			filePath = filePath.substring(filePath.indexOf("http://") + 7);
		}	
		
		filePath = FileUtils.parseFilePathAndName(filePath);
		//System.out.println(filePath);
		FileUtils.writeFile(html, rootDir.concat(filePath),charSet);
		
		//print("save file %s", filePath);
		
		String updateSavepath = JsoupUtil.getInstance().getInsertSavepathSql(spiderLink, rootDir.concat(filePath));
		DBExecutor.addTask(new DBTask(updateSavepath));
		//dbUtil.insert(updateSavepath);
	}
	
	public static String replaceCharWord(String oldString, char replace, char nowChar) {
		char[] res = oldString.toCharArray();
		
		for(int i = 0; i < res.length; i++) {
			if(res[i] == replace) {
				res[i] = nowChar;
			}
		}
		return new String(res);
	}
}
