package com1032.cw2.ls00735.ls00735_assignment2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


/**
 * Created by savou on 12/05/2017.
 * The distance calculation class accesses the location
 */
public class DistanceCalculation extends AsyncTask<String, Void, Void> implements LocationListener {

    private Location location;
    private String provider;
    public LocationManager locationManager;
    private MyService context = null;
    private ProgressDialog progDialog = null;

    public DistanceCalculation(MyService context) {
        super();
        this.context = context;


    }

    @Override
    protected Void doInBackground(String... params) {


        while(true){}


    }
    /*
    get permissions
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        progDialog.dismiss();
        //get permission to access the location
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    /*
        access device location method
         */
    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            provider = locationManager.getBestProvider(criteria, false); //get the provider of the best location
            location = locationManager.getLastKnownLocation(provider); //get the last known location
        }
        catch(SecurityException se){
            se.printStackTrace();

        }

        try {
            locationManager.requestLocationUpdates(provider, 400, 1, this); //request updates of the location

        }
        catch (SecurityException se){
            se.printStackTrace();

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            context.getDistance(location); //if location isn't null call the get distance method
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
