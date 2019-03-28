package com.example.tiktalk.UI.Fragments.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerSettingsActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerLoginActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

public class BuyerMenuFragment extends BaseFragment {

    RoundedImageView buyer_image;
    TextView home_textView, wallet_textView, inbox_textView, notification_textView, contact_textView, logout_textView;
    Button close_btn, settings_btn;
    FirebaseFirestore firestore;

    @Override
    public void initializeComponents(View rootView) {

        firestore = FirebaseFirestore.getInstance();
        buyer_image = rootView.findViewById(R.id.buyer_image);
        home_textView = rootView.findViewById(R.id.home_textView);
        wallet_textView = rootView.findViewById(R.id.wallet_textView);
        inbox_textView = rootView.findViewById(R.id.inbox_textView);
        notification_textView = rootView.findViewById(R.id.notification_textView);
        contact_textView = rootView.findViewById(R.id.contact_textView);
        logout_textView = rootView.findViewById(R.id.logout_textView);
        close_btn = rootView.findViewById(R.id.close_btn);
        settings_btn = rootView.findViewById(R.id.settings_btn);

        Glide.with(getContext()).load(PreferenceUtils.getImageUrl(getContext())).into(buyer_image);

    }

    @Override
    public void setupListeners(View rootView) {

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), BuyerSettingsActivity.class));
                getActivity().finish();
            }
        });

        home_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        wallet_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goBack();
                replaceFragment(R.id.buyer_frame, new BuyerMyWalletFragment(), true);
            }
        });

        inbox_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
                replaceFragment(R.id.buyer_frame, new ChannelsList_Fragment(), true);
            }
        });

        notification_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
                replaceFragment(R.id.buyer_frame, new BuyerNotificationFragment(), true);
            }
        });

        contact_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        logout_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseAuth.getInstance().signOut();
                PreferenceUtils.clearMemory(getContext());

                /*HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("isOnline", "0");

                firestore.collection("users")
                        .document(currentUser)
                        .update(map);*/

                startActivity(new Intent(getContext(), BuyerLoginActivity.class));
                getActivity().finish();

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buyer_menu, null);
        return view;
    }
}
