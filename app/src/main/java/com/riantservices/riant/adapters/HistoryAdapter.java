package com.riantservices.riant.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.riantservices.riant.R;
import com.riantservices.riant.models.HistoryElements;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Context mContext;
    private List<HistoryElements> HistoryList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView destination, dateTime, amount;
        LatLng location;
        ImageView map;

        MyViewHolder(View view) {
            super(view);
            destination = view.findViewById(R.id.destination_addr);
            dateTime = view.findViewById(R.id.dateTime);
            amount = view.findViewById(R.id.amount);
            map = view.findViewById(R.id.map);
            location = new LatLng(0,0);
        }
    }


    public HistoryAdapter(List<HistoryElements> HistoryList, Context context) {
        this.HistoryList = HistoryList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HistoryElements HistoryElements = HistoryList.get(position);
        holder.location=HistoryElements.getLatLng();
        String URL = "http://maps.google.com/maps/api/staticmap?center="+holder.location.latitude+","+holder.location.longitude+"&markers=color:red%7C"+holder.location.latitude+","+holder.location.longitude+"&zoom=15&size=400x160&sensor=false&key="+R.string.google_maps_key;
        Picasso.with(mContext).load(URL)
               .placeholder(R.drawable.default_placeholder)
               .error(R.drawable.default_placeholder)
               .fit().noFade().into(holder.map);
        holder.destination.setText(HistoryElements.getDestination());
        holder.dateTime.setText(HistoryElements.getDateTime());
        holder.amount.setText(String.format("Rs.%s", HistoryElements.getAmount()));
    }

    @Override
    public int getItemCount() {
        return HistoryList.size();
    }
}
