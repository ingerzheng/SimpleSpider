/**
 * @date 2017年3月17日
 * @version 1.0
 */
package junit.test;

import junit.framework.TestCase;
import test.LayeringTest;
import util.UrlUtils;

public class WebSiteTest extends TestCase{

	public void testIsBJTUWebsite() {
		
		String url = "http://www.bjtu.edu.cn";
		LayeringTest lt = new LayeringTest();
		boolean res = lt.isBJUTWebsite(url);
		assertTrue(res);
	}
}
