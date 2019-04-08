package com.example.tiktalk.UI.Fragments.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerHomeActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerLoginActivity;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.HashMap;

public class SellerInboxFragment extends BaseActivity {

    FirebaseFirestore firestore;
    TextView withdrawBtn, seller_name, total_earnings;
    public static final int RequestPermissionCode = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String APP_ID = "4B0405B2-D5BD-49F5-B912-C9F7C009F374";
    Button menu_btn;
    Button cancel_btn, settings_btn;
    String myId;

    ProgressBar progressBar;
    ProgressDialog dialog;

    public String[] menuName = {"Dashboard", "Inbox", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.dashboard, R.drawable.inbox, R.drawable.notification, R.drawable.contact, R.drawable.logout};

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.fragment_seller_inbox);
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

        Bundle bundle = new Bundle();
        bundle.putString("seller","seller");
        ChannelsList_Fragment channelsList_fragment = new ChannelsList_Fragment();
        channelsList_fragment.setArguments(bundle);
        onAddFragment(R.id.seller_inbox_container, channelsList_fragment, false);

        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);
        menu_btn = findViewById(R.id.menu_btn);

        drawer_layout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        View viewinflate = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        View viewinflate_footer = getLayoutInflater().inflate(R.layout.nav_footer_main, null);

        Navigations_ItemsAdapter navigations_itemsAdapter = new Navigations_ItemsAdapter(SellerInboxFragment.this, menuName, menuicons);
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
                Intent in = new Intent(SellerInboxFragment.this, SellerSettingsActivity.class);
                startActivity(in);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(SellerInboxFragment.this, drawer_layout, null, R.string.app_name, R.string.app_name) {
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
                        Intent in = new Intent(SellerInboxFragment.this, SellerHomeActivity.class);
                        startActivity(in);
                        finish();
                        break;
                    }
                    case 2:{
                        Intent in = new Intent(SellerInboxFragment.this, SellerInboxFragment.class);
                        startActivity(in);
                        finish();
                        break;
                    }
                    case 3:{
                        /*Intent in = new Intent(SellerInboxFragment.this, SellerInboxFragment.class);
                        startActivity(in);
                        finish();*/
                        break;
                    }
                    case 4:
                        break;
                    case 5:{
                        myId = PreferenceUtils.getId(SellerInboxFragment.this);

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
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            Intent intent = new Intent(SellerInboxFragment.this, SellerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
//                                            showToast("Unable to logout!");
                                            Toast.makeText(SellerInboxFragment.this, "Unable to logout!", Toast.LENGTH_SHORT).show();
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
