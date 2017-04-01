/**
 * @date 2017年3月17日
 * @version 1.0
 */
package junit.test;

import java.io.File;

import junit.framework.TestCase;
import test.LayeringTest;
import util.FileUtils;
import util.StringUtil;
import util.UrlUtils;

public class WebSiteTest extends TestCase{

	public void testIsBJTUWebsite() {
		
		String url = "http://www.bjtu.edu.cn/";
		String temp = url.substring(7);
		System.out.println(temp);
		int num = url.substring(7).indexOf("/");
		System.out.println(num + "");
		LayeringTest lt = new LayeringTest();
		boolean res = lt.isBJUTWebsite(url);
		assertTrue(res);
		
		String filePath = "www.bjtu.edu.cn/";
		
		File file = new File(filePath);
		String fileName = FileUtils.getUrlFileName(file.getName());
		String extName = FileUtils.getFileExtensionName(fileName);
		System.out.println(fileName + " " + extName);
		if(filePath.endsWith("/")) {
			filePath = filePath.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
		} else {
			if("".equals(fileName) || "/".equals(fileName)){
				fileName = "index.html";
			} else if(StringUtil.isNullOrEmpty(extName)){
				filePath = filePath.replaceAll("[\\?/:*|<>\"]", "_") + "index.html";
			} else {
				filePath = filePath.replaceAll("[\\?/:*|<>\"]", "_");
			}
		}
		
		System.out.println(filePath);
	}
}
