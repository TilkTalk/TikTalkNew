package com.example.tiktalk.UI.Activities.Seller;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.tiktalk.AppServices.MyFirebaseInstanceIDService;
import com.example.tiktalk.BaseClasses.BaseActivity;
import com.example.tiktalk.R;
import com.example.tiktalk.UI.Activities.Buyer.BuyerSignupActivity;
import com.example.tiktalk.UI.Activities.NumberVerificationActivity;
import com.example.tiktalk.Utils.AppUtils;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Range;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.validation.Validator;

import spencerstudios.com.bungeelib.Bungee;

public class SellerSignupActivity extends BaseActivity {

    ProgressBar progressBar;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 123;
    private static String TAG = "BuyerLoginActivity";
    String emailPattern = "[a-zA-Z0-9._-a-zA-Z0-9]+@[a-z._-]+\\.+[a-z]+";
    private AwesomeValidation awesomeValidation;

    CardView name_cardview, email_cardview, password_cardview;
    View name_view, email_view, password_view;
    EditText nameEditTxt;
    EditText emailEditTxt;
    EditText passwordEditTxt;
    Button signUpBtn;
    ImageButton cancelBtn;
    TextView signinTextview;
    Button facebookSignInBtn;
    Button googleSignInBtn;
    String code;

    LoginButton regButton_fb;
    CallbackManager callbackManager;

    String isActive = "0";
    String type = "Seller";
    String isOnline = "0";
    String rating = "0";
    String $perMin = "free";
    String coinPerMin = "free";
    String dollersEarned = "0";
    String about = "";
    String notifications = "0";

