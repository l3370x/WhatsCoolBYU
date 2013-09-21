package com.bluemoonscience.whatscoolbyu;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

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
	private static final String URL = "http://aaronapps.bluemoonscience.com/get.php";

	// Whether there is a Wi-Fi connection.
	private static boolean wifiConnected = false;
	// Whether there is a mobile connection.
	private static boolean mobileConnected = false;
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
				Fragment fragment = new ListSectionFragment();
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
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private ListView dummyListView;
		private TextView dummyEmptyTextView;
		private ProgressBar dummyEmptyProgressBar;
		ArrayList<Entry> image_details = new ArrayList<Entry>();
		ItemListBaseAdapter mAdapter;

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.d("dummy", "onCreateView & refreshDisplay = " + refreshDisplay);

			setRetainInstance(true);
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			dummyListView = (ListView) rootView.findViewById(R.id.dummyList);
			dummyEmptyTextView = (TextView) rootView.findViewById(R.id.dummyEmptyTextView);
			dummyEmptyProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
			dummyListView.setEmptyView(rootView.findViewById(R.id.relLayoutEmpty));
			if (mAdapter == null)
				mAdapter = new ItemListBaseAdapter(getActivity().getApplicationContext(),
						image_details);
			dummyListView.setAdapter(mAdapter);

			Log.d("dummy", "onCreateView & dummyAdapter = "
					+ dummyListView.getAdapter().toString());
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			((MainActivity) getActivity()).dummyFrag = this;
		}

		// Refreshes the display if the network connection and the
		// pref settings allow it.
		@Override
		public void onStart() {
			super.onStart();
			Log.d("dummy", "onStart & refreshDisplay = " + refreshDisplay);
			// Gets the user's network preference settings
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity().getApplicationContext());

			// Retrieves a string value for the preferences. The second parameter
			// is the default value to use if a preference value is not found.
			sPref = sharedPrefs.getString("listPref", "Wi-Fi");

			updateConnectedFlags();

			// Only loads the page if refreshDisplay is true. Otherwise, keeps previous
			// display. For example, if the user has set "Wi-Fi only" in prefs and the
			// device loses its Wi-Fi connection midway through the user using the app,
			// you don't want to refresh the display--this would force the display of
			// an error page instead of stackoverflow.com content.
			if (refreshDisplay || dummyEmptyTextView.getText().equals("Empty List")) {
				loadPage();
			}
		}

		// Checks the network connection and sets the wifiConnected and
		// mobileConnected
		// variables accordingly.
		private void updateConnectedFlags() {
			((MainActivity) getActivity()).updateInternetFlags();
		}

		// Uses AsyncTask subclass to download the XML feed from
		// stackoverflow.com.
		// This avoids UI lock up. To prevent network operations from
		// causing a delay that results in a poor user experience, always
		// perform
		// network operations on a separate thread from the UI.
		public void loadPage() {
			Log.d("dummy", "loadPage & refreshDisplay = " + refreshDisplay);
			if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
					|| ((sPref.equals(WIFI)) && (wifiConnected))) {
				// AsyncTask subclass
				Log.d("loadPage", "just before executing(URL)");
				new DownloadXmlTask().execute(URL);
				dummyEmptyTextView.setText("Success!");
			} else {
				showErrorPage();
			}
		}

		// Displays an error if the app is unable to load content.
		private void showErrorPage() {
			dummyEmptyTextView.setText(getResources().getString(R.string.connection_error));
			dummyEmptyProgressBar.setAlpha(0);
		}

		// Implementation of AsyncTask used to download XML feed from
		// stackoverflow.com.
		private class DownloadXmlTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... urls) {
				try {
					Log.d("downloadXMLTask", "starting loadXmlFromNetwork");
					return loadXmlFromNetwork(urls[0]);
				} catch (IOException e) {
					return getResources().getString(R.string.connection_error);
				} catch (XmlPullParserException e) {
					return getResources().getString(R.string.xml_error);
				}
			}

			@Override
			protected void onPostExecute(String result) {
				// dummyEmptyTextView.setText(result);
				// dummyListView.getAdapter().notify();
				mAdapter.notifyDataSetChanged();
			}
		}

		// Uploads XML from stackoverflow.com, parses it, and combines it with
		// HTML markup. Returns HTML string.
		private String loadXmlFromNetwork(String urlString) throws XmlPullParserException,
				IOException {
			InputStream stream = null;
			StackOverflowXmlParser stackOverflowXmlParser = new StackOverflowXmlParser();
			List<Entry> entries = null;

			try {
				Log.d("laodXML", "before downloadURL");
				stream = downloadUrl(urlString);
				Log.d("laodXML", "before parse");
				entries = stackOverflowXmlParser.parse(stream);
				Log.d("laodXML", "after parse");
				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
			Log.d("loadXML", "number of entries = " + Integer.toString(entries.size()));

			// StackOverflowXmlParser returns a List (called "entries") of Entry
			// objects.
			// Each Entry object represents a single post in the XML feed.
			// This section processes the entries list to combine each entry
			// with HTML markup.
			// Each entry is displayed in the UI as a link that optionally
			// includes
			// a text summary.
			for (Entry entry : entries) {

				image_details.add(entry);

				Log.d("Entry Added", entry.title);

				// htmlString.append(entry.timestamp + "\n");
				// htmlString.append(entry.title + "\n");
				// // If the user set the preference to include summary text,
				// // adds it to the display.
				// if (pref) {
				// htmlString.append(entry.pictureURL);
				// }
				// htmlString.append("\n\n");
			}
			return "done";
		}

		// Given a string representation of a URL, sets up a connection and gets
		// an input stream.
		private InputStream downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			InputStream stream = conn.getInputStream();
			return stream;
		}

	}

	/**
	 * The Map fragment.
	 */
	public static class MapSectionFragment extends Fragment {

		static final LatLng BYULatLng = new LatLng(40.254994, -111.659164);
		private GoogleMap map;

		public MapSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setRetainInstance(true);
			View rootView = inflater.inflate(R.layout.fragment_main_map, container, false);
			map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Marker byuMarker = map.addMarker(new
			// MarkerOptions().position(BYULatLng).title("BYU"));

			// Enable my location
			map.setMyLocationEnabled(true);

			// Move the camera instantly to BYU with a zoom of 12.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(BYULatLng, 12));

			return rootView;
		}
	}

	/**
	 * The List Fragment
	 */
	public static class ListSectionFragment extends Fragment {

		public ListSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setRetainInstance(true);
			View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
			ListView listview = (ListView) rootView.findViewById(R.id.listView1);
			String[] values = new String[] { "Android", "iPhone", "WindowsMobile", "Blackberry",
					"WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2", "1", "2", "3", "4" };

			ArrayAdapter<String> files = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, values);

			listview.setAdapter(files);
			return rootView;
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
