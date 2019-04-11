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
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.Notifications;
import com.example.tiktalk.R;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

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
                    case 1:{
                        Intent in = new Intent(SellerNotificationFragment.this, SellerHomeActivity.class);
                        startActivity(in);
                        drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 2:{
                        Intent intent1 = new Intent(SellerNotificationFragment.this, SellerInboxFragment.class);
                        intent1.putExtra("type", "seller");
                        startActivity(intent1);
                        drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 3:{
                        Intent in = new Intent(SellerNotificationFragment.this, SellerMyProfileActivity.class);
                        in.putExtra("myId", id);
                        in.putExtra("myName", username);
                        in.putExtra("myImage", imageUrl);
                        in.putExtra("myRating", rating);
                        in.putExtra("coinPerMin", coinPerMin);
                        in.putExtra("$PerMin", rateperMin);
                        in.putExtra("about", about);
                        startActivity(in);
                        drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 4:{
                        Intent in = new Intent(SellerNotificationFragment.this, SellerNotificationFragment.class);
                        startActivity(in);
                        finish();
                        drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 5:{
                        break;
                    }
                    case 6:{
                        myId = PreferenceUtils.getId(SellerNotificationFragment.this);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("isOnline", "0");

                        firestore.collection("users")
                                .document(myId)
                                .update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            //disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            Intent intent = new Intent(SellerNotificationFragment.this, SellerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SellerNotificationFragment.this, "Unable to logout!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        break;
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

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            final DocumentSnapshot documentSnapshot = dc.getDocument();

                            Notifications notify = new Notifications(documentSnapshot.getString("sender"),
                                    documentSnapshot.getString("receiver"),
                                    documentSnapshot.getString("name"),
                                    documentSnapshot.getString("message"),
                                    documentSnapshot.getString("image"));
                            notificationArrayList.add(notify);
                        }

                        adapter = new NotificationsAdapter(notificationArrayList, SellerNotificationFragment.this);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                        adapter.setOnClickListener(SellerNotificationFragment.this);
                    }
                });
    }

    @Override
    public void onClick(int position) {

    }
}