    String name, email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_seller_signup);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        Random random = new Random();
        code = String.format("%04d", random.nextInt(10000));

        setupComponents();

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
                Toast.makeText(SellerSignupActivity.this, "onError" + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        PushDownAnim.setPushDownAnimTo(facebookSignInBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginManager.getInstance().logInWithReadPermissions(SellerSignupActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
                    }
                });
    }

    @Override
    public void initializeComponents() {

        dialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        dialog.setIndeterminateDrawable(wave);

        nameEditTxt = findViewById(R.id.signup_name_seller);
        emailEditTxt = findViewById(R.id.signup_email_seller);
        passwordEditTxt = findViewById(R.id.signup_password_seller);
        signUpBtn = findViewById(R.id.signup_btn_seller);
        cancelBtn = findViewById(R.id.sign_cancel_btn);
        signinTextview = findViewById(R.id.signin_textView);
        name_view = findViewById(R.id.name_view);
        email_view = findViewById(R.id.email_view);
        password_view = findViewById(R.id.password_view);
        facebookSignInBtn = findViewById(R.id.facebookSignInBtn);
        regButton_fb = (LoginButton) findViewById(R.id.login_with_facebook);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        name_cardview = findViewById(R.id.name_cardview);
        email_cardview = findViewById(R.id.email_cardview);
        password_cardview = findViewById(R.id.password_cardview);

    }

    @Override
    public void setupListeners() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerSignupActivity.this, BuyerSignupActivity.class);
                startActivity(intent);
                Bungee.slideRight(SellerSignupActivity.this);
                finish();
            }
        });

        signinTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Bungee.slideUp(SellerSignupActivity.this);
            }
        });

        nameEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name_cardview.setCardElevation(4f);
                    email_cardview.setCardElevation(0f);
                    password_cardview.setCardElevation(0f);
                    name_view.setVisibility(View.GONE);
                    email_view.setVisibility(View.VISIBLE);
                    password_view.setVisibility(View.VISIBLE);
                }
            }
        });

        emailEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name_cardview.setCardElevation(0f);
                    email_cardview.setCardElevation(4f);
                    password_cardview.setCardElevation(0f);
                    name_view.setVisibility(View.VISIBLE);
                    email_view.setVisibility(View.GONE);
                    password_view.setVisibility(View.VISIBLE);
                }
            }
        });

        passwordEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name_cardview.setCardElevation(0f);
                    email_cardview.setCardElevation(0f);
                    password_cardview.setCardElevation(4f);
                    name_view.setVisibility(View.VISIBLE);
                    email_view.setVisibility(View.VISIBLE);
                    password_view.setVisibility(View.GONE);
                }
            }
        });
    }

    private void signUp() {
        String verificationCode = code;
        Intent intent = new Intent(this, NumberVerificationActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("code", verificationCode);
        startActivity(intent);
        Bungee.slideLeft(SellerSignupActivity.this);
        finish();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void SendOTP() {

        signUp();

        /*BackgroundMail bm = new BackgroundMail(this);

        bm.newBuilder(this)
                .withUsername("benibinyaminbenibinyamin@gmail.com")
                .withPassword("zxc!asd123")
                .withMailto(emailEditTxt.getText().toString())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Verification Code")
                .withBody(code)
                .withSendingMessage("Sending OTP")
                .send();*/

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
        } else {
//            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
            dialog.setMessage("Signing up...");
            dialog.show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            dialog.setMessage("Signing up...");
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
                                    users.put("about", about);
                                    users.put("notifications", notifications);

                                    firestore.collection("users")
                                            .document(userDetails.getUid())
                                            .set(users)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

//                                                    PreferenceUtils.saveSellerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("imageUrl"), users.get("isOnline"), users.get("$perMin"), users.get("rating"), users.get("coinPerMin"), users.get("about"), users.get("notifications"), SellerSignupActivity.this);
//                                                    MyFirebaseInstanceIDService.sendRegistrationToServer(SellerSignupActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                    dialog.dismiss();
//                                                    Intent in = new Intent(SellerSignupActivity.this, SellerHomeActivity.class);
//                                                    startActivity(in);
//                                                    finish();
                                                    final AlertDialog.Builder dialog = new AlertDialog.Builder(SellerSignupActivity.this);
                                                    dialog.setTitle("Approval Required!");
                                                    dialog.setMessage("Please wait for the approval of your account.");

                                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            Intent intent = new Intent(SellerSignupActivity.this, SellerLoginActivity.class);
                                                            startActivity(intent);
                                                            Bungee.slideDown(SellerSignupActivity.this);
                                                            finish();
                                                        }
                                                    });

                                                    dialog.show();
                                                }
                                            });

                                } else {
                                    //User already signed up.
                                    //Go to login activity to sign in.

                                    showToast("You are already signed up!");
                                    dialog.dismiss();

                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(SellerSignupActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } else {
            Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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
                                users.put("about", about);

                                firestore.collection("users")
                                        .document(userDetails.getUid())
                                        .set(users)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

//                                                PreferenceUtils.saveSellerData(users.get("username"), users.get("email"), users.get("password"), users.get("id"), users.get("IsActive"), users.get("Type"), users.get("imageUrl"), users.get("isOnline"), users.get("$perMin"), users.get("rating"), users.get("coinPerMin"), users.get("about"),SellerSignupActivity.this);
//                                                MyFirebaseInstanceIDService.sendRegistrationToServer(SellerSignupActivity.this.getClass().getSimpleName(), FirebaseInstanceId.getInstance().getToken(), userDetails.getUid());

                                                dialog.dismiss();
//                                                Intent in = new Intent(SellerSignupActivity.this, SellerHomeActivity.class);
//                                                startActivity(in);
//                                                finish();
                                                final AlertDialog.Builder dialog = new AlertDialog.Builder(SellerSignupActivity.this);
                                                dialog.setTitle("Approval Required!");
                                                dialog.setMessage("Please wait for the approval of your account.");

                                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(SellerSignupActivity.this, SellerLoginActivity.class);
                                                        startActivity(intent);
                                                        Bungee.slideDown(SellerSignupActivity.this);
                                                        finish();
                                                    }
                                                });

                                                dialog.show();
                                            }
                                        });
                            } else {

                                showToast("You are already signed up!");
                                dialog.dismiss();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SellerSignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void validate() {

        name = nameEditTxt.getText().toString();
        email = emailEditTxt.getText().toString();
        password = passwordEditTxt.getText().toString();

        name = name.trim();
        email = email.trim();
        password = password.trim();

        /*if (!Patterns.EMAIL_ADDRESS.matcher(emailEditTxt.getText().toString()).matches()) {
            emailEditTxt.setError("Please enter a Valid E-Mail Address!");
        }
        if (password.length()<6){
            passwordEditTxt.setError("Password Must Have 6 Characters");
        }
        else if (TextUtils.isEmpty(name)){
            nameEditTxt.setError("required");
        }else if(TextUtils.isEmpty(email)) {
            emailEditTxt.setError("required");
        }else if (TextUtils.isEmpty(password)) {
            passwordEditTxt.setError("required");
        } else
            SendOTP();*/

        //User side validation
        /*if (TextUtils.isEmpty(name)) {
            nameEditTxt.setError("required");
        } else if (TextUtils.isEmpty(email)) {
            emailEditTxt.setError("required");
        } else if (TextUtils.isEmpty(password)) {
            passwordEditTxt.setError("required");
        } else if (!emailEditTxt.getText().toString().matches(emailPattern)) {
            emailEditTxt.setError("Please enter a Valid E-Mail Address!");
        } else if (password.length() < 6) {
            passwordEditTxt.setError("Password Must Have 6 Characters");
        } else {
            SendOTP();
        }*/

        awesomeValidation.addValidation(this, R.id.signup_name_seller, "[a-zA-Z\\s]+", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.signup_email_seller, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.signup_password_seller, new SimpleCustomValidation() {

            @Override
            public boolean compare(String s) {
                try {
                    if (s.length() >= 6) {
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }, R.string.passworderror);

        if (awesomeValidation.validate()) {
            SendOTP();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SellerSignupActivity.this, BuyerSignupActivity.class);
        startActivity(intent);
        finish();
    }
}
