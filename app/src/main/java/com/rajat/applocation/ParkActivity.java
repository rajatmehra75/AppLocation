package com.rajat.applocation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rajat.applocation.service.LocationService;
import com.rajat.applocation.util.AppUtil;

public class ParkActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap googleMap;
    Button refresh,park,found;
    private final static int GPS_ENABLE_REQUEST = 0x548;
    private final static String PARK_LAT = "parkLat";
    private final static String PARK_LONG = "parkLong";
    private final static String MY_PREFS_NAME = "pref";
    private final static String IS_PARKED = "isParked";
    MarkerOptions markerOptions;
    Marker currMark;
    private String address="";
    private static String addressSta="";
    Double latitudePark,longitudePark,latitudeCurr,longitudeCurr;
    TextView dist;
    boolean isParked=false;
    boolean isRunning=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        AppUtil.setMapUiSettings(this.googleMap);

        park=(Button) findViewById(R.id.btn_park);
        refresh=(Button) findViewById(R.id.btn_refresh);
        found=(Button) findViewById(R.id.btn_found);
        dist=(TextView) findViewById(R.id.distance);

        park.setOnClickListener(this);
        refresh.setOnClickListener(this);
        found.setOnClickListener(this);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//		String restoredText = prefs.getString("text", null);
        double latSP = prefs.getFloat(PARK_LAT, 0.0f);
        double longSP = prefs.getFloat(PARK_LONG, 0.0f);
        isParked= prefs.getBoolean(IS_PARKED, false);

        if (LocationService.canGetLocation()) {
            System.out.println("test");
            Location loc = LocationService.getmCurrentLocation();
            if (loc != null) {
                //Set Current Location From GPS
                longitudeCurr=loc.getLongitude();
                latitudeCurr=loc.getLatitude();
                setMapView();
            }
        } else {
            showSettingsAlert(ParkActivity.this);
        }

        if(isParked){
            park.setVisibility(View.GONE);
        }else{
            found.setVisibility(View.GONE);
            dist.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_park:
                if (LocationService.canGetLocation()) {
                    System.out.println("test");
                    Location loc = LocationService.getmCurrentLocation();
                    if (loc != null) {
                        //Set Current Location From GPS
                        longitudeCurr=loc.getLongitude();
                        latitudeCurr=loc.getLatitude();
//					new GeocoderTask().execute(address);
                    }
                }
                if(latitudeCurr!=null && longitudeCurr!=null){
                    latitudePark=latitudeCurr;
                    longitudePark=longitudeCurr;
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putFloat(PARK_LAT, latitudeCurr.floatValue());
                    editor.putFloat(PARK_LONG, longitudeCurr.floatValue());
                    editor.putBoolean(IS_PARKED, true);
                    isParked=true;
                    editor.commit();
                    park.setVisibility(View.GONE);
                    found.setVisibility(View.VISIBLE);
                    setMapView();
                }else{
                    Toast.makeText(getApplicationContext(), "Cant get Location.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_refresh:
                if (LocationService.canGetLocation()) {
                    System.out.println("test");
                    Location loc = LocationService.getmCurrentLocation();
                    if (loc != null) {
                        //Set Current Location From GPS
                        longitudeCurr=loc.getLongitude();
                        latitudeCurr=loc.getLatitude();
                        setMapView();
                    }else{
                        Toast.makeText(getApplication(), "Can't get Location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showSettingsAlert(ParkActivity.this);
                }

                break;
            case R.id.btn_found:
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putFloat(PARK_LAT, 0.0f);
                editor.putFloat(PARK_LONG, 0.0f);
                editor.putBoolean(IS_PARKED, false);
                isParked=false;
                editor.commit();
                found.setVisibility(View.GONE);
                park.setVisibility(View.VISIBLE);
                refresh.callOnClick();

                break;

            default:
                break;
        }
    }

    public void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ENABLE_REQUEST);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void setMapView() {
        // Clears all the existing markers on the map
        googleMap.clear();

//	                latLng=new LatLng(22.719934, 75.871024);
        markerOptions = new MarkerOptions();


        LatLng latLngPark= new LatLng(0.0, 0.0);
        if(isParked){
            try{
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//	            		String restoredText = prefs.getString("text", null);
                latitudePark = (double) prefs.getFloat(PARK_LAT, 0.0f);
                longitudePark = (double) prefs.getFloat(PARK_LONG, 0.0f);
                //TODO destination location bhawarkua
//	                Double lat=22.692647;
//	                Double lon=75.867687;
                latLngPark = new LatLng(latitudePark, longitudePark);
                Log.d("Map", "destination Data==> lat=" + latitudePark + "  lon=" + longitudePark);
                markerOptions.position(latLngPark);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_park_car));
                googleMap.addMarker(markerOptions);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        //Current Location Marker
        final LatLng currentLatLng = new LatLng(latitudeCurr, longitudeCurr);
        markerOptions.position(currentLatLng);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue));
        currMark=googleMap.addMarker(markerOptions);

        if(isParked){
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            options.add(currentLatLng);
            options.add(latLngPark);
            googleMap.addPolyline(options);
        }

//	             LatLng ll= googleMap.getCameraPosition().target;//TODO get center of map
//        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentLatLng, 20);
//        googleMap.animateCamera(yourLocation);


//	            drawPath(googleMap, latLngPark);
        if(isParked){
            Location selected_location=new Location("locationA");
            selected_location.setLatitude(latitudeCurr);
            selected_location.setLongitude(longitudeCurr);
            Location near_locations=new Location("locationB");
            near_locations.setLatitude(latitudePark);
            near_locations.setLongitude(longitudePark);
            double distance=selected_location.distanceTo(near_locations);

            dist.setText("Distance =" + String.format("%.2f", distance) + " m");
            dist.setVisibility(View.VISIBLE);
        }else{
            dist.setVisibility(View.GONE);
        }

        final LatLng finalLatLngPark = latLngPark;
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (isParked) {
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    builder.include(currentLatLng);
                    builder.include(finalLatLngPark);
                    LatLngBounds latLngBounds = builder.build();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
                } else {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentLatLng, 20);
                    googleMap.animateCamera(yourLocation);
                }
            }
        });

//	            if(updateCurrentMarker.isCancelled())
//	            	updateCurrentMarker.execute("");
    }
}
