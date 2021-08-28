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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Objects;

public class TermsOfUse extends AppCompatActivity {


    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();



        //-----------------------------------------------
        // MARK - INITIALIZE WEBVIEW
        //-----------------------------------------------
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/tou.html");



        //-----------------------------------------------
        // MARK - BACK BUTTON
        //-----------------------------------------------
        Button backButt = findViewById(R.id.tosBackButt);
        backButt.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) { finish(); }});


    }// ./ onCreate



}// ./ end
