package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class UploadPhotoActivity extends AppCompatActivity {

    private static int Image_Request_Code = 1;

    ImageView signUpImage;
    Button doneBtn;
    Button uploadPhotoBtn;
    Button cancel_btn;

    String name;
    String email;
    String password;

    Uri imageUri;

    FirebaseAuth auth;
    StorageReference storage;
    FirebaseFirestore firestore;

    String isActive = "1";
    String type = "Buyer";
    String isOnline = "1";
    String imageUrl;
    ProgressDialog dialog;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_uploadphoto);

        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.signup_spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("ProfileImages/Buyer");
        firestore = FirebaseFirestore.getInstance();

        signUpImage = findViewById(R.id.signup_image);
        doneBtn = findViewById(R.id.done_btn);
        uploadPhotoBtn = findViewById(R.id.uploadPhoto_btn);
        cancel_btn = findViewById(R.id.uploadPhoto_cancel_btn);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please select Image!"), Image_Request_Code);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setMessage("Signing up...");
                dialog.show();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    final HashMap<String, String> users = new HashMap<String, String>();
                                    users.put("id", currentUser);
                                    users.put("username", name);
                                    users.put("email", email);
                                    users.put("password", password);
                                    users.put("IsActive", isActive);
                                    users.put("Type", type);
                                    users.put("isOnline", isOnline);

                                    firestore.collection("users")
                                            .document(currentUser)
                                            .set(users)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    PreferenceUtils.saveBuyerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("isOnline"),UploadPhotoActivity.this);

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

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                                            Intent intent = new Intent(UploadPhotoActivity.this, BuyerHomeActivity.class);
                                            startActivity(intent);
                                            Bungee.slideDown(UploadPhotoActivity.this);
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
            Picasso.with(UploadPhotoActivity.this).load(imageUri).centerCrop().resize(500,500).into(signUpImage);

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
