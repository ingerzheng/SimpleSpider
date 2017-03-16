package pojo;

import java.io.Serializable;
import java.sql.Date;

public class Link implements Serializable {
	
	private static final long serialVersionUID = 1165098694307553167L;
	
	/**
	 * ID
	 */
	private int id;
	
	/**
	 * link name
	 */
	private String urlName;
	
	/**
	 * link url
	 */
	private String url;
	
	/**
	 * link uo layer id	
	 */
	private int upLayerId;
	
	/**
	 * insert db date
	 */
	private Date date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the upLayerId
	 */
	public int getUpLayerId() {
		return upLayerId;
	}

	/**
	 * @param upLayerId the upLayerId to set
	 */
	public void setUpLayerId(int upLayerId) {
		this.upLayerId = upLayerId;
	}

}