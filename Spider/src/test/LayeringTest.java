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

public class LayeringTest {

	private static int urlId = 0;
	
	//root url
	private final String rootUrlName = "北京交通大学";
	private final String rootUrl = "http://www.bjtu.edu.cn/index.htm";
	
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
			layer.getAllLinks(spiderLink.getUrl(), spiderLink.getId());
			
		}
	}
	
	/**
	 * init the enviroments
	 */
	public void init() {
		
		System.out.println("Yinger's Spider Initialize...");
		Link rootLink = newLinkObject(rootUrlName, rootUrl, urlId);
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
	private Link newLinkObject(String urlName, String url, int id) {
		return new Link(urlId++, urlName, url, id);
	}
	
	/**
	 * get all Links according the url
	 * @param url
	 * @param upLayerId
	 * @throws IOException 
	 */
	public void getAllLinks(String url, int upLayerId) {
		
		print("Fetching url: %s...", url);
		
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(timeOut).get();
		} catch (IOException e) {
			return;
		}
		Elements links = doc.select("a[href]");
		for(Element link : links) {
			
			String urlLink = link.attr("abs:href");
			String urlName = link.text();
			
			if(urlLink.isEmpty()) {
				continue;
			} else if(urlLink.startsWith("http://") &&  urlLink.endsWith("/") && !urlLink.endsWith("shtml") 
					&& isBJUTWebsite(urlLink)) {
				
				if(!urlLinkHashmap.containsKey(urlLink.hashCode())) {
					Link tempLink = newLinkObject(urlName, urlLink, upLayerId);
					urlLinkHashmap.put(tempLink.hashCode(), tempLink);					
					//insert sql;					
					dbUtil.insert(jsoup.getInsertSql(tempLink));
					//print("%s %s %d", link.attr("abs:href"), link.text(), upLayerId);
					urlQueue.add(tempLink);
				}
			} else {
				if(isBJUTWebsite(urlLink)) {					
					if(!urlLinkHashmap.containsKey(urlLink.hashCode())) {
						Link tempLink = newLinkObject(urlName, urlLink, upLayerId);
						urlLinkHashmap.put(tempLink.hashCode(), tempLink);
						dbUtil.insert(jsoup.getInsertSql(tempLink));
					}
				}			
			}
		}
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
