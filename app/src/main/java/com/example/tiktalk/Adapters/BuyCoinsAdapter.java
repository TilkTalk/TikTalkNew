package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Model.CoinsCategory;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyCoinsAdapter extends RecyclerView.Adapter<BuyCoinsAdapter.ViewHolder> {

    private ArrayList<CoinsCategory> buyCoinsListItems;
    private Context context;
    private OnItemClickListener Listener;

    public interface OnItemClickListener {
        void onClick(int position);
    }


    public void setOnClickListener(OnItemClickListener listener) {
        //used to simulate the onItemClick of a listview
        Listener = listener;
    }

    public BuyCoinsAdapter(ArrayList<CoinsCategory> buyCoinsListItems, Context context) {
        this.buyCoinsListItems = buyCoinsListItems;
        this.context = context;
    }

    @Override
    public BuyCoinsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_coins_items, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.coins.setText(buyCoinsListItems.get(position).getCoins());
        holder.doller.setText(buyCoinsListItems.get(position).getAmount());

    }

    @Override
    public int getItemCount() {
        return buyCoinsListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView coins, doller;
        public Button buy_coins_btn;

        public ViewHolder(final View itemView) {
            super(itemView);

            coins = itemView.findViewById(R.id.coins);
            doller = itemView.findViewById(R.id.doller);
            buy_coins_btn = itemView.findViewById(R.id.buy_coins_btn);


            buy_coins_btn.setOnClickListener(new View.OnClickListener() {
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
