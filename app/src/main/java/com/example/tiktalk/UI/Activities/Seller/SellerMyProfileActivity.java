package com.example.tiktalk.UI.Activities.Seller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Adapters.SellerRatingAdapter;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.SellerRating;
import com.example.tiktalk.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SellerMyProfileActivity extends BaseActivity {

    FirebaseFirestore firestore;
    ImageView seller_profile_image;
    TextView profileName, profileRating, profileCoinsPerMin;
    SimpleRatingBar test;
    ImageButton CancelBtn;
    EditText about_edittext;

    String myId, myName, myImage, myRating, coinPerMin, ratePerMin, about;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    SellerRatingAdapter adapter;
    ArrayList<SellerRating> selleRatingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_my_profile);
        setupComponents();
    }

    @Override
    public void initializeComponents() {

        firestore = FirebaseFirestore.getInstance();
        selleRatingArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
//        recyclerView.setNestedScrollingEnabled(false);

        seller_profile_image = findViewById(R.id.seller_profile_image);
        profileName = findViewById(R.id.seller_profile_name);
        profileRating = findViewById(R.id.seller_profile_rating);
        profileCoinsPerMin = findViewById(R.id.seller_profile_coinsPerMin);
        test = findViewById(R.id.test);
        CancelBtn = findViewById(R.id.profile_cancel_btn);
        about_edittext = findViewById(R.id.about_edittext);

        myId = getIntent().getStringExtra("myId");
        myName = getIntent().getStringExtra("myName");
        myImage = getIntent().getStringExtra("myImage");
        myRating = getIntent().getStringExtra("myRating");
        coinPerMin = getIntent().getStringExtra("coinPerMin");
        ratePerMin = getIntent().getStringExtra("$PerMin");
        about = getIntent().getStringExtra("about");

        profileName.setText(myName);
        Glide.with(this).load(myImage).into(seller_profile_image);
        profileRating.setText(myRating);
        profileCoinsPerMin.setText(coinPerMin + " coins per minute");
        about_edittext.setText(about);
        test.setRating(Float.parseFloat(myRating));

        firestore.collection("ratings")
                .whereEqualTo("sellerId", myId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {

                            SellerRating sr = new SellerRating(
                                    documentSnapshot.getDocument().getString("userId"),
                                    documentSnapshot.getDocument().getString("userName"),
                                    documentSnapshot.getDocument().getString("userImage"),
                                    documentSnapshot.getDocument().getString("value"),
                                    documentSnapshot.getDocument().getString("id"),
                                    documentSnapshot.getDocument().getString("sellerId"),
                                    documentSnapshot.getDocument().getString("feedback"));

                            if (documentSnapshot.getDocument().get("updateDate") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.getDocument().get("updateDate")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                sr.updateDate = new Date(milliSeconds);
                                selleRatingArrayList.add(sr);
                            }
                        }

                        if (selleRatingArrayList != null) {

                            Comparator<SellerRating> c = new Comparator<SellerRating>() {

                                @Override
                                public int compare(SellerRating a, SellerRating b) {
                                    return Long.compare(b.getUpdateDate().getTime(), a.getUpdateDate().getTime());
                                }
                            };
                            Collections.sort(selleRatingArrayList, c);
                        }

                        adapter = new SellerRatingAdapter(selleRatingArrayList, SellerMyProfileActivity.this);
                        recyclerView.setAdapter(adapter);
//                        dialog.dismiss();
                    }
                });

    }

    @Override
    public void setupListeners() {

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerMyProfileActivity.this, SellerHomeActivity.class);
                startActivity(in);
                finish();
            }
        });

    }
}
