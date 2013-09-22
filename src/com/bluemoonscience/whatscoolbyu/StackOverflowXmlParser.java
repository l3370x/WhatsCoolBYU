/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.bluemoonscience.whatscoolbyu;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses XML feeds from stackoverflow.com. Given an InputStream representation of a
 * feed, it returns a List of entries, where each list element represents a single entry (post) in
 * the XML feed.
 */
public class StackOverflowXmlParser {
	private static final String ns = null;

	// We don't use namespaces

	public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Entry> entries = new ArrayList<Entry>();

		parser.require(XmlPullParser.START_TAG, ns, "result");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("row")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
	// off
	// to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
	private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "row");
		int id = 0;
		String title = null;
		float lat = 0;
		float lng = 0;
		double avgRating = 0;
		String timestamp = null;
		String sDescription = null;
		String pictureURL = null;
		String lastUpdateShort = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("timestamp")) {
				timestamp = readTimestamp(parser);
			} else if (name.equals("title")) {
				title = readTitle(parser);
			} else if (name.equals("picurl")) {
				pictureURL = readPictureURL(parser);
			} else if (name.equals("id")) {
				id = readID(parser);
			} else if (name.equals("lat")) {
				lat = readLat(parser);
			} else if (name.equals("lng")) {
				lng = readlng(parser);
			} else if (name.equals("avgRating")) {
				avgRating = readAvgRating(parser);
			} else if (name.equals("sDescription")) {
				sDescription = readSDescription(parser);
			} else if (name.equals("lastUpdateShort")) {
				lastUpdateShort = readLastUpdateShort(parser);
			} else {
				skip(parser);
			}
		}
		return new Entry(id, title, lat, lng, avgRating, timestamp, sDescription, pictureURL,
				lastUpdateShort,0);
	}

	private String readLastUpdateShort(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, ns, "lastUpdateShort");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "lastUpdateShort");
		return title;
	}

	private String readSDescription(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, ns, "sDescription");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "sDescription");
		return title;
	}

	private double readAvgRating(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "avgRating");
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "avgRating");
		return Double.parseDouble(text);
	}

	private float readlng(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "lng");
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "lng");
		if (text.equals("")) {
			return 0;
		}
		return Float.parseFloat(text);
	}

	private float readLat(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "lat");
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "lat");
		if (text.equals("")) {
			return 0;
		}
		return Float.parseFloat(text);
	}

	private int readID(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "id");
		String text = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "id");
		return Integer.parseInt(text);
	}

	// Processes title tags in the feed.
	private String readTimestamp(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "timestamp");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "timestamp");
		return title;
	}

	// Processes link tags in the feed.
	private String readPictureURL(XmlPullParser parser) throws IOException, XmlPullParserException {
		String link = "";
		parser.require(XmlPullParser.START_TAG, ns, "picurl");
		link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "picurl");
		return link;
	}

	// Processes summary tags in the feed.
	private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String summary = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return summary;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
	// if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
	// finds the matching END_TAG (as indicated by the value of "depth" being 0).
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
