package com.example.tiktalk.UI.Fragments.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;

public class BuyerNotificationFragment extends BaseFragment {

    CardView notification;

    @Override
    public void initializeComponents(View rootView) {

        notification = rootView.findViewById(R.id.notification_cardview);

    }

    @Override
    public void setupListeners(View rootView) {

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getContext(), BuyerChatActivity.class);
                startActivity(in);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buyer_notification, null);
        return view;
    }
}
