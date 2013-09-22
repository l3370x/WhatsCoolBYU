package com.bluemoonscience.whatscoolbyu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * The Map fragment.
 */
public class MapSectionFragment extends Fragment {

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