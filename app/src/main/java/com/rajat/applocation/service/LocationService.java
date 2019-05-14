package com.rajat.applocation.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener {
	//	private static final long MIN_TIME_INTERVAL_FOR_GPS_LOCATION = 1000;
//	private static final float MIN_DISTANCE_INTERVAL_FOR_GPS_LOCATION = 2.0f;
	private static final long MIN_TIME_INTERVAL_FOR_GPS_LOCATION = 0;
	private static final float MIN_DISTANCE_INTERVAL_FOR_GPS_LOCATION = 0;
	public static final String LOCATION_PERMISSION_NOT_GRANTED = "Location Permission not granted...";

	private static String TAG = LocationService.class.getSimpleName();
	private static LocationManager locationManager;
	private static Location mCurrentLocation;
	private Location gpsLocation;
	private Location networkLocation;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate...");
		System.out.println("onCreate of Location");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			Toast.makeText(getApplicationContext(), LOCATION_PERMISSION_NOT_GRANTED, Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL_FOR_GPS_LOCATION, MIN_DISTANCE_INTERVAL_FOR_GPS_LOCATION, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_FOR_GPS_LOCATION, MIN_DISTANCE_INTERVAL_FOR_GPS_LOCATION, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mCurrentLocation = getBestLocation();
		Log.i(TAG, "mCurrentLocation : " + mCurrentLocation);
	}

	private Location getBestLocation() {
		Log.i(TAG, "getBestLocation...");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			Toast.makeText(getApplicationContext(), LOCATION_PERMISSION_NOT_GRANTED, Toast.LENGTH_SHORT).show();
			return null;
		}
		Location location_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location location_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// If both are available, get the most recent
		if (location_gps != null && location_network != null) {
			return (location_gps.getTime() > location_network.getTime()) ? location_gps : location_network;
		} else if (location_gps == null && location_network == null) {
			return null;
		} else {
			return (location_gps == null) ? location_network : location_gps;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "onLocationChanged...provider : " + location.getProvider());
		if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
			gpsLocation = location;
		} else if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
			networkLocation = location;
		}
		if (gpsLocation != null && networkLocation != null) {
			if (Math.abs(gpsLocation.getTime() - networkLocation.getTime()) > MIN_TIME_INTERVAL_FOR_GPS_LOCATION) {
				if (gpsLocation.getTime() > networkLocation.getTime()) {
					mCurrentLocation = gpsLocation;
				} else {
					mCurrentLocation = networkLocation;
				}
			} else {
				if (gpsLocation.getAccuracy() < networkLocation.getAccuracy()) {
					mCurrentLocation = gpsLocation;
				} else {
					mCurrentLocation = networkLocation;
				}
			}
		} else if (gpsLocation != null) {
			mCurrentLocation = gpsLocation;
		} else if (networkLocation != null) {
			mCurrentLocation = networkLocation;
		}
//		Toast.makeText(getApplicationContext(), "mCurrentLocation=="+mCurrentLocation.getProvider(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		System.out.println("onProviderDisabled, provider : " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		System.out.println("onProviderEnabled, provider : " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		System.out.println("onStatusChanged, provider : " + provider);
	}

	public static Location getmCurrentLocation() {
		return mCurrentLocation;
	}

	public static void setmCurrentLocation(Location mCurrentLocation) {
		LocationService.mCurrentLocation = mCurrentLocation;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy...");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.removeUpdates(this);
	}
	
	 public static boolean canGetLocation() {
			if(locationManager!=null){
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			}else{
				return false;
			}
		}

}