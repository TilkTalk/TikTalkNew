package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class BuyerRatingActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    ProgressBar progressBar;
    ProgressDialog dialog;

    Button ratingSubmitBtn;
    String name, imageUrl, callDuration, buyerId, sellerId, coins, coinPerMin;
    RoundedImageView seller_image;
    TextView seller_name, call_duration, coins_used;
    SeekBar seekBar;
    ImageView star1, star2, star3, star4, star5;
    int remainingCoins;

    int hour = 0;
    int mins = 0;
    int sec = 0;
    int seekBarValue = 0;
    int size = 0;
    int totalRating = 0;
    float rating = 0;
    String s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_rating);

        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        name = getIntent().getStringExtra("name");
        imageUrl = getIntent().getStringExtra("imageUrl");
        callDuration = getIntent().getStringExtra("callDuration");
        buyerId = getIntent().getStringExtra("buyerId");
        sellerId = getIntent().getStringExtra("sellerId");
        coins = getIntent().getStringExtra("coins");
        coinPerMin = getIntent().getStringExtra("coinPerMin");

        ratingSubmitBtn = findViewById(R.id.rating_submit_btn);
        seller_image = findViewById(R.id.seller_image);
        seller_name = findViewById(R.id.seller_name);
        call_duration = findViewById(R.id.call_duration);
        seekBar = findViewById(R.id.seekBar);
        coins_used = findViewById(R.id.coins_used);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        seller_name.setText(name);
        Glide.with(this).load(imageUrl).into(seller_image);

        displayCallDuration();
        coinsCalculation();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue = progress + 1;
                //Toast.makeText(BuyerRatingActivity.this, String.valueOf(seekBarValue), Toast.LENGTH_SHORT).show();
                if (seekBarValue == 1) {
                    star1.setImageResource(R.drawable.star_blue);
                    star2.setImageResource(R.drawable.star_gray);
                    star3.setImageResource(R.drawable.star_gray);
                    star4.setImageResource(R.drawable.star_gray);
                    star5.setImageResource(R.drawable.star_gray);
                }
                if (seekBarValue == 2) {
                    star1.setImageResource(R.drawable.star_blue);
                    star2.setImageResource(R.drawable.star_blue);
                    star3.setImageResource(R.drawable.star_gray);
                    star4.setImageResource(R.drawable.star_gray);
                    star5.setImageResource(R.drawable.star_gray);
                }
                if (seekBarValue == 3) {
                    star1.setImageResource(R.drawable.star_blue);
                    star2.setImageResource(R.drawable.star_blue);
                    star3.setImageResource(R.drawable.star_blue);
                    star4.setImageResource(R.drawable.star_gray);
                    star5.setImageResource(R.drawable.star_gray);
                }
                if (seekBarValue == 4) {
                    star1.setImageResource(R.drawable.star_blue);
                    star2.setImageResource(R.drawable.star_blue);
                    star3.setImageResource(R.drawable.star_blue);
                    star4.setImageResource(R.drawable.star_blue);
                    star5.setImageResource(R.drawable.star_gray);
                }
                if (seekBarValue == 5) {
                    star1.setImageResource(R.drawable.star_blue);
                    star2.setImageResource(R.drawable.star_blue);
                    star3.setImageResource(R.drawable.star_blue);
                    star4.setImageResource(R.drawable.star_blue);
                    star5.setImageResource(R.drawable.star_blue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        firestore.collection("ratings")
                .whereEqualTo("UserId", buyerId)
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            size++;
                            String rating = String.valueOf(documentSnapshot.get("Value"));
                            totalRating = totalRating + Integer.valueOf(rating);
                        }
                    }
                });


        ratingSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final HashMap<String, Object> users = new HashMap<String, Object>();
                users.put("UserId", buyerId);
                users.put("Value", String.valueOf(seekBarValue));
                users.put("id", buyerId);
                users.put("sellerId", sellerId);
                users.put("updateDate", FieldValue.serverTimestamp());

                dialog.setMessage("Submitting...");
                dialog.show();

                firestore.collection("ratings")
                        .add(users)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                rating = (float) (totalRating + seekBarValue) / (size + 1);
                                s = String.format("%.1f", (float) (totalRating + seekBarValue) / (size + 1));

                                final HashMap<String, Object> ratingVal = new HashMap<String, Object>();
                                ratingVal.put("rating", s);

                                final HashMap<String, Object> coinsRemain = new HashMap<String, Object>();
                                coinsRemain.put("coins", String.valueOf(remainingCoins));

                                firestore.collection("users")
                                        .document(sellerId)
                                        .update(ratingVal);

                                firestore.collection("users")
                                        .document(buyerId)
                                        .update(coinsRemain);

                                /*firestore.collection("payment")
                                        .add()*/

                                dialog.dismiss();
                                Intent in = new Intent(BuyerRatingActivity.this, BuyerHomeActivity.class);
                                startActivity(in);
                                Bungee.slideDown(BuyerRatingActivity.this);
                                finish();
                            }
                        });

            }
        });
    }

    public void displayCallDuration(){

        String[] time = callDuration.split(":");

        if (time.length == 2){

            mins = Integer.parseInt(time[0].trim());
            sec = Integer.parseInt(time[1].trim());

            call_duration.setText(mins + " mins " + sec + " sec");
            return;
        }

        if (time.length == 3){

            hour = Integer.parseInt(time[0].trim());
            mins = Integer.parseInt(time[1].trim());
            sec = Integer.parseInt(time[1].trim());

            call_duration.setText(hour + " hrs " + mins + " mins");
            return;
        }
    }

    public void coinsCalculation(){

        int coinsUsed = 0;

        if (sec <= 59 && sec >= 1){

            coinsUsed = (mins + 1) * Integer.valueOf(coinPerMin);

            remainingCoins = Integer.valueOf(coins) - coinsUsed;
        }

        coins_used.setText(String.valueOf(coinsUsed) + " coins");
    }
}
