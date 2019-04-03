package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class BuyerSettingsActivity extends BaseActivity {

    ProgressBar progressBar;
    ProgressDialog pDialog;

    private static int Image_Request_Code = 1;
    Button finishSettingsBtn, changePhoto_btn;
    ImageView settings_image;
    TextView settings_email, settings_password;
    Uri imageUri;

    FirebaseAuth auth;
    StorageReference storage;
    FirebaseFirestore firestore;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_settings);
        setupComponents();

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("ProfileImages/Buyer");
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        pDialog.setIndeterminateDrawable(wave);
    }

    @Override
    public void initializeComponents() {

        finishSettingsBtn = findViewById(R.id.finish_settings_btn);
        settings_image = findViewById(R.id.settings_image);
        settings_email = findViewById(R.id.settings_email);
        settings_password = findViewById(R.id.settings_password);
        changePhoto_btn = findViewById(R.id.changePhoto_btn);

        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(settings_image);
        settings_email.setText(PreferenceUtils.getEmail(this));
        settings_password.setText(PreferenceUtils.getPassword(this));
    }

    @Override
    public void setupListeners() {
        finishSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BuyerSettingsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Save Changes?")
                        .setMessage("Are you sure you want to close this activity?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                user = FirebaseAuth.getInstance().getCurrentUser();

                                pDialog.setMessage("Loading...");
                                pDialog.show();

                                uploadImage();

                                if(!PreferenceUtils.getEmail(BuyerSettingsActivity.this).equals(settings_email.getText().toString()) &&
                                        !PreferenceUtils.getPassword(BuyerSettingsActivity.this).equals(settings_password.getText().toString())){

                                    final HashMap<String, Object> changes = new HashMap<>();
                                    changes.put("email", settings_email.getText().toString());
                                    changes.put("password", settings_password.getText().toString());

                                    firestore.collection("users")
                                            .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                            .update(changes);

                                    user.updateEmail(settings_email.getText().toString());
                                    user.updatePassword(settings_password.getText().toString());

                                    pDialog.dismiss();
                                    finish();
                                }
                                else {

                                    if(!PreferenceUtils.getEmail(BuyerSettingsActivity.this).equals(settings_email.getText().toString())){

                                        final HashMap<String, Object> changes = new HashMap<>();
                                        changes.put("email", settings_email.getText().toString());

                                        firestore.collection("users")
                                                .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                                .update(changes);

                                        user.updateEmail(settings_email.getText().toString());

                                        pDialog.dismiss();
                                        finish();
                                    }
                                    else if(!PreferenceUtils.getPassword(BuyerSettingsActivity.this).equals(settings_password.getText().toString())){

                                        final HashMap<String, Object> changes = new HashMap<>();
                                        changes.put("password", settings_password.getText().toString());

                                        firestore.collection("users")
                                                .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                                .update(changes);

                                        user.updatePassword(settings_password.getText().toString());

                                        pDialog.dismiss();
                                        finish();

                                    }

                                    pDialog.dismiss();
                                    finish();
                                }
                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        changePhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please select Image!"), Image_Request_Code);
            }
        });
    }

    private void uploadImage() {

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (imageUri != null) {

            final StorageReference fileRef = storage.child(PreferenceUtils.getId(this) + "." + getFileExtension(imageUri));

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

                                            PreferenceUtils.saveImageUrl(uri.toString(), BuyerSettingsActivity.this);

                                            firestore.collection("users")
                                                    .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                                    .update(map);

                                            MyFirebaseInstanceIDService.sendRegistrationToServer(BuyerSettingsActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), currentUser);

//                                            dialog.dismiss();
                                            Intent intent = new Intent(BuyerSettingsActivity.this, BuyerHomeActivity.class);
                                            startActivity(intent);
                                            Bungee.slideDown(BuyerSettingsActivity.this);
                                        }
                                    });
                        }
                    });

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            Picasso.with(BuyerSettingsActivity.this).load(imageUri).centerCrop().resize(500, 500).into(settings_image);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Save Changes?")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user = FirebaseAuth.getInstance().getCurrentUser();

                        pDialog.setMessage("Loading...");
                        pDialog.show();

                        uploadImage();

                        if(!PreferenceUtils.getEmail(BuyerSettingsActivity.this).equals(settings_email.getText().toString()) &&
                                !PreferenceUtils.getPassword(BuyerSettingsActivity.this).equals(settings_password.getText().toString())){

                            final HashMap<String, Object> changes = new HashMap<>();
                            changes.put("email", settings_email.getText().toString());
                            changes.put("password", settings_password.getText().toString());

                            firestore.collection("users")
                                    .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                    .update(changes);

                            user.updateEmail(settings_email.getText().toString());
                            user.updatePassword(settings_password.getText().toString());

                            pDialog.dismiss();
                            finish();
                        }
                        else {

                            if(!PreferenceUtils.getEmail(BuyerSettingsActivity.this).equals(settings_email.getText().toString())){

                                final HashMap<String, Object> changes = new HashMap<>();
                                changes.put("email", settings_email.getText().toString());

                                firestore.collection("users")
                                        .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                        .update(changes);

                                user.updateEmail(settings_email.getText().toString());

                                pDialog.dismiss();
                                finish();
                            }
                            else if(!PreferenceUtils.getPassword(BuyerSettingsActivity.this).equals(settings_password.getText().toString())){

                                final HashMap<String, Object> changes = new HashMap<>();
                                changes.put("password", settings_password.getText().toString());

                                firestore.collection("users")
                                        .document(PreferenceUtils.getId(BuyerSettingsActivity.this))
                                        .update(changes);

                                user.updatePassword(settings_password.getText().toString());

                                pDialog.dismiss();
                                finish();

                            }

                            pDialog.dismiss();
                            finish();
                        }
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
