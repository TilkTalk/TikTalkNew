package com.example.tiktalk.SendBird;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.TikTalk;
import com.example.tiktalk.Sinch.CallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.Utils.AppUtils;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendbirdChatActivity extends BaseActivity {
    String channelUrl, cover;
    TextView title;
    Button backBtn;
    ImageView Cover;
    String titleText, sellerId, sellerName, sellerImage, coinPerMin;
    Button voice;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendbird_chat);
        setupComponents();

    }

    @Override
    public void initializeComponents() {
        firestore = FirebaseFirestore.getInstance();
        //title = findViewById(R.id.title);
        //Cover = findViewById(R.id.cover);
        voice = findViewById(R.id.voice);

        backBtn = findViewById(R.id.back);
        if (getIntent().hasExtra("channelUrl")) {
            channelUrl = getIntent().getStringExtra("channelUrl");
            cover = getIntent().getStringExtra("cover");
            titleText = getIntent().getStringExtra("title");
            //title.setText(titleText);
            //Glide.with(this).load(cover).into(Cover);
            sellerId = getIntent().getStringExtra("sellerId");
            sellerName = getIntent().getStringExtra("SellerName");
            sellerImage = getIntent().getStringExtra("SellerImage");
            coinPerMin = getIntent().getStringExtra("coinPerMin");

        } else {
            AppUtils.Toast("Invalid Channel Url");
            finish();
        }


    }

    @Override
    public void setupListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GroupChatFragment fragment = GroupChatFragment.newInstance(channelUrl);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chatFragContainer, fragment)
                .commit();

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceCall();
            }
        });
    }

    public void voiceCall(){

//        Call call = getSinchServiceInterface().calluser(sellerId);
//        String callId = call.getCallId();
//        Intent callScreen = new Intent(this, BuyerCallActivity.class);
//        callScreen.putExtra(SinchService.CALL_ID, callId);
//        callScreen.putExtra("toid", sellerId);
//        callScreen.putExtra("name", sellerName);
//        callScreen.putExtra("fromid", PreferenceUtils.getId(this));
//        callScreen.putExtra("imageUrl", sellerImage);
//        callScreen.putExtra("coinPerMin", coinPerMin);
//        callScreen.putExtra("coins", PreferenceUtils.getCoins(this));
//        startActivity(callScreen);

        Call call = getSinchServiceInterface().calluser(sellerId);
        String callId = call.getCallId();
        Intent callScreen = new Intent(this, BuyerCallActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("toid", sellerId);
        callScreen.putExtra("name", sellerName);
        callScreen.putExtra("imageUrl", sellerImage);
        callScreen.putExtra("coinPerMin", coinPerMin);

        callScreen.putExtra("fromid", PreferenceUtils.getId(this));
        callScreen.putExtra("BuyerName", PreferenceUtils.getUsername(this));
        callScreen.putExtra("BuyerImage", PreferenceUtils.getImageUrl(this));
        callScreen.putExtra("coins", PreferenceUtils.getCoins(this));
        startActivity(callScreen);

        final HashMap<String, Object> callDetails = new HashMap<String, Object>();
        callDetails.put("buyerId", PreferenceUtils.getId(this));
        callDetails.put("buyerName", PreferenceUtils.getUsername(this));
        callDetails.put("buyerImage", PreferenceUtils.getImageUrl(this));
        callDetails.put("callTime", FieldValue.serverTimestamp());
        callDetails.put("status", "missed");

        firestore.collection("calls")
                .add(callDetails);
    }
}
