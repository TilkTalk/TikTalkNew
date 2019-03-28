package com.example.tiktalk.BaseClasses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.tiktalk.Listeners.NavigationRequestListener;
import com.example.tiktalk.R;
import com.example.tiktalk.Sinch.SinchService;
import com.example.tiktalk.Utils.FragmentUtils;

public abstract class BaseActivity extends AppCompatActivity implements NavigationRequestListener,ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private static final String TAG = "BaseActivity";
    Fragment currentFragment;

    /*public KcDatabase myDataBase;
    public KC_Repo myRepo;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
        //myDataBase = KcDatabase.kcDatabase(this);
        //myRepo = KC_Repo.getmInstance(KC_DataSource.journalDataSource(myDataBase.makeAPlanDao()));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    //This method will hide keyboard when you click outside the EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onReplaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        currentFragment = fragment;
        FragmentUtils.commitFragment(getSupportFragmentManager(), containerId, fragment, addToBackStack);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onAddFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentUtils.addFragment(getSupportFragmentManager(), containerId, fragment, addToBackStack);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onStartActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onGoBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();

        //override transition to skip the standard window transition
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void setupComponents() {

        initializeComponents();
        setupListeners();
    }

    public abstract void initializeComponents();

    public abstract void setupListeners();

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }


}

