/**
 * @date 2017年3月17日
 * @version 1.0
 */
package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import jsoup.JsoupUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import db.DbUtil;
import pojo.Link;
import util.FileUtils;

public class LayeringTest {

	private static int urlId = 0;
	
	//root url
	private final String rootUrlName = "北京交通大学";
	private final String rootUrl = "http://www.bjtu.edu.cn/index.htm";
	
	//file save root path
	private final String rootPath = "D:/spider/";
	private final String emptyPath = "";
	private final String charSet = "utf-8";
	
	//Jsoup Object
	private JsoupUtil jsoup = JsoupUtil.getInstance();
	//db util
	private DbUtil dbUtil = DbUtil.getInstance();
	
	//time out
	private final static int timeOut = 1000;
	
	//all url hash
	private HashMap<Integer, Link> urlLinkHashmap = new HashMap<Integer, Link>();
	
	private static Queue<Link> urlQueue = new LinkedList<Link>();
	
	private static String[] domain = {"bjtu.edu.cn", "njtu.edu.cn"}; 
	/**
	 * layering test
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		LayeringTest layer = new LayeringTest();
		layer.init();
		System.out.println("Yinger's Spider Start working...");
		while(!urlQueue.isEmpty()) {
			
			Link spiderLink = urlQueue.poll();
			layer.getAllLinks(spiderLink);
			
		}
		System.out.println("Yinger's Spider has finish the work.");
	}
	
	/**
	 * init the enviroments
	 */
	public void init() {
		
		System.out.println("Yinger's Spider Initialize...");
		Link rootLink = newLinkObject(rootUrlName, rootUrl, urlId, emptyPath);
		urlLinkHashmap.put(rootLink.hashCode(), rootLink);
		urlQueue.add(rootLink);	
		
		dbUtil.insert(jsoup.getInsertSql(rootLink));
	}
	
	/**
	 * create a new Link Object
	 * @param urlname
	 * @param url
	 * @param upLayerId
	 */	
	private Link newLinkObject(String urlName, String url, int id, String savePath) {
		return new Link(urlId++, urlName, url, id, savePath);
	}
	
	/**
	 * get all Links according the url
	 * @param url
	 * @param upLayerId
	 * @throws IOException 
	 */
	public void getAllLinks(Link spiderLink) {
		
		print("Fetching url: %s...", spiderLink.getUrl());
		
		Document doc;
		try {
			doc = Jsoup.connect(spiderLink.getUrl())
					.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)").timeout(timeOut).get();
		} catch (IOException e) {
			System.err.printf(spiderLink.getUrl() + " 页面无效。");
			return;
		}
		Elements links = doc.select("a[href]");
		for(Element link : links) {
			
			String urlLink = link.attr("abs:href");
			String urlName = link.text();
			
			if(urlLink.isEmpty()) {
				continue;
			} else if(urlLink.startsWith("http://") && (urlLink.endsWith("/") || urlLink.endsWith("index.htm"))&& !urlLink.endsWith("shtml") 
					&& isBJUTWebsite(urlLink)) {
				
				if(!urlLinkHashmap.containsKey(urlLink.hashCode())) {
					Link tempLink = newLinkObject(urlName, urlLink, spiderLink.getId(), emptyPath);
					urlLinkHashmap.put(tempLink.hashCode(), tempLink);					
					//insert sql;					
					dbUtil.insert(jsoup.getInsertSql(tempLink));
					//print("%s %s %d", link.attr("abs:href"), link.text(), upLayerId);
					urlQueue.add(tempLink);
				}
			} else {
				if(isBJUTWebsite(urlLink)) {					
					if(!urlLinkHashmap.containsKey(urlLink.hashCode())) {
						Link tempLink = newLinkObject(urlName, urlLink, spiderLink.getId(), emptyPath);
						urlLinkHashmap.put(tempLink.hashCode(), tempLink);
						dbUtil.insert(jsoup.getInsertSql(tempLink));
						
						//saveFile(tempLink);
					}
				}			
			}
		}
		
		saveFile(spiderLink);
	}
	
	public void saveFile(Link spiderLink) {
		Document doc;
		try {
			doc = Jsoup.connect(spiderLink.getUrl())
					.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)").timeout(timeOut).get();
		} catch (IOException e) {
			System.err.printf(spiderLink.getUrl() + " 页面无效。\n");
			return;
		}
		
		String html = doc.html();
		
		String filePath = spiderLink.getUrl().substring(7);
		filePath = replaceCharWord(filePath, '/', '.');
		FileUtils.writeFile(html, rootPath.concat(filePath), charSet);
		
		//print("save file %s", filePath);
		
		String updateSavepath = jsoup.getInsertSavepathSql(spiderLink, rootPath.concat(filePath));
		dbUtil.insert(updateSavepath);
		//print("update Link %s.", spiderLink.getUrl());
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
	
	/**
	 * format print
	 */
	public static void print(String msg, Object... args) {  
        System.out.println(String.format(msg, args));  
    } 
	
	/**
	 * is bjtu's website
	 */
	public static boolean isBJUTWebsite(String url) {
		
		if(url.contains(domain[0]) || url.contains(domain[1])) {
			return true;
		} else {
			String deleteHttpUrl = url.substring(7);
			char[] temp = deleteHttpUrl.toCharArray();
			if(temp[0] >= '0' && temp[0] <= '9' && ((temp[1] >= '0' && temp[1] <= '9') || temp[1] <= '.')
					&& ((temp[2] >= '0' && temp[2] <= '9') || temp[2] <= '.')
					&& ((temp[3] >= '0' && temp[3] <= '9') || temp[3] <= '.')
					&& temp[4] >= '0' && temp[4] <= '9') {
				return true;
			}
		}
		return false;
	}
	
}
