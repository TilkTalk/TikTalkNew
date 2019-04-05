package com.example.tiktalk.UI.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tiktalk.Adapters.CallSellersAdapter;
import com.example.tiktalk.Adapters.OnlineSellersAdapter;
import com.example.tiktalk.Adapters.TopRatedSellersAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerProfileActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.calling.Call;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.tiktalk.TikTalk.getContext;

public class AllSellerActivity extends BaseActivity implements CallSellersAdapter.OnCallClickListener, CallSellersAdapter.OnChatClickListener, CallSellersAdapter.OnImageClickListener{

    ProgressBar progressBar;
    ProgressDialog dialog;

    FirebaseFirestore firestore;
    LinearLayout sellerProfileBtn;
    Button cancelBtn;
    ImageView callBtn, chatBtn;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    CallSellersAdapter adapter;
    ArrayList<User> callArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sellers);
        setupComponents();
    }

    @Override
    public void initializeComponents() {

        firestore = FirebaseFirestore.getInstance();
        callArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        //sellerProfileBtn = findViewById(R.id.sellerProfile_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        /*callBtn = findViewById(R.id.call_btn);
        chatBtn = findViewById(R.id.chat_btn);

        sellerProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AllSellerActivity.this, SellerProfileActivity.class);
                startActivity(in);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AllSellerActivity.this, BuyerCallActivity.class);
                startActivity(in);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AllSellerActivity.this, BuyerChatActivity.class);
                startActivity(in);
            }
        });*/

    }

    @Override
    public void setupListeners() {

        dialog.setMessage("Loading...");
        dialog.show();

        firestore.collection("users")
                .whereEqualTo("isOnline", "1")
                .whereEqualTo("Type", "Seller")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            User ul = new User(
                                    documentSnapshot.getString("username"),
                                    documentSnapshot.getString("email"),
                                    documentSnapshot.getString("password"),
                                    documentSnapshot.getString("IsActive"),
                                    documentSnapshot.getString("Type"),
                                    documentSnapshot.getString("id"),
                                    documentSnapshot.getString("imageUrl"),
                                    documentSnapshot.getString("isOnline"),
                                    documentSnapshot.getString("rating"),
                                    documentSnapshot.getString("$perMin"),
                                    documentSnapshot.getString("coinPerMin"));
                            callArrayList.add(ul);

                        }
                        adapter = new CallSellersAdapter(callArrayList, AllSellerActivity.this);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                        adapter.setOnCallClickListener(AllSellerActivity.this);
                        adapter.setOnChatClickListener(AllSellerActivity.this);
                        adapter.setOnImageClickListener(AllSellerActivity.this);

                    }
                });

    }

    @Override
    public void onCallClick(int position) {
        directCall(position);
    }

    @Override
    public void onChatClick(int position) {
        directMessage(position);
    }

    @Override
    public void onImageClick(int position) {

        directProfile(position);
    }

    public void directCall(int position){

        SimpleDateFormat simpleCallTime = new SimpleDateFormat("mm:ss", Locale.US);
        SimpleDateFormat CallTime = new SimpleDateFormat("mm:ss", Locale.US);

        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.US);

        User user = callArrayList.get(position);
        String sellerId = user.id;
        String sellerName = user.username;
        String sellerImage = user.imageUrl;
        String coinPerMin = user.coinPerMin;

        int callMins = Integer.valueOf(PreferenceUtils.getCoins(AllSellerActivity.this)) / Integer.valueOf(coinPerMin);
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
            Intent callScreen = new Intent(AllSellerActivity.this, BuyerCallActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra("toid", sellerId);
            callScreen.putExtra("name", sellerName);
            callScreen.putExtra("imageUrl", sellerImage);
            callScreen.putExtra("coinPerMin", coinPerMin);

            callScreen.putExtra("fromid", PreferenceUtils.getId(AllSellerActivity.this));
            callScreen.putExtra("BuyerName", PreferenceUtils.getUsername(AllSellerActivity.this));
            callScreen.putExtra("BuyerImage", PreferenceUtils.getImageUrl(AllSellerActivity.this));
            callScreen.putExtra("coins", PreferenceUtils.getCoins(AllSellerActivity.this));

            callScreen.putExtra("callMins", String.valueOf(callMins));
            callScreen.putExtra("callEndTime", callEndTime);
            startActivity(callScreen);

            final HashMap<String, Object> callDetails = new HashMap<String, Object>();
            callDetails.put("buyerId", PreferenceUtils.getId(AllSellerActivity.this));
            callDetails.put("buyerName", PreferenceUtils.getUsername(AllSellerActivity.this));
            callDetails.put("buyerImage", PreferenceUtils.getImageUrl(AllSellerActivity.this));
            callDetails.put("callTime", FieldValue.serverTimestamp());
            callDetails.put("status", "missed");

            firestore.collection("calls")
                    .add(callDetails);
        }
        else {
            showToast("You don't have enough coins in your wallet.");
        }
    }

    public void directMessage(int position){

        User user = callArrayList.get(position);
        final String sellerId = user.id;
        String sellerName = user.username;
        String sellerImage = user.imageUrl;
        String coinPerMin = user.coinPerMin;


        GroupChannel.GroupChannelCreateHandler groupChannelCreateHandler = new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                }else {
                    final Intent chatIntent = new Intent(AllSellerActivity.this, SendbirdChatActivity.class);
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

    private void directProfile(int position) {

        User user = callArrayList.get(position);
        String sellerId = user.id;
        String SellerName = user.username;
        String SellerImage = user.imageUrl;
        String SellerRating = user.rating;
        String coinPerMin = user.coinPerMin;

        Intent in = new Intent(AllSellerActivity.this, SellerProfileActivity.class);
        in.putExtra("sellerId", sellerId);
        in.putExtra("sellerName", SellerName);
        in.putExtra("sellerImage", SellerImage);
        in.putExtra("sellerRating", SellerRating);
        in.putExtra("coinPerMin", coinPerMin);
        startActivity(in);
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
