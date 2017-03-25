/**
 * @date 2017年3月25日
 * @version 1.0
 */
package util;

public class KeyUtils {

	private static int urlId = 1;
	
	public static synchronized int getUrlId() {
		return urlId++;
	}
}
