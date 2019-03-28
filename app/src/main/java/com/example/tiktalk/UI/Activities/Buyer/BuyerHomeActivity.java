package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktalk.Adapters.BuyCoinsAdapter;
import com.example.tiktalk.Adapters.OnlineSellersAdapter;
import com.example.tiktalk.Adapters.TopRatedSellersAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.CoinsCategory;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendBirdService;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.AllSellerActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
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
import static com.example.tiktalk.TikTalk.getContext;

public class BuyerHomeActivity extends BaseActivity implements TopRatedSellersAdapter.OnCallClickListener, TopRatedSellersAdapter.OnChatClickListener, SinchService.StartFailedListener {

    ProgressBar progressBar;
    ProgressDialog dialog;

    TextView seeAllBtn, buyer_name;
    Button menu_btn, buycoins_btn;
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
    CoinsCategory buyCoins;

    String sellerId, SellerName, SellerImage, sellerName, coinPerMin;
    BuyCoinsAdapter adaper;

    String coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_home);

        SendBird.init(APP_ID, this.getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        setupComponents();

        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        firestore = FirebaseFirestore.getInstance();
        sellerArrayList = new ArrayList<>();
        onlineArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewNew = findViewById(R.id.recycler_view_new);
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
                        adapter = new TopRatedSellersAdapter(sellerArrayList, BuyerHomeActivity.this);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                        adapter.setOnCallClickListener(BuyerHomeActivity.this);
                        adapter.setOnChatClickListener(BuyerHomeActivity.this);

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

                        Comparator<User> c = new Comparator<User>() {

                            @Override
                            public int compare(User a, User b) {
                                return Float.compare(Float.valueOf(b.getRating()), Float.valueOf(a.getRating()));
                            }
                        };
                        Collections.sort(sellerArrayList, c);
                        adapternew = new OnlineSellersAdapter(onlineArrayList, BuyerHomeActivity.this);
                        recyclerViewNew.setAdapter(adapternew);

                    }
                });

        /*firestore.collection("users")
                .document(PreferenceUtils.getId(this))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        coins = documentSnapshot.getString("coins");
                        PreferenceUtils.saveBuyerData(documentSnapshot.getString("username"), documentSnapshot.getString("email"), documentSnapshot.getString("password"), documentSnapshot.getString("id"), documentSnapshot.getString("IsActive"), documentSnapshot.getString("Type"), documentSnapshot.getString("imageUrl"), documentSnapshot.getString("isOnline"), documentSnapshot.getString("coins"), BuyerHomeActivity.this);

                    }
                });*/


    }

    protected void get_group_channels() {
        GroupChannelListQuery channelListQuery = GroupChannel.createMyGroupChannelListQuery();
        channelListQuery.setIncludeEmpty(true);
        channelListQuery.setLimit(100);
        channelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
//                populate_group_channel_list(list);
                groupChannerls = list;
            }
        });
    }

    @Override
    public void initializeComponents() {

        SendBirdService.Connect(PreferenceUtils.getId(BuyerHomeActivity.this), PreferenceUtils.getUsername(this), PreferenceUtils.getImageUrl(this));

        seeAllBtn = findViewById(R.id.seeAll_btn);
        buyer_name = findViewById(R.id.buyer_name);
//        menu_btn = findViewById(R.id.menu_btn);
//        buycoins_btn = findViewById(R.id.buycoins_btn);
        home_layout = findViewById(R.id.home_layout);

        buyer_name.setText(PreferenceUtils.getUsername(BuyerHomeActivity.this));

        if (checkPermission()) {

        } else {
            requestPermission();
        }

    }

    @Override
    public void setupListeners() {

//        menu_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onReplaceFragment(R.id.buyer_content_frame, new BuyerMenuFragment(), true);
//            }
//        });

//        buycoins_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final AlertDialog.Builder dialog = new AlertDialog.Builder(BuyerHomeActivity.this);
//                final AlertDialog alert = dialog.create();
//                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//                final View attachFileLayout = inflater.inflate(R.layout.buy_coins_layout, null);
//                attachFileLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                alert.setView(attachFileLayout);
//
//                final RecyclerView recycler_view = attachFileLayout.findViewById(R.id.recycler_view);
//                LinearLayoutManager manager = new LinearLayoutManager(attachFileLayout.getContext());
//                final ArrayList<CoinsCategory> buyCoinsArrayList = new ArrayList<>();
//                recycler_view.setLayoutManager(manager);
//
//
//                firestore.collection("coins")
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//
//                                    CoinsCategory cc = new CoinsCategory(
//                                            documentSnapshot.getString("coins"),
//                                            documentSnapshot.getString("amount"));
//                                    buyCoinsArrayList.add(cc);
//                                }
//                                Comparator<CoinsCategory> c = new Comparator<CoinsCategory>() {
//
//                                    @Override
//                                    public int compare(CoinsCategory a, CoinsCategory b) {
//                                        return Integer.compare(Integer.valueOf(a.getCoins()), Integer.valueOf(b.getCoins()));
//                                    }
//                                };
//                                Collections.sort(buyCoinsArrayList, c);
//                                adaper = new BuyCoinsAdapter(buyCoinsArrayList, attachFileLayout.getContext());
//                                recycler_view.setAdapter(adaper);
//                                adaper.setOnClickListener(new BuyCoinsAdapter.OnCallClickListener() {
//                                    @Override
//                                    public void onClick(int position) {
//
//                                        buyCoins = buyCoinsArrayList.get(position);
//                                        final String coin = buyCoins.getCoins();
//                                        String amount = buyCoins.getAmount();
//
//                                        final AlertDialog alert;
//
//                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(BuyerHomeActivity.this);
//                                        dialog2.setTitle("Do you want to proceed?");
//                                        dialog2.setMessage("$" + amount + " would be deducted from your account.");
//
//                                        dialog2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                int totalCoins = Integer.valueOf(coins) + Integer.valueOf(coin);
//
//                                                HashMap<String, Object> coinMap = new HashMap<String, Object>();
//                                                coinMap.put("coins", String.valueOf(totalCoins));
//
//                                                firestore.collection("users")
//                                                        .document(PreferenceUtils.getId(BuyerHomeActivity.this))
//                                                        .update(coinMap);
//
//                                            }
//                                        });
//
//                                        dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//
//                                        dialog2.show();                                    }
//                                });
//                                alert.show();
//                            }
//                        });
//
//
//            }
//
//        });

        seeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(BuyerHomeActivity.this, AllSellerActivity.class);
                startActivity(in);
                Bungee.slideLeft(BuyerHomeActivity.this);
            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(BuyerHomeActivity.this, new
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
                        Toast.makeText(BuyerHomeActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BuyerHomeActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
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
        Intent callScreen = new Intent(this, BuyerCallActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("toid", sellerId);
        callScreen.putExtra("name", sellerName);
        callScreen.putExtra("imageUrl", SellerImage);
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

        directMessage();
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        loginClicked();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();

    }

    //Invoked when just after the service is connected with Sinch
    @Override
    public void onStarted() {
    }

    //Login is Clicked to manually to connect to the Sinch Service
    private void loginClicked() {


        if ((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted())) {
            getSinchServiceInterface().startClient(PreferenceUtils.getId(this));
        } else {
        }
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void directMessage(){


        GroupChannel.GroupChannelCreateHandler groupChannelCreateHandler = new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                }else {
                    final Intent chatIntent = new Intent(BuyerHomeActivity.this, SendbirdChatActivity.class);
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
                .setName(SellerName)
//                                .setCoverImage(FILE)
                .setCoverUrl(SellerImage);
//                                .setData(DATA)
//                                .setCustomType(CUSTOM_TYPE);
        GroupChannel.createChannel(params, groupChannelCreateHandler);
    }

}




