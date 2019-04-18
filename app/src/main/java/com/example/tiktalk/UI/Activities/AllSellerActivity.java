package com.example.tiktalk.UI.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tiktalk.Adapters.BuyCoinsAdapter;
import com.example.tiktalk.Adapters.CallSellersAdapter;
import com.example.tiktalk.Adapters.OnlineSellersAdapter;
import com.example.tiktalk.Adapters.TopRatedSellersAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.CoinsCategory;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.Buyer.BuyCoinsActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerHomeActivity;
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
import com.thekhaeng.pushdownanim.PushDownAnim;

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
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class AllSellerActivity extends BaseActivity implements CallSellersAdapter.OnCallClickListener, CallSellersAdapter.OnChatClickListener, CallSellersAdapter.OnImageClickListener{

    ProgressBar progressBar;
    ProgressDialog dialog;

    FirebaseFirestore firestore;
    ImageButton cancelBtn;
    Button buycoins_btn;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    CallSellersAdapter adapter;
    ArrayList<User> callArrayList;
    CoinsCategory buyCoins;
    BuyCoinsAdapter adaper;
    String coins;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sellers);
        setupComponents();
    }

    @Override
    public void initializeComponents() {

        coins = getIntent().getStringExtra("coins");

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

        cancelBtn = findViewById(R.id.cancel_btn);
        buycoins_btn = findViewById(R.id.buycoins_btn);

    }

    @Override
    public void setupListeners() {

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AllSellerActivity.this, BuyerHomeActivity.class);
                startActivity(in);
                Bungee.slideRight(AllSellerActivity.this);
                finish();
            }
        });

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
                                    documentSnapshot.getString("coinPerMin"),
                                    documentSnapshot.getString("about"),
                                    documentSnapshot.getString("notifications"));
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

        PushDownAnim.setPushDownAnimTo(buycoins_btn)
                .setScale(MODE_STATIC_DP, 3)
                .setDurationPush(0)
                .setDurationRelease(300)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

        buycoins_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(AllSellerActivity.this);
                final AlertDialog alert = dialog.create();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                final View attachFileLayout = inflater.inflate(R.layout.buy_coins_layout, null);
                attachFileLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.setView(attachFileLayout);

                final RecyclerView recycler_view = attachFileLayout.findViewById(R.id.recycler_view);
                LinearLayoutManager manager = new LinearLayoutManager(attachFileLayout.getContext());
                final ArrayList<CoinsCategory> buyCoinsArrayList = new ArrayList<>();
                recycler_view.setLayoutManager(manager);

                firestore.collection("coins")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    CoinsCategory cc = new CoinsCategory(
                                            documentSnapshot.getString("coins"),
                                            documentSnapshot.getString("amount"));
                                    buyCoinsArrayList.add(cc);
                                }
                                Comparator<CoinsCategory> c = new Comparator<CoinsCategory>() {

                                    @Override
                                    public int compare(CoinsCategory a, CoinsCategory b) {
                                        return Integer.compare(Integer.valueOf(a.getCoins()), Integer.valueOf(b.getCoins()));
                                    }
                                };
                                Collections.sort(buyCoinsArrayList, c);
                                adaper = new BuyCoinsAdapter(buyCoinsArrayList, attachFileLayout.getContext());
                                recycler_view.setAdapter(adaper);
                                adaper.setOnClickListener(new BuyCoinsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onClick(int position) {

                                        buyCoins = buyCoinsArrayList.get(position);
                                        final String coin = buyCoins.getCoins();
                                        String amount = buyCoins.getAmount();

                                        Intent in = new Intent(AllSellerActivity.this, BuyCoinsActivity.class);
                                        in.putExtra("coin", coin);
                                        in.putExtra("amount", amount);
                                        in.putExtra("totalCoins", coins);
                                        startActivity(in);
                                    }
                                });
                                alert.show();
                            }
                        });

            }

        });

    }

    @Override
    public void onCallClick(int position) {
        directCall(position);
    }

    @Override
    public void onChatClick(int position) {

        dialog.setMessage("Please wait...");
        dialog.show();
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
            Bungee.slideLeft(AllSellerActivity.this);
            finish();

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
                    chatIntent.putExtra("type","buyer");

                    dialog.dismiss();
                    startActivity(chatIntent);
                    Bungee.slideLeft(AllSellerActivity.this);
                    finish();
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
        String about = user.about;
        String isOnline = user.isOnline;

        Intent in = new Intent(AllSellerActivity.this, SellerProfileActivity.class);
        in.putExtra("sellerId", sellerId);
        in.putExtra("sellerName", SellerName);
        in.putExtra("sellerImage", SellerImage);
        in.putExtra("sellerRating", SellerRating);
        in.putExtra("coinPerMin", coinPerMin);
        in.putExtra("about", about);
        in.putExtra("isOnline", isOnline);
        startActivity(in);
        Bungee.slideLeft(AllSellerActivity.this);
        finish();
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(AllSellerActivity.this, BuyerHomeActivity.class);
        startActivity(in);
        Bungee.slideRight(AllSellerActivity.this);
        finish();
    }
}
