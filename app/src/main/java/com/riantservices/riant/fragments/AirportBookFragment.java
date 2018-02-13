package com.riantservices.riant.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.riantservices.riant.R;
import com.riantservices.riant.helpers.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;


public class AirportBookFragment extends Fragment implements View.OnClickListener {

    SessionManager session;
    ImageButton oneway, roundtrip;
    Button AC, NonAC;
    private EditText Pickup, Destination, FriendContact;
    private RadioButton radio2;
    private String strEmail, strBookFor, strTrip, strAC, strPickup, strDestination, strNumber;
    private LatLng pickup, destination;

    public AirportBookFragment(){}

    public static AirportBookFragment newInstance(Bundle intentExtra) {
        AirportBookFragment f = new AirportBookFragment();
        f.setArguments(intentExtra);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_airport_book, container, false);

        Bundle coordinates = getArguments();

        strBookFor = "";
        strTrip = "";
        strAC = "";
        strPickup = "";
        strDestination = "";
        strNumber = "";

        if (coordinates != null) {
            double[] lat = coordinates.getDoubleArray("lat");
            double[] lng = coordinates.getDoubleArray("lng");

            if (lat != null && lng != null) {
                pickup = new LatLng(lat[0], lng[0]);
                destination = new LatLng(lat[1], lng[1]);
            }

        }


        Button button = rootView.findViewById(R.id.button);
        Button button1 = rootView.findViewById(R.id.button1);
        oneway = rootView.findViewById(R.id.oneway);
        roundtrip = rootView.findViewById(R.id.roundtrip);
        AC = rootView.findViewById(R.id.AC);
        NonAC = rootView.findViewById(R.id.NonAC);

        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        oneway.setOnClickListener(this);
        roundtrip.setOnClickListener(this);
        AC.setOnClickListener(this);
        NonAC.setOnClickListener(this);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        session = new SessionManager(getContext());
        strEmail = session.getEmail();
        RadioGroup radio;
        Pickup = rootView.findViewById(R.id.edit1);
        Destination = rootView.findViewById(R.id.edit2);
        FriendContact = rootView.findViewById(R.id.edit3);
        radio = rootView.findViewById(R.id.radio);
        radio2 = rootView.findViewById(R.id.radio2);
        FriendContact.setVisibility(View.INVISIBLE);
        getAddress();
        radio.clearCheck();
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        strBookFor = "";
                        strBookFor = "For Yourself";
                        FriendContact.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radio2:
                        strBookFor = "";
                        strBookFor = "Friend";
                        FriendContact.setText("");
                        FriendContact.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TextView textView = (TextView) v;
                    textView.setBackground(getResources().getDrawable(R.drawable.textboxselected));
                    textView.setHintTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    TextView textView = (TextView) v;
                    textView.setBackground(getResources().getDrawable(R.drawable.textboxborder));
                    textView.setHintTextColor(getResources().getColor(R.color.colorWhite));
                    textView.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        };

        Pickup.setOnFocusChangeListener(onFocusChangeListener);
        Destination.setOnFocusChangeListener(onFocusChangeListener);
        FriendContact.setOnFocusChangeListener(onFocusChangeListener);

        return rootView;
    }

    public void getAddress() {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        Address address;
        String result;
        try {
            List<Address> pickupAddress;
            List<Address> destinationAddress;
            pickupAddress = geocoder.getFromLocation(pickup.latitude, pickup.longitude, 1);
            destinationAddress = geocoder.getFromLocation(destination.latitude, destination.longitude, 1);
            if (pickupAddress.size() > 0) {
                address = pickupAddress.get(0);
                result = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    result = result.concat(address.getAddressLine(i) + " ");
                Pickup.setText(result);
            }
            if (destinationAddress.size() > 0) {
                address = destinationAddress.get(0);
                result = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    result = result.concat(address.getAddressLine(i) + " ");
                Destination.setText(result);
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.oneway:
                oneway.setBackground(getResources().getDrawable(R.drawable.buttonselected));
                roundtrip.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                strTrip = "";
                strTrip = "Oneway";
                break;
            case R.id.roundtrip:
                roundtrip.setBackground(getResources().getDrawable(R.drawable.buttonselected));
                oneway.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                strTrip = "";
                strTrip = "Roundtrip";
                break;
            case R.id.AC:
                AC.setBackground(getResources().getDrawable(R.drawable.buttonselected));
                NonAC.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                strAC = "";
                strAC = "AC";
                break;
            case R.id.NonAC:
                NonAC.setBackground(getResources().getDrawable(R.drawable.buttonselected));
                AC.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                strAC = "";
                strAC = "NonAC";
                break;
            case R.id.button1:
                strPickup = Pickup.getText().toString();
                strDestination = Destination.getText().toString();
                strNumber = FriendContact.getText().toString();
                if (strPickup.matches("")) {
                    alertDialog("Please enter your Pickup Location", getContext());
                } else if (strDestination.matches("")) {
                    alertDialog("Please enter your Destination", getContext());
                } else if (strBookFor.matches("")) {
                    alertDialog("Please choose who are you booking the ride for.", getContext());
                } else if (radio2.isChecked() && strNumber.matches("")) {
                    alertDialog("Please enter friend's contact number.", getContext());
                } else if (strTrip.matches("")) {
                    alertDialog("Please choose between one way or round trip", getContext());
                } else if (strAC.matches("")) {
                    alertDialog("Please choose between AC or Non-AC", getContext());
                } else {
                    try {
                        Book();
                    } catch (UnsupportedEncodingException e) {
                        alertDialog("Unsupported Encoding", getContext());
                    }
                }
                break;
            case R.id.button:
                //estimate fare function
                break;
        }
    }

    public void alertDialog(String Message, Context context) {

        new AlertDialog.Builder(context).setTitle("Riant Alert").setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    protected void Book() throws UnsupportedEncodingException {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost("url");
                    json.put("email", strEmail);
                    json.put("pickup", strPickup);
                    json.put("pickupCoordinate", pickup);
                    json.put("destination", strDestination);
                    json.put("destinationCoordinate", destination);
                    json.put("bookFor", strBookFor);
                    json.put("number", strNumber);
                    json.put("ac", strAC);
                    json.put("trip", strTrip);
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
                    alertDialog("Error: Cannot Estabilish Connection", getContext());
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    public void respond(InputStream in) throws JSONException {
        JSONObject result = new JSONObject(in.toString());

        if (result.getInt("status") == 1) {
            alertDialog("Booking Successful", getContext());

        } else {
            alertDialog("System error, please contact with administrator", getContext());
        }
    }
}
