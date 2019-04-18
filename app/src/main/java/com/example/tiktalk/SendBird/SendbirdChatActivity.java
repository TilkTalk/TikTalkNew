package com.example.tiktalk.SendBird;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.bumptech.glide.Glide;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.TikTalk;
import com.example.tiktalk.Sinch.CallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerInboxFragment;
import com.example.tiktalk.UI.Fragments.Seller.SellerInboxFragment;
import com.example.tiktalk.Utils.AppUtils;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.calling.Call;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import spencerstudios.com.bungeelib.Bungee;

public class SendbirdChatActivity extends BaseActivity {
    String channelUrl, cover;
    TextView title;
    ImageButton backBtn, voice;
    ImageView Cover;
    String titleText, sellerId, sellerName, sellerImage, coinPerMin, rating, rateperMin, about, imageUrl, username;

    FirebaseFirestore firestore;

    String type, status;
    String isOnline;

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
            sellerId = getIntent().getStringExtra("members");
            coinPerMin = getIntent().getStringExtra("coinPerMin");
            type = getIntent().getStringExtra("type");
            status = getIntent().getStringExtra("status");


        } else {
            AppUtils.Toast("Invalid Channel Url");
            finish();
        }


        if (type.equals("seller")) {
            voice.setVisibility(View.GONE);

            firestore.collection("users")
                    .document(PreferenceUtils.getId(this))
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            isOnline = documentSnapshot.getString("isOnline");
                            coinPerMin = documentSnapshot.getString("coinPerMin");
                            rating = documentSnapshot.getString("rating");
                            rateperMin = documentSnapshot.getString("$perMin");
                            about = documentSnapshot.getString("about");
                            imageUrl = documentSnapshot.getString("imageUrl");
                            username = documentSnapshot.getString("username");
                        }
                    });
        }

        if (type.equals("buyer")){
            voice.setVisibility(View.VISIBLE);

            firestore.collection("users")
                    .document(sellerId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            isOnline = documentSnapshot.getString("isOnline");
                            coinPerMin = documentSnapshot.getString("coinPerMin");
                            rating = documentSnapshot.getString("rating");
                            rateperMin = documentSnapshot.getString("$perMin");
                            about = documentSnapshot.getString("about");
                            imageUrl = documentSnapshot.getString("imageUrl");
                            username = documentSnapshot.getString("username");
                        }
                    });
        }

        final CollectionReference notificationRef = firestore.collection("notifications");
        notificationRef.whereEqualTo("receiver", PreferenceUtils.getId(this))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("status", "read");
                                notificationRef.document(document.getId())
                                        .set(map, SetOptions.merge());

                            }
                        }
                    }
                });
    }

    @Override
    public void setupListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("seller")) {
                    Intent in = new Intent(SendbirdChatActivity.this, SellerInboxFragment.class);
                    in.putExtra("type", "seller");
                    startActivity(in);
                    Bungee.slideDown(SendbirdChatActivity.this);
                    finish();
                }

                if (type.equals("buyer")){
                    Intent in = new Intent(SendbirdChatActivity.this, BuyerInboxFragment.class);
                    in.putExtra("type", "buyer");
                    startActivity(in);
                    Bungee.slideDown(SendbirdChatActivity.this);
                    finish();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("name",titleText);
        bundle.putString("image",cover);
        bundle.putString("id",sellerId);
        bundle.putString("channel",channelUrl);
        bundle.putString("status",status);

        GroupChatFragment fragment = new GroupChatFragment();
        fragment.setArguments(bundle);
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

        SimpleDateFormat simpleCallTime = new SimpleDateFormat("mm:ss", Locale.US);
        SimpleDateFormat CallTime = new SimpleDateFormat("mm:ss", Locale.US);

        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.US);

        if (isOnline.equals("1")){
            if (coinPerMin.equals("free")) {
                Call call = getSinchServiceInterface().calluser(sellerId);
                String callId = call.getCallId();
                Intent callScreen = new Intent(this, BuyerCallActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                callScreen.putExtra("toid", sellerId);
                callScreen.putExtra("name", titleText);
                callScreen.putExtra("imageUrl", cover);
                callScreen.putExtra("coinPerMin", coinPerMin);

                callScreen.putExtra("fromid", PreferenceUtils.getId(this));
                callScreen.putExtra("BuyerName", PreferenceUtils.getUsername(this));
                callScreen.putExtra("BuyerImage", PreferenceUtils.getImageUrl(this));
                callScreen.putExtra("coins", PreferenceUtils.getCoins(this));

                startActivity(callScreen);
                Bungee.slideLeft(this);

                final HashMap<String, Object> callDetails = new HashMap<String, Object>();
                callDetails.put("buyerId", PreferenceUtils.getId(this));
                callDetails.put("buyerName", PreferenceUtils.getUsername(this));
                callDetails.put("buyerImage", PreferenceUtils.getImageUrl(this));
                callDetails.put("callTime", FieldValue.serverTimestamp());
                callDetails.put("status", "missed");

                firestore.collection("calls")
                        .add(callDetails);
            } else {

                int callMins = Integer.valueOf(PreferenceUtils.getCoins(SendbirdChatActivity.this)) / Integer.valueOf(coinPerMin);
                int displayHours = callMins / 60;
                int displayMins = callMins % 60;
                int displaySecs = 0;
                String callEndTime = null;

                if (displayHours == 0) {
                    String diplayTime = displayMins + ":00";

                    try {
                        Date d = simpleCallTime.parse(diplayTime);
                        callEndTime = String.valueOf(CallTime.format(d));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (displayHours > 0) {
                    String diplayTime = displayHours + ":" + displayMins + ":00";

                    try {
                        Date d = simpleTime.parse(diplayTime);
                        callEndTime = String.valueOf(time.format(d));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                if (Integer.valueOf(PreferenceUtils.getCoins(this)) > Integer.valueOf(coinPerMin)) {
                    Call call = getSinchServiceInterface().calluser(sellerId);
                    String callId = call.getCallId();
                    Intent callScreen = new Intent(this, BuyerCallActivity.class);
                    callScreen.putExtra(SinchService.CALL_ID, callId);
                    callScreen.putExtra("toid", sellerId);
                    callScreen.putExtra("name", titleText);
                    callScreen.putExtra("imageUrl", cover);
                    callScreen.putExtra("coinPerMin", coinPerMin);

                    callScreen.putExtra("fromid", PreferenceUtils.getId(this));
                    callScreen.putExtra("BuyerName", PreferenceUtils.getUsername(this));
                    callScreen.putExtra("BuyerImage", PreferenceUtils.getImageUrl(this));
                    callScreen.putExtra("coins", PreferenceUtils.getCoins(this));

                    callScreen.putExtra("callMins", String.valueOf(callMins));
                    callScreen.putExtra("callEndTime", callEndTime);
                    startActivity(callScreen);
                    Bungee.slideLeft(this);

                    final HashMap<String, Object> callDetails = new HashMap<String, Object>();
                    callDetails.put("buyerId", PreferenceUtils.getId(this));
                    callDetails.put("buyerName", PreferenceUtils.getUsername(this));
                    callDetails.put("buyerImage", PreferenceUtils.getImageUrl(this));
                    callDetails.put("callTime", FieldValue.serverTimestamp());
                    callDetails.put("status", "missed");

                    firestore.collection("calls")
                            .add(callDetails);
                } else {
//                showToast("You don't have enough coins in your wallet.");
                    Flashbar flashbar = new Flashbar.Builder(SendbirdChatActivity.this)
                            .gravity(Flashbar.Gravity.BOTTOM)
                            .duration(1000)
                            .message("You don't have enough coins in your wallet.")
                            .backgroundColorRes(R.color.colorPrimaryDark)
                            .showOverlay()
                            .build();
                    flashbar.show();
                }
            }
        }
        else {
            Flashbar flashbar = new Flashbar.Builder(SendbirdChatActivity.this)
                    .gravity(Flashbar.Gravity.BOTTOM)
                    .duration(1000)
                    .message("This user is not online right now.")
                    .backgroundColorRes(R.color.colorPrimaryDark)
                    .showOverlay()
                    .build();
            flashbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (type.equals("seller")) {
            Intent in = new Intent(SendbirdChatActivity.this, SellerInboxFragment.class);
            in.putExtra("type", "seller");
            in.putExtra("myId", PreferenceUtils.getId(this));
            in.putExtra("myName", username);
            in.putExtra("myImage", imageUrl);
            in.putExtra("myRating", rating);
            in.putExtra("coinPerMin", coinPerMin);
            in.putExtra("$PerMin", rateperMin);
            in.putExtra("about", about);
            startActivity(in);
            Bungee.slideDown(SendbirdChatActivity.this);
            finish();
        }

        if (type.equals("buyer")){
            Intent in = new Intent(SendbirdChatActivity.this, BuyerInboxFragment.class);
            in.putExtra("type", "buyer");
            startActivity(in);
            Bungee.slideDown(SendbirdChatActivity.this);
            finish();
        }
    }
}
