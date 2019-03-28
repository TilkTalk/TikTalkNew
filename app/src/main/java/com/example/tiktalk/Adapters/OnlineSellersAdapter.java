package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineSellersAdapter extends RecyclerView.Adapter<OnlineSellersAdapter.ViewHolder> {

    FirebaseAuth auth;
    private ArrayList<User> onlineListItems;
    private Context context;
    private OnItemClickListener xListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        //used to simulate the onItemClick of a listview
        xListener = listener;
    }

    public OnlineSellersAdapter(ArrayList<User> onlineListItems, Context context) {
        this.onlineListItems = onlineListItems;
        this.context = context;
    }

    @Override
    public OnlineSellersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_seller_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        auth = FirebaseAuth.getInstance();

        holder.online_seller_name.setText(onlineListItems.get(position).getUsername());
        Glide.with(context).load(onlineListItems.get(position).getImageUrl()).into(holder.online_seller_image);

    }

    @Override
    public int getItemCount() {
        return onlineListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView online_seller_name;
        public CircleImageView online_seller_image;

        public ViewHolder(final View itemView) {
            super(itemView);

            online_seller_name = itemView.findViewById(R.id.online_seller_name);
            online_seller_image = itemView.findViewById(R.id.online_seller_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (xListener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            xListener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
