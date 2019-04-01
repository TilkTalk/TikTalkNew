package com.example.tiktalk.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Seller.UploadPhotoActivity;

public class NumberVerificationActivity extends BaseActivity {

    Button cancelBtn, ok_btn;
    String name, email, password, code;
    EditText code1, code2, code3, code4;
    TextView emailTxt, resend_textView;
    String str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numberverification);
        setupComponents();

    }

    @Override
    public void initializeComponents() {

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        code = intent.getStringExtra("code");

        ok_btn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.number_cancel_btn);
        emailTxt = findViewById(R.id.email);
        resend_textView = findViewById(R.id.resend_textView);
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);

        emailTxt.setText(email);

    }

    @Override
    public void setupListeners() {

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent in = new Intent(NumberVerificationActivity.this, UploadPhotoActivity.class);
                startActivity(in);*/

                str = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString();
                //Toast.makeText(NumberVerificationActivity.this, str, Toast.LENGTH_SHORT).show();

                if (code.equals(str)){
                    Intent in = new Intent(NumberVerificationActivity.this, UploadPhotoActivity.class);
                    in.putExtra("name", name);
                    in.putExtra("email", email);
                    in.putExtra("password", password);
                    startActivity(in);
                    finish();
                }
                else{
                    Toast.makeText(NumberVerificationActivity.this, "Wrong verification code!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resend_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundMail.newBuilder(getApplicationContext())
                        .withUsername("benibinyaminbenibinyamin@gmail.com")
                        .withPassword("zxc!asd123")
                        .withMailto(email)
                        .withType(BackgroundMail.TYPE_PLAIN)
                        .withSubject("Verification Code")
                        .withBody(code)
                        .withSendingMessage("Sending OTP")
                        .send();
            }
        });
    }
}