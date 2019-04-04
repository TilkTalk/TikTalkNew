package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

public class BuyCoinsActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ProgressDialog dialog;
    FirebaseFirestore firestore;
    String coins, amount, totalCoins;
    ImageButton back_btn;
    CardMultilineWidget mCardMultilineWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);

        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        back_btn = findViewById(R.id.back_btn);

        coins = getIntent().getStringExtra("coin");
        totalCoins = getIntent().getStringExtra("totalCoins");
        amount = getIntent().getStringExtra("amount");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mCardMultilineWidget = findViewById(R.id.card_multiline_widget);

        final Stripe stripe = new Stripe(getApplicationContext());
        stripe.setDefaultPublishableKey("pk_test_W8SfnQTCA4Tf4Puwj1O5xRX000jnzEooa1");
        com.stripe.Stripe.apiKey = "sk_test_tkGctHUZtH516zxOuPJ1gGKk00mchkuFvT";

        findViewById(R.id.checkout_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card cardToSave = mCardMultilineWidget.getCard();
                dialog.setMessage("Loading...");
                dialog.show();

                if (cardToSave == null) {
                    Log.d("Error Log : ", "Invalid Card Data");
                    dialog.dismiss();
                    return;
                } else {
                    stripe.createToken(
                    new Card(cardToSave.getNumber(), cardToSave.getExpMonth(), cardToSave.getExpYear(), cardToSave.getCVC()),
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    Log.d("Error Log : ", token.getId().toString());

                                    Map<String, Object> params = new HashMap<>();
                                    params.put("amount", Integer.valueOf(amount) * 100);
                                    params.put("currency", "usd");
                                    params.put("description", "Charge");
                                    params.put("source", token.getId().toString());
                                    Map<String, String> metadata = new HashMap<>();
                                    metadata.put("name", PreferenceUtils.getUsername(BuyCoinsActivity.this));
                                    params.put("metadata", metadata);
                                    try {
                                        Charge charge = Charge.create(params);

                                        int addedCoins = Integer.valueOf(totalCoins) + Integer.valueOf(coins);

                                        final HashMap<String, Object> coinMap = new HashMap<String, Object>();
                                        coinMap.put("coins", String.valueOf(addedCoins));

                                        HashMap<String, Object> buyCoinsMap = new HashMap<String, Object>();
                                        buyCoinsMap.put("buyerId", PreferenceUtils.getId(BuyCoinsActivity.this));
                                        buyCoinsMap.put("buyerName", PreferenceUtils.getUsername(BuyCoinsActivity.this));
                                        buyCoinsMap.put("buyTime", FieldValue.serverTimestamp());
                                        buyCoinsMap.put("transactionId", charge.getId());
                                        buyCoinsMap.put("amountCharged", charge.getAmount() / 100);
                                        buyCoinsMap.put("coins", coins);

                                        firestore.collection("buyCoins")
                                                .add(buyCoinsMap)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                        if (task.isSuccessful()) {

                                                            firestore.collection("users")
                                                                    .document(PreferenceUtils.getId(BuyCoinsActivity.this))
                                                                    .update(coinMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {

                                                                        dialog.dismiss();
                                                                        Intent in = new Intent(BuyCoinsActivity.this, BuyerHomeActivity.class);
                                                                        startActivity(in);
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });

//                                        Toast.makeText(BuyCoinsActivity.this,"Done Transaction",Toast.LENGTH_LONG).show();
//                                        Toast.makeText(BuyCoinsActivity.this, String.valueOf(charge.getId()),Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(BuyCoinsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                public void onError(Exception error) {
                                    Toast.makeText(getApplicationContext(),
                                            error.getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                    );

                }

            }
        });
    }
}
