package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.choota.dev.ctimeago.TimeAgo;
import com.example.tiktalk.Model.SellerRating;
import com.example.tiktalk.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.Timestamp;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerRatingAdapter extends RecyclerView.Adapter<SellerRatingAdapter.ViewHolder> {

    private ArrayList<SellerRating> sellerRatingListItems;
    private Context context;
    TimeAgo timeAgo = new TimeAgo();

    public SellerRatingAdapter(ArrayList<SellerRating> sellerRatingListItems, Context context) {
        this.sellerRatingListItems = sellerRatingListItems;
        this.context = context;
    }

    @Override
    public SellerRatingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_rating_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(context).load(sellerRatingListItems.get(position).getUserImage()).into(holder.image);
        holder.name.setText(sellerRatingListItems.get(position).getUserName());
        holder.rating.setText(sellerRatingListItems.get(position).getValue());
        holder.feedback.setText(sellerRatingListItems.get(position).getFeedback());
        holder.date.setReferenceTime(sellerRatingListItems.get(position).getUpdateDate().getTime());
        holder.ratingBar.setRating(Float.parseFloat(sellerRatingListItems.get(position).getValue()));

    }

    @Override
    public int getItemCount() {
        return sellerRatingListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image;
        public TextView name;
        public TextView rating;
        public TextView feedback;
        public RelativeTimeTextView date;
        public SimpleRatingBar ratingBar;

        public ViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
            feedback = itemView.findViewById(R.id.feedback);
            date = itemView.findViewById(R.id.date);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }

    }

}
