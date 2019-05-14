package com.rajat.applocation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rajat.applocation.data.History;
import com.rajat.applocation.db.DB;
import com.rajat.applocation.service.LocationService;
import com.rajat.applocation.service.WebServiceCalls;
import com.rajat.applocation.util.AppUtil;

import java.util.List;

public class SaveActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap googleMap;
    Button refresh, saveBtn;
    private final static int GPS_ENABLE_REQUEST = 0x548;
    private final static String PARK_LAT = "parkLat";
    private final static String PARK_LONG = "parkLong";
    private final static String MY_PREFS_NAME = "pref";
    private final static String IS_PARKED = "isParked";
    MarkerOptions markerOptions;
    Marker currMark;
    private String address = "";
    private static String addressSta = "";
    Double latitudePark, longitudePark, latitudeCurr, longitudeCurr;
    TextView mark;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setActionBar(toolbar);
//        }
//        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        context = SaveActivity.this;
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

        saveBtn = (Button) findViewById(R.id.btn_save);
        refresh = (Button) findViewById(R.id.btn_refresh);
        mark = (TextView) findViewById(R.id.mark);

        saveBtn.setOnClickListener(this);
        refresh.setOnClickListener(this);

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
            showSettingsAlert(SaveActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                LatLng location = null;
                if (googleMap != null)
                    location = googleMap.getCameraPosition().target;

                if (location != null) {
                    Log.d("SaveActivity", "before save location.latitude===" + location.latitude);
                    Log.d("SaveActivity", "before save location.longitude===" + location.longitude);
                    alertDialogToSaveLocation(location);
                } else
                    Toast.makeText(getApplicationContext(), "Can't Save Location", Toast.LENGTH_LONG).show();

//                setMapView();

                break;
            case R.id.btn_refresh:
                if (LocationService.canGetLocation()) {
                    System.out.println("test");
                    Location loc = LocationService.getmCurrentLocation();
                    if (loc != null) {
                        //Set Current Location From GPS
                        longitudeCurr = loc.getLongitude();
                        latitudeCurr = loc.getLatitude();
                        setMapView();
                    } else {
                        Toast.makeText(getApplication(), "Can't get Location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showSettingsAlert(SaveActivity.this);
                }

                break;
//		case R.id.btn_found:
//
//
//			break;

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


        //Current Location Marker
        LatLng currentLatLng = new LatLng(latitudeCurr, longitudeCurr);
        markerOptions.position(currentLatLng);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_blue));
        currMark = googleMap.addMarker(markerOptions);


//	             LatLng ll= googleMap.getCameraPosition().target;//TODO get center of map
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentLatLng, 20);
        googleMap.animateCamera(yourLocation);
//	            if(updateCurrentMarker.isCancelled())
//	            	updateCurrentMarker.execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item=====" + item.getItemId());
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.sub_list:
                System.out.println("item sub_list");
                intent=new Intent(SaveActivity.this,HistoryListActivity.class);
                startActivity(intent);
                break;
            case R.id.sub_map:
                System.out.println("item sub_map");
                intent=new Intent(SaveActivity.this,HistoryMapActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_sync:
                System.out.println("item menu_sync");
                new SendDataAsync().execute("");
                break;
            default:
                break;
        }

        return true;
    }

    public class SendDataAsync extends AsyncTask<String, String, String> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(SaveActivity.this);
            dialog.setMessage("Please Wait...");
            dialog.setTitle("Processing");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            String url="http://192.168.1.19:8080/AppLocationServer/SaveHistory";
            List<History> histories= DB.getInstance(getApplicationContext()).getHistoryToUpload();
            System.out.println("histories size"+histories.size());
            String json=new Gson().toJson(histories);

            String response = WebServiceCalls.getInstance(getApplicationContext()).sendHttpPosttRequest(url, json);
            if("Success".equalsIgnoreCase(response)){
                for (History history : histories) {
                    DB.getInstance(getApplicationContext()).updateUploadStatus(history.getId(), ""+1);
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
//				et.setText(""+result);
            Toast.makeText(SaveActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private void alertDialogToSaveLocation(final LatLng location){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_save_location, null);
        final EditText name = (EditText) view.findViewById(R.id.location_name);
//        builder.setTitle("Save Location");
        builder.setView(view);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DB.getInstance(SaveActivity.this).insertData(location.latitude, location.longitude, name.getText().toString());
                Toast.makeText(getApplicationContext(), "Location Saved", Toast.LENGTH_LONG).show();
                setMapView();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMapView();
            }
        });
        builder.show();
    }
}
