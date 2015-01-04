package com.madgeek.devonaward.weather;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by devonaward on 1/4/15.
 */
public class LocateUser extends Service implements LocationListener {

    private final Context context;
    //Check GPS status
    boolean GPSEnabled = false;
    //Check Network status
    boolean NetworkEnabled = false;
    boolean GetLocationPossible = false;
    Location location;
    double latitude;
    double longitude;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    //Create a Location Manager
    protected LocationManager locationManager;

    public LocateUser(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation(){

        try{
            locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
            //Getting GPS status
            GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //Getting Network status
            NetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!GPSEnabled && !NetworkEnabled){
                Log.i("NO NETWORK", "NETWORK PROVIDER NOT ENABLED!");
            } else{
                this.GetLocationPossible = true;
                //Get location from Network provider
                if(NetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    Log.i("NETWORK", "NETWORK!");
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                //GPS enabled...get latitude and longitude using the GPS Services
                if(GPSEnabled){
                    if(location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                        Log.i("GPS", "GPS ENABLED!");
                        if(locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }
    //Get latitude
    public  double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //Get longitude
    public  double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //Check for best network provider
    public  boolean GetLocation(){
        return  this.GetLocationPossible;
    }
    //Display alert for settings
    public void SettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setTitle("GPS");
        // Setting Dialog Message
        alertDialog.setMessage("Location services is not enabled. Enable location services?");
        //Go to settings
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        //Cancel
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Display alert
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
