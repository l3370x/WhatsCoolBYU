package com.bluemoonscience.whatscoolbyu;

import java.util.Locale;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to
	 * switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	// Network globals
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";

	// Whether there is a Wi-Fi connection.
	static boolean wifiConnected = false;
	// Whether there is a mobile connection.
	static boolean mobileConnected = false;
	// Whether the display should be refreshed.
	public static boolean refreshDisplay = true;

	// The user's current network preference setting.
	public static String sPref = null;

	// The BroadcastReceiver that tracks network connectivity changes.
	private NetworkReceiver receiver = new NetworkReceiver();

	// END Network globals

	DummySectionFragment dummyFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("main", "onCreate");
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		// if (savedInstanceState == null)
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Retrieves a string value for the preferences. The second parameter
		// is the default value to use if a preference value is not found.
		sPref = sharedPrefs.getString("listPref", "Wi-Fi");

		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		this.registerReceiver(receiver, filter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("main", "onSaveInstanceState");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			this.unregisterReceiver(receiver);
		}
	}

	// Populates the activity's options menu.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("main", "onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	// Handles the user's menu selection.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(settingsActivity);
			return true;
		case R.id.refresh:
			if (dummyFrag != null) {
				dummyFrag.loadPage();
			} else {
				Log.d("main", "button pushed & dummyFrag == null");
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
	 * sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Log.d("main", "getItem");
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if (position == 2) {
				Fragment fragment = new NewDateSectionFragment();
				return fragment;

			}
			if (position == 1) {
				Fragment fragment = new MapSectionFragment();
				return fragment;
			}
			Fragment fragment = new DummySectionFragment();
			dummyFrag = (DummySectionFragment) fragment;
			return fragment;

		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * 
	 * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
	 * which indicates a connection change. It checks whether the type is TYPE_WIFI. If it is, it
	 * checks whether Wi-Fi is connected and sets the wifiConnected flag in the main activity
	 * accordingly.
	 * 
	 */
	public class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			// Checks the user prefs and the network connection. Based on the result, decides
			// whether
			// to refresh the display or keep the current display.
			// If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
			if (sPref != null) {
				Log.d("networkReceiver", "sPref = " + sPref.toString());
			} else {
				Log.d("networkReceiver", "sPref is null");
			}
			if (networkInfo != null) {
				Log.d("networkReceiver", "networkInfo is not null");
				Log.d("networkReceiver", "networkInfo.getType = " + networkInfo.getTypeName());
			} else {
				Log.d("networkReceiver", "networkInfo is null");
			}
			if (WIFI.equals(sPref) && networkInfo != null
					&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// If device has its Wi-Fi connection, sets refreshDisplay
				// to true. This causes the display to be refreshed when the user
				// returns to the app.
				refreshDisplay = true;
				Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

				// If the setting is ANY network and there is a network connection
				// (which by process of elimination would be mobile), sets refreshDisplay to true.
			} else if (ANY.equals(sPref) && networkInfo != null) {
				refreshDisplay = true;

				// Otherwise, the app can't download content--either because there is no network
				// connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
				// is no Wi-Fi connection.
				// Sets refreshDisplay to false.
			} else {
				refreshDisplay = false;
				Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
			}
			Log.d("NetworkReceiver", "done and refreshDisplay = " + refreshDisplay);
		}
	}

	public void updateInternetFlags() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected()) {
			wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
			mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
		} else {
			wifiConnected = false;
			mobileConnected = false;
		}

	}
}
