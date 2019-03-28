package com.example.tiktalk.UI.Activities.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.ResetPasswordActivity;
import com.example.tiktalk.UI.Activities.Seller.SellerLoginActivity;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class BuyerLoginActivity extends BaseActivity {

    private static int RC_SIGN_IN = 123;
    private static String TAG = "BuyerLoginActivity";
    private static final String APP_ID = "4B0405B2-D5BD-49F5-B912-C9F7C009F374";

    ProgressBar progressBar;
    ProgressDialog dialog;

    ImageView check;
    TextView signupBtn;
    Button googleSignInBtn;
    Button facebookSignInBtn;
    EditText emailEditText;
    EditText passwordEditText;
    Button loginBtn;
    TextView partnerLoginBtn;
    TextView forgotPasswordBtn;
    LinearLayout email_layout, password_layout;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    User user = new User();

    LoginButton regButton_fb;
    CallbackManager callbackManager;

    String isActive = "1";
    String type = "Buyer";
    String isOnline = "1";
    String coins = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_buyer_login);
        setupComponents();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);

        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        facebookSignInBtn = findViewById(R.id.facebookSignInBtn);
        signupBtn = findViewById(R.id.signup_textView);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        partnerLoginBtn = findViewById(R.id.partnerLogin_Btn);
        forgotPasswordBtn = findViewById(R.id.forget_textView);
        check = findViewById(R.id.check);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        regButton_fb = (LoginButton) findViewById(R.id.login_with_facebook);

        //Facebook button

        callbackManager = CallbackManager.Factory.create();
        regButton_fb.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        regButton_fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

