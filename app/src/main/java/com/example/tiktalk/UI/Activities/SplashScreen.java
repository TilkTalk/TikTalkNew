package com.example.tiktalk.UI.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerDashboard;
import com.example.tiktalk.UI.Activities.Buyer.BuyerHomeActivity;
import com.example.tiktalk.UI.Activities.Buyer.BuyerLoginActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerDashboardActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerHomeActivity;
import com.example.tiktalk.Utils.PreferenceUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import spencerstudios.com.bungeelib.Bungee;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.tiktalk",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                //74:B8:EF:A4:C7:CD:FD:37:5D:B4:93:84:D9:D4:74:C3:2E:49:A4:75

                byte[] sha1 = {
                        0x74, (byte)0xB8, (byte)0xEF, (byte)0xA4, (byte)0xC7, (byte)0xCD, (byte)0xFD, (byte)0x37, (byte)0x5D, (byte)0xB4, (byte)0x93, (byte)0x84, (byte)0xD9, (byte)0xD4, 0x74, (byte)0xC3, 0x2E, 0x49, (byte)0xA4, (byte)0x75
                };
                System.out.println("keyhashGooglePlaySignIn:"+ Base64.encodeToString(sha1, Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if (PreferenceUtils.getEmail(this) != null ){

            if (PreferenceUtils.getType(this).equals("Buyer")){

                Intent intent = new Intent(SplashScreen.this, BuyerHomeActivity.class);
                startActivity(intent);
                finish();
            }
            else  {

                Intent intent = new Intent(SplashScreen.this, SellerHomeActivity.class);
                startActivity(intent);
                finish();
            }

        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, BuyerLoginActivity.class);
                    startActivity(intent);
                    Bungee.slideUp(SplashScreen.this);
                    finish();
                }
            }, 2000);

        }
    }
}
