package com.example.tiktalk.UI.Fragments.Buyer;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerHomeActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerSettingsActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerHomeActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

import static com.example.tiktalk.TikTalk.getContext;

public class BuyerInboxFragment extends BaseActivity {

    FirebaseFirestore firestore;
    ImageButton menu_btn, buycoins_btn;
    String myId, type;

    private GoogleSignInClient mGoogleSignInClient;
    ImageButton cancel_btn, settings_btn;

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    int notificationCount = 0;
    ProgressBar progressBar;
    ProgressDialog dialog;

    public String[] menuName = {"Home", "My Wallet", "Inbox", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.home, R.drawable.my_wallets, R.drawable.inbox,
            R.drawable.notification, R.drawable.contact, R.drawable.logout};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.fragment_buyer_inbox);
        setupComponents();

        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        View viewinflate = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        View viewinflate_footer = getLayoutInflater().inflate(R.layout.nav_footer_main, null);

        Navigations_ItemsAdapter navigations_itemsAdapter = new Navigations_ItemsAdapter(BuyerInboxFragment.this, menuName, menuicons);
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
                Intent in = new Intent(BuyerInboxFragment.this, BuyerSettingsActivity.class);
                startActivity(in);
            }
        });

        RoundedImageView buyer_image = viewinflate_footer.findViewById(R.id.buyer_image);
        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(buyer_image);

        mDrawerToggle = new ActionBarDrawerToggle(BuyerInboxFragment.this, drawer_layout, null, R.string.app_name, R.string.app_name) {
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
                        Intent in = new Intent(BuyerInboxFragment.this, BuyerHomeActivity.class);
                        startActivity(in);
                        Bungee.zoom(BuyerInboxFragment.this);
                        finish();
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent(BuyerInboxFragment.this, BuyerMyWalletFragment.class);
                        startActivity(intent);
                        Bungee.zoom(BuyerInboxFragment.this);
                        finish();
                        break;
                    }
                    case 3: {
                        Intent intent1 = new Intent(BuyerInboxFragment.this, BuyerInboxFragment.class);
                        intent1.putExtra("type", "buyer");
                        startActivity(intent1);
                        Bungee.zoom(BuyerInboxFragment.this);
                        finish();
                        break;
                    }
                    case 4: {
                        Intent in = new Intent(BuyerInboxFragment.this, BuyerNotificationFragment.class);
                        startActivity(in);
                        Bungee.zoom(BuyerInboxFragment.this);
                        finish();
                        break;
                    }
                    case 5: {
                        Toast.makeText(BuyerInboxFragment.this, "Contact page is under development!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 6: {
                        dialog.setMessage("Please wait...");
                        dialog.show();
                        myId = PreferenceUtils.getId(BuyerInboxFragment.this);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("isOnline", "0");

                        firestore.collection("users")
                                .document(myId)
                                .update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            MyFirebaseInstanceIDService.deleteRegistrationFromServer(BuyerInboxFragment.this.getClass().getSimpleName(), myId);
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            dialog.dismiss();
                                            Intent intent = new Intent(BuyerInboxFragment.this, BuyerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(BuyerInboxFragment.this, "Unable to logout!", Toast.LENGTH_SHORT).show();
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

        type = getIntent().getStringExtra("type");

        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        ChannelsList_Fragment channelsList_fragment = new ChannelsList_Fragment();
        channelsList_fragment.setArguments(bundle);
        onAddFragment(R.id.activity_doctor, channelsList_fragment, false);

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
        Intent in = new Intent(BuyerInboxFragment.this, BuyerHomeActivity.class);
        startActivity(in);
        Bungee.zoom(BuyerInboxFragment.this);
        finish();
    }
}
