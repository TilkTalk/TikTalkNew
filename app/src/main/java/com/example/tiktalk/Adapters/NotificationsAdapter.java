package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Model.Notifications;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private ArrayList<Notifications> notificationListItems;
    private Context context;
    private OnClickListener Listener;

    public interface OnClickListener {
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        //used to simulate the onItemClick of a listview
        Listener = listener;
    }

    public NotificationsAdapter(ArrayList<Notifications> notificationListItems, Context context) {
        this.notificationListItems = notificationListItems;
        this.context = context;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.notification_name.setText(notificationListItems.get(position).getName());
        holder.notification_message.setText(notificationListItems.get(position).getMessage());
        Glide.with(context).load(notificationListItems.get(position).getImage()).into(holder.notification_image);

    }

    @Override
    public int getItemCount() {
        return notificationListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout notification_layout;
        public TextView notification_name;
        public TextView notification_message;
        public CircleImageView notification_image;

        public ViewHolder(final View itemView) {
            super(itemView);

            notification_layout = itemView.findViewById(R.id.notification_layout);
            notification_name = itemView.findViewById(R.id.notification_name);
            notification_message = itemView.findViewById(R.id.notification_message);
            notification_image = itemView.findViewById(R.id.notification_image);

            notification_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Listener != null) {
                        int position = getAdapterPosition();
                        //noposition to make sure the position is still valid
                        if (position != RecyclerView.NO_POSITION) {
                            //onItemclick is the method that we created on the interface
                            //UserList ul = userListItems.get(position);
                            Listener.onClick(position);
                        }
                    }
                }
            });

        }

    }

}
