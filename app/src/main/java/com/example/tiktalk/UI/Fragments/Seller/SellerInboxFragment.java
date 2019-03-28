package com.example.tiktalk.UI.Fragments.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;

public class SellerInboxFragment extends BaseFragment {

    LinearLayout inboxLayout;
    @Override
    public void initializeComponents(View rootView) {

        inboxLayout = rootView.findViewById(R.id.inbox_layout);

    }

    @Override
    public void setupListeners(View rootView) {

        inboxLayout.setOnClickListener(new View.OnClickListener() {
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

        View view = inflater.inflate(R.layout.fragment_seller_inbox, null);
        return view;
    }
}
