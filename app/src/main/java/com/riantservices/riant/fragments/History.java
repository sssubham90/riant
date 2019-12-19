package com.riantservices.riant.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.riantservices.riant.adapters.HistoryAdapter;
import com.riantservices.riant.interfaces.ClickListener;
import com.riantservices.riant.helpers.ListDividerItem;
import com.riantservices.riant.R;
import com.riantservices.riant.helpers.RecyclerTouchListener;
import com.riantservices.riant.helpers.SessionManager;
import com.riantservices.riant.models.HistoryElements;

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
import java.util.ArrayList;
import java.util.List;



public class History extends Fragment {

    private List<HistoryElements> HistoryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        HistoryAdapter mAdapter = new HistoryAdapter(HistoryList,this.getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ListDividerItem(getActivity(), LinearLayoutManager.VERTICAL, R.drawable.listdivider));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                HistoryElements HistoryElements = HistoryList.get(position);
                Toast.makeText(getActivity(), HistoryElements.getDestination(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        getDummyData();
        fetchHistoryData();
        return rootView;
    }

    private void getDummyData(){
        String destination,dateTime,fare;
        double lat,lng;
        HistoryElements HistoryElement;
        destination = "Plot No-176, Saheed Nagar";
        dateTime = "On 06/12/2017 at 10:30 am";
        fare = "100.00";
        lat = 20.2;
        lng = 85;
        HistoryElement = new HistoryElements(destination,dateTime,fare,lat,lng);
        HistoryList.add(HistoryElement);
    }

    private void fetchHistoryData() {
        final SessionManager session = new SessionManager(getActivity());
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost("url");
                    json.put("email", session.getEmail());
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        respond(in);
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    alertDialog("Error: Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    public void respond(InputStream in)throws JSONException {
        JSONObject result = new JSONObject(in.toString());
        JSONObject resultArrayJson;
        JSONArray history = result.getJSONArray("history");
        String destination,dateTime,fare;
        double lat,lng;
        HistoryElements HistoryElement;
        int lenArray = history.length();
        for (int i = 0; i < lenArray; i++) {
            resultArrayJson = history.getJSONObject(i);
            destination = resultArrayJson.getString("destination");
            dateTime = resultArrayJson.getString("dateTime");
            fare = resultArrayJson.getString("fare");
            lat = result.getDouble("lat");
            lng = result.getDouble("lng");
            HistoryElement = new HistoryElements(destination,dateTime,fare,lat,lng);
            HistoryList.add(HistoryElement);
        }
    }

    public void alertDialog(String Message) {

        new AlertDialog.Builder(getActivity()).setTitle("Riant Alert").setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }
}