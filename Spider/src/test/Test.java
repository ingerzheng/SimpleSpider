package test;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	public static void main(String[] args) {
		try {
			Document doc = Jsoup.connect("http://en.bjtu.edu.cn/")
							.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)")
							.timeout(5000).get();
			Elements hrefs = doc.select("a[href]");
			int count=0;
			for(Element elem:hrefs){
				count++;
				System.out.println(elem.attr("abs:href"));
				
			}
			System.out.println(count);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}