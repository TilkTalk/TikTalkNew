package com.example.tiktalk.UI.Fragments.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.tiktalk.Adapters.NotificationsAdapter;
import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.Notifications;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerChatActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class BuyerNotificationFragment extends BaseFragment implements NotificationsAdapter.OnClickListener{

    FirebaseFirestore firestore;
    ProgressBar progressBar;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    NotificationsAdapter adapter;
    ArrayList<Notifications> notificationArrayList;


    @Override
    public void initializeComponents(View rootView) {

        dialog = new ProgressDialog(getContext());
        progressBar = rootView.findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        firestore = FirebaseFirestore.getInstance();
        notificationArrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

    }

    @Override
    public void setupListeners(View rootView) {

        dialog.setMessage("Loading...");
        dialog.show();

        firestore.collection("notifications")
                .whereEqualTo("receiver", PreferenceUtils.getId(getContext()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            final DocumentSnapshot documentSnapshot = dc.getDocument();

                            Notifications notify = new Notifications(documentSnapshot.getString("sender"),
                                    documentSnapshot.getString("receiver"),
                                    documentSnapshot.getString("name"),
                                    documentSnapshot.getString("message"),
                                    documentSnapshot.getString("image"));

                            if (documentSnapshot.get("messageTime") != null) {

                                long seconds = ((Timestamp) (Object) documentSnapshot.get("messageTime")).getSeconds();
                                long milliSeconds = seconds * 1000;
                                notify.callTime = new Date(milliSeconds);
                                notificationArrayList.add(notify);
                            }
                        }

                        adapter = new NotificationsAdapter(notificationArrayList, getContext());
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                        adapter.setOnClickListener(BuyerNotificationFragment.this);
                    }
                });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buyer_notification, null);
        return view;
    }

    @Override
    public void onClick(int position) {

    }
}
