package com1032.cw2.ls00735.ls00735_assignment2;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;


/**
 * Created by savou on 18/05/2017.
 *
 * MyService class extends a service and keeps the async task running in the background and stops it from closing
 */
public class MyService extends Service {

    public MyService(){

    }

    @Override
    public void onCreate() {
        DistanceCalculation distanceCalculation = new DistanceCalculation(this);
        distanceCalculation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); //Executes the async task
    }

    /*
    getDistance method creates an intent of the distance and broadcasts it
     */
    public void getDistance(Location location)
    {
        Intent intent = new Intent();
        intent.setAction("distance");
        intent.putExtra("lat", location.getLatitude());
        intent.putExtra("long", location.getLongitude());
        this.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendOutBroadcast(View view){
        Intent i = new Intent();
        i.setAction("com1032.cw2.ls00735.ls00735_assignment2");
        i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(i);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;//keep async task running in background, stop device from closing it
    }


}
