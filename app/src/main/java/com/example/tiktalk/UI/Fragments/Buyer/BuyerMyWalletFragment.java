package com.example.tiktalk.UI.Fragments.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.BuyCoinsAdapter;
import com.example.tiktalk.Adapters.LastCallsAdapter;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.Model.Calls;
import com.example.tiktalk.Model.CoinsCategory;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyCoinsActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerHomeActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerSettingsActivity;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import spencerstudios.com.bungeelib.Bungee;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class BuyerMyWalletFragment extends BaseActivity {

    FirebaseFirestore firestore;
    TextView total_coins;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    LastCallsAdapter adapter;
    ArrayList<Calls> callsArrayList;
    ImageButton menu_btn;
    String myId;
    int notificationCount = 0;
    Button buycoins_btn;
    String coins;
    private SparkView sparkView;
    private RandomizedAdapter randomAdapter;

    ProgressBar progressBar;
    ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    ImageButton cancel_btn, settings_btn;

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    BuyCoinsAdapter adaper;
    CoinsCategory buyCoins;

    public String[] menuName = {"Home", "My Wallet", "Inbox", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.home, R.drawable.my_wallets, R.drawable.inbox,
            R.drawable.notification, R.drawable.contact, R.drawable.logout};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.fragment_buyer_mywallet);

        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        sparkView = findViewById(R.id.sparkview);
        randomAdapter = new RandomizedAdapter();
        sparkView.setAdapter(randomAdapter);
        sparkView.setLineWidth(7f);

        coins = getIntent().getStringExtra("coins");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        View viewinflate = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        View viewinflate_footer = getLayoutInflater().inflate(R.layout.nav_footer_main, null);

        Navigations_ItemsAdapter navigations_itemsAdapter = new Navigations_ItemsAdapter(BuyerMyWalletFragment.this, menuName, menuicons);
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
                Intent in = new Intent(BuyerMyWalletFragment.this, BuyerSettingsActivity.class);
                startActivity(in);
            }
        });

        RoundedImageView buyer_image = viewinflate_footer.findViewById(R.id.buyer_image);
        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(buyer_image);

        mDrawerToggle = new ActionBarDrawerToggle(BuyerMyWalletFragment.this, drawer_layout, null, R.string.app_name, R.string.app_name) {
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
                        Intent in = new Intent(BuyerMyWalletFragment.this, BuyerHomeActivity.class);
                        startActivity(in);
                        Bungee.zoom(BuyerMyWalletFragment.this);
                        finish();
                        break;
                    }
                    case 2:{
                        Intent intent = new Intent(BuyerMyWalletFragment.this, BuyerMyWalletFragment.class);
                        intent.putExtra("coins", coins);
                        startActivity(intent);
                        Bungee.zoom(BuyerMyWalletFragment.this);
                        finish();
                        break;
                    }
                    case 3:{
                        Intent intent1 = new Intent(BuyerMyWalletFragment.this, BuyerInboxFragment.class);
                        intent1.putExtra("type", "buyer");
                        startActivity(intent1);
                        Bungee.zoom(BuyerMyWalletFragment.this);
                        finish();
                        break;
                    }
                    case 4:{
                        Intent in = new Intent(BuyerMyWalletFragment.this, BuyerNotificationFragment.class);
                        startActivity(in);
                        Bungee.zoom(BuyerMyWalletFragment.this);
                        finish();
                        break;
                    }
                    case 5:{
                        Toast.makeText(BuyerMyWalletFragment.this, "Contact page is under development!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 6:

                        dialog.setMessage("Please wait...");
                        dialog.show();
                        myId = PreferenceUtils.getId(BuyerMyWalletFragment.this);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("isOnline", "0");

                        firestore.collection("users")
                                .document(myId)
                                .update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            MyFirebaseInstanceIDService.deleteRegistrationFromServer(BuyerMyWalletFragment.this.getClass().getSimpleName(), myId);
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            dialog.dismiss();
                                            Intent intent = new Intent(BuyerMyWalletFragment.this, BuyerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
//                                            showToast("Unable to logout!");
                                            Toast.makeText(BuyerMyWalletFragment.this, "Unable to logout!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        break;
                }
            }
        });
        setupComponents();

        firestore.collection("notifications")
                .whereEqualTo("receiver", PreferenceUtils.getId(this))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot dc : task.getResult()){
                                notificationCount++;
                            }
                        }
                    }
                });
    }

    @Override
    public void initializeComponents() {

        callsArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        total_coins = findViewById(R.id.total_coins);
        total_coins.setText(PreferenceUtils.getCoins(this));
        buycoins_btn = findViewById(R.id.buycoins_btn);

        menu_btn = findViewById(R.id.menu_btn);

    }

    @Override
    public void setupListeners() {

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(mDrawerList);
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

                final AlertDialog.Builder dialog = new AlertDialog.Builder(BuyerMyWalletFragment.this);
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

                                        Intent in = new Intent(BuyerMyWalletFragment.this, BuyCoinsActivity.class);
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

        firestore.collection("calls")
                .whereEqualTo("buyerId", PreferenceUtils.getId(BuyerMyWalletFragment.this))
                .whereEqualTo("status", "received")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            Calls ul = new Calls(
                                    documentSnapshot.getString("buyerId"),
                                    documentSnapshot.getString("buyerImage"),
                                    documentSnapshot.getString("buyerName"),
                                    documentSnapshot.getString("callDuration"),
                                    documentSnapshot.getString("dollerEarned"),
                                    documentSnapshot.getString("id"),
                                    documentSnapshot.getString("sellerId"),
                                    documentSnapshot.getString("sellerImage"),
                                    documentSnapshot.getString("sellerName"),
                                    documentSnapshot.getString("status"),
                                    documentSnapshot.getString("coinsUsed"));

                            if (documentSnapshot.get("callTime") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.get("callTime")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                ul.callTime = new Date(milliSeconds);
                                callsArrayList.add(ul);
                            }

                        }

                        if (callsArrayList != null) {

                            Comparator<Calls> c = new Comparator<Calls>() {

                                @Override
                                public int compare(Calls a, Calls b) {
                                    return Long.compare(b.getCallTime().getTime(), a.getCallTime().getTime());
                                }
                            };
                            Collections.sort(callsArrayList, c);
                        }

                        adapter = new LastCallsAdapter(callsArrayList, BuyerMyWalletFragment.this);
                        recyclerView.setAdapter(adapter);

                    }
                });

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

    @Override
    public void onBackPressed() {
        Intent in = new Intent(BuyerMyWalletFragment.this, BuyerHomeActivity.class);
        startActivity(in);
        Bungee.zoom(BuyerMyWalletFragment.this);
        finish();
    }

    public class RandomizedAdapter extends SparkAdapter {
        //private final float[] yData;
        private final ArrayList<Float> yData;
        private final Random random;

        public RandomizedAdapter() {
            random = new Random();
            yData = new ArrayList<>();
            //yData = new float[];
            randomize();
        }

        public void randomize() {
            /*for (int i = 0, count = yData.length; i < count; i++) {
                yData[i] = random.nextFloat();
            }*/

            firestore.collection("calls")
                    .whereEqualTo("buyerId", PreferenceUtils.getId(BuyerMyWalletFragment.this))
                    .whereEqualTo("status", "received")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            int i = 0;
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                yData.add(Float.valueOf(dc.getDocument().getString("coinsUsed")));
                            }

                            notifyDataSetChanged();
                        }
                    });
        }

        @Override
        public int getCount() {
            return yData.size();
        }

        @NonNull
        @Override
        public Object getItem(int position) {
            return yData.get(position);
        }

        @Override
        public float getY(int position) {
            return yData.get(position);
        }
    }
}
