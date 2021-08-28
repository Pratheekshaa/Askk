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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import static com.xscoder.askk.XServerSDK.XSResetPassword;
import static com.xscoder.askk.XServerSDK.XSSignIn;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.isInternetConnectionAvailable;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.popSemibold;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;


public class Login extends AppCompatActivity {

    // VIEWS //
    TextView titleTxt;
    EditText usernameTxt, passwordTxt;
    Button loginButt,signupButt, forgotPasswordButt, dismissButt;



    // VARIABLES //
    Context ctx = this;




    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        usernameTxt = findViewById(R.id.linUsernameTxt);
        usernameTxt.setTypeface(popRegular);
        passwordTxt = findViewById(R.id.linPasswordTxt);
        passwordTxt.setTypeface(popRegular);
        titleTxt = findViewById(R.id.linTitleTxt);
        titleTxt.setTypeface(popBold);
        loginButt = findViewById(R.id.linLoginButt);
        loginButt.setTypeface(popSemibold);
        signupButt = findViewById(R.id.linSignUpButt);
        signupButt.setTypeface(popSemibold);
        forgotPasswordButt = findViewById(R.id.linForgotPassButt);
        forgotPasswordButt.setTypeface(popSemibold);
        dismissButt = findViewById(R.id.linDismissButt);


        // Title
        titleTxt.setText("Log in to " + getString(R.string.app_name));



        //-----------------------------------------------
        // MARK - LOGIN BUTTON
        //-----------------------------------------------
        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for Internet connection
                if (!isInternetConnectionAvailable(ctx)) {
                    simpleAlert("Your Internet connection seems to be offline, please check it out and get connected to either WiFi or Mobile data.", ctx);
                } else {
                    showHUD(ctx);
                    dismissKeyboard();

                    XSSignIn((Activity)ctx, usernameTxt.getText().toString(), passwordTxt.getText().toString(), new XServerSDK.XSSignInHandler() {
                        @Override
                        public void done(boolean success, String e) {
                            if (e == null) {
                                hideHUD();
                                mustReload = true;
                                startActivity(new Intent(ctx, Home.class));
                            // error
                            } else { hideHUD(); simpleAlert(e, ctx); }
                        }});// ./ XSSignIn

                }// ./ If Internet connection
            }});




        //-----------------------------------------------
        // MARK - SIGN UP BUTTON
        //-----------------------------------------------
        signupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, SignUp.class));
        }});






        //-----------------------------------------------
        // MARK - FORGOT PASSWORD BUTTON
        //-----------------------------------------------
        forgotPasswordButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
                alert.setTitle(R.string.app_name);
                alert.setMessage("Type the valid email address you've used to register on this app");

                // EditText
                final EditText editTxt = new EditText (Login.this);
                editTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                editTxt.setTextSize(12);
                alert.setView(editTxt)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String email = editTxt.getText().toString();

                                if (!email.matches("")) {
                                    XSResetPassword((Activity)ctx, email, new XServerSDK.ResetPasswordHandler() {
                                        @Override
                                        public void done(String result, String error) {
                                            if (error == null) {
                                                simpleAlert(result, ctx);
                                            } else { simpleAlert(error, ctx); }
                                        }});// ./ XSResetPassword

                                // Type a valid email!
                                } else { simpleAlert("Please type a valid email address.", ctx); }
                            }});
                alert.show();

            }});





        //-----------------------------------------------
        // MARK - DISMISS BUTTON
        //-----------------------------------------------
        dismissButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate




    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordTxt.getWindowToken(), 0);
    }



} // ./ end
