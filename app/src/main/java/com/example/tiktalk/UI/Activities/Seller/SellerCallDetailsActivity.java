package com.example.tiktalk.UI.Activities.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SellerCallDetailsActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    ProgressBar progressBar;
    ProgressDialog dialog;

    String name, imageUrl, callDuration, buyerId, coinPerMin, docId;

    ImageView buyer_photo;
    TextView buyer_name, call_duration, amount_earned;
    Button ok_btn;

    int hour = 0;
    int mins = 0;
    int sec = 0;
    float dollerEarned;
    int coinsUsed = 0;
    float totalAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_calldetails);

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
        coinPerMin = getIntent().getStringExtra("coinPerMin");
        docId = getIntent().getStringExtra("docId");

        buyer_photo = findViewById(R.id.buyer_photo);
        buyer_name = findViewById(R.id.buyer_name);
        call_duration = findViewById(R.id.call_duration);
        amount_earned = findViewById(R.id.amount_earned);
        ok_btn = findViewById(R.id.ok_btn);

        Glide.with(this).load(imageUrl).into(buyer_photo);
        buyer_name.setText(name);

        displayCallDuration();

        rateCalculation();

        getDollerEarned();

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void rateCalculation() {

        if (sec >= 1 && sec <= 59){

            dollerEarned = Float.valueOf(PreferenceUtils.getRatePerMin(this)) * (mins + 1);
            coinsUsed = (mins + 1) * Integer.valueOf(coinPerMin);
        }

        amount_earned.setText("$" + String.valueOf(dollerEarned) + " USD");

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

    public void getDollerEarned(){

        firestore.collection("users")
                .document(PreferenceUtils.getId(this))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        totalAmount = Float.valueOf(documentSnapshot.getString("dollersEarned"));
                    }
                });

    }

    public void submit(){

        HashMap<String, Object> callDetails = new HashMap<>();
        callDetails.put("sellerId", PreferenceUtils.getId(this));
        callDetails.put("sellerName", PreferenceUtils.getUsername(this));
        callDetails.put("sellerImage", PreferenceUtils.getImageUrl(this));
        callDetails.put("id", docId);
        callDetails.put("callDuration", callDuration);
        callDetails.put("dollerEarned", String.valueOf(dollerEarned));
        callDetails.put("coinsUsed", String.valueOf(coinsUsed));
        callDetails.put("status", "received");

        dialog.setMessage("Submitting...");
        dialog.show();

        firestore.collection("calls")
                .document(docId)
                .update(callDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        totalAmount = totalAmount + dollerEarned;

                        HashMap<String, Object> amountEarned = new HashMap<>();
                        amountEarned.put("dollersEarned", String.valueOf(totalAmount));

                        firestore.collection("users")
                                .document(PreferenceUtils.getId(SellerCallDetailsActivity.this))
                                .update(amountEarned);

                        dialog.dismiss();
                        Intent in = new Intent(SellerCallDetailsActivity.this, SellerDashboardActivity.class);
                        startActivity(in);
                        finish();
                    }
                });

    }
}
