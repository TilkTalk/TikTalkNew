package com.example.tiktalk.UI.Fragments.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.example.tiktalk.Adapters.LastCallsAdapter;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.Model.Calls;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerHomeActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerSettingsActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class BuyerMyWalletFragment extends BaseActivity {

    FirebaseFirestore firestore;
    TextView total_coins;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    LastCallsAdapter adapter;
    ArrayList<Calls> callsArrayList;
    ImageButton menu_btn;
    String myId;

    ProgressBar progressBar;
    ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    ImageButton cancel_btn, settings_btn;

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public String[] menuName = {"Home", "My Wallet", "Inbox", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.home, R.drawable.my_wallets, R.drawable.inbox,
            R.drawable.notification, R.drawable.contact, R.drawable.logout};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.fragment_buyer_mywallet);

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
                        finish();
                        break;
                    }
                    case 2:{
                        Intent intent = new Intent(BuyerMyWalletFragment.this, BuyerMyWalletFragment.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case 3:{
                        Intent intent1 = new Intent(BuyerMyWalletFragment.this, BuyerInboxFragment.class);
                        startActivity(intent1);
                        finish();
                        break;
                    }
                    case 4:{
                        Intent in = new Intent(BuyerMyWalletFragment.this, BuyerNotificationFragment.class);
                        startActivity(in);
                        finish();
                        break;
                    }
                    case 5:{
                        Toast.makeText(BuyerMyWalletFragment.this, "Contact page is under development!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 6:

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
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

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
    }

    @Override
    public void initializeComponents() {


        firestore = FirebaseFirestore.getInstance();
        callsArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        total_coins = findViewById(R.id.total_coins);
        total_coins.setText(PreferenceUtils.getCoins(this));

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

}
