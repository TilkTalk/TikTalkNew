package com.example.tiktalk.UI.Activities.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class SellerLoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ProgressDialog dialog;

    Button loginBtn, cancelBtn;
    TextView signupBtn;
    EditText emailEditText;
    EditText passwordEditText;
    ImageView check;
    LinearLayout email_layout, password_layout;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    User user = new User();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);

        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        loginBtn = findViewById(R.id.login_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        signupBtn = findViewById(R.id.signup_textView);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        check = findViewById(R.id.check);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (emailEditText.getText().toString().contains(".com")){
                    check.setVisibility(View.VISIBLE);
                }
                else
                    check.setVisibility(View.GONE);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerLoginActivity.this, SellerSignupActivity.class);
                startActivity(in);
                Bungee.slideUp(SellerLoginActivity.this);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void login() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        if (TextUtils.isEmpty(email)) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(email_layout);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(password_layout);
            return;
        }

        dialog.setMessage("Signing in...");
        dialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            final String currentUser = auth.getCurrentUser().getUid();

                            firestore.collection("users")
                                    .document(currentUser)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                DocumentSnapshot dc = task.getResult();

                                                user.username = dc.getString("username");
                                                user.email = dc.getString("email");
                                                user.password = dc.getString("password");
                                                user.IsActive = dc.getString("IsActive");
                                                user.Type = dc.getString("Type");
                                                user.id = dc.getString("id");
                                                user.imageUrl = dc.getString("imageUrl");
                                                user.isOnline = dc.getString("isOnline");
                                                user.rating = dc.getString("rating");
                                                user.ratePerMin = dc.getString("$perMin");
                                                user.coinPerMin = dc.getString("coinPerMin");

                                                PreferenceUtils.saveSellerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.ratePerMin, user.rating, user.coinPerMin, SellerLoginActivity.this);

                                                MyFirebaseInstanceIDService.sendRegistrationToServer(SellerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), currentUser);

                                                dialog.dismiss();

                                                if (user.IsActive.equals("1")){

                                                    HashMap<String, Object> online = new HashMap<String, Object>();
                                                    online.put("isOnline", "1");

                                                    firestore.collection("users")
                                                            .document(currentUser)
                                                            .update(online);

                                                    Intent intent = new Intent(SellerLoginActivity.this, SellerDashboardActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else {
                                                    Toast.makeText(SellerLoginActivity.this, "Your account is not approved yet.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });


                        } else
                            Toast.makeText(SellerLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
