package com.example.tiktalk.UI.Activities.Buyer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.Sinch.AudioPlayer;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.UI.Activities.Seller.SellerCallDetailsActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ncorti.slidetoact.SlideToActView;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.calling.CallState;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BuyerCallActivity extends BaseActivity {

    static final String TAG = BuyerCallActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId, name, toid, fromid, endMsg, imageUrl, callDuration, coins, coinPerMin, docId;
    private String BuyerName, BuyerImage;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;
    String callMins, callEndTime;

    private TextView call_time;
    private TextView buyer_callName;
    private RoundedImageView buyer_callImage;
    private SlideToActView call_end;
    private ToggleButton mic, video;
    private ToggleButton loadSpeaker;
    private AudioManager audioManager;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            BuyerCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_call);
        setupComponents();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAudioPlayer = new AudioPlayer(this);
        call_time = (TextView) findViewById(R.id.call_time);
        buyer_callName = findViewById(R.id.buyer_callName);
        buyer_callImage = findViewById(R.id.buyer_callImage);


        //  Button endCallButton = (Button) findViewById(R.id.hangupButton);

//        endCallButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                endCall();
//            }
//        });

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        name = getIntent().getStringExtra("name");
        toid = getIntent().getStringExtra("toid");
        fromid = getIntent().getStringExtra("fromid");
        imageUrl = getIntent().getStringExtra("imageUrl");
        coins = getIntent().getStringExtra("coins");
        coinPerMin = getIntent().getStringExtra("coinPerMin");
        docId = getIntent().getStringExtra("docId");
        callMins = getIntent().getStringExtra("callMins");
        callEndTime = getIntent().getStringExtra("callEndTime");

        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(new SinchCallListener() {

                });
                mAddedListener = true;
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }

        updateUI();
    }

    //method to update video feeds in the UI
    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            buyer_callName.setText(name);
            Glide.with(this).load(imageUrl).into(buyer_callImage);

            //Toast.makeText(this, call.getState().toString(), Toast.LENGTH_SHORT).show();
            //mCallState.setText(call.getState().toString());
            if (call.getState() == CallState.ESTABLISHED) {
            }
        }
    }

    //stop the timer when call is ended
    @Override
    public void onStop() {
        super.onStop();
        //mDurationTask.cancel();
        //mTimer.cancel();
//        removeVideoViews();
    }

    //start the timer for the call duration here
    @Override
    public void onStart() {
        super.onStart();
//        mTimer = new Timer();
//        mDurationTask = new UpdateCallDurationTask();
//        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    @Override
    public void initializeComponents() {

        call_end = findViewById(R.id.call_end);
        loadSpeaker = findViewById(R.id.loadSpeaker);
        mic = findViewById(R.id.mic);
        video = findViewById(R.id.video);

        loadSpeaker.toggle();
        mic.toggle();
        video.toggle();
    }

    @Override
    public void setupListeners() {

        call_end.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {

                endCall();

            }
        });

        loadSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audioManager.setMode(AudioManager.MODE_IN_CALL);
                if (audioManager.isSpeakerphoneOn()){
                    getSinchServiceInterface().getAudioController().disableSpeaker();
                }
                else {
                    getSinchServiceInterface().getAudioController().enableSpeaker();
                }


            }
        });

        /*loadSpeaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    getSinchServiceInterface().getAudioController().enableSpeaker();
                }else{
                    getSinchServiceInterface().getAudioController().disableSpeaker();
                }
            }
        });*/

        mic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    getSinchServiceInterface().getAudioController().mute();
                }else{
                    getSinchServiceInterface().getAudioController().unmute();
                }
            }
        });

        video.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    
                }else{

                }
            }
        });
    }


    //method to end the call
    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
            if (PreferenceUtils.getType(BuyerCallActivity.this).equals("Buyer")) {

                if (callDuration == null){
                    Intent in = new Intent(BuyerCallActivity.this, BuyerHomeActivity.class);
                    startActivity(in);
                    finish();
                }
                else {
                    Intent in = new Intent(BuyerCallActivity.this, BuyerRatingActivity.class);
                    in.putExtra("name", name);
                    in.putExtra("imageUrl", imageUrl);
                    in.putExtra("callDuration", callDuration);
                    in.putExtra("buyerId", fromid);
                    in.putExtra("sellerId", toid);
                    in.putExtra("coins", coins);
                    in.putExtra("coinPerMin", coinPerMin);
                    startActivity(in);
                    finish();
                }
            } else {
                Intent in = new Intent(BuyerCallActivity.this, SellerCallDetailsActivity.class);
                in.putExtra("name", name);
                in.putExtra("imageUrl", imageUrl);
                in.putExtra("callDuration", callDuration);
                in.putExtra("buyerId", fromid);
                in.putExtra("sellerId", toid);
                in.putExtra("coins", coins);
                in.putExtra("coinPerMin",  PreferenceUtils.getCoinPerMin(this));
                in.putExtra("docId", docId);
                startActivity(in);
                finish();
            }
            return;
        }
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    //method to update live duration of the call
    private void updateCallDuration() {
        if (mCallStart > 0) {
            call_time.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
            callDuration = formatTimespan(System.currentTimeMillis() - mCallStart);

            if (call_time.getText().equals(callEndTime)){
                endCall();
            }

        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            endMsg = call.getDetails().getEndCause().toString();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            //mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
//            AudioController audioController = getSinchServiceInterface().getAudioController();
//            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 500);
            Toast.makeText(BuyerCallActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }


    }

}
