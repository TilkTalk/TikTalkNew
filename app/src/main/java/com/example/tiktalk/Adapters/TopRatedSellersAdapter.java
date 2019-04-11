package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.bumptech.glide.Glide;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.TikTalk;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class TopRatedSellersAdapter extends RecyclerView.Adapter<TopRatedSellersAdapter.ViewHolder> {

    FirebaseAuth auth;
    private ArrayList<User> sellerListItems;
    private Context context;
    private OnCallClickListener xListener;
    private OnChatClickListener yListener;
    private OnImageClickListener zListener;

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
        xListener = listener;
    }

    public void setOnChatClickListener(OnChatClickListener listener) {
        //used to simulate the onItemClick of a listview
        yListener = listener;
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        //used to simulate the onItemClick of a listview
        zListener = listener;
    }

    public TopRatedSellersAdapter(ArrayList<User> sellerListItems, Context context) {
        this.sellerListItems = sellerListItems;
        this.context = context;
    }

    @Override
    public TopRatedSellersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        auth = FirebaseAuth.getInstance();

        holder.seller_name.setText(sellerListItems.get(position).getUsername());
        holder.seller_rating.setText(String.valueOf(sellerListItems.get(position).getRating()));
        Glide.with(context).load(sellerListItems.get(position).getImageUrl()).into(holder.seller_image);

        if (sellerListItems.get(position).getIsOnline().equals("1")){
            holder.call_btn.setEnabled(true);
        }
        else {
            holder.call_btn.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return sellerListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView seller_name;
        public RoundedImageView seller_image;
        public ImageButton call_btn;
        public ImageButton chat_btn;
        public TextView seller_rating;

        public ViewHolder(final View itemView) {
            super(itemView);

            seller_name = itemView.findViewById(R.id.seller_name);
            seller_image = itemView.findViewById(R.id.seller_image);
            call_btn = itemView.findViewById(R.id.call_btn);
            chat_btn = itemView.findViewById(R.id.chat_btn);
            seller_rating = itemView.findViewById(R.id.seller_rating);

            PushDownAnim.setPushDownAnimTo(call_btn)
                    .setScale(MODE_STATIC_DP, 2)
                    .setDurationPush(0)
                    .setDurationRelease(300)
                    .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                    .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

            call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (xListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            xListener.onCallClick(position);
                        }
                    }
                }
            });

            PushDownAnim.setPushDownAnimTo(chat_btn)
                    .setScale(MODE_STATIC_DP, 2)
                    .setDurationPush(0)
                    .setDurationRelease(300)
                    .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                    .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

            chat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (yListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            yListener.onChatClick(position);
                        }
                    }
                }
            });

            PushDownAnim.setPushDownAnimTo(seller_image)
                    .setScale(MODE_STATIC_DP, 2)
                    .setDurationPush(0)
                    .setDurationRelease(300)
                    .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                    .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

            seller_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (zListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            zListener.onImageClick(position);
                        }
                    }
                }
            });

        }

    }

}
