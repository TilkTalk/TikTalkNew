package com.example.tiktalk.UI.Fragments.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktalk.Adapters.OnlineSellersAdapter;
import com.example.tiktalk.Adapters.TopRatedSellersAdapter;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendBirdService;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.AllSellerActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
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
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class BuyerHome extends BaseFragment implements TopRatedSellersAdapter.OnCallClickListener, TopRatedSellersAdapter.OnChatClickListener, TopRatedSellersAdapter.OnImageClickListener, OnlineSellersAdapter.OnItemClickListener, SinchService.StartFailedListener {

    ProgressBar progressBar;
    ProgressDialog dialog;

    TextView seeAllBtn, buyer_name;
    LinearLayout home_layout;
    FirebaseFirestore firestore;

    RecyclerView recyclerView, recyclerViewNew;
    LinearLayoutManager manager;
    LinearLayoutManager managerNew;
    TopRatedSellersAdapter adapter;
    OnlineSellersAdapter adapternew;
    ArrayList<User> sellerArrayList;
    ArrayList<User> onlineArrayList;
    private List<GroupChannel> groupChannerls;

    public static final int RequestPermissionCode = 1;

    private static final String APP_ID = "4B0405B2-D5BD-49F5-B912-C9F7C009F374";

    User user;

    String sellerId, SellerName, SellerImage, sellerName, coinPerMin, SellerRating;

    @Override
    public void initializeComponents(View rootView) {

        SendBird.init(APP_ID, getContext());
        SendBirdService.Connect(PreferenceUtils.getId(getContext()), PreferenceUtils.getUsername(getContext()), PreferenceUtils.getImageUrl(getContext()));

        seeAllBtn = rootView.findViewById(R.id.seeAll_btn);
        buyer_name = rootView.findViewById(R.id.buyer_name);
        home_layout = rootView.findViewById(R.id.home_layout);

        buyer_name.setText(PreferenceUtils.getUsername(getContext()));

        if (checkPermission()) {

        } else {
            requestPermission();
        }

        dialog = new ProgressDialog(getContext());
        progressBar = rootView.findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        firestore = FirebaseFirestore.getInstance();
        sellerArrayList = new ArrayList<>();
        onlineArrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerViewNew = rootView.findViewById(R.id.recycler_view_new);
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        managerNew = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerViewNew.setLayoutManager(managerNew);

        dialog.setMessage("Loading...");
        dialog.show();

        firestore.collection("users")
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
                            sellerArrayList.add(ul);

                        }

                        Comparator<User> c = new Comparator<User>() {

                            @Override
                            public int compare(User a, User b) {
                                return Float.compare(Float.valueOf(b.getRating()), Float.valueOf(a.getRating()));
                            }
                        };
                        Collections.sort(sellerArrayList, c);
                        adapter = new TopRatedSellersAdapter(sellerArrayList, getContext());
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                        adapter.setOnCallClickListener(BuyerHome.this);
                        adapter.setOnChatClickListener(BuyerHome.this);
                        adapter.setOnImageClickListener(BuyerHome.this);

                    }
                });

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
                            onlineArrayList.add(ul);

                        }

                        /*Comparator<User> c = new Comparator<User>() {

                            @Override
                            public int compare(User a, User b) {
                                return Float.compare(Float.valueOf(b.getRating()), Float.valueOf(a.getRating()));
                            }
                        };
                        Collections.sort(sellerArrayList, c);*/
                        adapternew = new OnlineSellersAdapter(onlineArrayList, getContext());
                        recyclerViewNew.setAdapter(adapternew);
                        adapternew.setOnClickListener(BuyerHome.this);

                    }
                });

    }

    @Override
    public void setupListeners(View rootView) {

        seeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getContext(), AllSellerActivity.class);
                startActivity(in);
                Bungee.slideLeft(getContext());
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_buyer_home, null);
        return view;
    }

    @Override
    public void onCallClick(int position) {

        Comparator<User> c = new Comparator<User>() {

            @Override
            public int compare(User a, User b) {
                return Float.compare(Float.valueOf(b.getRating()), Float.valueOf(a.getRating()));
            }
        };
        Collections.sort(sellerArrayList, c);

        user = sellerArrayList.get(position);
        sellerId = user.getId();
        sellerName = user.getUsername();
        coinPerMin = user.getCoinPerMin();
        SellerImage = user.getImageUrl();

        Call call = getSinchServiceInterface().calluser(sellerId);
        String callId = call.getCallId();
        Intent callScreen = new Intent(getContext(), BuyerCallActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("toid", sellerId);
        callScreen.putExtra("name", sellerName);
        callScreen.putExtra("imageUrl", SellerImage);
        callScreen.putExtra("coinPerMin", coinPerMin);

        callScreen.putExtra("fromid", PreferenceUtils.getId(getContext()));
        callScreen.putExtra("BuyerName", PreferenceUtils.getUsername(getContext()));
        callScreen.putExtra("BuyerImage", PreferenceUtils.getImageUrl(getContext()));
        callScreen.putExtra("coins", PreferenceUtils.getCoins(getContext()));
        startActivity(callScreen);

        final HashMap<String, Object> callDetails = new HashMap<String, Object>();
        callDetails.put("buyerId", PreferenceUtils.getId(getContext()));
        callDetails.put("buyerName", PreferenceUtils.getUsername(getContext()));
        callDetails.put("buyerImage", PreferenceUtils.getImageUrl(getContext()));
        callDetails.put("callTime", FieldValue.serverTimestamp());
        callDetails.put("status", "missed");

        firestore.collection("calls")
                .add(callDetails);

    }

    @Override
    public void onChatClick(int position) {

        Comparator<User> c = new Comparator<User>() {

            @Override
            public int compare(User a, User b) {
                return Float.compare(Float.valueOf(b.getRating()), Float.valueOf(a.getRating()));
            }
        };
        Collections.sort(sellerArrayList, c);

        user = sellerArrayList.get(position);
        sellerId = user.id;
        SellerName=user.username;
        SellerImage=user.imageUrl;
        coinPerMin = user.coinPerMin;

        directMessage();

    }

    @Override
    public void onImageClick(int position) {

        Comparator<User> c = new Comparator<User>() {

            @Override
            public int compare(User a, User b) {
                return Float.compare(Float.valueOf(b.getRating()), Float.valueOf(a.getRating()));
            }
        };
        Collections.sort(sellerArrayList, c);

        user = sellerArrayList.get(position);
        sellerId = user.id;
        SellerName = user.username;
        SellerImage = user.imageUrl;
        SellerRating = user.rating;
        coinPerMin = user.coinPerMin;

        Intent in = new Intent(getContext(), SellerProfileActivity.class);
        in.putExtra("sellerId", sellerId);
        in.putExtra("sellerName", SellerName);
        in.putExtra("sellerImage", SellerImage);
        in.putExtra("sellerRating", SellerRating);
        in.putExtra("coinPerMin", coinPerMin);
        startActivity(in);
    }

    @Override
    public void onItemClick(int position) {

        User user = onlineArrayList.get(position);
        String sellerId = user.id;
        String SellerName = user.username;
        String SellerImage = user.imageUrl;
        String SellerRating = user.rating;
        String coinPerMin = user.coinPerMin;

        Intent in = new Intent(getContext(), SellerProfileActivity.class);
        in.putExtra("sellerId", sellerId);
        in.putExtra("sellerName", SellerName);
        in.putExtra("sellerImage", SellerImage);
        in.putExtra("sellerRating", SellerRating);
        in.putExtra("coinPerMin", coinPerMin);
        startActivity(in);

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        loginClicked();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

    }

    //Invoked when just after the service is connected with Sinch
    @Override
    public void onStarted() {
    }

    //Login is Clicked to manually to connect to the Sinch Service
    private void loginClicked() {


        if ((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted())) {
            getSinchServiceInterface().startClient(PreferenceUtils.getId(getContext()));
        } else {
        }
    }

    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void directMessage(){


        GroupChannel.GroupChannelCreateHandler groupChannelCreateHandler = new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                }else {
                    final Intent chatIntent = new Intent(getContext(), SendbirdChatActivity.class);
                    chatIntent.putExtra("title", groupChannel.getName());
                    chatIntent.putExtra("channelUrl", groupChannel.getUrl());
                    chatIntent.putExtra("cover",groupChannel.getCoverUrl());
                    chatIntent.putExtra("sellerId", sellerId);
                    chatIntent.putExtra("SellerName", SellerName);
                    chatIntent.putExtra("SellerImage", SellerImage);
                    chatIntent.putExtra("coinPerMin", coinPerMin);

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
                .setName(SellerName)
//                                .setCoverImage(FILE)
                .setCoverUrl(SellerImage);
//                                .setData(DATA)
//                                .setCustomType(CUSTOM_TYPE);
        GroupChannel.createChannel(params, groupChannelCreateHandler);
    }

}
