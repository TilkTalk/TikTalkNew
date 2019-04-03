package com.example.tiktalk.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends BaseActivity {

    FirebaseAuth auth;
    Button sendPasswordBtn;
    Button cancelBtn;
    EditText forgotpassword_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        setupComponents();
    }

    @Override
    public void initializeComponents() {

        auth = FirebaseAuth.getInstance();
        sendPasswordBtn = findViewById(R.id.sendpassword_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        forgotpassword_email = findViewById(R.id.forgotpassword_email);
    }

    @Override
    public void setupListeners() {
        sendPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.sendPasswordResetEmail(forgotpassword_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                    AppUtils.Toast("Password Send ");
//                                    AppUtils.Toast("Check Email");
                                    Toast.makeText(ResetPasswordActivity.this, "Password Sent!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(ResetPasswordActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
