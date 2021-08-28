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
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mb3364.http.RequestParams;

import java.util.ArrayList;
import java.util.Objects;

import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.DATABASE_PATH;
import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_EDUCATION;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_LOCATION;
import static com.xscoder.askk.XServerSDK.USERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.XSGetStringFromArray;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSSignUp;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.isInternetConnectionAvailable;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.popSemibold;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class SignUp extends AppCompatActivity {

    // VIEWS //
    TextView titleTxt, tosTxt;
    EditText usernameTxt, passwordTxt, emailTxt, fullnameTxt;
    Button signUpButt, checkboxButt, dismissButt;

    

    // VARIABLES //
    Context ctx = this;
    boolean tosAccepted = false;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS 
        //-----------------------------------------------
        titleTxt = findViewById(R.id.suTitleTxt);
        titleTxt.setTypeface(popSemibold);
        usernameTxt = findViewById(R.id.supUsernameTxt);
        usernameTxt.setTypeface(popRegular);
        passwordTxt = findViewById(R.id.supPasswordTxt);
        passwordTxt.setTypeface(popRegular);
        emailTxt = findViewById(R.id.supEmailTxt);
        emailTxt.setTypeface(popRegular);
        fullnameTxt = findViewById(R.id.supFullnameTxt);
        fullnameTxt.setTypeface(popRegular);
        tosTxt = findViewById(R.id.supTosTxt);
        tosTxt.setTypeface(popSemibold);
        signUpButt = findViewById(R.id.supSignUpButt);
        signUpButt.setTypeface(popSemibold);
        checkboxButt = findViewById(R.id.supCheckboxButt);
        tosTxt = findViewById(R.id.supTosTxt);
        tosTxt.setTypeface(popSemibold);
        dismissButt = findViewById(R.id.supDismissButt);



        //-----------------------------------------------
        // MARK - SIGN UP BUTTON
        //-----------------------------------------------
        signUpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for Internet connection
                if (!isInternetConnectionAvailable(ctx)) {
                    simpleAlert("Your Internet connection seems to be offline, please check it out and get connected to either WiFi or Mobile data.", ctx);
                } else {

                    // YOU ACCEPTED THE TERMS OF SERVICE
                    if (tosAccepted) {

                        // SOME FIELD IS EMPTY
                        if (usernameTxt.getText().toString().matches("") || passwordTxt.getText().toString().matches("") ||
                                emailTxt.getText().toString().matches("") || fullnameTxt.getText().toString().matches("")
                                ) {
                            simpleAlert("You must fill all the fields to Sign Up!", ctx);


                        // ALL FIELDS HAVE BEEN FILLED
                        } else {
                            showHUD(ctx);
                            dismissKeyboard();

                            XSSignUp((Activity) ctx, usernameTxt.getText().toString(), passwordTxt.getText().toString(), emailTxt.getText().toString(), "", new XServerSDK.XSSignUpHandler() {
                                @Override public void done(String result, String e) {
                                    if (e == null) {
                                        // Additional data
                                        RequestParams params = new RequestParams();
                                        params.put("tableName", "Users");
                                        params.put("ID_id", result);
                                        params.put(USERS_FULLNAME, fullnameTxt.getText().toString());
                                        params.put(USERS_AVATAR, DATABASE_PATH + "assets/img/default_avatar.png");
                                        params.put(USERS_REPORTED_BY, XSGetStringFromArray(new ArrayList<String>()));
                                        params.put(USERS_EDUCATION, "");
                                        params.put(USERS_LOCATION, "");

                                        XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                            if (e == null) {
                                                mustReload = true;
                                                hideHUD();
                                                startActivity(new Intent(ctx, Home.class));
                                                // error
                                            } else { hideHUD(); simpleAlert(e, ctx);
                                            }}});// ./ XSObject

                                        // error
                                    } else { hideHUD(); simpleAlert(e, ctx); }
                                }});// ./ XSSignUp

                        }// ./ If

                        // YOU HAVEN'T ACCEPTED THE TERMS OF SERVICE
                    } else {
                        dismissKeyboard();
                        simpleAlert("You must agree with Terms of Service in order to Sign Up.", ctx);

                    }// ./ If tosAccepted

                }// ./ If Internet connection

        }});




        //-----------------------------------------------
        // MARK - TOS CHECKBOX BUTTON
        //-----------------------------------------------
        checkboxButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              tosAccepted = true;
              checkboxButt.setBackgroundResource(R.drawable.checkbox_butt_on);
        }});

    
        
        //-----------------------------------------------
        // MARK - TERMS OS SERVICE  
        //-----------------------------------------------
        tosTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, TermsOfUse.class));
        }});




        // MARK: - DISMISS BUTTON ---------------------------------------------------------------
        dismissButt.setTypeface(popSemibold);
        dismissButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }});

        
    }// ./ onCreate





    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(emailTxt.getWindowToken(), 0);
    }



} // ./ end
