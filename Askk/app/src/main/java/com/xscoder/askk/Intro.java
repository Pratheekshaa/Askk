package com.xscoder.askk;

/**

 Askk [XServer]

 © XScoder 2020
 All Rights reserved

 * IMPORTANT *
 RE-SELLING THIS SOURCE CODE TO ANY ONLINE MARKETPLACE
 IS A SERIOUS COPYRIGHT INFRINGEMENT, AND YOU WILL BE
 LEGALLY PROSECUTED

 **/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mb3364.http.RequestParams;

import java.util.ArrayList;
import java.util.Objects;

import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.DATABASE_PATH;
import static com.xscoder.askk.XServerSDK.PREFS;
import static com.xscoder.askk.XServerSDK.TAG;
import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_EDUCATION;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_LOCATION;
import static com.xscoder.askk.XServerSDK.USERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.XSGetStringFromArray;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSSignUp;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.popBlack;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.popSemibold;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;


public class Intro extends AppCompatActivity {

    // VIEWS //
    TextView appNameTxt, descriptionTxt;
    Button dismissButt, googleSignInButton, signupButt, loginButt, termsOfUseButt;



    // VARIABLES //
    Context ctx = this;
    GoogleSignInClient mGoogleSignInClient;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Hide Status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        appNameTxt = findViewById(R.id.inAppNameTxt);
        appNameTxt.setTypeface(popBlack);
        descriptionTxt = findViewById(R.id.inDescriptionTxt);
        descriptionTxt.setTypeface(popRegular);
        googleSignInButton = findViewById(R.id.inGoogleButton);
        googleSignInButton.setTypeface(popBold);
        signupButt = findViewById(R.id.inSignupButt);
        signupButt.setTypeface(popSemibold);
        loginButt = findViewById(R.id.inloginButt);
        loginButt.setTypeface(popSemibold);
        termsOfUseButt = findViewById(R.id.inTosButt);
        termsOfUseButt.setTypeface(popRegular);
        dismissButt = findViewById(R.id.inDismissButt);


        // App name
        appNameTxt.setText(getString(R.string.app_name));


        // Configure Google sign-in to request the user's ID, email address, and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(ctx, gso);


        //-----------------------------------------------
        // MARK - GOOGLE SIGN IN BUTTON
        //-----------------------------------------------
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHUD(ctx);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 99);
        }});



        //-----------------------------------------------
        // MARK - SING UP BUTTON
        //-----------------------------------------------
        signupButt.setTypeface(popBold);
        signupButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(ctx, SignUp.class));
         }});



        //-----------------------------------------------
        // MARK - LOGIN BUTTON
        //-----------------------------------------------
        loginButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(ctx, Login.class));
          }});



        //-----------------------------------------------
        // MARK - DISMISS BUTTON
        //-----------------------------------------------
        dismissButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(ctx, Home.class));
        }});




        //-----------------------------------------------
        // MARK - TERMS OF SERVICE BUTTON
        //-----------------------------------------------
        termsOfUseButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(ctx, TermsOfUse.class));
        }});



    }// ./ onCreate





    //-----------------------------------------------
    // MARK - ON ACTIVITY RESULT
    //-----------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 99) {
            // Handle Google Sign In
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }


    //-----------------------------------------------
    // MARK - HANDLE GOOGLE SIGN IN
    //-----------------------------------------------
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount user = completedTask.getResult(ApiException.class);

            // Get user's data
            assert user != null;
            String password = user.getId();
            String firstName = user.getGivenName();
            String lastName = user.getFamilyName();
            String email = "";
            if (user.getEmail() != null) { email = user.getEmail();
            } else { email = password + "@google.com"; }
            Uri profilePicURL = null;
            if (user.getPhotoUrl() != null) { profilePicURL = user.getPhotoUrl();
            } else { profilePicURL = Uri.parse(DATABASE_PATH + "assets/img/default_avatar.png"); }

            assert lastName != null;
            assert firstName != null;
            final String username = firstName.toLowerCase() + lastName.toLowerCase();
            final String fullName = firstName + " " + lastName;

            Log.i(TAG, "** GOOGLE SIGN IN: " + "USERNAME: " + username + "\nPASSWORD: " + password + "\nEMAIL: " + email + "\nFULL NAME: " + fullName + "\nPROFILE PIC URL: " + profilePicURL + "\n----------------");

            final Uri finalProfilePicURL = profilePicURL;
            XSSignUp((Activity)ctx, username, password, email, "google", new XServerSDK.XSSignUpHandler() {
                @Override public void done(String result, String e) {
                    if (e == null) {
                        String[] resultsArr = result.split("-");
                        String uID = resultsArr[0];
                        String isSignInWithSocial = "";
                        if (resultsArr.length == 2) { isSignInWithSocial = resultsArr[1]; }
                        PREFS.edit().putString("currentUser", uID).apply();

                        // Go back
                        if (isSignInWithSocial.matches("true")) {
                            mustReload = true;
                            hideHUD();
                            startActivity(new Intent(ctx, Home.class));

                            // Add additional data
                        } else {
                            RequestParams params = new RequestParams();
                            params.put("tableName", "Users");
                            params.put("ID_id", uID);
                            params.put(USERS_FULLNAME, fullName);
                            params.put(USERS_AVATAR, String.valueOf(finalProfilePicURL));
                            params.put(USERS_REPORTED_BY, XSGetStringFromArray(new ArrayList<String>()));
                            params.put(USERS_EDUCATION, "");
                            params.put(USERS_LOCATION, "");

                            XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() {
                                @Override
                                public void done(String e, JSON obj) {
                                    if (e == null) {
                                        mustReload = true;
                                        hideHUD();
                                        finish();
                                        // startActivity(new Intent(ctx, Home.class));

                                    // error
                                    } else { hideHUD(); simpleAlert(e, ctx);
                            }}});// ./ XSObject

                        }// ./ If

                        // error
                    } else { hideHUD(); simpleAlert(e, ctx);
            }}});// ./ XSSignUp


        // error on Google SignIn
        } catch (ApiException e) { simpleAlert("Something went wrong: " + e.getStatusCode(), ctx); }
    }




    @Override
    public void onBackPressed() { }




}// ./ end
