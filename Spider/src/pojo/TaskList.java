/**
 * @date 2017年3月25日
 * @version 1.0
 */
package pojo;

public class TaskList {

	private String url;
	private int upLayerId;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}



	/**
	 * @return the upLayerId
	 */
	public int getUpLayerId() {
		return upLayerId;
	}



	public TaskList(String url, int upLayerId) {
		this.url = url;
		this.upLayerId = upLayerId;
	}
}
