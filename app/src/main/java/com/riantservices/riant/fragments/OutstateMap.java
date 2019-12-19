package com.riantservices.riant.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.riantservices.riant.R;
import com.riantservices.riant.activities.OutstateActivity;
import com.riantservices.riant.helpers.AddressResultReceiver;
import com.riantservices.riant.helpers.Constants;
import com.riantservices.riant.helpers.DownloadRouteTask;
import com.riantservices.riant.helpers.GeocodeAddressIntentService;
import com.riantservices.riant.helpers.SingleShotLocationProvider;
import com.riantservices.riant.interfaces.AsyncResponse;
import com.riantservices.riant.interfaces.SendMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutstateMap extends Fragment implements OnMapReadyCallback {

    private View mRootView;
    private LatLng pickup,destination;
    private GoogleMap googleMap;
    private Marker userMarker;
    private TextView TV1,TV2;
    private SendMessage SM;
    private DownloadRouteTask downloadRouteTask;
    private Marker destinationMarker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_outstate_map, container, false);
        MapView mapView = rootView.findViewById(R.id.map);
        TV1 = rootView.findViewById(R.id.pickup_addr);
        TV2 = rootView.findViewById(R.id.destination_addr);
        TV1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SM.sendData(pickup, TV1.getText().toString(),0);
            }
        });
        TV2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SM.sendData(destination, TV2.getText().toString(),1);
            }
        });
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        final SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        mRootView = rootView;
        return rootView;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;
        SearchView searchView = (mRootView.findViewById(R.id.searchView));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Address> addressList = new ArrayList<>();

                if (query != null && !query.equals("")) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(query, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address;
                    if (!addressList.isEmpty()) {
                        address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title(query));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mark(latLng);
            }
        });
        MarkerOptions options=new MarkerOptions().position(new LatLng(20.2961,85.8245)).title("Current Location");
        userMarker = googleMap.addMarker(options);
        userMarker.setVisible(false);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(),15);
        googleMap.animateCamera(update);
        requestLocation();
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        Button button1 = mRootView.findViewById(R.id.clear);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickup=null;
                TV1.setText("");
                destination=null;
                TV2.setText("");
                ((OutstateActivity)getActivity()).clearState();
                map.clear();
            }
        });
        ImageButton locate = mRootView.findViewById(R.id.locate);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });
    }

    protected void requestLocation(){
        SingleShotLocationProvider.requestSingleUpdate(getActivity(),
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        LatLng CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());
                        userMarker.setPosition(CURRENT_LOCATION);
                        userMarker.setVisible(true);
                        userMarker.showInfoWindow();
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION,15);
                        googleMap.animateCamera(update);
                    }
                });
    }

    protected void mark(LatLng latLng) {
        if(pickup == null){
            pickup = latLng;
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(latLng).title("Pickup"));
        }
        else if (destination == null) {
            destination = latLng;
            destinationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).position(latLng).title("Destination"));
            String url = getDirectionsUrl(pickup, destination);
            downloadRouteTask = new DownloadRouteTask(googleMap,new AsyncResponse() {
                @Override
                public void processFinish(float output,float output1) {
                    SM.sendData(null,String.valueOf(output),3);
                }
            });
            downloadRouteTask.execute(url);
        }
        Intent intent = new Intent(getActivity(), GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, new AddressResultReceiver(null,(OutstateActivity)getActivity()));
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_LOCATION);
        intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,latLng.latitude);
        intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,latLng.longitude);
        getActivity().startService(intent);

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        return  "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    public void alertSameState() {
        new AlertDialog.Builder(getActivity()).setTitle("Riant Alert")
                .setMessage("Pickup and Destination cannot be in the same state.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
        destination = null;
        downloadRouteTask.cancel(true);
        TV2.setText("");
        destinationMarker.remove();
    }

    public void setTextViews(String value) {
        if(TV1.getText().toString().isEmpty())
            TV1.setText(value);
        else if(TV2.getText().toString().isEmpty())
            TV2.setText(value);
    }
}