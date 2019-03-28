package com.example.tiktalk.UI.Activities.Seller;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendBirdService;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Fragments.Seller.SellerHomeFragment;
import com.example.tiktalk.UI.Fragments.Seller.SellerNotificationFragment;
import com.example.tiktalk.UI.Fragments.TestingFragment;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.dialogplus.DialogPlus;
import com.sendbird.android.SendBird;
import com.sinch.android.rtc.SinchError;

import java.util.HashMap;

public class SellerDashboardActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SinchService.StartFailedListener {

    Button buyerSettingBtn;
    Button dashboardFinishBtn;
    RoundedImageView seller_profileImage;
    FirebaseFirestore firestore;
    Switch online_switch;

    private static final String APP_ID = "4B0405B2-D5BD-49F5-B912-C9F7C009F374";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        SendBird.init(APP_ID, this.getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_seller);
        View header = getLayoutInflater().inflate(R.layout.seller_navheader_dashboard, null, false);
        navigationView.addHeaderView(header);
        navigationView.setNavigationItemSelectedListener(this);

        onReplaceFragment(R.id.seller_content_frame, new SellerHomeFragment(), true);

        buyerSettingBtn = header.findViewById(R.id.buyer_setting_btn);
        dashboardFinishBtn = header.findViewById(R.id.dashboard_finish_btn);
        online_switch = findViewById(R.id.online_switch);

        buyerSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerDashboardActivity.this, SellerSettingsActivity.class);
                startActivity(in);
            }
        });

        dashboardFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
            }
        });

        setupComponents();
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

        SendBirdService.Connect(PreferenceUtils.getId(this), PreferenceUtils.getUsername(this), PreferenceUtils.getImageUrl(this));
        firestore = FirebaseFirestore.getInstance();
        seller_profileImage = findViewById(R.id.seller_profileImage);
        Glide.with(getApplicationContext()).load(PreferenceUtils.getImageUrl(getApplicationContext())).into(seller_profileImage);

    }

    @Override
    public void setupListeners() {

        online_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    //onAddFragment(R.id.testing_content_frame, new TestingFragment(), true);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(SellerDashboardActivity.this);
                    final AlertDialog alert = dialog.create();
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    final View attachFileLayout = inflater.inflate(R.layout.testing_fragment, null);
                    attachFileLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.setView(attachFileLayout);
//                    alert.show();


                    Window window = alert.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.TOP;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    window.getAttributes().windowAnimations = R.style.DialogAnimation_2; //style id
                    alert.show();

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    alert.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int displayWidth = displayMetrics.widthPixels;
                    int displayHeight = displayMetrics.heightPixels;

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

                    layoutParams.copyFrom(alert.getWindow().getAttributes());

                    int dialogWindowWidth = (int) (displayWidth * 0.999f);
                    int dialogWindowHeight = (int) (displayHeight * 0.9f);

                    layoutParams.width = dialogWindowWidth;
                    layoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT;

                    alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
                    alert.getWindow().setAttributes(layoutParams);


                    /*DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int displayWidth = displayMetrics.widthPixels;
                    int dialogWindowWidth = (int) (displayWidth *1);
                    wlp.width = dialogWindowWidth;

                    alert.getWindow().setAttributes(wlp);*/


                    /*final Dialog contacts_dialog = new Dialog(SellerDashboardActivity.this);
                    contacts_dialog.setContentView(R.layout.buy_coins_layout);

                    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    contacts_dialog.setCancelable(true);
                    contacts_dialog.setCanceledOnTouchOutside(true);
                    contacts_dialog.show();*/

                    // Create Alert using Builder
                    /*CFAlertDialog.Builder builder = new CFAlertDialog.Builder(SellerDashboardActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                            .setTitle("You've hit the limit")
                            .setMessage("Looks like you've hit your usage limit. Upgrade to our paid plan to continue without any limits.");

                    builder.show();*/

                } else {

                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.seller_dashboard, menu);
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

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
            onReplaceFragment(R.id.seller_content_frame, new SellerHomeFragment(), true);

        } else if (id == R.id.nav_inbox) {
            onReplaceFragment(R.id.seller_content_frame, new ChannelsList_Fragment(), true);

        } else if (id == R.id.nav_notifications) {
            onReplaceFragment(R.id.seller_content_frame, new SellerNotificationFragment(), true);

        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_logout) {

            final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseAuth.getInstance().signOut();
            PreferenceUtils.clearMemory(getApplicationContext());

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("isOnline", "0");

            firestore.collection("users")
                    .document(currentUser)
                    .update(map);

            Intent intent = new Intent(SellerDashboardActivity.this, SellerLoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            getSinchServiceInterface().startClient(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else {
            //AppUtils.Toast("not connected");
            //Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();

        }
    }
}
