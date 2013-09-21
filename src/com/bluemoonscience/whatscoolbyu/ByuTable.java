package com.bluemoonscience.whatscoolbyu;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ByuTable {

	/*
	 * public int id; public String title; public float lat; public float lng; public double
	 * avgRating; public String timestamp; public String sDescription; public String pictureURL;
	 * public String lastUpdateShort;
	 */

	// Database table
	public static final String TABLE_BYU = "byu";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	public static final String COLUMN_AVGRATING = "avgRating";
	public static final String COLUMN_TIMESTAMP = "timestmp";
	public static final String COLUMN_SDESCRIPTION = "sDescription";
	public static final String COLUMN_PICTUREURL = "pictureURL";
	public static final String COLUMN_LASTUPDATESHORT = "lastUpdateShort";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE "+TABLE_BYU+" ("
			+ " "+COLUMN_ID+" integer primary key autoincrement,"
			+ " "+COLUMN_TITLE+" text NOT NULL DEFAULT 'Title',"
			+ " "+COLUMN_LAT+" text DEFAULT NULL,"
			+ " "+COLUMN_LNG+" text DEFAULT NULL,"
			+ " "+COLUMN_AVGRATING+" text NOT NULL DEFAULT '2.5',"
			+ " "+COLUMN_TIMESTAMP+" text NOT NULL DEFAULT CURRENT_TIMESTAMP,"
			+ " "+COLUMN_SDESCRIPTION+" text NOT NULL DEFAULT 'Short Description',"
			+ " "+COLUMN_PICTUREURL+" text NOT NULL DEFAULT 'http://aaronapps.bluemoonscience.com/photos/noimage.jpg',"
			+ " "+COLUMN_LASTUPDATESHORT+" text NOT NULL DEFAULT 'June 2013'"
			+ " );";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);

		String initialAdd = "INSERT INTO `byu` (`id`, `title`, `lat`, `lng`, `avgRating`, `timestamp`, `sDescription`, `picurl`, `lastUpdateShort`) VALUES (1, 'BYU Creamery', NULL, NULL, 2.5, '2013-09-21 04:23:24', 'Delicious ice cream', 'http://byu.bluemoonscience.com/photos/noimage.jpg', 'Sep 2013');"
				+ " INSERT INTO `byu` (`id`, `title`, `lat`, `lng`, `avgRating`, `timestamp`, `sDescription`, `picurl`, `lastUpdateShort`) VALUES (2, 'Hike Y Mountain', NULL, NULL, 2.5, '2013-09-21 04:24:34', 'Enjoy a steep hike with a great view of the Utah valley', 'http://byu.bluemoonscience.com/photos/noimage.jpg', 'Sep 2013');"
				+ " INSERT INTO `byu` (`id`, `title`, `lat`, `lng`, `avgRating`, `timestamp`, `sDescription`, `picurl`, `lastUpdateShort`) VALUES (3, 'MOA croquet', NULL, NULL, 2.5, '2013-09-21 04:26:02', 'Play croquet on the grass outside the MOA', 'http://byu.bluemoonscience.com/photos/noimage.jpg', 'Sep 2013');";
		//database.execSQL(initialAdd);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ByuTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_BYU);
		onCreate(database);
	}
}
