package com.example.tiktalk.SendBird;

import android.widget.Toast;

import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.Utils.AppUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

public class SendBirdService {

    public static void Connect(final String UserID, final String NickName, final String ProfilePicture){
        //Registering user to SendBird
        SendBird.connect(UserID, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
//                    AppUtils.Toast(e.getMessage());
                } else {
                    boolean autoAccept = true;    // If true, a user will automatically join a group channel with no choice of accepting and declining an invitation.
                    SendBird.setChannelInvitationPreference(autoAccept, new SendBird.SetChannelInvitationPreferenceHandler() {
                        @Override
                        public void onResult(SendBirdException e) {
                            if (e != null) {    // Error.
//                                AppUtils.Toast(e.getMessage());
                            }
                        }
                    });
                    SendBird.updateCurrentUserInfo(NickName, ProfilePicture, new SendBird.UserInfoUpdateHandler() {
                        @Override
                        public void onUpdated(SendBirdException e) {
                            if (e != null) {
//                                AppUtils.Toast(e.getMessage());
                            } else {
//                                MyFirebaseInstanceIDService.sendRegistrationToServer(SendBirdService.class.getSimpleName(), FirebaseInstanceId.getInstance().getToken(),UserID);
                                sendRegistrationToSendbirdServer(FirebaseInstanceId.getInstance().getToken());
//                                PreferenceUtils.setNotificationsShowPreviews(true);
                            }
                        }
                    });
                }
            }
        });
    }

    private static void sendRegistrationToSendbirdServer(final String token) {
        SendBird.registerPushTokenForCurrentUser(token, new SendBird.RegisterPushTokenWithStatusHandler() {
            @Override
            public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                if (e != null) {
//                    AppUtils.Toast("" + e.getCode() + ":" + e.getMessage());
                    return;
                }

                if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
//                    AppUtils.Toast("Connection required to register push token.");
                }
            }
        });
    }

}
