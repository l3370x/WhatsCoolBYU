package com.bluemoonscience.whatscoolbyu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The List Fragment
 */
public class NewDateSectionFragment extends Fragment {

	private EditText etTitle;
	private EditText etSDesc;

	public NewDateSectionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		setRetainInstance(true);
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_main_newdate, container, false);
		etTitle = (EditText) rootView.findViewById(R.id.newd_etTitle);
		etSDesc = (EditText) rootView.findViewById(R.id.newd_etSDesc);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.newd_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.newd_save:
			Log.d("newd", "save");
			doSave();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void doSave() {
		Log.d("newd", "doSave");
		String title = etTitle.getText().toString();
		String sDescription = etSDesc.getText().toString();

		// Only save if either summary or description
		// is available

		if (sDescription.length() == 0) {
			makeToast("title");
			return;
		}
		if (title.length() == 0) {
			makeToast("short description");
			return;
		}

		ContentValues values = new ContentValues();
		values.put(ByuTable.COLUMN_TITLE, title);
		values.put(ByuTable.COLUMN_SDESCRIPTION, sDescription);
		values.put(ByuTable.COLUMN_TIMESTAMP, "2000-06-26 03:03:07");

		// New byu
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());
		int nextUniqueID = sharedPrefs.getInt("uniqueNextID", 1337);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt("uniqueNextID", nextUniqueID + 1);
		editor.commit();
		values.put(ByuTable.COLUMN_UNIQUENEXT,String.valueOf(nextUniqueID));
		getActivity().getContentResolver().insert(MyByuContentProvider.CONTENT_URI, values);
		Log.d("newd", "afterSave");

		// attempt to upload all non uploaded entries to cloud in background
		upload(values, nextUniqueID);

		// perform clean up operations
		afterSave();

	}

	private void upload(ContentValues values, int nextUniqueID) {
		Log.d("newd", "upload");
		Entry e = new Entry(0, values.getAsString(ByuTable.COLUMN_TITLE), 0, 0, 2.5, "5",
				values.getAsString(ByuTable.COLUMN_SDESCRIPTION), "abcd", "Sep 13", 0);
		String theURL = buildNewdURL(e);
		new UploadNewDate().execute(theURL, String.valueOf(nextUniqueID));
		Log.d("url", theURL);
	}

//	private void upload() {
//		Cursor c = getActivity().getContentResolver().query(MyByuContentProvider.CONTENT_URI,
//				null, ByuTable.COLUMN_CLOUDID + " = " + 0, null, null);
//		while (c.moveToNext()) {
//			Entry e = new Entry(c.getInt(c.getColumnIndex(ByuTable.COLUMN_ID)), c.getString(c
//					.getColumnIndex(ByuTable.COLUMN_TITLE)), c.getFloat(c
//					.getColumnIndex(ByuTable.COLUMN_LAT)), c.getFloat(c
//					.getColumnIndex(ByuTable.COLUMN_LNG)), c.getDouble(c
//					.getColumnIndex(ByuTable.COLUMN_AVGRATING)), c.getString(c
//					.getColumnIndex(ByuTable.COLUMN_TIMESTAMP)), c.getString(c
//					.getColumnIndex(ByuTable.COLUMN_SDESCRIPTION)), c.getString(c
//					.getColumnIndex(ByuTable.COLUMN_PICTUREURL)), c.getString(c
//					.getColumnIndex(ByuTable.COLUMN_LASTUPDATESHORT)), 0);
//			String theURL = buildNewdURL(e);
//			Log.d("url", theURL);
//			new UploadNewDate().execute(theURL);
//
//		}
//		c.close();
//	}

	private String buildNewdURL(Entry e) {
		// $vals = "'".$_GET['title']."', "."'".$_GET['lat']."', "."'".$_GET['lng']."',
		// "."'".$_GET['avgRating']."', "."'".$_GET['sDescription']."',
		// "."'".$_GET['pictul']."', "."'".$_GET['lastUpdateShort']."'";
		String url = "http://aaronapps.bluemoonscience.com/submit.php";
		// URLEncoder.encode("apples oranges", "utf-8");
		try {
			url += "?title=" + URLEncoder.encode(e.title, "utf-8");
			url += "&lat=" + URLEncoder.encode(String.valueOf(e.lat), "utf-8");
			url += "&lng=" + URLEncoder.encode(String.valueOf(e.lng), "utf-8");
			url += "&avgRating=" + URLEncoder.encode(String.valueOf(e.avgRating), "utf-8");
			url += "&sDescription=" + URLEncoder.encode(e.sDescription, "utf-8");
			url += "&pictul=" + URLEncoder.encode(e.pictureURL, "utf-8");
			url += "&lastUpdateShort=" + URLEncoder.encode(e.lastUpdateShort, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		return url;
	}

	private void afterSave() {
		// hide keyboard
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);

		// clear text from edit texts
		etTitle.setText("");
		etSDesc.setText("");

		// return to main list
		((MainActivity) getActivity()).mViewPager.setCurrentItem(0, true);
	}

	private void makeToast(String what) {
		Toast.makeText(getActivity().getApplicationContext(), "Please enter a " + what + ".",
				Toast.LENGTH_LONG).show();
	}

	class UploadNewDate extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			Log.d("url", "do in background");
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				response = httpclient.execute(new HttpGet(uri[0]));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				Log.d("url", e.toString());
			} catch (IOException e) {
				Log.d("url", e.toString());
			}
			Log.d("url", "will return " + responseString);
			if (responseString.startsWith("n=")) {
				Log.d("url",
						"will return " + responseString.substring(2, responseString.length() - 2)
								+ "," + uri[1]);
				return responseString.substring(2, responseString.length() - 2) + "," + uri[1];
			} else {
				Log.d("url", "will return bad");
				return "bad";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("url", result);
			int commaLoc = result.indexOf(",");
			int locid = Integer.parseInt(result.substring(commaLoc + 1, result.length()));
			int cloudid = Integer.parseInt(result.substring(0, commaLoc));
			Log.d("url", "cloudID = " + cloudid + ", locid = " + locid);
			String[] projection = { ByuTable.COLUMN_ID, ByuTable.COLUMN_CLOUDID, ByuTable.COLUMN_UNIQUENEXT };
			Cursor c = getActivity().getContentResolver().query(MyByuContentProvider.CONTENT_URI,
					projection, ByuTable.COLUMN_UNIQUENEXT + " = " + locid, null, null);
			ContentValues values = new ContentValues();
			values.put(ByuTable.COLUMN_CLOUDID, cloudid);
			if (c.getCount() > 0) {
				Log.d("query", "the query returned a result");
				c.moveToFirst();
				getActivity().getContentResolver().update(MyByuContentProvider.CONTENT_URI,
						values, ByuTable.COLUMN_UNIQUENEXT + " = " + locid, null);
			}
			c.close();
		}
	}

}