//                        Toast.makeText(BuyerLoginActivity.this, "Success Login Success", Toast.LENGTH_SHORT).show();

                handleFacebookAccessToken(loginResult.getAccessToken());
                Intent in = new Intent(BuyerLoginActivity.this, BuyerDashboard.class);
                startActivity(in);
            }

            @Override
            public void onCancel() {
//                progressDialog.dismiss();
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
//                progressDialog.dismiss();
                System.out.println("onError");
                Toast.makeText(BuyerLoginActivity.this, "onError"+exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(BuyerLoginActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyerLoginActivity.this, BuyerSignupActivity.class);
                startActivity(intent);
                Bungee.slideUp(BuyerLoginActivity.this);
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (emailEditText.getText().toString().contains(".com")){
                    check.setVisibility(View.VISIBLE);
                }
                else
                    check.setVisibility(View.GONE);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        partnerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyerLoginActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyerLoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void setupListeners() {

    }

    //Facebook Result
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                            final FirebaseUser userDetails = auth.getCurrentUser();

                            if (newuser){

                                final HashMap<String, String> users = new HashMap<String, String>();
                                users.put("id", userDetails.getUid());
                                users.put("username", userDetails.getDisplayName());
                                users.put("email", userDetails.getEmail());
                                users.put("password", "");
                                users.put("imageUrl", String.valueOf(userDetails.getPhotoUrl()));
                                users.put("IsActive", isActive);
                                users.put("Type", type);
                                users.put("isOnline", isOnline);
                                users.put("coins", coins);

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .set(users)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                PreferenceUtils.saveBuyerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("imageUrl"), users.get("isOnline"), users.get("coins"), BuyerLoginActivity.this);

                                                MyFirebaseInstanceIDService.sendRegistrationToServer(BuyerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                dialog.dismiss();
                                                Intent in = new Intent(BuyerLoginActivity.this, BuyerDashboard.class);
                                                startActivity(in);
                                            }
                                        });
                            }
                            else{

                                HashMap<String, Object> online = new HashMap<String, Object>();
                                online.put("isOnline", "1");

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .update(online);

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    DocumentSnapshot dc = task.getResult();

                                                    user.username = dc.getString("username");
                                                    user.email = dc.getString("email");
                                                    user.password = dc.getString("password");
                                                    user.IsActive = dc.getString("IsActive");
                                                    user.Type = dc.getString("Type");
                                                    user.id = dc.getString("id");
                                                    user.imageUrl = dc.getString("imageUrl");
                                                    user.isOnline = dc.getString("isOnline");
                                                    user.coins = dc.getString("coins");

//                                                connectToSendBird(userId, userNickname);

                                                    PreferenceUtils.saveBuyerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.coins, BuyerLoginActivity.this);

                                                    MyFirebaseInstanceIDService.sendRegistrationToServer(BuyerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                    dialog.dismiss();
                                                    Intent intent = new Intent(BuyerLoginActivity.this, BuyerDashboard.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(BuyerLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            dialog.setMessage("Signing in...");
            dialog.show();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else {

            callbackManager.onActivityResult(requestCode, resultCode, data);
//            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                            final FirebaseUser userDetails = auth.getCurrentUser();

                            if(newuser){

                                final HashMap<String, String> users = new HashMap<String, String>();
                                users.put("id", userDetails.getUid());
                                users.put("username", userDetails.getDisplayName());
                                users.put("email", userDetails.getEmail());
                                users.put("password", "");
                                users.put("imageUrl", String.valueOf(userDetails.getPhotoUrl()));
                                users.put("IsActive", isActive);
                                users.put("Type", type);
                                users.put("isOnline", isOnline);
                                users.put("coins", coins);

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .set(users)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                PreferenceUtils.saveBuyerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("imageUrl"), users.get("isOnline"), users.get("coins"), BuyerLoginActivity.this);

                                                MyFirebaseInstanceIDService.sendRegistrationToServer(BuyerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                dialog.dismiss();
                                                Intent in = new Intent(BuyerLoginActivity.this, BuyerDashboard.class);
                                                startActivity(in);
                                                finish();
                                            }
                                        });

                            }else{
                                //Continue with Sign up

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    DocumentSnapshot dc = task.getResult();

                                                    user.username = dc.getString("username");
                                                    user.email = dc.getString("email");
                                                    user.password = dc.getString("password");
                                                    user.IsActive = dc.getString("IsActive");
                                                    user.Type = dc.getString("Type");
                                                    user.id = dc.getString("id");
                                                    user.imageUrl = dc.getString("imageUrl");
                                                    user.isOnline = dc.getString("isOnline");
                                                    user.coins = dc.getString("coins");

//                                                connectToSendBird(userId, userNickname);

                                                    PreferenceUtils.saveBuyerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.coins, BuyerLoginActivity.this);

                                                    MyFirebaseInstanceIDService.sendRegistrationToServer(BuyerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                    dialog.dismiss();
                                                    Intent intent = new Intent(BuyerLoginActivity.this, BuyerDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                            }

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = auth.getCurrentUser();
//                            Toast.makeText(BuyerLoginActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(BuyerLoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void login() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        if (TextUtils.isEmpty(email)) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(email_layout);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(password_layout);
            return;
        }

        //progressBar.setVisibility(View.VISIBLE);
        dialog.setMessage("Signing in...");
        dialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            final String currentUser = auth.getCurrentUser().getUid();

                            firestore.collection("users")
                                    .document(currentUser)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                DocumentSnapshot dc = task.getResult();

                                                user.username = dc.getString("username");
                                                user.email = dc.getString("email");
                                                user.password = dc.getString("password");
                                                user.IsActive = dc.getString("IsActive");
                                                user.Type = dc.getString("Type");
                                                user.id = dc.getString("id");
                                                user.imageUrl = dc.getString("imageUrl");
                                                user.isOnline = dc.getString("isOnline");
                                                user.coins = dc.getString("coins");

//                                                connectToSendBird(userId, userNickname);

                                                PreferenceUtils.saveBuyerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.coins, BuyerLoginActivity.this);

                                                MyFirebaseInstanceIDService.sendRegistrationToServer(BuyerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), currentUser);

                                                //progressBar.setVisibility(View.GONE);
                                                dialog.dismiss();
                                                Intent intent = new Intent(BuyerLoginActivity.this, BuyerDashboard.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });


                        } else
                            Toast.makeText(BuyerLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}
