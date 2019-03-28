package com.example.tiktalk.UI.Activities.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Seller.SellerSignupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import spencerstudios.com.bungeelib.Bungee;

public class BuyerSignupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    LinearLayout name_layout, email_layout, password_layout;
    View name_view, email_view, password_view;
    EditText nameEditTxt;
    EditText emailEditTxt;
    EditText passwordEditTxt;
    Button signUpBtn;
    TextView partnerSignUpBtn;
    TextView signinTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_signup);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nameEditTxt = findViewById(R.id.signup_name);
        emailEditTxt = findViewById(R.id.signup_email);
        passwordEditTxt = findViewById(R.id.signup_password);
        signUpBtn = findViewById(R.id.signup_btn);
        partnerSignUpBtn = findViewById(R.id.partnerSignup_Btn);
        signinTextView = findViewById(R.id.signin_textView);
        name_layout = findViewById(R.id.name_layout);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        name_view = findViewById(R.id.name_view);
        email_view = findViewById(R.id.email_view);
        password_view = findViewById(R.id.password_view);

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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        partnerSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyerSignupActivity.this, SellerSignupActivity.class);
                startActivity(intent);
            }
        });

        signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyerSignupActivity.this, BuyerLoginActivity.class);
                startActivity(intent);
                Bungee.slideUp(BuyerSignupActivity.this);
            }
        });
    }

    private void signUp() {

        String name = nameEditTxt.getText().toString();
        String email = emailEditTxt.getText().toString();
        String password = passwordEditTxt.getText().toString();

        Intent intent = new Intent(this, UploadPhotoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
