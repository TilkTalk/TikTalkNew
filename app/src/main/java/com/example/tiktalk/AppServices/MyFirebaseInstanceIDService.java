package com.example.tiktalk.AppServices;

import android.util.Log;
import android.widget.Toast;

import com.example.tiktalk.Utils.AppUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    static FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public static void sendRegistrationToServer(final String TAG, String token, String UserID) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("token", token);

        firestore.collection("users")
                .document(UserID)
                .update(map);

    }

    public static void deleteRegistrationFromServer(final String TAG, String UserID) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("token", "");

        firestore.collection("users")
                .document(UserID)
                .update(map);

    }
}

