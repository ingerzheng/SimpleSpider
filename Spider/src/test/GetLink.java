package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsoup.JsoupUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pojo.Link;
import util.FileUtils;

import db.Constants;
import db.DbUtil;

public class GetLink {
	private final static String charSet = "utf-8";
	//private final static String rootDir="D:/spider";
	//private final static String rootUrl = "Constants.URL";
	private static JsoupUtil ju = JsoupUtil.getInstance();     
    private static DbUtil du = DbUtil.getInstance();      
    private static Link link = new Link();      
    private static String insertSql = "";  
      
    public static void getLink(String url) {  
        try {  
        	Document document = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)")
					.timeout(5000).get();
            Elements hrefs = document.select("a[href]");
           
          //  getLinkByUrl(s);
            int count=0;
            Iterator<Element> hrefIter = hrefs.iterator();  
            while (hrefIter.hasNext()) {  
                Element href = hrefIter.next(); 
               // String s=getLinkStringByUrl();  
                link.setUrlName(href.text());  
                link.setUrl(href.attr("href"));  
                insertSql = ju.getInsertSql(link);  
                du.insert(insertSql);  
                
               
            }  
            Elements srcs = document.select("img[src]");  
            Iterator<Element> srcIter = srcs.iterator();  
            while(srcIter.hasNext()){  
                Element src = srcIter.next();  
              //  link.setId(ju.getUUID());  
                link.setUrlName(src.attr("alt"));  
                link.setUrl(src.attr("src"));  
                insertSql = ju.getInsertSql(link);  
                du.insert(insertSql);  
            }  
            Elements opts = document.select("option[value]");  
            Iterator<Element> optIter = opts.iterator();  
            while(optIter.hasNext()){  
                Element opt = optIter.next();  
            //    link.setId();  
                link.setUrlName(opt.text());  
                link.setUrl(opt.attr("value"));  
                insertSql = ju.getInsertSql(link);  
                du.insert(insertSql);  
            }  
            Elements links = document.select("link[href]");  
            Iterator<Element> linkIter = links.iterator();  
            while(linkIter.hasNext()){  
                Element li =  linkIter.next();  
                //link.setId(ju.getUUID());  
                link.setUrlName(li.text());  
                link.setUrl(li.attr("href"));  
                insertSql = ju.getInsertSql(link);  
                du.insert(insertSql);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
    
  public static  void getLinkStringByUrl() {
	List ls  =du.queryByUrl();
	 System.out.println(ls.size());
  }
	
    
    public static void main(String[] args) { 
    	//Logger logger=  Logger.getLogger(GetLink.class);
         new GetLink().getLink(Constants.URL);
       
       //  String s=getLinkStringByUrl(); 
        // System.out.println(s);
      // if(s.startsWith("http")){
     //	getLink(s.substring(0, s.length()-1));  
      // }// String s=getLinkStringByUrl();
    }  
  
}  