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

import com.riantservices.riant.R;
import com.riantservices.riant.adapters.BookAdapter;
import com.riantservices.riant.helpers.ListDividerItem;
import com.riantservices.riant.helpers.RecyclerTouchListener;
import com.riantservices.riant.helpers.SessionManager;
import com.riantservices.riant.interfaces.ClickListener;
import com.riantservices.riant.models.BookElements;

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

public class Book extends Fragment {

    private List<BookElements> BookList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        BookAdapter mAdapter = new BookAdapter(this.getActivity(),BookList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ListDividerItem(getActivity(), LinearLayoutManager.VERTICAL, R.drawable.listdivider));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                BookElements bookElements = BookList.get(position);
                Toast.makeText(getActivity(), bookElements.getPickup()+ " to "+bookElements.getDestination(), Toast.LENGTH_SHORT).show();
            }
        }));
        getDummyData();
        fetchBookData();
        return rootView;
    }

    private void getDummyData(){
        String pickup,destination,dateTime,distance,driver,contact,fare;
        BookElements BookElement;
        pickup = "District Center, Chandrasekharpur";
        dateTime = "On 06/12/2017 at 10:30 am";
        fare = "100.00";
        distance = "15.1 kms";
        driver = "Vahan Vahak";
        contact = "9999999999";
        destination = "Plot No-176, Saheed Nagar";
        BookElement = new BookElements(pickup,destination,dateTime,distance,driver,contact,fare);
        BookList.add(BookElement);
        BookList.add(BookElement);
        BookList.add(BookElement);
        BookList.add(BookElement);
        BookList.add(BookElement);
    }

    private void fetchBookData() {
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

    private void respond(InputStream in)throws JSONException {
        JSONObject result = new JSONObject(in.toString());
        JSONObject resultArrayJson;
        JSONArray book = result.getJSONArray("history");
        String pickup,destination,dateTime,distance,driver,contact,fare;
        BookElements BookElement;
        int lenArray = book.length();
        for (int i = 0; i < lenArray; i++) {
            resultArrayJson = book.getJSONObject(i);
            pickup = resultArrayJson.getString("pickup");
            destination = resultArrayJson.getString("destination");
            dateTime = resultArrayJson.getString("dateTime");
            distance = result.getString("distance");
            driver = result.getString("driver");
            contact = result.getString("contact");
            fare = result.getString("fare");
            BookElement = new BookElements(pickup,destination,dateTime,distance,driver,contact,fare);
            BookList.add(BookElement);
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