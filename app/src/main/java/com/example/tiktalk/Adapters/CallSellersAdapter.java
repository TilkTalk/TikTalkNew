package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallSellersAdapter extends RecyclerView.Adapter<CallSellersAdapter.ViewHolder> {

    private ArrayList<User> sellerListItems;
    private Context context;
    private OnCallClickListener callListener;
    private OnChatClickListener chatListener;
    private OnImageClickListener imageListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface OnCallClickListener {
        void onCallClick(int position);
    }

    public interface OnChatClickListener {
        void onChatClick(int position);
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public void setOnCallClickListener(OnCallClickListener listener) {
        //used to simulate the onItemClick of a listview
        callListener = listener;
    }

    public void setOnChatClickListener(OnChatClickListener listener) {
        //used to simulate the onItemClick of a listview
        chatListener = listener;
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        //used to simulate the onItemClick of a listview
        imageListener = listener;
    }

    public CallSellersAdapter(ArrayList<User> sellerListItems, Context context) {
        this.sellerListItems = sellerListItems;
        this.context = context;
    }

    @Override
    public CallSellersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_call_seller, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        viewBinderHelper.bind(holder.swipe_layout, sellerListItems.get(position).getId());
        viewBinderHelper.setOpenOnlyOne(true);

        Glide.with(context).load(sellerListItems.get(position).getImageUrl()).into(holder.seller_photo);
        holder.seller_name.setText(sellerListItems.get(position).getUsername());
        holder.seller_rating.setText(sellerListItems.get(position).getRating());
        holder.rates.setText(sellerListItems.get(position).getCoinPerMin());
        holder.rating_bar.setRating(Float.parseFloat(sellerListItems.get(position).getRating()));

    }

    @Override
    public int getItemCount() {
        return sellerListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView call_btn, chat_btn;
        public CircleImageView seller_photo;
        public TextView seller_name, seller_rating, rates;
        public RatingBar rating_bar;
        public SwipeRevealLayout swipe_layout;

        public ViewHolder(final View itemView) {
            super(itemView);

            call_btn = itemView.findViewById(R.id.call_btn);
            chat_btn = itemView.findViewById(R.id.chat_btn);
            seller_photo = itemView.findViewById(R.id.seller_photo);
            seller_name = itemView.findViewById(R.id.seller_name);
            seller_rating = itemView.findViewById(R.id.seller_rating);
            rates = itemView.findViewById(R.id.rates);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            call_btn = itemView.findViewById(R.id.call_btn);
            chat_btn = itemView.findViewById(R.id.chat_btn);
            swipe_layout = itemView.findViewById(R.id.swipe_layout);

            call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            callListener.onCallClick(position);
                        }
                    }
                }
            });

            chat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            chatListener.onChatClick(position);
                        }
                    }
                }
            });

            seller_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            imageListener.onImageClick(position);
                        }
                    }
                }
            });

        }
    }
}
