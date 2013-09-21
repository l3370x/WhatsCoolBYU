package com.bluemoonscience.whatscoolbyu;

public class Entry {
    public final String title;
    public final String pictureURL;
    public final String timestamp;

    public Entry(String title, String timestamp, String pictureURL) {
        this.title = title;
        this.timestamp = timestamp;
        this.pictureURL = pictureURL;
    }

	public CharSequence getTitle() {
		return title;
	}
	public String getTimestamp() {
		return timestamp;
	}
}