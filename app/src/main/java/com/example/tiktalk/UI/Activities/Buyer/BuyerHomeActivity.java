package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.BuyCoinsAdapter;
import com.example.tiktalk.Adapters.ContactSearchDialogCompat;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.Adapters.OnlineSellersAdapter;
import com.example.tiktalk.Adapters.TopRatedSellersAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.Model.CoinsCategory;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendBirdService;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.AllSellerActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerProfileActivity;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerInboxFragment;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerMyWalletFragment;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerNotificationFragment;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.model.Charge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import spencerstudios.com.bungeelib.Bungee;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class BuyerHomeActivity extends BaseActivity implements TopRatedSellersAdapter.OnCallClickListener, TopRatedSellersAdapter.OnChatClickListener, TopRatedSellersAdapter.OnImageClickListener, OnlineSellersAdapter.OnItemClickListener, SinchService.StartFailedListener {

    ProgressBar progressBar;
    ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;

    TextView seeAllBtn, buyer_name;
    Button menu_btn, buycoins_btn;
    LinearLayout home_layout;
    EditText searchView;
    FirebaseFirestore firestore;
    Button cancel_btn, settings_btn;

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

    String sellerId, SellerName, SellerImage, sellerName, coinPerMin, SellerRating;
    BuyCoinsAdapter adaper;

    String coins, firstName, middleName, lastName, myId;

    public String[] menuName = {"Home", "My Wallet", "Inbox", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.home, R.drawable.my_wallets, R.drawable.inbox,
            R.drawable.notification, R.drawable.contact, R.drawable.logout};

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_buyer_home);
        SendBird.init(APP_ID, this.getApplicationContext());
        setupComponents();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        home_layout = findViewById(R.id.home_layout);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        View viewinflate = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        View viewinflate_footer = getLayoutInflater().inflate(R.layout.nav_footer_main, null);

        Navigations_ItemsAdapter navigations_itemsAdapter = new Navigations_ItemsAdapter(BuyerHomeActivity.this, menuName, menuicons);
        mDrawerList.setAdapter(navigations_itemsAdapter);
        mDrawerList.addHeaderView(viewinflate);
        mDrawerList.addFooterView(viewinflate_footer);

        cancel_btn = viewinflate.findViewById(R.id.cancel_btn);
        settings_btn = viewinflate.findViewById(R.id.settings_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.closeDrawer(mDrawerList);
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(BuyerHomeActivity.this, BuyerSettingsActivity.class);
                startActivity(in);
            }
        });

        RoundedImageView buyer_image = viewinflate_footer.findViewById(R.id.buyer_image);
        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(buyer_image);

        mDrawerToggle = new ActionBarDrawerToggle(BuyerHomeActivity.this, drawer_layout, null, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawer_layout.bringChildToFront(drawerView);
                drawer_layout.requestLayout();
            }

        };
        drawer_layout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 1:{
                        Intent in = new Intent(BuyerHomeActivity.this, BuyerHomeActivity.class);
                        startActivity(in);
                        finish();
                        break;
                    }
                    case 2:{
                        Intent intent = new Intent(BuyerHomeActivity.this, BuyerMyWalletFragment.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case 3:{
                        Intent intent1 = new Intent(BuyerHomeActivity.this, BuyerInboxFragment.class);
                        startActivity(intent1);
                        finish();
                        break;
                    }
                    case 4:{
                        Intent in = new Intent(BuyerHomeActivity.this, BuyerNotificationFragment.class);
                        startActivity(in);
                        finish();
                        break;
                    }
                    case 5:{
                        Toast.makeText(BuyerHomeActivity.this, "Contact page is under development!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 6:{
                        myId = PreferenceUtils.getId(BuyerHomeActivity.this);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("isOnline", "0");

                        firestore.collection("users")
                                .document(myId)
                                .update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            Intent intent = new Intent(BuyerHomeActivity.this, BuyerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            showToast("Unable to logout!");
                                        }
                                    }
                                });

                        break;
                    }
                }
            }
        });

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
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        managerNew = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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
                        adapter.setOnImageClickListener(BuyerHomeActivity.this);

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
                        adapternew.setOnClickListener(BuyerHomeActivity.this);

                    }
                });

        firestore.collection("users")
                .document(PreferenceUtils.getId(this))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        coins = documentSnapshot.getString("coins");
                        PreferenceUtils.saveBuyerData(documentSnapshot.getString("username"), documentSnapshot.getString("email"), documentSnapshot.getString("password"), documentSnapshot.getString("id"), documentSnapshot.getString("IsActive"), documentSnapshot.getString("Type"), documentSnapshot.getString("imageUrl"), documentSnapshot.getString("isOnline"), documentSnapshot.getString("coins"), BuyerHomeActivity.this);

                    }
                });
    }

    protected void get_group_channels() {
        GroupChannelListQuery channelListQuery = GroupChannel.createMyGroupChannelListQuery();
        channelListQuery.setIncludeEmpty(true);
        channelListQuery.setLimit(100);
        channelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {
                    return;
                }
                groupChannerls = list;
            }
        });
    }

    @Override
    public void initializeComponents() {

        SendBirdService.Connect(PreferenceUtils.getId(BuyerHomeActivity.this), PreferenceUtils.getUsername(this), PreferenceUtils.getImageUrl(this));

        seeAllBtn = findViewById(R.id.seeAll_btn);
        buyer_name = findViewById(R.id.buyer_name);
        menu_btn = findViewById(R.id.menu_btn);
        buycoins_btn = findViewById(R.id.buycoins_btn);
        searchView = findViewById(R.id.searchView);

        String[] fullName = PreferenceUtils.getUsername(this).split(" ");

        if (fullName.length == 1) {
            firstName = fullName[0].trim();
        }

        if (fullName.length == 2) {
            firstName = fullName[0].trim();
            lastName = fullName[1].trim();
        }

        if (fullName.length == 3) {
            firstName = fullName[0].trim();
            middleName = fullName[1].trim();
            lastName = fullName[2].trim();
        }

        buyer_name.setText(firstName);

        if (checkPermission()) {

        } else {
            requestPermission();
        }

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ContactSearchDialogCompat<>(BuyerHomeActivity.this, "Search...",
                        "What are you looking for...?", null, sellerArrayList,
                        new SearchResultListener<User>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog, User item, int position) {
//                                dialog.dismiss();
                                Intent in = new Intent(BuyerHomeActivity.this, SellerProfileActivity.class);
                                in.putExtra("sellerId", item.id);
                                in.putExtra("sellerName", item.username);
                                in.putExtra("sellerImage", item.imageUrl);
                                in.putExtra("sellerRating", item.rating);
                                in.putExtra("coinPerMin", item.coinPerMin);
                                startActivity(in);
                            }
                        }
                ).show();
            }
        });

    }

    @Override
    public void setupListeners() {

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(mDrawerList);
            }
        });

        buycoins_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(BuyerHomeActivity.this);
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

                                        Intent in = new Intent(BuyerHomeActivity.this, BuyCoinsActivity.class);
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

        seeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(BuyerHomeActivity.this, AllSellerActivity.class);
                in.putExtra("coins", coins);
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

        SimpleDateFormat simpleCallTime = new SimpleDateFormat("mm:ss", Locale.US);
        SimpleDateFormat CallTime = new SimpleDateFormat("mm:ss", Locale.US);

        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.US);

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

        int callMins = Integer.valueOf(coins) / Integer.valueOf(coinPerMin);
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

            callScreen.putExtra("callMins", String.valueOf(callMins));
            callScreen.putExtra("callEndTime", callEndTime);
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
        else {
            showToast("You don't have enough coins in your wallet.");
        }

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

        Intent in = new Intent(this, SellerProfileActivity.class);
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

        Intent in = new Intent(this, SellerProfileActivity.class);
        in.putExtra("sellerId", sellerId);
        in.putExtra("sellerName", SellerName);
        in.putExtra("sellerImage", SellerImage);
        in.putExtra("sellerRating", SellerRating);
        in.putExtra("coinPerMin", coinPerMin);
        startActivity(in);

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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void directMessage() {


        GroupChannel.GroupChannelCreateHandler groupChannelCreateHandler = new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                } else {
                    final Intent chatIntent = new Intent(BuyerHomeActivity.this, SendbirdChatActivity.class);
                    chatIntent.putExtra("title", groupChannel.getName());
                    chatIntent.putExtra("channelUrl", groupChannel.getUrl());
                    chatIntent.putExtra("cover", groupChannel.getCoverUrl());
                    chatIntent.putExtra("members", sellerId);
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
                .setName(SellerName)
//                                .setCoverImage(FILE)
                .setCoverUrl(SellerImage);
//                                .setData(DATA)
//                                .setCustomType(CUSTOM_TYPE);
        GroupChannel.createChannel(params, groupChannelCreateHandler);
    }

    private void disconnect() {
        SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
            @Override
            public void onUnregistered(SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

//                Toast.makeText(Patient_HomeActivity.this, "All push tokens unregistered.", Toast.LENGTH_SHORT)
//                        .show();

                SendBird.disconnect(new SendBird.DisconnectHandler() {
                    @Override
                    public void onDisconnected() {
//                        Intent intent = new Intent(getApplicationContext(), User_Selection_Screen.class);
//                        startActivity(intent);
//                        finish();
                    }
                });
            }
        });
    }

}




