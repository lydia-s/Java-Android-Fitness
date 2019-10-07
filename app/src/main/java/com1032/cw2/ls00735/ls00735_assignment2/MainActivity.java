package com1032.cw2.ls00735.ls00735_assignment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author savou
 * MainActivity displays map and inflates widgets
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private TextView latitudeField, longitudeField, distanceValue, getPrevious;
    private Database database = null;
    private LocationManager locationManager;
    private LatLng location;
    private double runningTotal = 0.0;
    DecimalFormat df = new DecimalFormat("00.0");

    private TextView startPoint;
    private TextView finishPoint;
    private TextView clearDatabase;
    Intent locatorService = null;

    private BroadcastReceiver receiver = null;

    private double calories = 0.0;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        database = new Database(this, "distanceDB", null); //create new object of database
        runningTotal = database.getDistance();

        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, MyService.class); //create intent between MainActivity and MyService class
        this.startService(i);//start the service

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("distance");

        /*
         * onReceive gets location using intents
         */
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LatLng location = new LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("long", 0.0)); //get intents
                ((MainActivity) context).setDistanceFromReceiver(location);
                calories = database.getDistance() * 90; //approximate calories burnt

                /*
                call methods goToLocationZoom and setMarker if the map is not null
                 */
                if(mGoogleMap != null) {
                    goToLocationZoom(location.latitude, location.longitude, 15);
                    setMarker(String.valueOf(Locale.getDefault()), location.latitude, location.longitude);
                    initMap();

                }


            }
        };


        registerReceiver(receiver, intentFilter); //receive broadcast while running

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        code that inflates UI widgets
         */
        latitudeField = (TextView) findViewById(R.id.LatitudeValue);
        longitudeField = (TextView) findViewById(R.id.LongitudeValue);
        distanceValue = (TextView) findViewById(R.id.distanceValue);
        startPoint = (TextView) findViewById(R.id.button3);
        finishPoint = (TextView) findViewById(R.id.button2);
        getPrevious = (TextView) findViewById(R.id.getPrevious);
        clearDatabase = (TextView) findViewById(R.id.clearDatabase);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        /*
        get the last distance saved to the database
         */
        startPoint.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (database.getDistance() != 0.0) { //if distance is 0.0 database has been cleared

                    String s = df.format(database.getDistance()) + " km " + "\n" + df.format(database.getDistance()*90) + " Calories burnt previously ";
                    getPrevious.setText(s);
                } else {
                    Toast.makeText(getApplication(), "Nothing saved!",
                            Toast.LENGTH_LONG).show();

                }

            }
        });
        /*
        save current distance to database
         */
        finishPoint.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.updateDistance(runningTotal);//when button is pressed save the running total to the database
                Toast.makeText(getApplication(), "Distance saved!",
                        Toast.LENGTH_LONG).show();
           }

        });
        /*
        'clear' the distance saved to the database by setting it to 0.0
         */
        clearDatabase.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.clearDistance();
                Toast.makeText(getApplication(), "Previous data cleared!",
                        Toast.LENGTH_LONG).show();

            }

        });
        latitudeField.setText("Location not available");
        longitudeField.setText("Location not available");
        distanceValue.setText("Location not available");

    }

    /*
    unregister receiver when app closes
     */

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver); //unregister the receiver
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    /*
    setDistanceFromReceiver checks if new location is the same as the old location
    if not it calculates the distance between the two locations and adds this to the runningTotal
     */
    public void setDistanceFromReceiver(LatLng location) {

        if (this.location == null) {//if no new location is found display old location
            this.location = location;
            latitudeField.setText(String.valueOf(location.latitude));
            longitudeField.setText(String.valueOf(location.longitude));
            distanceValue.setText(String.valueOf(runningTotal));
        } else { //if a new location is found calculate the distance between the old and new location
            latitudeField.setText(String.valueOf(this.location.latitude));
            longitudeField.setText(String.valueOf(this.location.longitude));

            Location loc1 = new Location(LocationManager.GPS_PROVIDER);
            Location loc2 = new Location(LocationManager.GPS_PROVIDER);


            loc1.setLatitude(this.location.latitude);
            loc1.setLongitude(this.location.longitude);

            loc2.setLatitude(location.latitude);
            loc2.setLongitude(location.longitude);

            runningTotal += loc1.distanceTo(loc2)/1000;//calculate the distance between two locations and add this to running total to create a cumulative distance

            distanceValue.setText(String.valueOf(runningTotal));

        }

    }

    /*
    initMap initialises the map fragment
     */
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    goToLocationZoom centers and zooms into the current location
     */
    private void goToLocationZoom(double lat, double lng, float zoom) {

        LatLng ll = new LatLng(lat, lng);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

    Marker marker;

    /*
    setMarker adds a marker to the current location of the runner
     */
    private void setMarker(String locality, double lat, double lng) {
        if (marker != null) {

            marker.remove(); //remove marker if it already exists
        }

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); //get the address from the latitude and longitude
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); //get address
        String city = addresses.get(0).getLocality(); //get city



            MarkerOptions options = new MarkerOptions()
                    .title(city + "\n" + address)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_pin_circle))
                    .position(new LatLng(lat, lng))
                    .snippet("You are here");

           marker =  mGoogleMap.addMarker(options); //add a marker to the map

    }

        @Override
        public void onMapReady (GoogleMap googleMap){
            mGoogleMap = googleMap;

            //set a custom info window to display on the map
            if (mGoogleMap != null) {
                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

/*
getInfoContents displays a window info_window.xml when user presses the marker set to the current location on the map
the window should display the current latitude, longitude, distance travelled and calories burnt
 */
            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                TextView tvDist = (TextView) v.findViewById(R.id.tv_distance);
                TextView tvCal = (TextView) v.findViewById(R.id.tv_calories);
                TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                LatLng ll = marker.getPosition();
                tvLocality.setText(marker.getTitle());

                DecimalFormat df = new DecimalFormat("00.00");
                String distance = df.format(runningTotal) + " km";
                String cal = df.format(runningTotal*90) + " Calories";

                tvLat.setText("Latitude: " + df.format(location.latitude));
                tvLng.setText("Longitude: " + df.format(location.longitude));
                tvDist.setText("You've run: " + distance);
                tvCal.setText("You've burnt: " + cal);

                tvSnippet.setText(marker.getSnippet());

                return v;
            }
        });



            }
        }



}
