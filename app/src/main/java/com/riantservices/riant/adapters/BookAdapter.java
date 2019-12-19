package com.riantservices.riant.adapters;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.riantservices.riant.R;
import com.riantservices.riant.models.BookElements;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {
    private List<BookElements> bookList;
    private Context mcontext;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pickup, destination, dateTime, distance, driver, contact, fare;
        ImageButton call;

        MyViewHolder(View view) {
            super(view);
            pickup = view.findViewById(R.id.pickup_addr);
            destination = view.findViewById(R.id.destination_addr);
            dateTime = view.findViewById(R.id.dateTime);
            distance = view.findViewById(R.id.distance);
            driver = view.findViewById(R.id.driver);
            contact = view.findViewById(R.id.contact);
            fare = view.findViewById(R.id.fare);
            call = view.findViewById(R.id.call);
        }
    }


    public BookAdapter(Context context, List<BookElements> bookList) {
        this.bookList = bookList;
        mcontext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BookElements bookElements = bookList.get(position);
        holder.pickup.setText(bookElements.getPickup());
        holder.destination.setText(bookElements.getDestination());
        holder.fare.setText(String.format("Rs.%s", bookElements.getFare()));
        holder.contact.setText(bookElements.getContact());
        holder.driver.setText(bookElements.getDriver());
        holder.dateTime.setText(bookElements.getDateTime());
        holder.distance.setText(String.format("Distance: %s", bookElements.getDistance()));
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mcontext)
                        .setTitle("Riant")
                        .setMessage("Do you want to call "+holder.driver.getText()+"?")
                        .setIcon(R.drawable.bar)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String uri = "tel:" + holder.contact.getText();
                                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                                if (checkPermission(mcontext))
                                    mcontext.startActivity(call);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private static boolean checkPermission(Context context){
        int MY_PERMISSIONS_REQUEST_LOCATION = 1;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        else if(context.checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED)
            return true;
        else
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_LOCATION);
        return MY_PERMISSIONS_REQUEST_LOCATION==PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
