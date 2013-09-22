package com.bluemoonscience.whatscoolbyu;

public class Entry {

	public int id;
	public String title;
	public float lat;
	public float lng;
	public double avgRating;
	public String timestamp;
	public String sDescription;
	public String pictureURL;
	public String lastUpdateShort;

	public Entry() {

	}

	public Entry(int id, String title, float lat, float lng, double avgRating, String timestamp,
			String sDescription, String pictureURL, String lastUpdateShort) {
		this.id = id;
		this.title = title;
		this.lat = lat;
		this.lng = lng;
		this.avgRating = avgRating;
		this.timestamp = timestamp;
		this.sDescription = sDescription;
		this.pictureURL = pictureURL;
		this.lastUpdateShort = lastUpdateShort;
	}
	
	@Override
	public String toString() {
		return "Entry [id=" + id + ", title=" + title + ", lat=" + lat + ", lng=" + lng
				+ ", avgRating=" + avgRating + ", timestamp=" + timestamp + ", sDescription="
				+ sDescription + ", pictureURL=" + pictureURL + ", lastUpdateShort="
				+ lastUpdateShort + "]";
	}
}