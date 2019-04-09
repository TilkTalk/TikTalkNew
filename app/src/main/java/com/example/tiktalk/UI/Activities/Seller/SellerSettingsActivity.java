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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class SellerSettingsActivity extends BaseActivity {

    ProgressBar progressBar;
    ProgressDialog pDialog;

    private static int Image_Request_Code = 1;
    Button changePhoto_btn;
    ImageButton finishSettingsBtn;
    ImageView settings_image;
    TextView settings_email, settings_password;
    Spinner settings_spinner;
    Uri imageUri;
    Button update_btn;
    EditText about_edittext;

    FirebaseAuth auth;
    StorageReference storage;
    FirebaseFirestore firestore;
    private FirebaseUser user;

    ArrayAdapter<String> adapter;
    String newRates, email, password, about;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_settings);
        setupComponents();

        pDialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        pDialog.setIndeterminateDrawable(wave);
    }

    @Override
    public void initializeComponents() {

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("ProfileImages/Seller");
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        finishSettingsBtn = findViewById(R.id.finish_settings_btn);
        settings_image = findViewById(R.id.settings_image);
        settings_email = findViewById(R.id.settings_email);
        settings_password = findViewById(R.id.settings_password);
        changePhoto_btn = findViewById(R.id.changePhoto_btn);
        settings_spinner = findViewById(R.id.settings_spinner);
        update_btn = findViewById(R.id.update_btn);
        about_edittext = findViewById(R.id.about_edittext);

        final List<String> list = new ArrayList<String>();

        firestore.collection("users")
                .document(PreferenceUtils.getId(this))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        newRates = documentSnapshot.getString("$perMin");
                        email = documentSnapshot.getString("email");
                        password = documentSnapshot.getString("password");
                        about = documentSnapshot.getString("about");
                        PreferenceUtils.setRatePerMin(newRates, SellerSettingsActivity.this);
                        PreferenceUtils.setEmail(email, SellerSettingsActivity.this);
                        PreferenceUtils.setPassword(password, SellerSettingsActivity.this);
                        PreferenceUtils.setAbout(about, SellerSettingsActivity.this);

                    }
                });

        firestore.collection("rates")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            String rate = documentSnapshot.getString("amount");
                            list.add(rate);
                        }

                        Collections.sort(list);

                        adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        settings_spinner.setAdapter(adapter);
                        int selectionPosition= adapter.getPosition(newRates);
                        settings_spinner.setSelection(selectionPosition);
                    }
                });

        Glide.with(this).load(PreferenceUtils.getImageUrl(this)).into(settings_image);
        settings_email.setText(PreferenceUtils.getEmail(this));
        settings_password.setText(PreferenceUtils.getPassword(this));
        about_edittext.setText(PreferenceUtils.getAbout(this));

    }

    @Override
    public void setupListeners() {

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String val = settings_spinner.getSelectedItem().toString();
                float result = Float.valueOf(val);
                result = result * 10 + 1;

                HashMap<String, Object> update = new HashMap<>();
                update.put("email", settings_email.getText().toString());
                update.put("password", settings_password.getText().toString());
                update.put("$perMin", settings_spinner.getSelectedItem().toString());

                update.put("coinPerMin", String.valueOf((int) result));
                update.put("about", about_edittext.getText().toString());

                uploadImage();

                firestore.collection("users")
                        .document(PreferenceUtils.getId(SellerSettingsActivity.this))
                        .update(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent in = new Intent(SellerSettingsActivity.this, SellerHomeActivity.class);
                        startActivity(in);
                        finish();
                    }
                });
            }
        });

        finishSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerSettingsActivity.this, SellerHomeActivity.class);
                startActivity(in);
                finish();
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

            pDialog.setMessage("Loading...");
            pDialog.show();

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

                                            PreferenceUtils.saveImageUrl(uri.toString(), SellerSettingsActivity.this);

                                            firestore.collection("users")
                                                    .document(PreferenceUtils.getId(SellerSettingsActivity.this))
                                                    .update(map);

//                                            MyFirebaseInstanceIDService.sendRegistrationToServer(SellerSettingsActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), currentUser);

                                            PreferenceUtils.saveImageUrl(uri.toString(), SellerSettingsActivity.this);
                                            pDialog.dismiss();

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
            Picasso.with(SellerSettingsActivity.this).load(imageUri).centerCrop().resize(500, 500).into(settings_image);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(SellerSettingsActivity.this, SellerHomeActivity.class);
        startActivity(in);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
