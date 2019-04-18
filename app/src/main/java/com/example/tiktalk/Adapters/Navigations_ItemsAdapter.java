package com.example.tiktalk.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class Navigations_ItemsAdapter extends BaseAdapter {

    public Context context;
    public String[] menuName;
    public int[] menuIcons;
    public LayoutInflater layoutInflater;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public Navigations_ItemsAdapter(FragmentActivity activity, String[] menuName, int[] menuIcons) {

        this.context = activity;
        this.menuName = menuName;
        this.menuIcons = menuIcons;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return menuName.length;
    }

    @Override
    public Object getItem(int i) {
        return menuName[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View view1 = layoutInflater.inflate(R.layout.navinner_view, null);

        TextView textView = (TextView) view1.findViewById(R.id.text_nav);
        ImageView imageView = (ImageView) view1.findViewById(R.id.icon_nav);
        final TextView notification_count = (TextView) view1.findViewById(R.id.notification_count);
        final CardView notification_cardView = view1.findViewById(R.id.notification_cardView);

        textView.setText(menuName[i]);
        imageView.setImageResource(menuIcons[i]);

        firestore.collection("notifications")
                .whereEqualTo("receiver", PreferenceUtils.getId(context))
                .whereEqualTo("status", "unread")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        int notificationCount = 0;
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                            switch (dc.getType()) {
                                case ADDED: {
                                    notificationCount = queryDocumentSnapshots.getDocuments().size();
                                    break;
                                }

                                case MODIFIED: {
                                    notificationCount = queryDocumentSnapshots.getDocuments().size();
                                    break;
                                }

                                case REMOVED: {
                                    notificationCount = queryDocumentSnapshots.getDocuments().size();
                                    break;
                                }
                            }
                        }

                        notification_count.setText(String.valueOf(notificationCount));

                        if (menuName[i].equals("Notifications")) {
                            if (String.valueOf(notificationCount).equals("0")){
                                notification_cardView.setVisibility(View.GONE);
                            }
                            else {
                                notification_cardView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            notification_cardView.setVisibility(View.GONE);
                        }

                    }
                });


        return view1;
    }


}
