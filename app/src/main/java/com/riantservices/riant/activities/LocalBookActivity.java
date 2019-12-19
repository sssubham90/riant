package com.riantservices.riant.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocalBookActivity extends AppCompatActivity implements View.OnClickListener {
    public static float rslt;
    SessionManager session;
    ImageButton oneway, roundtrip;
    Button AC, NonAC;
    private EditText  FriendContact;
    private TextView distance,Pickup, Destination,Date,Time,Date1,Time1;
    private RadioButton radio2;
    private String strEmail, strBookFor, strTrip, strAC, strPickup, strDestination, strNumber;
    private LinearLayout rDateTime;
    private double distanceValue,timeValue;
    private LatLng pickup;
    private List<LatLng> destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        destination = new ArrayList<>();
        strBookFor = "";
        strTrip = "";
        strAC = "";
        strPickup = "";
        strDestination = "";
        strNumber = "";
        setContentView(R.layout.activity_local_book);
        final Calendar c = Calendar.getInstance();
        rDateTime = findViewById(R.id.rDateTime);
        rDateTime.setVisibility(View.GONE);
        distance = findViewById(R.id.distance1);
        oneway = findViewById(R.id.oneway);
        roundtrip = findViewById(R.id.roundtrip);
        Date = findViewById(R.id.pDate);
        Time = findViewById(R.id.pTime);
        Date1 = findViewById(R.id.rDate);
        Time1 = findViewById(R.id.rTime);
        AC = findViewById(R.id.AC);
        NonAC = findViewById(R.id.NonAC);
        findViewById(R.id.estimator).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        oneway.setOnClickListener(this);
        roundtrip.setOnClickListener(this);
        AC.setOnClickListener(this);
        NonAC.setOnClickListener(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        session = new SessionManager(getApplicationContext());
        strEmail = session.getEmail();
        RadioGroup radio;
        Pickup = findViewById(R.id.edit1);
        Pickup.setPadding(6,6,6,6);
        Destination = findViewById(R.id.edit2);
        Destination.setPadding(6,6,6,6);
        FriendContact = findViewById(R.id.edit3);
        radio = findViewById(R.id.radio);
        radio2 = findViewById(R.id.radio2);
        FriendContact.setVisibility(View.INVISIBLE);
        Bundle Data = getIntent().getBundleExtra("Data");
        if (Data != null) {
            double[] lat = Data.getDoubleArray("lat");
            double[] lng = Data.getDoubleArray("lng");
            if (lat != null && lng != null) {
                pickup = new LatLng(lat[0], lng[0]);
                for (int i = 1; i < lat.length; i++)
                    destination.add(new LatLng(lat[i], lng[i]));
            }
            Pickup.setText(Data.getString("pickupAddr"));
            Destination.setText(Data.getString("destinationAddr"));
            distanceValue = Data.getFloat("distance");
            timeValue = Data.getFloat("time");
            fillDistance();
        }
        radio.clearCheck();
        Date.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d",c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH), c.get(Calendar.YEAR)));
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LocalBookActivity.this,new DatePickerDialog.OnDateSetListener() {
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
                new TimePickerDialog(LocalBookActivity.this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Time.setText(String.format(Locale.ENGLISH,"%02d:%02d", hourOfDay, minute));
                    }
                },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();
            }
        });
        Date1.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d",c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH), c.get(Calendar.YEAR)));
        Date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(LocalBookActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Date.setText(String.format(Locale.ENGLISH,"%02d-%02d-%02d", dayOfMonth, monthOfYear + 1, year));

                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Time1.setText(String.format(Locale.ENGLISH,"%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)));
        Time1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(LocalBookActivity.this,new TimePickerDialog.OnTimeSetListener() {
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
        FriendContact.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void fillDistance() {
        String result;
        if(distanceValue == 0)
            result = "NA";
        else if(distanceValue>1000)
            result = String.format(Locale.ENGLISH,"%.2f KM",distanceValue/1000);
        else
            result = String.format(Locale.ENGLISH,"%.0f metres",distanceValue);
        distance.setText(result);
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
            case R.id.estimator:
                if(strAC.equals("AC"))
                    fareEstimator("STANDARD",1,distanceValue/1000,timeValue/60);
                else
                    fareEstimator("STANDARD",0,distanceValue/1000,timeValue/60);
                break;
            case R.id.button1:
                strPickup = Pickup.getText().toString();
                strDestination = Destination.getText().toString();
                strNumber = FriendContact.getText().toString();
                if (strPickup.matches("")) {
                    alertDialog("Please enter your Pickup Location");
                } else if (strDestination.matches("")) {
                    alertDialog("Please enter your Destination");
                } else if (strBookFor.matches("")) {
                    alertDialog("Please choose who are you booking the ride for.");
                } else if (radio2.isChecked() && strNumber.matches("")) {
                    alertDialog("Please enter friend's contact number.");
                } else if (strTrip.matches("")) {
                    alertDialog("Please choose between one way or round trip");
                } else if (strAC.matches("")) {
                    alertDialog("Please choose between AC or Non-AC");
                } else {
                    try {
                        Book();
                    } catch (UnsupportedEncodingException e) {
                        alertDialog("Unsupported Encoding");
                    }
                }
                break;
        }
    }

    private void fareEstimator(String type,int ac, double distanceValue, double timeValue) {

    }

    public void alertDialog(String Message) {

        new AlertDialog.Builder(this).setTitle("Riant Alert").setMessage(Message)
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
                    alertDialog("Error: Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    public void respond(InputStream in) throws JSONException {
        JSONObject result = new JSONObject(in.toString());

        if (result.getInt("status") == 1) {
            alertDialog("Booking Successful");

        } else {
            alertDialog("System error, please contact with administrator");
        }
    }


}
