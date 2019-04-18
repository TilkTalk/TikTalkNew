package com.example.tiktalk.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.Utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.thekhaeng.pushdownanim.PushDownAnim;

import spencerstudios.com.bungeelib.Bungee;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class ResetPasswordActivity extends BaseActivity {

    FirebaseAuth auth;
    Button sendPasswordBtn;
    ImageButton cancelBtn;
    EditText forgotpassword_email;
    ImageView check;

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
        check = findViewById(R.id.check);
    }

    @Override
    public void setupListeners() {

        forgotpassword_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Patterns.EMAIL_ADDRESS.matcher(forgotpassword_email.getText().toString()).matches()) {
                    check.setVisibility(View.VISIBLE);
                } else
                    check.setVisibility(View.GONE);
            }
        });

        PushDownAnim.setPushDownAnimTo(sendPasswordBtn)
                .setScale(MODE_STATIC_DP, 3)
                .setDurationPush(0)
                .setDurationRelease(300)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

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
                                    Intent in = new Intent(ResetPasswordActivity.this, BuyerLoginActivity.class);
                                    startActivity(in);
                                    Bungee.slideLeft(ResetPasswordActivity.this);
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
                Intent in = new Intent(ResetPasswordActivity.this, BuyerLoginActivity.class);
                startActivity(in);
                Bungee.slideDown(ResetPasswordActivity.this);
                finish();
            }
        });
    }
}
