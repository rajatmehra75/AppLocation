package com.rajat.applocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rajat.applocation.data.History;
import com.rajat.applocation.db.DB;
import com.rajat.applocation.service.LocationService;
import com.rajat.applocation.util.AppUtil;

import java.util.List;

public class HistoryMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    MarkerOptions markerOptions;
    private double longitudeCurr, latitudeCurr;
    private final static int GPS_ENABLE_REQUEST = 0x648;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_map);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"Permission Not Granted...",Toast.LENGTH_SHORT).show();
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        AppUtil.setMapUiSettings(this.googleMap);
//        this.googleMap.getUiSettings().setMapToolbarEnabled(true);
//        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
//        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
//        this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
//        this.googleMap.getUiSettings().setTiltGesturesEnabled(false);

        if (LocationService.canGetLocation()) {
            System.out.println("test");
            Location loc = LocationService.getmCurrentLocation();
            if (loc != null) {
                //Set Current Location From GPS
                longitudeCurr = loc.getLongitude();
                latitudeCurr = loc.getLatitude();
                setMapView();
            }
        } else {
            AppUtil.showSettingsAlert(HistoryMapActivity.this,GPS_ENABLE_REQUEST);
        }
    }

    private void setMapView() {
        // Clears all the existing markers on the map
        googleMap.clear();
        LatLng currentLatLng= new LatLng(latitudeCurr, longitudeCurr);
        final List<History> histories= DB.getInstance(getApplicationContext()).getHistory();
        Log.d("HistoryMap", "histories ====" + histories.size());
        for (History history : histories) {
            history.getName();
            Log.d("HistoryMap", "history ===="+history);

//                latLng=new LatLng(22.719934, 75.871024);
            markerOptions = new MarkerOptions();


            //Current Location Marker
            currentLatLng = new LatLng(history.getLatitude(), history.getLongitude());
            markerOptions.position(currentLatLng);
            markerOptions.title(history.getName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue));
            googleMap.addMarker(markerOptions);

        }

//             LatLng ll= googleMap.getCameraPosition().target;//TODO get center of map
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentLatLng, 20);
        googleMap.animateCamera(yourLocation);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = LatLngBounds.builder();
//                builder.include(finalLatLngPark);
                for (History history : histories) {
                    LatLng latLng = new LatLng(history.getLatitude(), history.getLongitude());
                    builder.include(latLng);
                }
                LatLngBounds latLngBounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
            }
        });
//            if(updateCurrentMarker.isCancelled())
//            	updateCurrentMarker.execute("");
    }
}
