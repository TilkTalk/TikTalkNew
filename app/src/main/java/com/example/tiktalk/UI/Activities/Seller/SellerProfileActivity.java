package com.example.tiktalk.UI.Activities.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

import static com.example.tiktalk.TikTalk.getContext;

public class SellerProfileActivity extends BaseActivity {

    FirebaseFirestore firestore;
    ImageView seller_profile_image;
    TextView profileName, profileRating, profileCoinsPerMin;
    RatingBar sellerRatingBar;
    SimpleRatingBar test;
    Button CancelBtn;
    Button profileChatBtn;
    Button profileCallBtn;

    String sellerId, sellerName, sellerImage, sellerRating, coinPerMin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        setupComponents();

    }

    @Override
    public void initializeComponents() {

        firestore = FirebaseFirestore.getInstance();

        seller_profile_image = findViewById(R.id.seller_profile_image);
        profileName = findViewById(R.id.seller_profile_name);
        profileRating = findViewById(R.id.seller_profile_rating);
        profileCoinsPerMin = findViewById(R.id.seller_profile_coinsPerMin);
//        sellerRatingBar = findViewById(R.id.seller_profile_ratingBar);
        test = findViewById(R.id.test);

        CancelBtn = findViewById(R.id.profile_cancel_btn);
        profileChatBtn = findViewById(R.id.profile_chat_btn);
        profileCallBtn = findViewById(R.id.profile_call_btn);

        sellerId = getIntent().getStringExtra("sellerId");
        sellerName = getIntent().getStringExtra("sellerName");
        sellerImage = getIntent().getStringExtra("sellerImage");
        sellerRating = getIntent().getStringExtra("sellerRating");
        coinPerMin = getIntent().getStringExtra("coinPerMin");

        profileName.setText(sellerName);
        Glide.with(this).load(sellerImage).into(seller_profile_image);
        profileRating.setText(sellerRating);
        profileCoinsPerMin.setText(coinPerMin + " coins per minute");
//        sellerRatingBar.setRating(Float.parseFloat(sellerRating));
        test.setRating(Float.parseFloat(sellerRating));

    }

    @Override
    public void setupListeners() {

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                directCall();
            }
        });

        profileChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                directMessage();
            }
        });

    }

    public void directCall(){

        Call call = getSinchServiceInterface().calluser(sellerId);
        String callId = call.getCallId();
        Intent callScreen = new Intent(SellerProfileActivity.this, BuyerCallActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("toid", sellerId);
        callScreen.putExtra("name", sellerName);
        callScreen.putExtra("imageUrl", sellerImage);
        callScreen.putExtra("coinPerMin", coinPerMin);

        callScreen.putExtra("fromid", PreferenceUtils.getId(SellerProfileActivity.this));
        callScreen.putExtra("BuyerName", PreferenceUtils.getUsername(SellerProfileActivity.this));
        callScreen.putExtra("BuyerImage", PreferenceUtils.getImageUrl(SellerProfileActivity.this));
        callScreen.putExtra("coins", PreferenceUtils.getCoins(SellerProfileActivity.this));
        startActivity(callScreen);

        final HashMap<String, Object> callDetails = new HashMap<String, Object>();
        callDetails.put("buyerId", PreferenceUtils.getId(SellerProfileActivity.this));
        callDetails.put("buyerName", PreferenceUtils.getUsername(SellerProfileActivity.this));
        callDetails.put("buyerImage", PreferenceUtils.getImageUrl(SellerProfileActivity.this));
        callDetails.put("callTime", FieldValue.serverTimestamp());
        callDetails.put("status", "missed");

        firestore.collection("calls")
                .add(callDetails);
    }

    public void directMessage(){


        GroupChannel.GroupChannelCreateHandler groupChannelCreateHandler = new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                }else {
                    final Intent chatIntent = new Intent(SellerProfileActivity.this, SendbirdChatActivity.class);
                    chatIntent.putExtra("title", groupChannel.getName());
                    chatIntent.putExtra("channelUrl", groupChannel.getUrl());
                    chatIntent.putExtra("cover",groupChannel.getCoverUrl());
                    chatIntent.putExtra("members",sellerId);

                    startActivity(chatIntent);
                }
            }
        };
        List<String> users = new ArrayList<>();
        users.add(sellerId);

        GroupChannelParams params = new GroupChannelParams()
                .setPublic(false)
                .setEphemeral(false)
                .setDistinct(true)//If true same users can not create new channel
                .addUserIds(users)
                .setName(sellerName)
//                                .setCoverImage(FILE)
                .setCoverUrl(sellerImage);
//                                .setData(DATA)
//                                .setCustomType(CUSTOM_TYPE);
        GroupChannel.createChannel(params, groupChannelCreateHandler);
    }
}
