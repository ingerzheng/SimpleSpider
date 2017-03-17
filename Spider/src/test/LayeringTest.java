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

import pojo.Link;

public class LayeringTest {

	private static int urlId = 0;
	
	//root url
	private final String rootUrlName = "北京交通大学";
	private final String rootUrl = "http://www.bjtu.edu.cn/index.htm";
	
	//Jsoup Object
	private JsoupUtil jsoup = JsoupUtil.getInstance();
	
	//time out
	private final static int timeOut = 1000;
	
	//all url hash
	private HashMap<Integer, Link> urlLinkHashmap = new HashMap<Integer, Link>();
	
	private static Queue<Link> urlQueue = new LinkedList<Link>();
	
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
			} else if((urlLink.endsWith("htm") || urlLink.endsWith("html") || urlLink.endsWith("\\/")) && !urlLink.endsWith("shtml") && !urlName.isEmpty()) {
				Link tempLink = newLinkObject(urlName, urlLink, upLayerId);
				if(!urlLinkHashmap.containsKey(tempLink.hashCode())) {
					urlLinkHashmap.put(tempLink.hashCode(), tempLink);					
					//insert sql;
					print("%s %s %d", link.attr("abs:href"), link.text(), upLayerId);
					urlQueue.add(tempLink);
				}
			} else {
				Link tempLink = newLinkObject(urlName, urlLink, upLayerId);
				if(!urlLinkHashmap.containsKey(tempLink.hashCode())) {
					urlLinkHashmap.put(tempLink.hashCode(), tempLink);
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
	
}
