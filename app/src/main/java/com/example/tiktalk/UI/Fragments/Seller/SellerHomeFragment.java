package com.example.tiktalk.UI.Fragments.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktalk.Adapters.CallHistoryAdapter;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.Calls;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.AllSellerActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerCallActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerHomeActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerProfileActivity;
import com.example.tiktalk.UI.Activities.Seller.UploadPhotoActivity;
import com.example.tiktalk.UI.Activities.WithdrawActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import spencerstudios.com.bungeelib.Bungee;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SellerHomeFragment extends BaseFragment {

    FirebaseFirestore firestore;
    TextView withdrawBtn, seller_name, total_earnings;
    public static final int RequestPermissionCode = 1;

    ProgressBar progressBar;
    ProgressDialog dialog;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    CallHistoryAdapter adapter;
    ArrayList<Calls> historyArrayList;

    @Override
    public void initializeComponents(View rootView) {

        firestore = FirebaseFirestore.getInstance();
        historyArrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        dialog = new ProgressDialog(getContext());
        progressBar = rootView.findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        withdrawBtn = rootView.findViewById(R.id.withdraw_btn);
        seller_name = rootView.findViewById(R.id.seller_name);
        total_earnings = rootView.findViewById(R.id.total_earnings);

        seller_name.setText(PreferenceUtils.getUsername(getContext()));

        if (checkPermission()) {

        } else {
            requestPermission();
        }

    }

    @Override
    public void setupListeners(View rootView) {

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getContext(), WithdrawActivity.class);
                startActivity(in);
                Bungee.slideLeft(getContext());
            }
        });

        dialog.setMessage("Loading...");
        dialog.show();

        firestore.collection("users")
                .document(PreferenceUtils.getId(getContext()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        String totalEarnings = documentSnapshot.getString("dollersEarned");
                        total_earnings.setText("$" + totalEarnings + "0");
                    }
                });

        firestore.collection("calls")
                .whereEqualTo("sellerId", PreferenceUtils.getId(getContext()))
                .whereEqualTo("status", "received")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()){

                            Calls ul = new Calls(
                                    documentSnapshot.getDocument().getString("buyerId"),
                                    documentSnapshot.getDocument().getString("buyerImage"),
                                    documentSnapshot.getDocument().getString("buyerName"),
                                    documentSnapshot.getDocument().getString("callDuration"),
                                    documentSnapshot.getDocument().getString("dollerEarned"),
                                    documentSnapshot.getDocument().getString("id"),
                                    documentSnapshot.getDocument().getString("sellerId"),
                                    documentSnapshot.getDocument().getString("sellerImage"),
                                    documentSnapshot.getDocument().getString("sellerName"),
                                    documentSnapshot.getDocument().getString("status"),
                                    documentSnapshot.getDocument().getString("coinsUsed"));


                            if (documentSnapshot.getDocument().get("callTime") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.getDocument().get("callTime")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                ul.callTime = new Date(milliSeconds);
                                historyArrayList.add(ul);
                            }
                        }

                        if (historyArrayList != null) {

                            Comparator<Calls> c = new Comparator<Calls>() {

                                @Override
                                public int compare(Calls a, Calls b) {
                                    return Long.compare(b.getCallTime().getTime(), a.getCallTime().getTime());
                                }
                            };
                            Collections.sort(historyArrayList, c);
                        }

                        adapter = new CallHistoryAdapter(historyArrayList, getContext());
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_home, null);
        return view;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}

