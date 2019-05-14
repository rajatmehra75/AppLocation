package com.rajat.applocation.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by ist-112 on 15/4/17.
 */
public class AppUtil {

    public static void showSettingsAlert(final Context context,final int GPS_ENABLE_REQUEST) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ((Activity)context).startActivityForResult(intent, GPS_ENABLE_REQUEST);
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

    public static void setMapUiSettings(GoogleMap googleMap){
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
    }
}
