package com.example.tiktalk.Sinch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoCallListener;

import java.util.List;

public class CallActivity extends BaseActivity {

    static final String TAG = CallActivity.class.getSimpleName();
    private String mCallId, mCallName, mCallImage;
    private AudioPlayer mAudioPlayer;

    ImageView answer, decline;
    TextView remoteUser;
    RoundedImageView reciverImage;

    String buyerId, buyerName, buyerImage, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        setupComponents();

        answer = findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        decline = findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);
        remoteUser = findViewById(R.id.remoteUser);
        reciverImage = findViewById(R.id.reciverImage);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        //mCallName = getIntent().getStringExtra("name");
        //mCallImage = getIntent().getStringExtra("imageUrl");

        buyerId = getIntent().getStringExtra("buyerId");
        buyerName = getIntent().getStringExtra("buyerName");
        buyerImage = getIntent().getStringExtra("buyerImage");
        docId = getIntent().getStringExtra("docId");
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void setupListeners() {

    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener() {

            });

            remoteUser.setText(buyerName);
            Glide.with(this).load(buyerImage).into(reciverImage);

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, BuyerCallActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            //intent.putExtra("buyerId", buyerId);
            intent.putExtra("name", buyerName);
            intent.putExtra("imageUrl", buyerImage);
            intent.putExtra("docId", docId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    public class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }


    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };


}

