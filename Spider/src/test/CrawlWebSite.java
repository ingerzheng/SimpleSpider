package test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jsoup.JsoupUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements; 

import pojo.Link;

import db.DbUtil;

import util.FileUtils;

public class CrawlWebSite {
	private final static String charSet = "utf-8";
	private final static String rootDir = "D:/spider/";
    private static JsoupUtil ju = JsoupUtil.getInstance();  
    
    private static DbUtil du = DbUtil.getInstance();  
    private static String insertSql = "";  
    private static Link link = new Link(); 
	
	private final static String rootUrl = "http://www.bjtu.edu.cn/";
	
	private final static int timeOut = 30000;	
	
	/**网站上相对地址与绝对地址的映射*/
	private static Map<String,String> absRelativeUrlMap = new HashMap<String,String>();
	
	/**网站上的url与最终本地使用的url映射*/
	private static Map<String,String> urlmapMap = new HashMap<String,String>();	
	
	/**网站上的css,js*/
	private static Map<String,String> cssjsmapMap = new HashMap<String,String>();
	
	private static List<File> allFiles = new ArrayList<File>();	
	/**过滤掉不爬取的内容格式*/
	
	public static final String filterExtArray []  = {"rar","zip","bmp","dib","gif","jfif","jpe","jpeg","jpg","png","tif","tiff","ico","pdf","doc","docx","xls","xlsx"}; 
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		
			System.out.println("start....");
		
			//获取所有urls
			getSubUrls(rootUrl,rootUrl);
			
			//保存文件
			for(String absUrl : absRelativeUrlMap.keySet()){
				
				String content;
				try {
					content = readContent(absUrl);
				} catch (IOException e) {
					System.err.println("url="+absUrl+", 页面无效！");
					continue;
				}
				if(!absUrl.startsWith(rootUrl)){
					continue;
				}
				String filePath = absUrl.substring(rootUrl.length());
				filePath = FileUtils.parseFilePath(filePath);
				 
				//urlmapMap.put(absRelativeUrlMap.get(absUrl), filePath);//脱机运行和在服务器运行有所不同。。。
				urlmapMap.put(absRelativeUrlMap.get(absUrl), rootDir.concat(filePath));//脱机运行。。。
				FileUtils.writeFile(content, rootDir.concat(filePath),charSet);
			}
			  
			System.out.println("--------------------"); 
			//更新文件中的url
			getAllFiles(new File(rootDir)); 
			Document doc = null;
			for(File file : allFiles){
				try {
					doc = Jsoup.parse(file, "utf8",rootUrl);
					dealCssJsFile(doc);
					replaceUrl(doc);
				} catch (Exception e) { 
					e.printStackTrace();
				}  
				String newContent = doc.html();
				FileUtils.writeFile(newContent, file.getAbsolutePath(),charSet);
			}
			
