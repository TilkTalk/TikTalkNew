package com.example.tiktalk.UI.Activities.Seller;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class UploadPhotoActivity extends BaseActivity {

    private static int Image_Request_Code = 1;
    Button cancelBtn, doneBtn;
    String name, email, password;

    String isActive = "0";
    String type = "Seller";
    String isOnline = "1";
    String rating = "0";

    Uri imageUri;

    FirebaseAuth auth;
    StorageReference storage;
    FirebaseFirestore firestore;

    ImageView seller_signup_image;
    Button seller_uploadPhoto_btn;
    Spinner seller_spinner;
    String value, coins;

    ProgressDialog dialog;
    ProgressBar progressBar;
    ArrayAdapter<String> adapter;
    AlertDialog alert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_uploadphoto);
        setupComponents();

    }

    @Override
    public void initializeComponents() {


        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.signup_spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("ProfileImages/Seller");
        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        cancelBtn = findViewById(R.id.cancel_btn);
        doneBtn = findViewById(R.id.seller_done_btn);
        seller_signup_image = findViewById(R.id.seller_signup_image);
        seller_uploadPhoto_btn = findViewById(R.id.seller_uploadPhoto_btn);
        seller_spinner = findViewById(R.id.seller_spinner);
        final List<String> list = new ArrayList<String>();

        firestore.collection("rates")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            String rate = documentSnapshot.getString("totalAmount");
                            list.add(rate);
                        }

                        Collections.sort(list);

                        adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        seller_spinner.setAdapter(adapter);
                    }
                });

    }

    @Override
    public void setupListeners() {

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        seller_uploadPhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please select Image!"), Image_Request_Code);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent in = new Intent(UploadPhotoActivity.this, SellerDashboardActivity.class);
                startActivity(in);
                Bungee.slideDown(UploadPhotoActivity.this);*/

                if (seller_spinner.getSelectedItem().toString().equals("$0.2")){

                    value = "0.2";
                    coins = "3";
                }

                if (seller_spinner.getSelectedItem().toString().equals("$0.3")){

                    value = "0.3";
                    coins = "4";
                }

                if (seller_spinner.getSelectedItem().toString().equals("$0.4")){

                    value = "0.4";
                    coins = "5";
                }

                if (seller_spinner.getSelectedItem().toString().equals("$0.5")){

                    value = "0.5";
                    coins = "6";
                }

                if (seller_spinner.getSelectedItem().toString().equals("$0.6")){

                    value = "0.6";
                    coins = "7";
                }

                if (seller_spinner.getSelectedItem().toString().equals("$0.7")){

                    value = "0.7";
                    coins = "8";
                }

                dialog.setMessage("Signing up...");
                dialog.show();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    final HashMap<String, Object> users = new HashMap<String, Object>();
                                    users.put("id", currentUser);
                                    users.put("username", name);
                                    users.put("email", email);
                                    users.put("password", password);
                                    users.put("IsActive", isActive);
                                    users.put("Type", type);
                                    users.put("isOnline", isOnline);
                                    users.put("$perMin", value);
                                    users.put("rating", rating);
                                    users.put("coinPerMin", coins);

                                    firestore.collection("users")
                                            .document(currentUser)
                                            .set(users)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    PreferenceUtils.saveSellerData(String.valueOf(users.get("username")), String.valueOf(users.get("email")), String.valueOf(users.get("password")), String.valueOf(users.get("id")), String.valueOf(users.get("IsActive")), String.valueOf(users.get("Type")), String.valueOf(users.get("isOnline")), String.valueOf(users.get("$perMin")), String.valueOf(users.get("rating")), String.valueOf(users.get("coinPerMin")),UploadPhotoActivity.this);
                                                    uploadImage();
                                                }
                                            });
                                } else
                                    showToast("Failed");
                            }
                        });
            }
        });

    }

    private void uploadImage() {

        final String currentUser = auth.getCurrentUser().getUid();

        if (imageUri != null){

            final StorageReference fileRef = storage.child(currentUser + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                            map.put("imageUrl", uri.toString());

                                            PreferenceUtils.saveImageUrl(uri.toString(), UploadPhotoActivity.this);

                                            firestore.collection("users")
                                                    .document(currentUser)
                                                    .update(map);

                                            MyFirebaseInstanceIDService.sendRegistrationToServer(UploadPhotoActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), currentUser);

                                            dialog.dismiss();

                                            final AlertDialog.Builder dialog = new AlertDialog.Builder(UploadPhotoActivity.this);
                                            dialog.setTitle("Approval Required!");
                                            dialog.setMessage("Please wait for the approval of your account.");

                                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(UploadPhotoActivity.this, SellerLoginActivity.class);
                                                    startActivity(intent);
                                                    Bungee.slideDown(UploadPhotoActivity.this);
                                                    finish();
                                                }
                                            });

                                            dialog.show();
                                        }
                                    });
                            showToast("Image uploaded!");
                        }
                    });

        }
        else{
            showToast("No image selected!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code  && resultCode == RESULT_OK && data != null && data.getData() != null ) {

            imageUri = data.getData();
            Picasso.with(UploadPhotoActivity.this).load(imageUri).centerCrop().resize(500,500).into(seller_signup_image);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
