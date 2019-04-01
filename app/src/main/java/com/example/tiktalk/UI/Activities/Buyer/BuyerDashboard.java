package com.example.tiktalk.UI.Activities.Buyer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.BuyCoinsAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.Model.CoinsCategory;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerInboxFragment;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerMenuFragment;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerMyWalletFragment;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerNotificationFragment;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerHome;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.annotation.Nullable;

public class BuyerDashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RoundedImageView buyer_image;
    FirebaseFirestore firestore;
    Button buyCoinsBtn;
    Button buyerSettingBtn;
    Button dashboardFinishBtn;

    BuyCoinsAdapter adaper;
    CoinsCategory buyCoins;
    String coins;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_buyer_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firestore = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        buyer_image = findViewById(R.id.buyer_image);
        Glide.with(BuyerDashboard.this).load(PreferenceUtils.getImageUrl(BuyerDashboard.this)).into(buyer_image);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_buyer);
        View header = getLayoutInflater().inflate(R.layout.buyer_navheader_dashboard, null, false);
        navigationView.addHeaderView(header);
        navigationView.setNavigationItemSelectedListener(this);

        onReplaceFragment(R.id.buyer_content_frame, new BuyerHome(), false);

        buyCoinsBtn = findViewById(R.id.buycoins_btn);
        buyerSettingBtn = header.findViewById(R.id.buyer_setting_btn);
        dashboardFinishBtn = header.findViewById(R.id.dashboard_finish_btn);

        firestore.collection("users")
                .document(PreferenceUtils.getId(this))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        coins = documentSnapshot.getString("coins");
                        PreferenceUtils.saveBuyerData(documentSnapshot.getString("username"), documentSnapshot.getString("email"), documentSnapshot.getString("password"), documentSnapshot.getString("id"), documentSnapshot.getString("IsActive"), documentSnapshot.getString("Type"), documentSnapshot.getString("imageUrl"), documentSnapshot.getString("isOnline"), documentSnapshot.getString("coins"), BuyerDashboard.this);

                    }
                });

        buyCoinsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(BuyerDashboard.this);
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

                                        final AlertDialog alert;

                                        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(BuyerDashboard.this);
                                        dialog2.setTitle("Do you want to proceed?");
                                        dialog2.setMessage("$" + amount + " would be deducted from your account.");

                                        dialog2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                int totalCoins = Integer.valueOf(coins) + Integer.valueOf(coin);

                                                HashMap<String, Object> coinMap = new HashMap<String, Object>();
                                                coinMap.put("coins", String.valueOf(totalCoins));

                                                firestore.collection("users")
                                                        .document(PreferenceUtils.getId(BuyerDashboard.this))
                                                        .update(coinMap);

                                            }
                                        });

                                        dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog2.show();
                                    }
                                });
                                alert.show();
                            }
                        });


            }

        });

        buyerSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(BuyerDashboard.this, BuyerSettingsActivity.class);
                startActivity(in);
            }
        });

        dashboardFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void setupListeners() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            onReplaceFragment(R.id.buyer_content_frame, new BuyerHome(), false);
        } else if (id == R.id.nav_mywallet) {
            onReplaceFragment(R.id.buyer_content_frame, new BuyerMyWalletFragment(), false);

        } else if (id == R.id.nav_inbox) {
            onReplaceFragment(R.id.buyer_content_frame, new ChannelsList_Fragment(), false);

        } else if (id == R.id.nav_notifications) {
            onReplaceFragment(R.id.buyer_content_frame, new BuyerNotificationFragment(), false);

        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_logout) {

            //final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("isOnline", "0");

            firestore.collection("users")
                    .document(PreferenceUtils.getId(BuyerDashboard.this))
                    .update(map);

            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut();
            LoginManager.getInstance().logOut();
            PreferenceUtils.clearMemory(getApplicationContext());

            Intent intent = new Intent(BuyerDashboard.this, BuyerLoginActivity.class);
            startActivity(intent);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
