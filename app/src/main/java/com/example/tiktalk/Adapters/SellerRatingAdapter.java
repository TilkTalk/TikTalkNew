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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerRatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SellerRating> sellerRatingListItems;
    private ArrayList<String> firstListItems;
    private ArrayList<String> secondListItems;
    private Context context;
    TimeAgo timeAgo = new TimeAgo();
    private static final int LAYOUT_ONE= 0;
    private static final int LAYOUT_TWO= 1;
    private static final int LAYOUT_THREE= 2;
    int newPosition = 0;

    public SellerRatingAdapter(ArrayList<SellerRating> sellerRatingListItems, Context context) {
        this.firstListItems = firstListItems;
        this.sellerRatingListItems = sellerRatingListItems;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==0){
            return LAYOUT_ONE;
        }
        else {
            return LAYOUT_TWO;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =null;
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType==LAYOUT_ONE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coinspermin_layout_item,parent,false);
            viewHolder = new ViewHolderOne(view);
        }

        if(viewType==LAYOUT_TWO)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_rating_item,parent,false);
            viewHolder = new ViewHolderTwo(view);
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_rating_item, parent, false);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType()== LAYOUT_ONE)
        {
            ViewHolderOne holderOne = (ViewHolderOne) holder;
            holderOne.profileCoinsPerMin.setText(sellerRatingListItems.get(position).getCoin() + " coins per minute");
            holderOne.seller_profile_about.setText(sellerRatingListItems.get(position).getAbout());
        }

        if(holder.getItemViewType()== LAYOUT_TWO)
        {
            ViewHolderTwo holderTwo = (ViewHolderTwo) holder;
            Glide.with(context).load(sellerRatingListItems.get(position-1).getUserImage()).into(holderTwo.image);
            holderTwo.name.setText(sellerRatingListItems.get(position-1).getUserName());
            holderTwo.rating.setText(sellerRatingListItems.get(position-1).getValue());
            holderTwo.feedback.setText(sellerRatingListItems.get(position-1).getFeedback());
            holderTwo.date.setReferenceTime(sellerRatingListItems.get(position-1).getUpdateDate().getTime());
            holderTwo.ratingBar.setRating(Float.parseFloat(sellerRatingListItems.get(position-1).getValue()));
        }

    }

    @Override
    public int getItemCount() {
        return sellerRatingListItems.size();
    }

    public class ViewHolderOne extends RecyclerView.ViewHolder {

        public TextView profileCoinsPerMin;
        public TextView seller_profile_about;

        public ViewHolderOne(View itemView) {
            super(itemView);

            profileCoinsPerMin = itemView.findViewById(R.id.seller_profile_coinsPerMin);
            seller_profile_about = itemView.findViewById(R.id.seller_profile_about);
        }
    }

    public class ViewHolderTwo extends RecyclerView.ViewHolder {

        public CircleImageView image;
        public TextView name;
        public TextView rating;
        public TextView feedback;
        public RelativeTimeTextView date;
        public SimpleRatingBar ratingBar;

        public ViewHolderTwo(final View itemView) {
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
