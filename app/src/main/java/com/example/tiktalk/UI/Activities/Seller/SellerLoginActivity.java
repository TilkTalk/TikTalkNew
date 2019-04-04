package com.example.tiktalk.UI.Activities.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Arrays;
import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_STATIC_DP;

public class SellerLoginActivity extends AppCompatActivity {

    private static int RC_SIGN_IN = 123;
    private static String TAG = "SellerLoginActivity";
    private GoogleSignInClient mGoogleSignInClient;

    ProgressBar progressBar;
    ProgressDialog dialog;

    Button loginBtn, cancelBtn;
    TextView signupBtn;
    EditText emailEditText;
    EditText passwordEditText;
    ImageView check;
    LinearLayout email_layout, password_layout;
    Button googleSignInBtn;
    Button facebookSignInBtn;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    User user = new User();

    LoginButton regButton_fb;
    CallbackManager callbackManager;

    String isActive = "1";
    String type = "Seller";
    String isOnline = "1";
    String rating = "0";
    String $perMin = "0";
    String coinPerMin = "0";
    String dollersEarned = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);

        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        loginBtn = findViewById(R.id.login_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        signupBtn = findViewById(R.id.signup_textView);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        check = findViewById(R.id.check);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        facebookSignInBtn = findViewById(R.id.facebookSignInBtn);
        regButton_fb = (LoginButton) findViewById(R.id.login_with_facebook);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("459654563361-f1f2d6fkhlpbim0ljb7rrabs4gdf7vrq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        PushDownAnim.setPushDownAnimTo(googleSignInBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        signIn();
                    }
                });

        //Facebook button
        callbackManager = CallbackManager.Factory.create();
        regButton_fb.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        regButton_fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                Toast.makeText(SellerLoginActivity.this, "onError" + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        PushDownAnim.setPushDownAnimTo(facebookSignInBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LoginManager.getInstance().logInWithReadPermissions(SellerLoginActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
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

        PushDownAnim.setPushDownAnimTo(loginBtn)
                .setScale(MODE_STATIC_DP, 3)
                .setDurationPush(0)
                .setDurationRelease(300)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SellerLoginActivity.this, SellerSignupActivity.class);
                startActivity(in);
                Bungee.slideUp(SellerLoginActivity.this);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

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

                            if (newuser) {

                                final HashMap<String, String> users = new HashMap<String, String>();
                                users.put("id", userDetails.getUid());
                                users.put("username", userDetails.getDisplayName());
                                users.put("email", userDetails.getEmail());
                                users.put("password", "");
                                users.put("imageUrl", String.valueOf(userDetails.getPhotoUrl()));
                                users.put("IsActive", isActive);
                                users.put("Type", type);
                                users.put("isOnline", isOnline);
                                users.put("rating", rating);
                                users.put("$perMin", $perMin);
                                users.put("coinPerMin", coinPerMin);
                                users.put("dollersEarned", dollersEarned);

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .set(users)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                PreferenceUtils.saveBuyerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("imageUrl"), users.get("isOnline"), users.get("coins"), SellerLoginActivity.this);
                                                MyFirebaseInstanceIDService.sendRegistrationToServer(SellerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                dialog.dismiss();
                                                Intent in = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                                startActivity(in);
                                                finish();
                                            }
                                        });
                            } else {

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    DocumentSnapshot dc = task.getResult();

                                                    if (dc.getString("Type").equals("Buyer")){

                                                        Toast.makeText(SellerLoginActivity.this, "You are already signed up as a Buyer!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                    else {

                                                        HashMap<String, Object> online = new HashMap<String, Object>();
                                                        online.put("isOnline", "1");

                                                        firestore.collection("users")
                                                                .document(userDetails.getUid())
                                                                .update(online);

                                                        user.username = dc.getString("username");
                                                        user.email = dc.getString("email");
                                                        user.password = dc.getString("password");
                                                        user.IsActive = dc.getString("IsActive");
                                                        user.Type = dc.getString("Type");
                                                        user.id = dc.getString("id");
                                                        user.imageUrl = dc.getString("imageUrl");
                                                        user.isOnline = dc.getString("isOnline");
                                                        user.rating = dc.getString("rating");
                                                        user.ratePerMin = dc.getString("$perMin");
                                                        user.coinPerMin = dc.getString("coinPerMin");
                                                        user.dollersEarned = dc.getString("dollersEarned");

                                                        PreferenceUtils.saveBuyerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.coins, SellerLoginActivity.this);
                                                        MyFirebaseInstanceIDService.sendRegistrationToServer(SellerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                        dialog.dismiss();
                                                        Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }


                                                }
                                            }
                                        });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SellerLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else {

            callbackManager.onActivityResult(requestCode, resultCode, data);
            dialog.setMessage("Signing in...");
            dialog.show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            dialog.setMessage("Signing in...");
            dialog.show();
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                                final FirebaseUser userDetails = auth.getCurrentUser();

                                if (newuser) {

                                    final HashMap<String, String> users = new HashMap<String, String>();
                                    users.put("id", userDetails.getUid());
                                    users.put("username", userDetails.getDisplayName());
                                    users.put("email", userDetails.getEmail());
                                    users.put("password", "");
                                    users.put("imageUrl", String.valueOf(userDetails.getPhotoUrl()));
                                    users.put("IsActive", isActive);
                                    users.put("Type", type);
                                    users.put("isOnline", isOnline);
                                    users.put("rating", rating);
                                    users.put("$perMin", $perMin);
                                    users.put("coinPerMin", coinPerMin);
                                    users.put("dollersEarned", dollersEarned);

                                    firestore.collection("users")
                                            .document(userDetails.getUid())
                                            .set(users)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    PreferenceUtils.saveBuyerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("imageUrl"), users.get("isOnline"), users.get("coins"), SellerLoginActivity.this);
                                                    MyFirebaseInstanceIDService.sendRegistrationToServer(SellerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                    dialog.dismiss();
                                                    Intent in = new Intent(SellerLoginActivity.this, SellerDashboardActivity.class);
                                                    startActivity(in);
                                                    finish();
                                                }
                                            });

                                } else {

                                    firestore.collection("users")
                                            .document(userDetails.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot dc = task.getResult();

                                                        if (dc.getString("Type").equals("Buyer")){
                                                            Toast.makeText(SellerLoginActivity.this, "You are already signed up as a Buyer!", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                        else {

                                                            HashMap<String, Object> online = new HashMap<String, Object>();
                                                            online.put("isOnline", "1");

                                                            firestore.collection("users")
                                                                    .document(userDetails.getUid())
                                                                    .update(online);

                                                            user.username = dc.getString("username");
                                                            user.email = dc.getString("email");
                                                            user.password = dc.getString("password");
                                                            user.IsActive = dc.getString("IsActive");
                                                            user.Type = dc.getString("Type");
                                                            user.id = dc.getString("id");
                                                            user.imageUrl = dc.getString("imageUrl");
                                                            user.isOnline = dc.getString("isOnline");
                                                            user.rating = dc.getString("rating");
                                                            user.ratePerMin = dc.getString("$perMin");
                                                            user.coinPerMin = dc.getString("coinPerMin");
                                                            user.dollersEarned = dc.getString("dollersEarned");

                                                            PreferenceUtils.saveBuyerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.coins, SellerLoginActivity.this);
                                                            MyFirebaseInstanceIDService.sendRegistrationToServer(SellerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                            dialog.dismiss();
                                                            Intent intent = new Intent(SellerLoginActivity.this, SellerDashboardActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                }
                                            });
                                }
                                Log.d(TAG, "signInWithCredential:success");
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(SellerLoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } else {
            Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();
        }
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
                                                user.rating = dc.getString("rating");
                                                user.ratePerMin = dc.getString("$perMin");
                                                user.coinPerMin = dc.getString("coinPerMin");

                                                PreferenceUtils.saveSellerData(user.username, user.email, user.password, user.id, user.IsActive, user.Type, user.imageUrl, user.isOnline, user.ratePerMin, user.rating, user.coinPerMin, SellerLoginActivity.this);

                                                MyFirebaseInstanceIDService.sendRegistrationToServer(SellerLoginActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), currentUser);

                                                dialog.dismiss();

                                                if (user.IsActive.equals("1")){

                                                    HashMap<String, Object> online = new HashMap<String, Object>();
                                                    online.put("isOnline", "1");

                                                    firestore.collection("users")
                                                            .document(currentUser)
                                                            .update(online);

                                                    Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else {
                                                    Toast.makeText(SellerLoginActivity.this, "Your account is not approved yet.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });


                        } else
                            Toast.makeText(SellerLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
