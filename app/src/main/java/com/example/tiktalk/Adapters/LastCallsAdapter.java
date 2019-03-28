package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Model.Calls;
import com.example.tiktalk.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LastCallsAdapter extends RecyclerView.Adapter<LastCallsAdapter.ViewHolder> {

    private ArrayList<Calls> callsListItems;
    private Context context;


    public LastCallsAdapter(ArrayList<Calls> callsListItems, Context context) {
        this.callsListItems = callsListItems;
        this.context = context;
    }

    @Override
    public LastCallsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_call_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        SimpleDateFormat simpleTime = new SimpleDateFormat("MMM-dd, yyyy, hh:mm a");
        String time = simpleTime.format(callsListItems.get(position).getCallTime());

        Glide.with(context).load(callsListItems.get(position).getSellerImage()).into(holder.last_call_image);
        holder.last_call_name.setText(callsListItems.get(position).getSellerName());
        holder.last_call_time.setText(time);
        holder.last_call_coins.setText(callsListItems.get(position).getCoinsUsed());

    }

    @Override
    public int getItemCount() {
        return callsListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView last_call_image;
        public TextView last_call_name, last_call_time, last_call_coins;

        public ViewHolder(final View itemView) {
            super(itemView);

            last_call_image = itemView.findViewById(R.id.last_call_image);
            last_call_name = itemView.findViewById(R.id.last_call_name);
            last_call_time = itemView.findViewById(R.id.last_call_time);
            last_call_coins = itemView.findViewById(R.id.last_call_coins);

        }
    }
}