			System.out.println("finished.");
		
		 
	}
		
	
	/**
	 * 获取指定url页面中的所有链接
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static void getSubUrls(String absUrl,String relativeUrl)   {
		if(absRelativeUrlMap.get(absUrl)!=null || filter(absUrl)){
			return;
		}
		System.out.println(absUrl);
		absRelativeUrlMap.put(absUrl,relativeUrl); 
		Document doc = null;
		try {
			doc = Jsoup.connect(absUrl).timeout(timeOut).get();
		} catch (IOException e) {
			System.err.println("url="+absUrl+", 页面无效！");
			return;
		}
		
		Elements eles = doc.body().select("a[href]");
		System.out.println(eles);
		
		for(Element ele : eles){ 
			String absHref = ele.attr("abs:href").replaceAll("\\/", "");
			String href = ele.attr("href"); 
			if(href.startsWith("javascript") ||
					href.startsWith("#") ||
					(href.contains("(") && href.contains(""))){
				continue;
			}
			if(absHref.startsWith(rootUrl)){ 
				getSubUrls(absHref,href);
			} 
		}  
	}
		// TODO Auto-generated method stub
		  
//		if(absRelativeUrlMap.get(absUrl)!=null || filter(absUrl)){
//			return;
//		}
//		System.out.println(absUrl);
//		absRelativeUrlMap.put(absUrl,relativeUrl); 
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(absUrl).timeout(timeOut).get();
//		} catch (IOException e) {
//			System.err.println("url="+absUrl+", 页面无效！");
//			return;
//		}
//Elements eles = doc.body().select("a[href]");
//		
//		for(Element ele : eles){ 
//			String absHref = ele.attr("abs:href").replaceAll("\\.\\.\\/", "");
//			String href = ele.attr("href"); 
//			if(href.startsWith("javascript") ||
//					href.startsWith("#") ||
//					(href.contains("(") && href.contains(""))){
//				continue;
//			}
//			if(absHref.startsWith(rootUrl)){ 
//				getSubUrls(absHref,href);
//			} 
//		}  
//		Elements herfs = doc.body().select("a[href]");
//		 System.out.println(herfs);
//		
//		for(Element ele : herfs){ 
//			String absHref = ele.attr("abs:href").replaceAll("\\.\\.\\/", "");
//			String herf1 = ele.attr("href"); 
//			
////			if(link.startsWith("javascript") ||
////					link.startsWith("#") ||
////					(link.contains("(") && link.contains(""))){
////				continue;
////			}  
//		 Iterator<Element> hrefIter =herfs.iterator();  
//         while (hrefIter.hasNext()) {  
//             Element href = hrefIter.next();  
//             link.setId(ju.getUUID());  
//             link.setUrlName(href.text());  
//             link.setUrl(href.attr("href"));  
//             insertSql = ju.getInsertSql(link);  
//             du.insert(insertSql);  
//             if(absHref.startsWith(rootUrl)){ 
// 				getSubUrls(absHref,hrefs);
// 			} 
//         }  ; 
//         
//			
//		}  
	
	/**
	 * 读取指定url中的html
	 * @param absUrl
	 * @return
	 * @throws IOException
	 */
	public static String readContent(String absUrl) throws IOException{		
		Document doc = Jsoup.connect(absUrl).timeout(timeOut).get();	
		//替换图片地址为绝对地址
		for (Element img : doc.body().select("img")) {
			String absImageUrl = img.attr("abs:src");//获得绝对路径
			img.attr("src",absImageUrl);	
		}
		
		return doc.html();
	}
	
	
	  //显示目录的方法 
	 public static void getAllFiles(File f){ 
	     //判断传入对象是否为一个文件夹对象 
	     if(!f.isDirectory()){ 
	         System.err.println("你输入的不是一个文件夹，请检查路径是否有误！！"); 
	     }else{ 
	         File[] t = f.listFiles(); 
	         for(int i=0;i<t.length;i++){ 
	             //判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止 
	             if(t[i].isDirectory()){ 
	            	 getAllFiles(t[i]); 
	             }else{ 
	            	 allFiles.add(t[i].getAbsoluteFile()); 
	             } 
	         } 
	     } 
	 }

	
	/**
	 * 替换指定doc中的url
	 * @param doc
	 * @throws IOException 
	 */
	public static void replaceUrl(Document doc) {		
		Elements eles = doc.body().select("a[href]"); 
		for(Element ele : eles){  
			String href = ele.attr("href"); 
			String localHref = urlmapMap.get(href);
			if(localHref!=null){
				ele.attr("href",localHref);		
			}
		}   
	}
	
	/**
	 * 下载css和js文件，并更新相关链接
	 * @param doc
	 * @throws IOException 
	 */
	public static void dealCssJsFile(Document doc) throws Exception{ 
		//css
		Elements linkEles = doc.select("link[href][rel=stylesheet]"); 
		for(Element ele : linkEles){
			String cssUrl = ele.attr("href").replaceAll("\\.\\.\\/", "");//获得绝对路径  
			cssUrl = cssUrl.startsWith("/")?cssUrl.substring(1):cssUrl;
			cssUrl = rootUrl.concat(cssUrl.replace(rootUrl, "")); 
			String localCssPath = rootDir.concat("linksfile/").concat(cssUrl.substring(rootUrl.length()));
			localCssPath = FileUtils.parseFilePath(localCssPath);
			localCssPath = localCssPath.substring(0, localCssPath.lastIndexOf(".css")).concat(".css");
			if(cssjsmapMap.get(localCssPath)!=null){
				ele.attr("href", localCssPath); 
				continue;
			} 
			cssjsmapMap.put(localCssPath, cssUrl);
			System.out.println(cssUrl);
			
			FileUtils.writeFile(FileUtils.readFromUrl(cssUrl,charSet),localCssPath,charSet);	
			ele.attr("href", localCssPath); 
		}
		//js
		Elements scriptEles = doc.select("script[src]"); 
		for(Element ele : scriptEles){
			String jsUrl = ele.attr("src").replaceAll("\\.\\.\\/", "");//获得绝对路径			
			jsUrl = jsUrl.startsWith("/")?jsUrl.substring(1):jsUrl;
			jsUrl = rootUrl.concat(jsUrl.replace(rootUrl, "")); 
			String localJsPath = rootDir.concat("scriptsfile/").concat(jsUrl.substring(rootUrl.length()));
			localJsPath = FileUtils.parseFilePath(localJsPath);
			localJsPath = localJsPath.substring(0, localJsPath.lastIndexOf(".js")).concat(".js");
			if(cssjsmapMap.get(localJsPath)!=null){
				ele.attr("src", localJsPath);
				continue;
			} 
			cssjsmapMap.put(localJsPath, jsUrl);
			System.out.println(jsUrl);
			FileUtils.writeFile(FileUtils.readFromUrl(jsUrl,charSet),localJsPath,charSet);	
			ele.attr("src", localJsPath);
			
		}
	} 
	/**
	 * 在原始的css和js链接上加上绝对地址
	 * @param doc
	 * @throws IOException
	 */
	public static void dealCssJsFile2(Document doc) throws Exception{ 
		//css
		Elements linkEles = doc.select("link[href]"); 
		for(Element ele : linkEles){
			String cssUrl = ele.attr("href");//获得绝对路径 
			cssUrl = cssUrl.startsWith("/")?cssUrl.substring(1):cssUrl;
			cssUrl = rootUrl.concat(cssUrl.replace(rootUrl, ""));  
			ele.attr("href", cssUrl); 
			System.out.println(cssUrl);
		}
		//js
		Elements scriptEles = doc.select("script[src]"); 
		for(Element ele : scriptEles){
			String jsUrl = ele.attr("src");//获得绝对路径			
			jsUrl = jsUrl.startsWith("/")?jsUrl.substring(1):jsUrl;
			jsUrl = rootUrl.concat(jsUrl.replace(rootUrl, ""));  
			ele.attr("src", jsUrl);
			System.out.println(jsUrl);
			
		}
	}
	/**
	 * 检查页面中是否存在有效的链接
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static boolean existSubUrls(String url)   {
		// TODO Auto-generated method stub
		
		boolean exist = false;
		Document doc = null; 
		try {
			doc = Jsoup.connect(url).timeout(timeOut).get();
		} catch (IOException e) {
			System.err.println("url="+url+", 页面无效！");
			return false;
		}
		
		Elements eles = doc.body().select("a[href]");
		
		for(Element ele : eles){			
			String absHref = ele.attr("abs:href").replaceAll("\\.\\.\\/", "");
			String href = ele.attr("href"); 
			if(href.startsWith("javascript") ||
					href.startsWith("#") ||
					(href.contains("(") && href.contains(""))){
				continue;
			}
			if(absHref.startsWith("http") && !url.equalsIgnoreCase(absHref)){
				exist = true;
				break;
			}  
			
		}
		return exist;
		 
	}
	
	public static boolean filter(String url){
		for(String ext : filterExtArray){
			if(FileUtils.isValidFiles(url.toLowerCase(), ext)){
				return true;
			}
		}
		return false;
	}

}
