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
	 * link up layer id	
	 */
	private int upLayerId;
	
	/**
	 * link doc save path
	 */
	private String savePath;
	
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
	
	/**
	 * @return the savePath
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * @param savePath the savePath to set
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public Link() {
		
	}
	
	/**
	 * @param id
	 * @param urlName
	 * @param url
	 * @param upLayerId
	 * @param date
	 */
	public Link(int id, String urlName, String url, int upLayerId, String savePath) {
		
		this.id = id;
		this.urlName = urlName;
		this.url = url;
		this.upLayerId = upLayerId;
		this.savePath = savePath;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherObject) {
		
		//a quick test to see if the objects are identical
		if(this == otherObject) {
			return true;
		}
		
		//must return false if the explicit parameter is null
		if(otherObject == null) {
			return false;
		}
		
		//if the classes don't match, they can't be equal
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		
		//now we know otherObject is a non-null Link
		Link other = (Link) otherObject;
		return url.equals(other.url);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() { 		
		return url.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + "[" + urlName + "  " + url + "]";
	}
}