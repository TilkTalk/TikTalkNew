package com.example.tiktalk.UI.Fragments.Buyer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiktalk.Adapters.LastCallsAdapter;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.Calls;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class BuyerMyWalletFragment extends BaseFragment {

    FirebaseFirestore firestore;
    TextView total_coins;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    LastCallsAdapter adapter;
    ArrayList<Calls> callsArrayList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buyer_mywallet, null);
        return view;
    }

    @Override
    public void initializeComponents(View rootView) {

        firestore = FirebaseFirestore.getInstance();
        callsArrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        total_coins = rootView.findViewById(R.id.total_coins);
        total_coins.setText(PreferenceUtils.getCoins(getContext()));

    }

    @Override
    public void setupListeners(View rootView) {

        firestore.collection("calls")
                .whereEqualTo("buyerId", PreferenceUtils.getId(getContext()))
                .whereEqualTo("status", "received")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            Calls ul = new Calls(
                                    documentSnapshot.getString("buyerId"),
                                    documentSnapshot.getString("buyerImage"),
                                    documentSnapshot.getString("buyerName"),
                                    documentSnapshot.getString("callDuration"),
                                    documentSnapshot.getString("dollerEarned"),
                                    documentSnapshot.getString("id"),
                                    documentSnapshot.getString("sellerId"),
                                    documentSnapshot.getString("sellerImage"),
                                    documentSnapshot.getString("sellerName"),
                                    documentSnapshot.getString("status"),
                                    documentSnapshot.getString("coinsUsed"));

                            if (documentSnapshot.get("callTime") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.get("callTime")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                ul.callTime = new Date(milliSeconds);
                                callsArrayList.add(ul);
                            }

                        }

                        if (callsArrayList != null) {

                            Comparator<Calls> c = new Comparator<Calls>() {

                                @Override
                                public int compare(Calls a, Calls b) {
                                    return Long.compare(b.getCallTime().getTime(), a.getCallTime().getTime());
                                }
                            };
                            Collections.sort(callsArrayList, c);
                        }

                        adapter = new LastCallsAdapter(callsArrayList, getContext());
                        recyclerView.setAdapter(adapter);

                    }
                });

    }
}
