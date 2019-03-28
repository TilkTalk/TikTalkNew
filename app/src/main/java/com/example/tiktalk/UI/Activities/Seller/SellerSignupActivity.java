package com.example.tiktalk.UI.Activities.Seller;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.NumberVerificationActivity;
import com.example.tiktalk.Utils.AppUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import spencerstudios.com.bungeelib.Bungee;

public class SellerSignupActivity extends BaseActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    LinearLayout name_layout, email_layout, password_layout;
    View name_view, email_view, password_view;
    EditText nameEditTxt;
    EditText emailEditTxt;
    EditText passwordEditTxt;
    Button signUpBtn, cancelBtn;
    TextView signinTextview;
    String code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_signup);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Random random = new Random();
        code = String.format("%04d", random.nextInt(10000));

        setupComponents();
    }

    @Override
    public void initializeComponents() {
        nameEditTxt = findViewById(R.id.signup_name_seller);
        emailEditTxt = findViewById(R.id.signup_email_seller);
        passwordEditTxt = findViewById(R.id.signup_password_seller);
        signUpBtn = findViewById(R.id.signup_btn_seller);
        cancelBtn = findViewById(R.id.sign_cancel_btn);
        signinTextview = findViewById(R.id.signup_textView);
        name_layout = findViewById(R.id.name_layout);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        name_view = findViewById(R.id.name_view);
        email_view = findViewById(R.id.email_view);
        password_view = findViewById(R.id.password_view);

    }

    @Override
    public void setupListeners() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendOTP();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signinTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Bungee.slideUp(SellerSignupActivity.this);
            }
        });

        nameEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name_layout.setBackgroundResource(R.drawable.edit_text_shadow);
                    email_layout.setBackgroundResource(R.color.transparent);
                    password_layout.setBackgroundResource(R.color.transparent);
                    name_view.setVisibility(View.GONE);
                    email_view.setVisibility(View.VISIBLE);
                    password_view.setVisibility(View.VISIBLE);
                }
            }
        });

        emailEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name_layout.setBackgroundResource(R.color.transparent);
                    email_layout.setBackgroundResource(R.drawable.edit_text_shadow);
                    password_layout.setBackgroundResource(R.color.transparent);
                    name_view.setVisibility(View.VISIBLE);
                    email_view.setVisibility(View.GONE);
                    password_view.setVisibility(View.VISIBLE);
                }
            }
        });

        passwordEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name_layout.setBackgroundResource(R.color.transparent);
                    email_layout.setBackgroundResource(R.color.transparent);
                    password_layout.setBackgroundResource(R.drawable.edit_text_shadow);
                    name_view.setVisibility(View.VISIBLE);
                    email_view.setVisibility(View.VISIBLE);
                    password_view.setVisibility(View.GONE);
                }
            }
        });
    }

    private void signUp() {

        String name = nameEditTxt.getText().toString();
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();
        String verificationCode = code;

        Intent intent = new Intent(this, NumberVerificationActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("code", verificationCode);
        startActivity(intent);
    }


    public void SendOTP() {

        BackgroundMail bm = new BackgroundMail(this);

        bm.newBuilder(this)
                .withUsername("benibinyaminbenibinyamin@gmail.com")
                .withPassword("zxc!asd123")
                .withMailto(emailEditTxt.getText().toString())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Verification Code")
                .withBody(code)
                .withSendingMessage("Sending OTP")
                .send();

        signUp();

    }
}
