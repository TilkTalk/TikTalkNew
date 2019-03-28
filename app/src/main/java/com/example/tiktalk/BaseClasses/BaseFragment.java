package com.example.tiktalk.BaseClasses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.tiktalk.Listeners.NavigationRequestListener;
import com.example.tiktalk.R;
import com.example.tiktalk.Sinch.SinchService;

import static android.content.Context.BIND_AUTO_CREATE;

public abstract class BaseFragment extends Fragment implements ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private static final String TAG = "BaseFragment";
    private NavigationRequestListener mNavigationRequestListener;
    public View view;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().bindService(new Intent(getActivity(), SinchService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);

        try {
            mNavigationRequestListener = (NavigationRequestListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.getClass().getSimpleName()
                    + " must implement " + NavigationRequestListener.class.getSimpleName());
        }
    }

    public void goBack() {
        mNavigationRequestListener.onGoBack();
    }

    public void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        mNavigationRequestListener.onReplaceFragment(containerId, fragment, addToBackStack);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void addFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        mNavigationRequestListener.onAddFragment(containerId, fragment, addToBackStack);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void startActivity(Intent intent) {
        mNavigationRequestListener.onStartActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().bindService(new Intent(getActivity(), SinchService.class), this,
                BIND_AUTO_CREATE);
        this.view = view;
        setupComponents(view);
    }

    public void setupComponents(View rootView) {
        initializeComponents(rootView);
        setupListeners(rootView);
    }

    public abstract void initializeComponents(View rootView);

    public abstract void setupListeners(View rootView);

    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            doWorkOnVisible(view);
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            doWorkOnGone(view);
        }
    }

    //override this method to inflate view when Fragment is in visible state(Specially when using ViewPager)
    public void doWorkOnVisible(View view){

    }

    public void doWorkOnGone(View view){

    }

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
