package com.example.tiktalk.UI.Activities.Seller;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.flashbar.Flashbar;
import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.CallHistoryAdapter;
import com.example.tiktalk.Adapters.Navigations_ItemsAdapter;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.AppServices.TimerService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.MessageModule.ChannelsList_Fragment;
import com.example.tiktalk.Model.Calls;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.SendBirdService;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.WithdrawActivity;
import com.example.tiktalk.UI.Fragments.Buyer.BuyerInboxFragment;
import com.example.tiktalk.UI.Fragments.Seller.SellerInboxFragment;
import com.example.tiktalk.UI.Fragments.Seller.SellerNotificationFragment;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.makeramen.roundedimageview.RoundedImageView;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;
import com.robinhood.spark.animation.LineSparkAnimator;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sinch.android.rtc.SinchError;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;

import javax.annotation.Nullable;

import spencerstudios.com.bungeelib.Bungee;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class SellerHomeActivity extends BaseActivity implements SinchService.StartFailedListener {

    FirebaseFirestore firestore;
    TextView withdrawBtn, seller_name, total_earnings;
    public static final int RequestPermissionCode = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String APP_ID = "4B0405B2-D5BD-49F5-B912-C9F7C009F374";
    boolean doubleBackToExitPressedOnce = false;
    private SparkView sparkView;
    private RandomizedAdapter randomAdapter;

    ProgressBar progressBar;
    ProgressDialog dialog;

    public String[] menuName = {"Dashboard", "Inbox", "My Profile", "Notifications", "Contact", "Logout"};
    public int[] menuicons = {R.drawable.dashboard, R.drawable.inbox, R.drawable.my_profile, R.drawable.notification, R.drawable.contact, R.drawable.logout};

    public DrawerLayout drawer_layout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    CallHistoryAdapter adapter;
    ArrayList<Calls> historyArrayList;

    LinearLayout home_layout;
    Switch online_switch;
    ImageButton cancel_btn, settings_btn;
    Button menu_btn;
    String myId;
    TextView onlineTime;

    String date_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
    String selectedValue = "Offline";
    String text, rateperMin, isActive, type, coinPerMin, totalEarnings, email, id, imageUrl, isOnline, password, rating, token, updateDate, username, about, notifications;
    String firstName, middleName, lastName;
    int notificationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_seller_home);
        SendBird.init(APP_ID, this);
        setupComponents();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        RoundedImageView buyer_image = findViewById(R.id.buyer_image);
        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(buyer_image);

        mpref = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mpref.edit();
        text = mpref.getString("mins", "");

        if (text.equals("")) {
            online_switch.setChecked(false);
            onlineTime.setVisibility(View.GONE);
        } else {
            online_switch.setChecked(true);
            onlineTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initializeComponents() {

        SendBirdService.Connect(PreferenceUtils.getId(this), PreferenceUtils.getUsername(this), PreferenceUtils.getImageUrl(this));

        firestore = FirebaseFirestore.getInstance();
        historyArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        withdrawBtn = findViewById(R.id.withdraw_btn);
        seller_name = findViewById(R.id.seller_name);
        total_earnings = findViewById(R.id.total_earnings);
        menu_btn = findViewById(R.id.menu_btn);
        online_switch = findViewById(R.id.online_switch);
        onlineTime = (TextView) findViewById(R.id.online_time);
        sparkView = findViewById(R.id.sparkview);
        randomAdapter = new RandomizedAdapter();
        sparkView.setAdapter(randomAdapter);
//        sparkView.setFillType(SparkView.FillType.DOWN);
        sparkView.setLineWidth(7f);

        home_layout = findViewById(R.id.home_layout);
        drawer_layout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        View viewinflate = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        View viewinflate_footer = getLayoutInflater().inflate(R.layout.nav_footer_main, null);

        Navigations_ItemsAdapter navigations_itemsAdapter = new Navigations_ItemsAdapter(SellerHomeActivity.this, menuName, menuicons);
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

        PushDownAnim.setPushDownAnimTo(settings_btn)
                .setScale(MODE_STATIC_DP, 2)
                .setDurationPush(0)
                .setDurationRelease(300)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerHomeActivity.this, SellerSettingsActivity.class);
                startActivity(in);
                Bungee.slideLeft(SellerHomeActivity.this);
                finish();
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(SellerHomeActivity.this, drawer_layout, null, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawer_layout.bringChildToFront(drawerView);
                drawer_layout.requestLayout();
//                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
//                drawerView.startAnimation(animFadeIn);
//                drawer_layout.openDrawer(drawerView);
            }

        };
        drawer_layout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 1: {
                        Intent in = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
                        startActivity(in);
                        Bungee.zoom(SellerHomeActivity.this);
                        finish();
                        break;
                    }
                    case 2: {
                        Intent in = new Intent(SellerHomeActivity.this, SellerInboxFragment.class);
                        in.putExtra("type", "seller");
                        in.putExtra("myId", id);
                        in.putExtra("myName", username);
                        in.putExtra("myImage", imageUrl);
                        in.putExtra("myRating", rating);
                        in.putExtra("coinPerMin", coinPerMin);
                        in.putExtra("$PerMin", rateperMin);
                        in.putExtra("about", about);
                        startActivity(in);
                        Bungee.zoom(SellerHomeActivity.this);
                        finish();
                        //drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 3: {
                        Intent in = new Intent(SellerHomeActivity.this, SellerMyProfileActivity.class);
                        in.putExtra("myId", id);
                        in.putExtra("myName", username);
                        in.putExtra("myImage", imageUrl);
                        in.putExtra("myRating", rating);
                        in.putExtra("coinPerMin", coinPerMin);
                        in.putExtra("$PerMin", rateperMin);
                        in.putExtra("about", about);
                        startActivity(in);
                        Bungee.zoom(SellerHomeActivity.this);
                        finish();
                        //drawer_layout.closeDrawer(mDrawerList);
                        break;
                    }
                    case 4: {
                        Intent in = new Intent(SellerHomeActivity.this, SellerNotificationFragment.class);
                        in.putExtra("myId", id);
                        in.putExtra("myName", username);
                        in.putExtra("myImage", imageUrl);
                        in.putExtra("myRating", rating);
                        in.putExtra("coinPerMin", coinPerMin);
                        in.putExtra("$PerMin", rateperMin);
                        in.putExtra("about", about);
                        startActivity(in);
                        Bungee.zoom(SellerHomeActivity.this);
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

                        myId = PreferenceUtils.getId(SellerHomeActivity.this);

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

                                            MyFirebaseInstanceIDService.deleteRegistrationFromServer(SellerHomeActivity.this.getClass().getSimpleName(), myId);
                                            PreferenceUtils.clearMemory(getApplicationContext());
                                            disconnect();
                                            FirebaseAuth.getInstance().signOut();
                                            mGoogleSignInClient.signOut();
                                            LoginManager.getInstance().logOut();

                                            dialog.dismiss();
                                            Intent intent = new Intent(SellerHomeActivity.this, SellerLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            showToast("Something went wrong. Try again later!");
                                        }
                                    }
                                });
                        break;
                    }
                }
            }
        });

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

        seller_name.setText(firstName);

        if (checkPermission()) {

        } else {
            requestPermission();
        }

    }

    @Override
    public void setupListeners() {

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(mDrawerList);
            }
        });

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerHomeActivity.this, WithdrawActivity.class);
                startActivity(in);
                Bungee.slideLeft(SellerHomeActivity.this);
            }
        });

        dialog.setMessage("Loading...");
        dialog.show();

        firestore.collection("users")
                .document(PreferenceUtils.getId(this))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        username = documentSnapshot.getString("username");
                        email = documentSnapshot.getString("email");
                        password = documentSnapshot.getString("password");
                        id = documentSnapshot.getString("id");
                        isActive = documentSnapshot.getString("IsActive");
                        type = documentSnapshot.getString("Type");
                        imageUrl = documentSnapshot.getString("imageUrl");
                        isOnline = documentSnapshot.getString("isOnline");
                        rateperMin = documentSnapshot.getString("$perMin");
                        rating = documentSnapshot.getString("rating");
                        coinPerMin = documentSnapshot.getString("coinPerMin");
                        totalEarnings = documentSnapshot.getString("dollersEarned");
                        about = documentSnapshot.getString("about");
                        notifications = documentSnapshot.getString("notifications");
                        token = documentSnapshot.getString("token");

                        DecimalFormat df = new DecimalFormat("0.00");
                        double number = Double.parseDouble(totalEarnings);
                        String val = df.format(number);

                        total_earnings.setText("$" + val);

                        PreferenceUtils.saveSellerData(username, email, password, id, isActive, type, imageUrl, isOnline, rateperMin, rating, coinPerMin, about, notifications, SellerHomeActivity.this);

                        if (rateperMin.equals("free")) {

                            Flashbar flashbar = new Flashbar.Builder(SellerHomeActivity.this)
                                    .gravity(Flashbar.Gravity.BOTTOM)
                                    .title("Alert!")
                                    .message("Your teriff is set to free. You won't earn on calls.")
                                    .backgroundColorRes(R.color.colorPrimaryDark)
                                    .showOverlay()
                                    .positiveActionText("Set rate")
                                    .negativeActionText("Not now")
                                    .positiveActionTextColorRes(R.color.white)
                                    .negativeActionTextColorRes(R.color.white)
                                    .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                                        @Override
                                        public void onActionTapped(@NotNull final Flashbar flashbar) {
                                            Intent in = new Intent(SellerHomeActivity.this, SellerSettingsActivity.class);
                                            startActivity(in);
                                            finish();
                                        }
                                    })
                                    .negativeActionTapListener(new Flashbar.OnActionTapListener() {
                                        @Override
                                        public void onActionTapped(@NotNull Flashbar flashbar) {
                                            flashbar.dismiss();
                                        }
                                    })
                                    .build();
                            flashbar.show();

                        }
                    }
                });

        firestore.collection("calls")
                .whereEqualTo("sellerId", PreferenceUtils.getId(this))
                .whereEqualTo("status", "received")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {

                            Calls ul = new Calls(
                                    documentSnapshot.getDocument().getString("buyerId"),
                                    documentSnapshot.getDocument().getString("buyerImage"),
                                    documentSnapshot.getDocument().getString("buyerName"),
                                    documentSnapshot.getDocument().getString("callDuration"),
                                    documentSnapshot.getDocument().getString("dollerEarned"),
                                    documentSnapshot.getDocument().getString("id"),
                                    documentSnapshot.getDocument().getString("sellerId"),
                                    documentSnapshot.getDocument().getString("sellerImage"),
                                    documentSnapshot.getDocument().getString("sellerName"),
                                    documentSnapshot.getDocument().getString("status"),
                                    documentSnapshot.getDocument().getString("coinsUsed"));


                            if (documentSnapshot.getDocument().get("callTime") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.getDocument().get("callTime")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                ul.callTime = new Date(milliSeconds);
                                historyArrayList.add(ul);
                            }
                        }

                        if (historyArrayList != null) {

                            Comparator<Calls> c = new Comparator<Calls>() {

                                @Override
                                public int compare(Calls a, Calls b) {
                                    return Long.compare(b.getCallTime().getTime(), a.getCallTime().getTime());
                                }
                            };
                            Collections.sort(historyArrayList, c);
                        }

                        adapter = new CallHistoryAdapter(historyArrayList, SellerHomeActivity.this);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                });

        online_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked && onlineTime.getVisibility() == View.GONE) {

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(SellerHomeActivity.this);
                    final AlertDialog alert = dialog.create();
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    final View attachFileLayout = inflater.inflate(R.layout.seller_online_dialog, null);
                    attachFileLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.setView(attachFileLayout);
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);

                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("mm:ss");
                    date_time = simpleDateFormat.format(calendar.getTime());

                    ImageButton ok_btn = attachFileLayout.findViewById(R.id.ok_btn);
                    RadioGroup radio_grp = attachFileLayout.findViewById(R.id.radio_grp);
                    final RadioButton radio15 = attachFileLayout.findViewById(R.id.radio_15);
                    final RadioButton radio30 = attachFileLayout.findViewById(R.id.radio_30);
                    final RadioButton radio45 = attachFileLayout.findViewById(R.id.radio_45);
                    final RadioButton radio60 = attachFileLayout.findViewById(R.id.radio_60);
                    final RadioButton radiooffline = attachFileLayout.findViewById(R.id.radio_offline);

                    radio_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (checkedId) {
                                case R.id.radio_15: {
                                    selectedValue = "1";
                                    radio15.setChecked(true);
                                    break;
                                }

                                case R.id.radio_30: {
                                    selectedValue = "30";
                                    radio30.setChecked(true);
                                    break;
                                }

                                case R.id.radio_45: {
                                    selectedValue = "45";
                                    radio45.setChecked(true);
                                    break;
                                }

                                case R.id.radio_60: {
                                    selectedValue = "60";
                                    radio60.setChecked(true);
                                    break;
                                }

                                case R.id.radio_offline: {
                                    selectedValue = "Offline";
                                    radiooffline.setChecked(true);
                                    break;
                                }
                            }
                        }
                    });

                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (selectedValue.equals("Offline")) {
                                Intent intent = new Intent(getApplicationContext(), TimerService.class);
                                stopService(intent);
//                                mEditor.clear().commit();
                                mEditor.remove("data").commit();
                                mEditor.remove("mins").commit();
                                onlineTime.setText("");
                                onlineTime.setVisibility(View.GONE);
                                online_switch.setChecked(false);

                                myId = PreferenceUtils.getId(SellerHomeActivity.this);

                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("isOnline", "0");

                                firestore.collection("users")
                                        .document(myId)
                                        .update(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //PreferenceUtils.clearMemory(getApplicationContext());
                                                    //disconnect();

                                                } else {
                                                    showToast("Unable to logout!");
                                                }
                                            }
                                        });
                            } else {
                                mEditor.putString("data", date_time).commit();
                                mEditor.putString("mins", selectedValue).commit();
                                Intent intent_service = new Intent(SellerHomeActivity.this, TimerService.class);
                                startService(intent_service);
                                online_switch.setChecked(true);
                                onlineTime.setVisibility(View.VISIBLE);

                                myId = PreferenceUtils.getId(SellerHomeActivity.this);

                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("isOnline", "1");

                                firestore.collection("users")
                                        .document(myId)
                                        .update(map);
                            }
                            alert.dismiss();
                        }
                    });

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

                } else if (isChecked && onlineTime.getVisibility() == View.VISIBLE) {

                } else {
                    Intent intent = new Intent(getApplicationContext(), TimerService.class);
                    stopService(intent);
//                    mEditor.clear().commit();
                    mEditor.remove("data").commit();
                    mEditor.remove("mins").commit();
                    onlineTime.setText("");
                    onlineTime.setVisibility(View.GONE);
                    online_switch.setChecked(false);
                    selectedValue = "Offline";

                    myId = PreferenceUtils.getId(SellerHomeActivity.this);

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("isOnline", "0");

                    firestore.collection("users")
                            .document(myId)
                            .update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //PreferenceUtils.clearMemory(getApplicationContext());
                                        //disconnect();

                                    } else {
                                        showToast("Unable to logout!");
                                    }
                                }
                            });
                }


            }
        });

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str_time = intent.getStringExtra("time");
            onlineTime.setText(str_time);

            if (onlineTime.getVisibility() == View.VISIBLE) {
                online_switch.setChecked(true);
            }

            if (onlineTime.getText().toString().equals("0:0:1")) {
                onlineTime.setVisibility(View.GONE);
                online_switch.setChecked(false);

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
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
                        Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        loginClicked();

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

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Flashbar flashbar = new Flashbar.Builder(SellerHomeActivity.this)
                    .gravity(Flashbar.Gravity.BOTTOM)
                    .duration(2000)
                    .message("Click back again to exit!")
                    .backgroundColorRes(R.color.colorPrimaryDark)
                    .build();
            flashbar.show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public class RandomizedAdapter extends SparkAdapter {
        //private final float[] yData;
        private final ArrayList<Float> yData;
        private final Random random;

        public RandomizedAdapter() {
            random = new Random();
            yData = new ArrayList<>();
            //yData = new float[20];
            randomize();
        }

        public void randomize() {
            /*for (int i = 0, count = yData.length; i < count; i++) {
                yData[i] = random.nextFloat();
            }*/

            firestore.collection("calls")
                    .whereEqualTo("sellerId", PreferenceUtils.getId(SellerHomeActivity.this))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            int i = 0;
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                yData.add(Float.valueOf(dc.getDocument().getString("dollerEarned")));
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
