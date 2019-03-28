package com.example.tiktalk;

import android.content.SharedPreferences;

import com.example.tiktalk.Model.User;
import com.example.tiktalk.Utils.GsonUtils;

import org.json.JSONObject;

public class UserPrefs {

    private static String Type = "user";

    private static SharedPreferences.Editor userPrefs = TikTalk.getContext().getSharedPreferences(Type+"Prefs", 0).edit();
    private static UserPrefs manager;
    public static UserPrefs getInstance() {
        if (manager == null)
            manager = new UserPrefs();
        return manager;
    }

    public static User getUser() {
        SharedPreferences sharedPreferences = TikTalk.getContext().getSharedPreferences(Type+"Prefs", 0);
        return GsonUtils.fromJSON(sharedPreferences.getString(Constants.USER,null), User.class);
    }

    public void saveUser(JSONObject user) {
        userPrefs.putString(Constants.USER, user.toString());
        userPrefs.commit();
    }

    public void clearUser() {
        userPrefs.remove(Constants.USER);
        userPrefs.commit();
    }

    private static final class Constants {
        static final String USER = Type,
                EMAIL = "email",
                NAME = "firstName",
                PHONE = "lastName",
                ADDRESS = "lastLogin",
                USER_ID = "userId",
                PROFILE_IMAGE = "image",
                CITY = "city",
                POST_CODE = "postcode",
                COUNTRY = "country",
                LATITUDE = "latitude",
                LONGITUDE = "longitude",
                ISDELIVERYALLOWED = "deliveryoption";
    }
}
