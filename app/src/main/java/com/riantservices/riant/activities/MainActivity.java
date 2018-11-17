package com.riantservices.riant.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.riantservices.riant.R;
import com.riantservices.riant.helpers.AddressResultReceiver;
import com.riantservices.riant.helpers.Constants;
import com.riantservices.riant.helpers.DownloadRouteTask;
import com.riantservices.riant.helpers.GPSTracker;
import com.riantservices.riant.helpers.GeocodeAddressIntentService;
import com.riantservices.riant.helpers.SingleShotLocationProvider;
import com.riantservices.riant.interfaces.AsyncResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker userMarker;
    private LatLng pickup;
    private List<LatLng> destination;
    private DrawerLayout mDrawerLayout;
    private TextView TV1,TV2;
    private ActionBarDrawerToggle mDrawerToggle;
    private float distance;
    private Bundle location;
    private LatLngBounds.Builder builder;
    private float time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = getIntent().getExtras();
        if (location==null||(location.getDouble("lat")==0 && location.getDouble("lng")==0)) {
            requestLocation();
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        TV1 = findViewById(R.id.pickup_addr);
        TV2 = findViewById(R.id.destination_addr);
        distance = 0;
        builder = new LatLngBounds.Builder();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
        else Log.d("ERR","Map fragment null");
        ImageButton bar = findViewById(R.id.bar);
        bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START,true);
            }
        });

        OnClickListener iconClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.account1:
                    case R.id.textAccount1:
                        Intent goAccount = new Intent(MainActivity.this, AccountActivity.class);
                        startActivity(goAccount);
                        break;
                    case R.id.trips1:
                    case R.id.textTrips1:
                        Intent goTrips = new Intent(MainActivity.this, TripsActivity.class);
                        startActivity(goTrips);
                        break;
                    case R.id.notifictions1:
                    case R.id.textNotifications1:
                        alertDialog("Notifications is clicked");
                        break;
                    case R.id.settings1:
                    case R.id.textSettings1:
                        alertDialog("Settings is clicked");
                        break;
                    case R.id.help1:
                    case R.id.textHelp1:
                        alertDialog("Help is clicked");
                        break;
                    case R.id.outstate1:
                    case R.id.textOutstate1:
                        Intent goOutstate = new Intent(MainActivity.this, OutstateActivity.class);
                        startActivity(goOutstate);
                        break;
                    case R.id.outstation1:
                    case R.id.textOutstation1:
                        Intent goOutstation = new Intent(MainActivity.this, OutstationActivity.class);
                        startActivity(goOutstation);
                        break;
                }
            }
        };
        findViewById(R.id.account1).setOnClickListener(iconClickListener);
        findViewById(R.id.trips1).setOnClickListener(iconClickListener);
        findViewById(R.id.settings1).setOnClickListener(iconClickListener);
        findViewById(R.id.notifictions1).setOnClickListener(iconClickListener);
        findViewById(R.id.help1).setOnClickListener(iconClickListener);
        findViewById(R.id.outstate1).setOnClickListener(iconClickListener);
        findViewById(R.id.outstation1).setOnClickListener(iconClickListener);
        findViewById(R.id.textAccount1).setOnClickListener(iconClickListener);
        findViewById(R.id.textTrips1).setOnClickListener(iconClickListener);
        findViewById(R.id.textNotifications1).setOnClickListener(iconClickListener);
        findViewById(R.id.textSettings1).setOnClickListener(iconClickListener);
        findViewById(R.id.textHelp1).setOnClickListener(iconClickListener);
        findViewById(R.id.textOutstate1).setOnClickListener(iconClickListener);
        findViewById(R.id.textOutstation1).setOnClickListener(iconClickListener);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        destination = new ArrayList<>();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
            }

            @Override
            public void onError(Status status) {
                Log.d("Error", "An error occurred: " + status);
            }
        });
        Button button = findViewById(R.id.proceed);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destination.size() != 0)
                    proceed(pickup, destination);
                else
                    alertDialog("Choose a Destination");
            }
        });
        ImageButton button1 = findViewById(R.id.clear);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pickup=null;
                TV1.setText("");
                destination.clear();
                TV2.setText("");
                map.clear();
                distance = 0;
            }
        });

        ImageButton locate = findViewById(R.id.locate);
        locate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);
        googleMap = map;
        if(location!=null){
            LatLng CURRENT_LOCATION = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION,15);
            googleMap.animateCamera(update);
            MarkerOptions options=new MarkerOptions().position(CURRENT_LOCATION).title("Current Location").visible(true);
            userMarker = googleMap.addMarker(options);
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mark(latLng);
            }
        });
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void requestLocation(){
        SingleShotLocationProvider.requestSingleUpdate(MainActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("loc","My cordinates:"+ location.getLatitude()+","+ location.getLongitude());
                        LatLng CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION,15);
                        googleMap.animateCamera(update);
                        MarkerOptions options=new MarkerOptions().position(CURRENT_LOCATION).title("Current Location").visible(true);
                        userMarker = googleMap.addMarker(options);
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestLocation();
    }

    protected void mark(LatLng latLng) {
        builder.include(latLng);
        if (pickup!=null) {
            destination.add(latLng);
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).position(latLng).title("destination".concat(String.valueOf(destination.size()))));
            if (destination.size() == 1) {
                String url = getDirectionsUrl(pickup, destination.get(destination.size()-1));
                DownloadRouteTask downloadRouteTask = new DownloadRouteTask(googleMap, new AsyncResponse() {
                    @Override
                    public void processFinish(float output,float output1) {
                        distance+=output;
                        time+=output1;
                    }
                });
                downloadRouteTask.execute(url);
            } else {
                String url = getDirectionsUrl(destination.get(destination.size()-2), destination.get(destination.size()-1));
                DownloadRouteTask downloadRouteTask = new DownloadRouteTask(googleMap, new AsyncResponse() {
                    @Override
                    public void processFinish(float output,float output1) {
                        distance+=output;
                        time+=output1;
                    }
                });
                downloadRouteTask.execute(url);
            }

            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            googleMap.animateCamera(cu);

        } else {
            pickup = latLng;
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng).title("pickup"));
        }
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, new AddressResultReceiver(null,this));
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_LOCATION);
        intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,latLng.latitude);
        intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,latLng.longitude);
        this.startService(intent);
    }

    protected void proceed(LatLng pickup, List<LatLng> destination) {
        int i = 0;
        final Intent intent = new Intent(MainActivity.this, LocalBookActivity.class);
        Bundle bundle = new Bundle();
        double[] lat = new double[destination.size() + 1];
        double[] lng = new double[destination.size() + 1];
        lat[i] = pickup.latitude;
        lng[i] = pickup.longitude;
        for (LatLng t : destination) {
            i++;
            lat[i] = t.latitude;
            lng[i] = t.longitude;
        }
        bundle.putDoubleArray("lat", lat);
        bundle.putDoubleArray("lng", lng);
        bundle.putString("pickupAddr",TV1.getText().toString());
        bundle.putString("destinationAddr",TV2.getText().toString());
        bundle.putFloat("distance",distance);
        bundle.putFloat("time",time);
        intent.putExtra("Data", bundle);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        startActivity(intent);
    }

    protected void UpdateGPS(final String Email) throws UnsupportedEncodingException {
        GPSTracker gps = new GPSTracker(this);
        final double lat = gps.getLatitude();
        final double lon = gps.getLongitude();
        userMarker.setPosition(new LatLng(lat,lon));
        Thread t = new Thread() {
            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost("url");
                    json.put("email", Email);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

            /*Checking response */
                    if (response != null) {
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        respond(in);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    alertDialog("Error: Cannot Estabilish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.start();
    }

    public void alertDialog(String Message) {

        new AlertDialog.Builder(this).setTitle("Riant Alert").setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void respond(InputStream in) throws JSONException {
        final JSONObject result = new JSONObject(in.toString());
        Thread t = new Thread(){
            public void run(){
                try {
                    JSONObject resultArrayJson;
                    String Latitude,Longitude,CabType;
                    double Lat,Lon;
                    LatLng Taxi_Location;
                    JSONObject StatusRequest = result.getJSONObject("result");
                    JSONArray TaxiDrivers = StatusRequest.getJSONArray("TaxiDrivers");
                    int lenArray = TaxiDrivers.length();
                    for (int i = 0; i < lenArray; i++) {
                        resultArrayJson = TaxiDrivers.getJSONObject(i);
                        Latitude = resultArrayJson.getString("Latitude");
                        Longitude = resultArrayJson.getString("Longitude");
                        CabType = resultArrayJson.getString("CabType");
                        Lat = Double.parseDouble(Latitude);
                        Lon = Double.parseDouble(Longitude);
                        Taxi_Location = new LatLng(Lat, Lon);
                        googleMap.addMarker(new MarkerOptions()
                                .position(Taxi_Location)
                                .title("Riant " + CabType)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bar)));
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=driving";
        String key = "key="+getString(R.string.google_maps_key);
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + key;
        String output = "json";
        return  "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    public void setTextViews(String value) {
        if(TV1.getText().toString().isEmpty())
            TV1.setText(value);
        else
            TV2.setText(value);
    }
}