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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {

    private ArrayList<Calls> historyListItems;
    private Context context;
    int hour = 0;
    int mins = 0;
    int sec = 0;

    public CallHistoryAdapter(ArrayList<Calls> historyListItems, Context context) {
        this.historyListItems = historyListItems;
        this.context = context;
    }

    @Override
    public CallHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_history_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        SimpleDateFormat simpleTime = new SimpleDateFormat("MMM-dd, yyyy, hh:mm a");
        SimpleDateFormat simpleCallTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        SimpleDateFormat CallTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String time = simpleTime.format(historyListItems.get(position).getCallTime());

        String[] calltime = historyListItems.get(position).getCallDuration().split(":");

        if (calltime.length == 2){

            mins = Integer.parseInt(calltime[0].trim());
            sec = Integer.parseInt(calltime[1].trim());

            String t = hour + ":" + mins + ":" + sec;
            try {
                Date d = simpleCallTime.parse(t);
                holder.callduration.setText(String.valueOf(CallTime.format(d)));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Glide.with(context).load(historyListItems.get(position).getBuyerImage()).into(holder.buyerimage);
            holder.buyername.setText(historyListItems.get(position).getBuyerName());
            holder.calltime.setText(time);
            holder.callamount.setText("$" + historyListItems.get(position).getDollerEarned());
            return;
        }

        if (calltime.length == 3){

            hour = Integer.parseInt(calltime[0].trim());
            mins = Integer.parseInt(calltime[1].trim());
            sec = Integer.parseInt(calltime[2].trim());

            String t = hour + ":" + mins + ":" + sec;
            try {
                Date d = simpleCallTime.parse(t);
                holder.callduration.setText(String.valueOf(CallTime.format(d)));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Glide.with(context).load(historyListItems.get(position).getSellerImage()).into(holder.buyerimage);
            holder.buyername.setText(historyListItems.get(position).getSellerName());
            holder.calltime.setText(time);
            holder.callamount.setText("$" + historyListItems.get(position).getDollerEarned());
            return;
        }

    }

    @Override
    public int getItemCount() {
        return historyListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView buyerimage;
        public TextView buyername, calltime, callamount, callduration;

        public ViewHolder(final View itemView) {
            super(itemView);

            buyerimage = itemView.findViewById(R.id.buyerimage);
            buyername = itemView.findViewById(R.id.buyername);
            calltime = itemView.findViewById(R.id.calltime);
            callamount = itemView.findViewById(R.id.callamount);
            callduration = itemView.findViewById(R.id.callduration);

        }
    }
}