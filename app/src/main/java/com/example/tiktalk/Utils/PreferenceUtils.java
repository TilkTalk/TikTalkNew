package com.example.tiktalk.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.tiktalk.Model.User;
import com.example.tiktalk.TikTalk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferenceUtils {

    //static SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TikTalk.getContext());

   public static ArrayList<User>buyerArrayList=new ArrayList<>();
    public PreferenceUtils(){

    }

    //Login
    public static boolean saveBuyerData(String username, String email, String password, String id, String isActive, String type, String imageUrl, String isOnline, String coins, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("username", username);
        prefsEditor.putString("email", email);
        prefsEditor.putString("password", password);
        prefsEditor.putString("id", id);
        prefsEditor.putString("IsActive", isActive);
        prefsEditor.putString("Type", type);
        prefsEditor.putString("imageUrl", imageUrl);
        prefsEditor.putString("isOnline", isOnline);
        prefsEditor.putString("coins", coins);
        prefsEditor.apply();
        return true;
    }

    //Signup
    public static boolean saveBuyerData(String username, String email, String password, String id, String isActive, String type, String isOnline, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("username", username);
        prefsEditor.putString("email", email);
        prefsEditor.putString("password", password);
        prefsEditor.putString("id", id);
        prefsEditor.putString("IsActive", isActive);
        prefsEditor.putString("Type", type);
        prefsEditor.putString("isOnline", isOnline);
        prefsEditor.apply();
        return true;
    }

    public static boolean saveImageUrl(String imageUrl, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("imageUrl", imageUrl);
        prefsEditor.apply();
        return true;
    }

    public static boolean saveSellerData(String username, String email, String password, String id, String isActive, String type, String imageUrl, String isOnline, String value, String rating, String coins, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("username", username);
        prefsEditor.putString("email", email);
        prefsEditor.putString("password", password);
        prefsEditor.putString("id", id);
        prefsEditor.putString("IsActive", isActive);
        prefsEditor.putString("Type", type);
        prefsEditor.putString("imageUrl", imageUrl);
        prefsEditor.putString("isOnline", isOnline);
        prefsEditor.putString("$perMin", value);
        prefsEditor.putString("rating", rating);
        prefsEditor.putString("coinPerMin", coins);
        prefsEditor.apply();
        return true;
    }

    public static boolean saveSellerData(String username, String email, String password, String id, String isActive, String type, String isOnline, String value, String rating, String coins, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("username", username);
        prefsEditor.putString("email", email);
        prefsEditor.putString("password", password);
        prefsEditor.putString("id", id);
        prefsEditor.putString("IsActive", isActive);
        prefsEditor.putString("Type", type);
        prefsEditor.putString("isOnline", isOnline);
        prefsEditor.putString("$perMin", value);
        prefsEditor.putString("rating", rating);
        prefsEditor.putString("coinPerMin", coins);
        prefsEditor.apply();
        return true;
    }

    /*public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("email", null);
    }*/

    public static boolean savePassword(String password, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("password", password);
        prefsEditor.apply();
        return true;
    }

    public static String getCoinPerMin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("coinPerMin", null);
    }

    public static String getUsername(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("username", null);
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("email", null);
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("password", null);
    }

    public static String getId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("id", null);
    }

    public static String getIsActive(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("IsActive", null);
    }

    public static String getType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("Type", null);
    }

    public static String getImageUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("imageUrl", null);
    }

    public static String getCoins(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("coins", null);
    }

    public static boolean clearMemory(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        return true;
    }

    public static ArrayList<User> getBuyerData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, String> buyerDetails = new HashMap<String,String>();

        User ul = new User(
                prefs.getString("username", null),
                prefs.getString("email", null),
                prefs.getString("password", null),
                prefs.getString("IsActive", null),
                prefs.getString("Type", null),
                prefs.getString("id", null),
                prefs.getString("imageUrl", null),
                prefs.getString("isOnline", null),
                prefs.getString("coins", null));
        buyerArrayList.add(ul);

        return buyerArrayList;

    }

    public static boolean setSellerUsername(String username, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("SellerName", username);
        prefsEditor.apply();
        return true;
    }

    public static boolean setSellerImageUrl(String imageUrl, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("SellerImage", imageUrl);
        prefsEditor.apply();
        return true;
    }

    public static String getSellerUsername(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("SellerName", null);
    }

    public static String getSellerImageUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("SellerImage", null);
    }

    public static String getRatePerMin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("$perMin", null);
    }

}
