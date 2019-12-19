package com.riantservices.riant.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

public class OutstationBook extends Fragment implements View.OnClickListener {
    private ImageButton oneway, roundtrip;
    private Button AC, NonAC;
    private EditText FriendContact;
    private RadioButton radio2;
    private TextView distance;
    private TextView Pickup;
    private TextView Destination;
    private TextView Date;
    private TextView Time;
    private String strEmail, strBookFor, strTrip, strAC, strPickup, strDestination, strNumber;
    private LinearLayout rDateTime;
    private LatLng pickupLoc,destinationLoc;
    private float distanceValue;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_outstation, container, false);
        strBookFor = "";
        strTrip = "";
        strAC = "";
        strPickup = "";
        strDestination = "";
        strNumber = "";
        final Calendar c = Calendar.getInstance();
        rDateTime = rootView.findViewById(R.id.rDateTime);
        rDateTime.setVisibility(View.GONE);
        Button button = rootView.findViewById(R.id.button);
        Button button1 = rootView.findViewById(R.id.button1);
        distance = rootView.findViewById(R.id.distance1);
        oneway = rootView.findViewById(R.id.oneway);
        roundtrip = rootView.findViewById(R.id.roundtrip);
        Date = rootView.findViewById(R.id.pDate);
        Time = rootView.findViewById(R.id.pTime);
        TextView date1 = rootView.findViewById(R.id.rDate);
        TextView time1 = rootView.findViewById(R.id.rTime);
        AC = rootView.findViewById(R.id.AC);
        NonAC = rootView.findViewById(R.id.NonAC);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        oneway.setOnClickListener(this);
        roundtrip.setOnClickListener(this);
        AC.setOnClickListener(this);
        SessionManager session = new SessionManager(getActivity());
        strEmail = session.getEmail();
        RadioGroup radio;
        Pickup = rootView.findViewById(R.id.edit1);
        Pickup.setPadding(6,6,6,6);
        Destination = rootView.findViewById(R.id.edit2);
        Destination.setPadding(6,6,6,6);
        FriendContact = rootView.findViewById(R.id.edit3);
        radio = rootView.findViewById(R.id.radio);
        radio2 = rootView.findViewById(R.id.radio2);
        FriendContact.setVisibility(View.INVISIBLE);
        radio.clearCheck();
        Date.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d",c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH), c.get(Calendar.YEAR)));
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Date.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d", dayOfMonth, monthOfYear + 1, year));

                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Time.setText(String.format(Locale.ENGLISH,"%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)));
        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Time.setText(String.format(Locale.ENGLISH,"%02d:%02d", hourOfDay, minute));
                    }
                },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();
            }
        });
        date1.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d",c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH), c.get(Calendar.YEAR)));
        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Date.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d", dayOfMonth, monthOfYear + 1, year));

                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        time1.setText(String.format(Locale.ENGLISH,"%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)));
        time1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Time.setText(String.format(Locale.ENGLISH,"%02d:%02d", hourOfDay, minute));
                    }
                },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();
            }
        });
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        strBookFor = "";
                        strBookFor = "For Yourself";
                        FriendContact.setVisibility(View.GONE);
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
                    textView.setPadding(6,6,6,6);
                    textView.setHintTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    TextView textView = (TextView) v;
                    textView.setBackground(getResources().getDrawable(R.drawable.textboxborder));
                    textView.setPadding(6,6,6,6);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.oneway:
                oneway.setBackground(getResources().getDrawable(R.drawable.buttonselected));
                roundtrip.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                rDateTime.setVisibility(View.GONE);
                strTrip = "";
                strTrip = "Oneway";
                break;
            case R.id.roundtrip:
                roundtrip.setBackground(getResources().getDrawable(R.drawable.buttonselected));
                oneway.setBackground(getResources().getDrawable(R.drawable.buttonshape));
                rDateTime.setVisibility(View.VISIBLE);
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
                    alertDialog("Please enter your Pickup Location", getActivity());
                } else if (strDestination.matches("")) {
                    alertDialog("Please enter your Destination", getActivity());
                } else if (strBookFor.matches("")) {
                    alertDialog("Please choose who are you booking the ride for.", getActivity());
                } else if (radio2.isChecked() && strNumber.matches("")) {
                    alertDialog("Please enter friend's contact number.", getActivity());
                } else if (strTrip.matches("")) {
                    alertDialog("Please choose between one way or round trip", getActivity());
                } else if (strAC.matches("")) {
                    alertDialog("Please choose between AC or Non-AC", getActivity());
                } else {
                    try {
                        Book();
                    } catch (UnsupportedEncodingException e) {
                        alertDialog("Unsupported Encoding", getActivity());
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

    private void Book() throws UnsupportedEncodingException {
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
                    //json.put("pickupCoordinate", pickup);
                    json.put("destination", strDestination);
                    //json.put("destinationCoordinate", destination);
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
                    alertDialog("Error: Cannot Establish Connection", getActivity());
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    private void respond(InputStream in) throws JSONException {
        JSONObject result = new JSONObject(in.toString());

        if (result.getInt("status") == 1) {
            alertDialog("Booking Successful", getActivity());

        } else {
            alertDialog("System error, please contact with administrator", getActivity());
        }
    }

    public void displayReceivedData(LatLng location, String message, int i)
    {
        if(i==0){
            Pickup.setText(message);
            pickupLoc = location;
        }
        else if(i==1){
            Destination.setText(message);
            destinationLoc = location;
        }
        else if(i==3){
            String result;
            float value = Float.parseFloat(message);
            if(value>1000)
                result = String.format(Locale.ENGLISH,"%.2f KM",value/1000);
            else
                result = String.format(Locale.ENGLISH,"%.0f metres",value);
            distance.setText(result);
            distanceValue = value;
        }
    }
}
