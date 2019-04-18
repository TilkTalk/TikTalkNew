package com.example.tiktalk.UI.Fragments.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.Adapters.NotificationsAdapter;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.Notifications;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerHomeActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerLoginActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerMyProfileActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerSettingsActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class SellerNotificationFragment extends BaseActivity implements NotificationsAdapter.OnClickListener {

    FirebaseFirestore firestore;
    ProgressBar progressBar;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    NotificationsAdapter adapter;
    ArrayList<Notifications> notificationArrayList;
    String id, username, imageUrl, rating, coinPerMin, rateperMin, about;
    private GoogleSignInClient mGoogleSignInClient;
    ImageButton menu_btn;
    ImageButton cancel_btn, settings_btn;
    String myId, type;
    int notificationCount = 0;

    public String[] menuName = {"Dashboard", "Inbox", "My Profile", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.dashboard, R.drawable.inbox, R.drawable.my_profile, R.drawable.notification, R.drawable.contact, R.drawable.logout};

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.fragment_seller_notification);
        setupComponents();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        RoundedImageView buyer_image = findViewById(R.id.buyer_image);
        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(buyer_image);
    }

    @Override
    public void initializeComponents() {

        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        id = getIntent().getStringExtra("myId");
        username = getIntent().getStringExtra("myName");
        imageUrl = getIntent().getStringExtra("myImage");
        rating = getIntent().getStringExtra("myRating");
        coinPerMin = getIntent().getStringExtra("coinPerMin");
        rateperMin = getIntent().getStringExtra("$PerMin");
        about = getIntent().getStringExtra("about");

        firestore = FirebaseFirestore.getInstance();
        notificationArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        menu_btn = findViewById(R.id.menu_btn);

        drawer_layout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        View viewinflate = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        View viewinflate_footer = getLayoutInflater().inflate(R.layout.nav_footer_main, null);

        Navigations_ItemsAdapter navigations_itemsAdapter = new Navigations_ItemsAdapter(SellerNotificationFragment.this, menuName, menuicons);
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
                Intent in = new Intent(SellerNotificationFragment.this, SellerSettingsActivity.class);
                startActivity(in);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(SellerNotificationFragment.this, drawer_layout, null, R.string.app_name, R.string.app_name) {
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
                    case 1: {
                        Intent in = new Intent(SellerNotificationFragment.this, SellerHomeActivity.class);
                        startActivity(in);
                        Bungee.zoom(SellerNotificationFragment.this);
                        finish();
                        //drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 2: {
                        Intent intent1 = new Intent(SellerNotificationFragment.this, SellerInboxFragment.class);
                        intent1.putExtra("type", "seller");
                        startActivity(intent1);
                        Bungee.zoom(SellerNotificationFragment.this);
                        finish();
                        //drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 3: {
                        Intent in = new Intent(SellerNotificationFragment.this, SellerMyProfileActivity.class);
                        in.putExtra("myId", id);
                        in.putExtra("myName", username);
                        in.putExtra("myImage", imageUrl);
                        in.putExtra("myRating", rating);
                        in.putExtra("coinPerMin", coinPerMin);
                        in.putExtra("$PerMin", rateperMin);
                        in.putExtra("about", about);
                        startActivity(in);
                        Bungee.zoom(SellerNotificationFragment.this);
                        finish();
                        //drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 4: {
                        Intent in = new Intent(SellerNotificationFragment.this, SellerNotificationFragment.class);
                        startActivity(in);
                        Bungee.zoom(SellerNotificationFragment.this);
                        finish();
                        //drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 5: {
                        break;
                    }
                    case 6: {

                        dialog.setMessage("Please wait...");
                        dialog.show();

                        myId = PreferenceUtils.getId(SellerNotificationFragment.this);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("isOnline", "0");
                        map.put("notifications", "0");

                        firestore.collection("users")
                                .document(myId)
                                .update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            MyFirebaseInstanceIDService.deleteRegistrationFromServer(SellerNotificationFragment.this.getClass().getSimpleName(), myId);
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            dialog.dismiss();
                                            Intent intent = new Intent(SellerNotificationFragment.this, SellerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SellerNotificationFragment.this, "Something went wrong. Try again later!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;
                    }
                }
            }
        });

        firestore.collection("notifications")
                .whereEqualTo("receiver", PreferenceUtils.getId(this))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot dc : task.getResult()) {
                                notificationCount++;
                            }
                        }
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

        dialog.setMessage("Loading...");
        dialog.show();

        firestore.collection("notifications")
                .whereEqualTo("receiver", PreferenceUtils.getId(this))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        notificationArrayList.clear();
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {

                            Notifications notify = new Notifications(dc.getString("sender"),
                                    dc.getString("receiver"),
                                    dc.getString("name"),
                                    dc.getString("message"),
                                    dc.getString("image"),
                                    dc.getString("status"));

                            if (dc.get("messageTime") != null) {

                                long seconds = ((Timestamp) (Object) dc.get("messageTime")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                notify.messageTime = new Date(milliSeconds);
                                notificationArrayList.add(notify);
                            }
                        }

                        if (notificationArrayList != null) {

                            Comparator<Notifications> c = new Comparator<Notifications>() {

                                @Override
                                public int compare(Notifications a, Notifications b) {
                                    return Long.compare(b.getCallTime().getTime(), a.getCallTime().getTime());
                                }
                            };
                            Collections.sort(notificationArrayList, c);
                        }

                        adapter = new NotificationsAdapter(notificationArrayList, SellerNotificationFragment.this);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                        adapter.setOnClickListener(SellerNotificationFragment.this);
                    }
                });
    }

    @Override
    public void onClick(final int position) {

        dialog.setMessage("Please wait...");
        dialog.show();

        GroupChannel.GroupChannelCreateHandler groupChannelCreateHandler = new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                } else {
                    final Intent chatIntent = new Intent(SellerNotificationFragment.this, SendbirdChatActivity.class);
                    chatIntent.putExtra("title", groupChannel.getName());
                    chatIntent.putExtra("channelUrl", groupChannel.getUrl());
                    chatIntent.putExtra("cover", groupChannel.getCoverUrl());
                    chatIntent.putExtra("members", notificationArrayList.get(position).sender);
                    chatIntent.putExtra("type", "seller");

                    dialog.dismiss();
                    startActivity(chatIntent);
                    finish();
                }
            }
        };
        List<String> users = new ArrayList<>();
        users.add(notificationArrayList.get(position).sender);

        GroupChannelParams params = new GroupChannelParams()
                .setPublic(false)
                .setEphemeral(false)
                .setDistinct(true)//If true same users can not create new channel
                .addUserIds(users)
                .setName(notificationArrayList.get(position).name)
                .setCoverUrl(notificationArrayList.get(position).image);
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
        Intent in = new Intent(SellerNotificationFragment.this, SellerHomeActivity.class);
        startActivity(in);
        Bungee.zoom(SellerNotificationFragment.this);
        finish();
    }
}
