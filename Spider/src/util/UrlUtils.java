/**
 * @date 2017年3月25日
 * @version 1.0
 */
package util;

public class UrlUtils {

	private static String[] domain = {"bjtu.edu.cn", "njtu.edu.cn"}; 
	
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
