package com.example.tiktalk.UI.Activities.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.SellerRatingAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.SellerRating;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.calling.Call;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import spencerstudios.com.bungeelib.Bungee;

import static com.example.tiktalk.TikTalk.getContext;

public class SellerProfileActivity extends BaseActivity {

    FirebaseFirestore firestore;
    ImageView seller_profile_image;
    TextView profileName, profileRating, profileCoinsPerMin;
    SimpleRatingBar test;
    ImageButton CancelBtn;
    ImageButton profileChatBtn;
    ImageButton profileCallBtn;

    String sellerId, sellerName, sellerImage, sellerRating, coinPerMin;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    SellerRatingAdapter adapter;
    ArrayList<SellerRating> selleRatingArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        setupComponents();

    }

    @Override
    public void initializeComponents() {

        firestore = FirebaseFirestore.getInstance();
        selleRatingArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

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

        firestore.collection("ratings")
                .whereEqualTo("sellerId", sellerId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {

                            SellerRating sr = new SellerRating(
                                    documentSnapshot.getDocument().getString("userId"),
                                    documentSnapshot.getDocument().getString("userName"),
                                    documentSnapshot.getDocument().getString("userImage"),
                                    documentSnapshot.getDocument().getString("value"),
                                    documentSnapshot.getDocument().getString("id"),
                                    documentSnapshot.getDocument().getString("sellerId"),
                                    documentSnapshot.getDocument().getString("feedback"));

                            if (documentSnapshot.getDocument().get("updateDate") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.getDocument().get("updateDate")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                sr.updateDate = new Date(milliSeconds);
                                selleRatingArrayList.add(sr);
                            }
                        }

                        if (selleRatingArrayList != null) {

                            Comparator<SellerRating> c = new Comparator<SellerRating>() {

                                @Override
                                public int compare(SellerRating a, SellerRating b) {
                                    return Long.compare(b.getUpdateDate().getTime(), a.getUpdateDate().getTime());
                                }
                            };
                            Collections.sort(selleRatingArrayList, c);
                        }

                        adapter = new SellerRatingAdapter(selleRatingArrayList, SellerProfileActivity.this);
                        recyclerView.setAdapter(adapter);
//                        dialog.dismiss();
                    }
                });

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

        SimpleDateFormat simpleCallTime = new SimpleDateFormat("mm:ss", Locale.US);
        SimpleDateFormat CallTime = new SimpleDateFormat("mm:ss", Locale.US);

        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.US);

        int callMins = Integer.valueOf(PreferenceUtils.getCoins(SellerProfileActivity.this)) / Integer.valueOf(coinPerMin);
        int displayHours = callMins / 60;
        int displayMins = callMins % 60;
        int displaySecs = 0;
        String callEndTime = null;

        if (displayHours == 0){
            String diplayTime = displayMins + ":00";

            try {
                Date d = simpleCallTime.parse(diplayTime);
                callEndTime = String.valueOf(CallTime.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (displayHours > 0){
            String diplayTime = displayHours + ":" + displayMins + ":00";

            try {
                Date d = simpleTime.parse(diplayTime);
                callEndTime = String.valueOf(time.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (Integer.valueOf(PreferenceUtils.getCoins(this)) > Integer.valueOf(coinPerMin)){
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

            callScreen.putExtra("callMins", String.valueOf(callMins));
            callScreen.putExtra("callEndTime", callEndTime);
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
        else {
            Toast.makeText(this, "You don't have enough coins in your wallet.", Toast.LENGTH_SHORT).show();
        }
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
                    chatIntent.putExtra("seller","buyer");

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
