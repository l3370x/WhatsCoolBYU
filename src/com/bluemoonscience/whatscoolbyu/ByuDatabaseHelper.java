package com.bluemoonscience.whatscoolbyu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ByuDatabaseHelper extends SQLiteOpenHelper {

	  private static final String DATABASE_NAME = "byutable.db";
	  private static final int DATABASE_VERSION = 9;

	  public ByuDatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  // Method is called during creation of the database
	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    ByuTable.onCreate(database);
	  }

	  // Method is called during an upgrade of the database,
	  // e.g. if you increase the database version
	  @Override
	  public void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
	    ByuTable.onUpgrade(database, oldVersion, newVersion);
	  }
	}
	 
