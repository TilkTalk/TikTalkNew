package com.example.tiktalk.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Seller.UploadPhotoActivity;

public class NumberVerificationActivity extends BaseActivity {

    Button  ok_btn;
    ImageButton cancelBtn;
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

        code1.addTextChangedListener(new GenericTextWatcher(code1));
        code2.addTextChangedListener(new GenericTextWatcher(code2));
        code3.addTextChangedListener(new GenericTextWatcher(code3));
        code4.addTextChangedListener(new GenericTextWatcher(code4));

    }

    public class GenericTextWatcher implements TextWatcher
    {
        private View view;
        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.code1:
                    if(text.length()==1)
                        code2.requestFocus();
                    break;
                case R.id.code2:
                    if(text.length()==1)
                        code3.requestFocus();
                    else if(text.length()==0)
                        code1.requestFocus();
                    break;
                case R.id.code3:
                    if(text.length()==1)
                        code4.requestFocus();
                    else if(text.length()==0)
                        code2.requestFocus();
                    break;
                case R.id.code4:
                    if(text.length()==0)
                        code3.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
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
