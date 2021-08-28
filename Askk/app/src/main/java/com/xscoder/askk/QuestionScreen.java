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

import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.ActionSheet;
import android.actionsheet.demo.com.khoiron.actionsheetiosforandroid.Interface.ActionSheetCallBack;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.mb3364.http.RequestParams;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.ANSWERS_ANSWER;
import static com.xscoder.askk.XServerSDK.ANSWERS_CREATED_AT;
import static com.xscoder.askk.XServerSDK.ANSWERS_DISLIKED_BY;
import static com.xscoder.askk.XServerSDK.ANSWERS_IMAGE;
import static com.xscoder.askk.XServerSDK.ANSWERS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.ANSWERS_IS_BEST;
import static com.xscoder.askk.XServerSDK.ANSWERS_LIKED_BY;
import static com.xscoder.askk.XServerSDK.ANSWERS_QUESTION_POINTER;
import static com.xscoder.askk.XServerSDK.ANSWERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.ANSWERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.ANSWERS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.GRAY;
import static com.xscoder.askk.XServerSDK.MAIN_COLOR;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_CURRENT_USER;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_OTHER_USER;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_TEXT;
import static com.xscoder.askk.XServerSDK.PREFS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_ANSWERS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_HAS_BEST_ANSWER;
import static com.xscoder.askk.XServerSDK.QUESTIONS_IMAGE;
import static com.xscoder.askk.XServerSDK.QUESTIONS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_QUESTION;
import static com.xscoder.askk.XServerSDK.QUESTIONS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.QUESTIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.QUESTIONS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.QUESTIONS_VIEWS;
import static com.xscoder.askk.XServerSDK.TAG;
import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.USERS_USERNAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSDelete;
import static com.xscoder.askk.XServerSDK.XSGetArrayFromJSONArray;
import static com.xscoder.askk.XServerSDK.XSGetDateFromString;
import static com.xscoder.askk.XServerSDK.XSGetPointer;
import static com.xscoder.askk.XServerSDK.XSGetStringFromArray;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSQuery;
import static com.xscoder.askk.XServerSDK.XSRefreshObjectData;
import static com.xscoder.askk.XServerSDK.XSRemoveDuplicatesFromArray;
import static com.xscoder.askk.XServerSDK.XSSendAndroidPush;
import static com.xscoder.askk.XServerSDK.XSSendiOSPush;
import static com.xscoder.askk.XServerSDK.fireInterstitialAd;
import static com.xscoder.askk.XServerSDK.getImageUri;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.roundLargeNumber;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class QuestionScreen extends AppCompatActivity {

   // VIEWS //
   ListView answersListView;
   TextView questionTxt, answersViewsTxt, fullnameTxt, moreTextView, moreFullnameTxt, moreAnsweredTxt;
   CircleImageView avatarImg, moreAvatarImg;
   ImageView questionImgButton;
   RelativeLayout imagePreviewView, moreView;
   PhotoView imagePreview;
   Button postAnswerButton, editQuestionButton, reportQuestionButton, shareButton, backButton, dismissMoreViewButton,
         dismissImgPreviewButton, questionUserButton;



   // VARIABLES //
   Context ctx = this;
   JSON currentUser;
   JSON qObj;
   List<JSON> answersArray = new ArrayList<>();
   int adCount = 0;




   //-----------------------------------------------
   // MARK - ON START
   //-----------------------------------------------
   @Override
   protected void onStart() {
      super.onStart();

      if (mustReload) {
         mustReload = false;

         // Get Extras
         Bundle extras = getIntent().getExtras();
         assert extras != null;
         qObj = new JSON(extras.getString("object"));

         // Get Current User
         XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
             // Current User IS LOGGED IN!
             if (currUser != null) {
                currentUser = currUser;
             }

            // Call function
            showQuestionDetails();
         }}); // ./ XSCurrentUser

      } //./ If
   }





   //-----------------------------------------------
   // MARK - ON CREATE
   //-----------------------------------------------
   @SuppressLint("SourceLockedOrientationActivity")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.question_screen);
      super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      // Hide ActionBar
      Objects.requireNonNull(getSupportActionBar()).hide();


      //-----------------------------------------------
      // MARK - INITIALIZE VIEWS
      //-----------------------------------------------
      questionTxt = findViewById(R.id.qsQuestionTxt);
      questionTxt.setTypeface(popBold);
      answersViewsTxt = findViewById(R.id.qsAnswersViewsTxt);
      answersViewsTxt.setTypeface(popRegular);
      fullnameTxt = findViewById(R.id.qsFullnameTxt);
      fullnameTxt.setTypeface(popBold);
      avatarImg = findViewById(R.id.qsAvatarImg);
      questionImgButton = findViewById(R.id.qsQuestionImgButton);
      questionImgButton.setClipToOutline(true);
      postAnswerButton = findViewById(R.id.qsPostAnswerButton);
      editQuestionButton = findViewById(R.id.qsEditButton);
      reportQuestionButton = findViewById(R.id.qsReportQuestionButton);
      shareButton = findViewById(R.id.qsShareButton);
      backButton = findViewById(R.id.qsBackButton);
      answersListView = findViewById(R.id.qsAnswersListView);
      moreView = findViewById(R.id.qsMoreView);
      dismissMoreViewButton = findViewById(R.id.qsDismissMoreViewButton);
      moreAvatarImg = findViewById(R.id.qsMoreAvatarImg);
      moreFullnameTxt = findViewById(R.id.qsMoreFullNameTxt);
      moreAnsweredTxt = findViewById(R.id.qsMoreAnsweredTxt);
      moreTextView = findViewById(R.id.qsMoreTextView);
      imagePreviewView = findViewById(R.id.qsImagePreviewView);
      imagePreview = findViewById(R.id.qsImagePreview);
      dismissImgPreviewButton = findViewById(R.id.qsDismissImgPreviewButton);
      questionUserButton = findViewById(R.id.qsQuestionUserButt);



      // Get Extras
      Bundle extras = getIntent().getExtras();
      assert extras != null;
      qObj = new JSON(extras.getString("object"));

      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
         // Current User IS LOGGED IN!
         if (currUser != null) { currentUser = currUser; }

         // Call function
         showQuestionDetails();
      }}); // ./ XSCurrentUser



      //-----------------------------------------------
      // MARK - QUESTION IMAGE BUTTON
      //-----------------------------------------------
      questionImgButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            imagePreviewView.setVisibility(View.VISIBLE);
            Glide.with(ctx).load(qObj.key(QUESTIONS_IMAGE).stringValue()).into(imagePreview);
      }});




      //-----------------------------------------------
      // MARK - SHARE QUESTION BUTTON
      //-----------------------------------------------
      shareButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bitmap shareBM;
            Uri uri;

            Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "Check out this question on our app called " + getString(R.string.app_name) + ": "  + questionTxt.getText().toString());

            if (questionImgButton.getDrawable() != null) {
               shareBM = ((BitmapDrawable) questionImgButton.getDrawable()).getBitmap();
               uri = getImageUri(shareBM, ctx);
               i.putExtra(Intent.EXTRA_STREAM, uri);
               i.setType("image/jpeg");
            } else { i.setType("text/plain"); }

            startActivity(Intent.createChooser(i, "Share on..."));
      }});



      //-----------------------------------------------
      // MARK - QUESTION USER BUTTON
      //-----------------------------------------------
      questionUserButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (currentUser == null){
               startActivity(new Intent(ctx, Intro.class));

            } else {
               // Get Pointer
               XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                  @Override public void done(final JSON userPointer, String e) {
                     if (userPointer != null) {

                        boolean isAnonymous = qObj.key(QUESTIONS_IS_ANONYMOUS).booleanValue();
                        if (isAnonymous) {
                           simpleAlert("You're not allowed to view this User's Profile", ctx);

                        } else {
                           Intent i = new Intent(ctx, Account.class);
                           Bundle extras = new Bundle();

                           if (userPointer.key("ID_id").stringValue().matches(currentUser.key("ID_id").stringValue())) {
                              extras.putBoolean("isCurrentUser", true);
                           } else {
                              extras.putBoolean("isCurrentUser", false);
                              extras.putString("userObj", String.valueOf(userPointer));
                           }
                           extras.putBoolean("showBackButton", true);

                           i.putExtras(extras);
                           startActivity(i);
                        }

                     // error
                     } else { simpleAlert(e, ctx);
               }}}); // ./ XSGetPointer

            } // ./ If
       }});




      //-----------------------------------------------
      // MARK - REPORT QUESTION BUTTON
      //-----------------------------------------------
      reportQuestionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            // USER IS NOT LOGGED IN...
              if (currentUser == null){
                 startActivity(new Intent(ctx, Intro.class));

              // USER IS LOGGED IN!
              } else {
                 final List<String>reportedBy = XSGetArrayFromJSONArray(qObj.key(QUESTIONS_REPORTED_BY).getJsonArray());

                 List<String>buttons = new ArrayList<>();
                 buttons.add("Report Question");
                 buttons.add("Report User");
                 new ActionSheet(ctx, buttons)
                         .setTitle("What do you want to report to the Admin?")
                         .setCancelTitle("Cancel")
                         .setColorTitle(Color.parseColor(MAIN_COLOR))
                         .setColorTitleCancel(Color.parseColor(GRAY))
                         .setColorData(Color.parseColor(MAIN_COLOR))
                         .create(new ActionSheetCallBack() {
                             @Override
                             public void data(@NotNull String data, int position) {
                                 switch (position){

                                    // [REPORT QUESTION] -----------------------------------------
                                    case 0:
                                       // Fire alert
                                       AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
                                       alert.setMessage("Do you want to report this Question as inappropriate/abusive to the admin?")
                                             .setTitle(R.string.app_name)
                                             .setPositiveButton("Report Question", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                   RequestParams params = new RequestParams();
                                                   params.put("tableName", QUESTIONS_TABLE_NAME);
                                                   params.put("ID_id", qObj.key("ID_id").stringValue());
                                                   params.put(QUESTIONS_REPORTED_BY, XSGetStringFromArray(reportedBy));

                                                   XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                      if (e == null) {

                                                         // Fire alert2
                                                         AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx, R.style.alert);
                                                         alert2.setMessage("Thanks for reporting this Question. We'll take action within 24h")
                                                                 .setTitle(R.string.app_name)
                                                                 .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                       mustReload = true;
                                                                       finish();
                                                                 }})
                                                                 .setCancelable(false)
                                                                 .setIcon(R.drawable.logo)
                                                                 .create().show();

                                                      // error
                                                      } else { simpleAlert(e, ctx);
                                                   }}});// ./ XSObject

                                             }})
                                             .setNegativeButton("Cancel", null)
                                             .setCancelable(false)
                                             .setIcon(R.drawable.logo)
                                             .create().show();
                                    break;




                                    // [REPORT USER] ------------------------------------------------
                                    case 1:

                                       // Get Pointer
                                       XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                                          @Override public void done(final JSON userPointer, String e) {
                                             if (userPointer != null) {

                                                // Fire alert
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
                                                alert.setMessage("Are you sure you want to report this User to the Admin?")
                                                        .setTitle(R.string.app_name)
                                                        .setPositiveButton("Report User", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {

                                                                  List<String> uReportedBy = XSGetArrayFromJSONArray(userPointer.key(USERS_REPORTED_BY).getJsonArray());
                                                                  uReportedBy.add(currentUser.key("ID_id").stringValue());

                                                                  showHUD(ctx);

                                                                  RequestParams params = new RequestParams();
                                                                  params.put("tableName", USERS_TABLE_NAME);
                                                                  params.put("ID_id", userPointer.key("ID_id").stringValue());
                                                                  params.put(USERS_REPORTED_BY, XSGetStringFromArray(uReportedBy));

                                                                  XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                                     if (e == null) {
                                                                        hideHUD();
                                                                        mustReload = true;


                                                                        // 1. Query all Questions of this user and report them (if any)
                                                                        XSQuery((Activity)ctx, QUESTIONS_TABLE_NAME, "", "", new XServerSDK.XSQueryHandler() {
                                                                           @Override public void done(JSON objects, String error) {
                                                                              if (error == null) {

                                                                                 if (objects.count() != 0) {
                                                                                    for (int i = 0; i < objects.count(); i++) {
                                                                                       JSON obj = objects.index(i);

                                                                                       if (obj.key(QUESTIONS_USER_POINTER).stringValue().matches(userPointer.key("ID_id").stringValue())) {
                                                                                          List<String> qReportedBy = XSGetArrayFromJSONArray(obj.key(QUESTIONS_REPORTED_BY).getJsonArray());
                                                                                          qReportedBy.add(currentUser.key("ID_id").stringValue());

                                                                                          RequestParams params2 = new RequestParams();
                                                                                          params2.put("tableName", QUESTIONS_TABLE_NAME);
                                                                                          params2.put("ID_id", obj.key("ID_id").stringValue());
                                                                                          params2.put(QUESTIONS_REPORTED_BY, XSGetStringFromArray(qReportedBy));

                                                                                          XSObject((Activity) ctx, params2, new XServerSDK.XSObjectHandler() {
                                                                                             @Override
                                                                                             public void done(String e, JSON obj) {
                                                                                                if (e != null) { simpleAlert(e, ctx);
                                                                                          }}}); //./ XSObject

                                                                                       } // ./ If
                                                                                    } //./ For

                                                                                 } //./ If

                                                                              // error
                                                                              } else { hideHUD(); simpleAlert(error, ctx); }
                                                                        }});// /. XSQuery


                                                                        // 2. Query all Answers of this user and report them (if any)
                                                                        XSQuery((Activity)ctx, ANSWERS_TABLE_NAME, "", "", new XServerSDK.XSQueryHandler() {
                                                                           @Override public void done(JSON objects, String error) {
                                                                              if (error == null) {

                                                                                 if (objects.count() != 0) {
                                                                                    for (int i = 0; i < objects.count(); i++) {
                                                                                       JSON obj = objects.index(i);

                                                                                       if (obj.key(ANSWERS_USER_POINTER).stringValue().matches(userPointer.key("ID_id").stringValue())) {
                                                                                          List<String> aReportedBy = XSGetArrayFromJSONArray(obj.key(ANSWERS_REPORTED_BY).getJsonArray());
                                                                                          aReportedBy.add(currentUser.key("ID_id").stringValue());

                                                                                          RequestParams params3 = new RequestParams();
                                                                                          params3.put("tableName", ANSWERS_TABLE_NAME);
                                                                                          params3.put("ID_id", obj.key("ID_id").stringValue());
                                                                                          params3.put(ANSWERS_REPORTED_BY, XSGetStringFromArray(aReportedBy));

                                                                                          XSObject((Activity) ctx, params3, new XServerSDK.XSObjectHandler() {
                                                                                             @Override
                                                                                             public void done(String e, JSON obj) {
                                                                                                if (e != null) { simpleAlert(e, ctx);
                                                                                          }}}); //./ XSObject

                                                                                       } // ./ If
                                                                                    } //./ For

                                                                                 } //./ If

                                                                                 // error
                                                                              } else { hideHUD(); simpleAlert(error, ctx); }
                                                                        }});// /. XSQuery


                                                                        // Fire alert
                                                                        AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx, R.style.alert);
                                                                        alert2.setMessage("Thanks for reporting @" + userPointer.key(USERS_USERNAME).stringValue() + " to us. We'll take action for it within 24h.")
                                                                                .setTitle(R.string.app_name)
                                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                   @Override
                                                                                   public void onClick(DialogInterface dialog, int which) {
                                                                                      startActivity(new Intent(ctx, Home.class));
                                                                                }})
                                                                                .setCancelable(false)
                                                                                .setIcon(R.drawable.logo)
                                                                                .create().show();

                                                                     // error
                                                                     } else { hideHUD(); simpleAlert(e, ctx);
                                                                  }}});// ./ XSObject

                                                            }})
                                                            .setNegativeButton("Cancel", null)
                                                            .setCancelable(false)
                                                            .setIcon(R.drawable.logo)
                                                            .create().show();

                                                      // error
                                                   } else { simpleAlert(e, ctx);
                                                   }}}); // ./ XSGetPointer

                                    break;

                                 }}}); //./ ActionSheet

              } // ./ If

         }});




         //-----------------------------------------------
         // MARK - EDIT QUESTION BUTTON
         //-----------------------------------------------
         editQuestionButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              final boolean isAnonymous = qObj.key(QUESTIONS_IS_ANONYMOUS).booleanValue();

              // Fire alert
              List<String>buttons = new ArrayList<>();
              buttons.add("Delete Question");
              buttons.add("Edit Question");
              new ActionSheet(ctx, buttons)
                      .setTitle("Select option")
                      .setCancelTitle("Cancel")
                      .setColorTitle(Color.parseColor(GRAY))
                      .setColorTitleCancel(Color.parseColor(GRAY))
                      .setColorData(Color.parseColor(MAIN_COLOR))
                      .create(new ActionSheetCallBack() {
                          @Override
                          public void data(@NotNull String data, int position) {
                              switch (position){

                                 // Delete Question
                                 case 0:
                                    showHUD(ctx);
                                    final String qObjID = qObj.key("ID_id").stringValue();

                                    // Delete
                                    XSDelete((Activity) ctx, QUESTIONS_TABLE_NAME, qObjID, new XServerSDK.XSDeleteHandler() {
                                       @Override
                                       public void done(boolean success, String error) {
                                          if (error == null) {
                                             hideHUD();
                                             mustReload = true;

                                             // Query and delete all the qObj's relative Answers
                                             XSQuery((Activity)ctx, ANSWERS_TABLE_NAME, "", "", new XServerSDK.XSQueryHandler() {
                                                @Override public void done(JSON objects, String error) {
                                                   if (error == null) {

                                                      if (objects.count() != 0) {
                                                         for (int i = 0; i < objects.count(); i++) {
                                                            JSON obj = objects.index(i);

                                                            if (obj.key(ANSWERS_QUESTION_POINTER).stringValue().matches(qObjID)) {
                                                               XSDelete((Activity) ctx, QUESTIONS_TABLE_NAME, qObjID, new XServerSDK.XSDeleteHandler() {
                                                                  @Override
                                                                  public void done(boolean success, String error) {
                                                                     if (error == null) {
                                                                        Log.i(TAG, "ANSWER DELETED!");
                                                                     } else { simpleAlert(error, ctx);
                                                               }}});//./ XSDelete

                                                            } //./ If
                                                         } // ./ For
                                                      } //./ If

                                                   // error
                                                   } else { hideHUD(); simpleAlert(error, ctx); }
                                             }});// /. XSQuery


                                             // Fire alert
                                             AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
                                             alert.setMessage("Your Question has been deleted.")
                                                     .setTitle(R.string.app_name)
                                                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                           finish();
                                                     }})
                                                     .setCancelable(false)
                                                     .setIcon(R.drawable.logo)
                                                     .create().show();

                                          // error
                                          } else { hideHUD(); simpleAlert(error, ctx); }
                                    }});//./ XSDelete

                                 break;


                                    // Edit Question
                                    case 1:
                                       Intent i = new Intent(ctx, PostScreen.class);
                                       Bundle extras = new Bundle();

                                       extras.putBoolean("isAnonymous", isAnonymous);
                                       extras.putBoolean("isQuestion", true);
                                       extras.putBoolean("isEditingMode", true);
                                       extras.putString("qObject", String.valueOf(qObj));

                                       i.putExtras(extras);
                                       startActivity(i);
                                       break;

                      }}});
         }});



         //-----------------------------------------------
         // MARK - POST ANSWER BUTTON
         //-----------------------------------------------
         postAnswerButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

              // USER IS NOT LOGGED IN...
              if (currentUser == null){
                 startActivity(new Intent(ctx, Intro.class));

              // USER IS LOGGED IN!
              } else {

                 // Fire alert
                 List<String>buttons = new ArrayList<>();
                 buttons.add("Answer with name");
                 buttons.add("Answer anonymously");
                 new ActionSheet(ctx, buttons)
                         .setTitle("How do you want to answer to this question?")
                         .setCancelTitle("Cancel")
                         .setColorTitle(Color.parseColor(GRAY))
                         .setColorTitleCancel(Color.parseColor(GRAY))
                         .setColorData(Color.parseColor(MAIN_COLOR))
                         .create(new ActionSheetCallBack() {
                             @Override
                             public void data(@NotNull String data, int position) {
                                 switch (position){

                                    // Answer with name
                                    case 0:
                                        Intent i = new Intent(ctx, PostScreen.class);
                                        Bundle extras = new Bundle();

                                        extras.putBoolean("isAnonymous", false);
                                        extras.putBoolean("isQuestion", false);
                                        extras.putString("qObject", String.valueOf(qObj));

                                        i.putExtras(extras);
                                        startActivity(i);
                                        break;

                                    // Answer anonymously
                                    case 1:
                                       Intent i2 = new Intent(ctx, PostScreen.class);
                                       Bundle extras2 = new Bundle();

                                       extras2.putBoolean("isAnonymous", true);
                                       extras2.putBoolean("isQuestion", false);
                                       extras2.putString("qObject", String.valueOf(qObj));

                                       i2.putExtras(extras2);
                                       startActivity(i2);
                                       break;
                         }}});

              }// ./ If

         }});





      //-----------------------------------------------
      // MARK - DISMISS IMAGE PREVIEW BUTTON
      //-----------------------------------------------
      dismissImgPreviewButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            imagePreviewView.setVisibility(View.GONE);
      }});




      //-----------------------------------------------
      // MARK - DISMISS MORE VIEW BUTTON
      //-----------------------------------------------
      dismissMoreViewButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           moreView.setVisibility(View.GONE);
      }});





      //-----------------------------------------------
      // MARK - BACK BUTTON
      //-----------------------------------------------
      backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) { finish(); }});


      // Fire AdMob Interstitial
      adCount = PREFS.getInt("adCount", adCount);
      adCount += 1;
      if (adCount == 3){
         fireInterstitialAd(ctx);
         adCount = 0;
      }
      PREFS.edit().putInt("adCount", adCount).apply();

   }// ./ onCreate






   //-----------------------------------------------
   // MARK - SHOW QUESTION DETAILS
   //-----------------------------------------------
   void showQuestionDetails() {

      XSRefreshObjectData((Activity) ctx, QUESTIONS_TABLE_NAME, qObj, new XServerSDK.XSRefreshObjectDataHandler() {
          @Override
          public void done(JSON object, String error) {
             if (error == null) {
                // Update object
                qObj = object;

                // userPointer
                XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                   @SuppressLint("SetTextI18n")
                   @Override public void done(final JSON userPointer, String e) {
                      if (userPointer != null) {

                         // isAnonymous
                         boolean isAnonymuos = qObj.key(QUESTIONS_IS_ANONYMOUS).booleanValue();

                         // Question Image
                         String qImgFile = qObj.key(QUESTIONS_IMAGE).stringValue();
                         if (!qImgFile.matches("")){
                            questionImgButton.setVisibility(View.VISIBLE);
                            Glide.with(ctx).load(qObj.key(QUESTIONS_IMAGE).stringValue()).into(questionImgButton);
                         } else{
                            questionImgButton.setVisibility(View.GONE);
                         }

                         // Avatar • Fullname
                         if (isAnonymuos){
                            avatarImg.setImageResource(R.drawable.anonymous_avatar);
                            fullnameTxt.setText("Anonymous");
                         } else{
                            Glide.with(ctx).load(userPointer.key(USERS_AVATAR).stringValue()).into(avatarImg);
                            fullnameTxt.setText(userPointer.key(USERS_FULLNAME).stringValue());
                         }

                         // Question • Answers • Views
                         questionTxt.setText(qObj.key(QUESTIONS_QUESTION).stringValue());
                         int answers = qObj.key(QUESTIONS_ANSWERS).intValue();
                         int views = qObj.key(QUESTIONS_VIEWS).intValue();
                         answersViewsTxt.setText(roundLargeNumber(answers) + " Answers • " + roundLargeNumber(views) + " Views");

                         // Increase Views
                         RequestParams params = new RequestParams();
                         params.put("tableName", QUESTIONS_TABLE_NAME);
                         params.put("ID_id", qObj.key("ID_id").stringValue());
                         params.put(QUESTIONS_VIEWS, String.valueOf(qObj.key(QUESTIONS_VIEWS).intValue() + 1));

                         XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                            if (e != null) { simpleAlert(e, ctx);
                         }}});// ./ XSObject


                         // Edit Button
                         if (currentUser != null){
                            if (currentUser.key("ID_id").stringValue().matches(userPointer.key("ID_id").stringValue()) ){
                               editQuestionButton.setVisibility(View.VISIBLE);
                            } else { editQuestionButton.setVisibility(View.INVISIBLE); }
                         } else{ editQuestionButton.setVisibility(View.INVISIBLE); }


                         // Call query
                         queryAnswers();


                      // error
                      } else { simpleAlert(e, ctx);
                }}}); // ./ XSGetPointer

          // error
          } else { hideHUD(); simpleAlert(error, ctx); }
      }});//./ XSRefreshObjectData


   }





   //-----------------------------------------------
   // MARK -  QUERY ANSWERS
   //-----------------------------------------------
   void queryAnswers() {
      showHUD(ctx);
      answersArray = new ArrayList<>();
      answersListView.invalidateViews();
      answersListView.refreshDrawableState();

      XSQuery((Activity)ctx, ANSWERS_TABLE_NAME, ANSWERS_CREATED_AT, "", new XServerSDK.XSQueryHandler() {
         @Override public void done(JSON objects, String error) {
            if (error == null) {
               for (int i = 0; i < objects.count(); i++) {
                  JSON obj = objects.index(i);

                  List<String> reportedBy = XSGetArrayFromJSONArray(obj.key(ANSWERS_REPORTED_BY).getJsonArray());

                  if (obj.key(ANSWERS_QUESTION_POINTER).stringValue().matches(qObj.key("ID_id").stringValue()) ) {
                     if (currentUser != null) {
                        if (!reportedBy.contains(currentUser.key("ID_id").stringValue())
                        ){ answersArray.add(obj); }
                     } else { answersArray.add(obj); }
                  }

                  // [Finalize array of objects]
                  if (i == objects.count()-1) { answersArray = XSRemoveDuplicatesFromArray(answersArray); }
               } // ./ For


               // There area some objects
               if (answersArray.size() != 0) {
                  hideHUD();
                  answersListView.setVisibility(View.VISIBLE);
                  showDataInListView();

                  // Check for BestAnswer
                  for (int i=0; i<answersArray.size(); i++){
                     JSON aObj = answersArray.get(i);
                     boolean isBestAnswer = aObj.key(ANSWERS_IS_BEST).booleanValue();
                     if (isBestAnswer){
                        answersArray.add(0, aObj);
                        answersArray.remove(i+1);
                        showDataInListView();
                     }
                     if (i == answersArray.size()-1){ showDataInListView(); }
                  } // ./ For

               // NO objects
               } else {
                  hideHUD();
                  answersListView.setVisibility(View.INVISIBLE);
               }

            // error
            } else { hideHUD(); simpleAlert(error, ctx); }
      }});// /. XSQuery




                       // Check for BestAnswer


   }



   //-----------------------------------------------
   // MARK - SHOW DATA IN LISTVIEW
   //-----------------------------------------------
   void showDataInListView() {
      class ListAdapter extends BaseAdapter {
         private Context context;
         private ListAdapter(Context context) {
            super();
            this.context = context;
         }
         @SuppressLint("InflateParams")
         @Override
         public View getView(int position, View cell, ViewGroup parent) {
            if (cell == null) {
               LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               assert inflater != null;
               cell = inflater.inflate(R.layout.cell_answer, null);
            }

            //-----------------------------------------------
            // MARK - INITIALIZE VIEWS
            //-----------------------------------------------
            final CircleImageView avatarImg = cell.findViewById(R.id.caAvatarImg);
            final TextView fullnameTxt = cell.findViewById(R.id.caFullnameTxt);
            fullnameTxt.setTypeface(popBold);
            final TextView answerTxt = cell.findViewById(R.id.caAnswerTxt);
            answerTxt.setTypeface(popRegular);
            final TextView answeredTxt = cell.findViewById(R.id.caAnsweredTxt);
            answeredTxt.setTypeface(popRegular);
            final ImageView answerImgButton = cell.findViewById(R.id.caAnswerImgButton);
            answerImgButton.setClipToOutline(true);
            final TextView likesTxt = cell.findViewById(R.id.caLikesTxt);
            likesTxt.setTypeface(popRegular);
            final TextView dislikesTxt = cell.findViewById(R.id.caDislikesTxt);
            dislikesTxt.setTypeface(popRegular);
            final TextView bestAnswerTxt = cell.findViewById(R.id.caBestAnswerTxt);
            bestAnswerTxt.setTypeface(popBold);
            final Button likeButton = cell.findViewById(R.id.caLikeButton);
            final Button dislikeButton = cell.findViewById(R.id.caDislikeButton);
            final Button editAnswerButton = cell.findViewById(R.id.caEditAnswerButton);
            final Button reportAnswerButton = cell.findViewById(R.id.caReportAnswerButton);
            final Button setBestAnswerButton = cell.findViewById(R.id.caSetBestAnswerButton);
            final Button moreButton = cell.findViewById(R.id.caMoreButton);
            moreButton.setTypeface(popBold);


            // Obj
            final JSON aObj = answersArray.get(position);

            // Get Pointer
            XSGetPointer((Activity)ctx, aObj.key(ANSWERS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
               @SuppressLint("SetTextI18n")
               @Override public void done(final JSON userPointer, String e) {
                  if (userPointer != null) {
                     // isAnonymous
                     boolean isAnonymous = aObj.key(ANSWERS_IS_ANONYMOUS).booleanValue();

                     // Avatar • Fullname
                     if (isAnonymous) {
                        avatarImg.setImageResource(R.drawable.anonymous_avatar);
                        fullnameTxt.setText("Anonymous");
                     } else {
                        Glide.with(ctx).load(userPointer.key(USERS_AVATAR).stringValue()).into(avatarImg);
                        fullnameTxt.setText(userPointer.key(USERS_FULLNAME).stringValue());
                     }

                     // Answer Text
                     answerTxt.setText(aObj.key(ANSWERS_ANSWER).stringValue());

                     // Image (optional)
                     String imageFile = aObj.key(ANSWERS_IMAGE).stringValue();
                     if (!imageFile.matches("")){
                        answerImgButton.setVisibility(View.VISIBLE);
                        Glide.with(ctx).load(aObj.key(ANSWERS_IMAGE).stringValue()).into(answerImgButton);
                     } else {
                        answerImgButton.setVisibility(View.GONE);
                        answerImgButton.setImageBitmap(null);
                     }

                     // Date
                     Date aDate = XSGetDateFromString(aObj.key(ANSWERS_CREATED_AT).stringValue());
                     SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                     String dateStr = df.format(aDate);
                     answeredTxt.setText("Answered " + dateStr);

                     // Likes
                     List<String>likedBy = XSGetArrayFromJSONArray(aObj.key(ANSWERS_LIKED_BY).getJsonArray());
                     likesTxt.setText(roundLargeNumber(likedBy.size()));
                     if (currentUser != null) {
                        if (likedBy.contains(currentUser.key("ID_id").stringValue())) {
                           likeButton.setBackgroundResource(R.drawable.liked_butt);
                        } else {
                           likeButton.setBackgroundResource(R.drawable.like_butt);
                        }
                     }

                     // Dislikes
                     List<String>dislikedBy = XSGetArrayFromJSONArray(aObj.key(ANSWERS_DISLIKED_BY).getJsonArray());
                     dislikesTxt.setText(roundLargeNumber(dislikedBy.size()));
                     if (currentUser != null) {
                        if (dislikedBy.contains(currentUser.key("ID_id").stringValue())) {
                           dislikeButton.setBackgroundResource(R.drawable.disliked_butt);
                        } else {
                           dislikeButton.setBackgroundResource(R.drawable.dislike_butt);
                        }
                     }

                     // Edit Button
                     if (currentUser != null) {
                        if (currentUser.key("ID_id").stringValue().matches(userPointer.key("ID_id").stringValue())) {
                           editAnswerButton.setVisibility(View.VISIBLE);
                        } else { editAnswerButton.setVisibility(View.INVISIBLE); }
                     } else { editAnswerButton.setVisibility(View.INVISIBLE); }

                     // Best Answer
                     boolean isBestAnswer = aObj.key(ANSWERS_IS_BEST).booleanValue();
                     if (isBestAnswer) { bestAnswerTxt.setVisibility(View.VISIBLE);
                     } else { bestAnswerTxt.setVisibility(View.INVISIBLE); }


                     // More Button
                     int chars = answerTxt.getText().length();
                     if (chars >= 160) { moreButton.setVisibility(View.VISIBLE);
                     } else { moreButton.setVisibility(View.INVISIBLE); }


                      // Hide/Show setBestButton
                      final boolean questionHasBestAnswer = qObj.key(QUESTIONS_HAS_BEST_ANSWER).booleanValue();
                      if (currentUser != null) {
                         // questionUserPointer
                         XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                            @Override public void done(final JSON questionUserPointer, String e) {
                               if (questionUserPointer != null) {

                                  if (currentUser.key("ID_id").stringValue().matches(questionUserPointer.key("ID_id").stringValue())) {
                                     if (questionHasBestAnswer) { setBestAnswerButton.setVisibility(View.INVISIBLE);
                                     } else { setBestAnswerButton.setVisibility(View.VISIBLE); }
                                  } else { setBestAnswerButton.setVisibility(View.INVISIBLE); }

                               // error
                               } else { simpleAlert(e, ctx);
                         }}}); // ./ XSGetPointer

                      } else { setBestAnswerButton.setVisibility(View.INVISIBLE); }





                      //-----------------------------------------------
                      // MARK - MORE BUTTON
                      //-----------------------------------------------
                      moreButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            // Show data
                            moreView.setVisibility(View.VISIBLE);
                            moreTextView.setText(answerTxt.getText().toString());
                            moreFullnameTxt.setText(fullnameTxt.getText().toString());
                            Bitmap avatarBM = ((BitmapDrawable) avatarImg.getDrawable()).getBitmap();
                            moreAvatarImg.setImageBitmap(avatarBM);
                            moreAnsweredTxt.setText(answeredTxt.getText().toString());
                         }
                      });


                      //-----------------------------------------------
                      // MARK - ANSWER IMAGE BUTTON
                      //-----------------------------------------------
                      answerImgButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            imagePreviewView.setVisibility(View.VISIBLE);
                            Glide.with(ctx).load(aObj.key(ANSWERS_IMAGE).stringValue()).into(imagePreview);
                      }});


                      //-----------------------------------------------
                      // MARK - ANSWER USER BUTTON
                      //-----------------------------------------------
                      avatarImg.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {

                            // USER IS NOT LOGGED IN...
                            if (currentUser == null) {
                               startActivity(new Intent(ctx, Intro.class));

                            // USER IS LOGGED IN!
                            } else {

                               boolean isAnonymous = aObj.key(ANSWERS_IS_ANONYMOUS).booleanValue();
                               if (isAnonymous) {
                                  simpleAlert("You're not allowed to view this User's Profile", ctx);

                               } else {
                                  // Get Pointer
                                  XSGetPointer((Activity) ctx, aObj.key(ANSWERS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                                     @Override
                                     public void done(final JSON userPointer, String e) {
                                        if (userPointer != null) {

                                           Intent i = new Intent(ctx, Account.class);
                                           Bundle extras = new Bundle();

                                           if (userPointer.key("ID_id").stringValue().matches(currentUser.key("ID_id").stringValue())) {
                                              extras.putBoolean("isCurrentUser", true);
                                           } else {
                                              extras.putString("userObj", String.valueOf(userPointer));
                                              extras.putBoolean("isCurrentUser", false);
                                           }
                                           extras.putBoolean("showBackButton", true);

                                           i.putExtras(extras);
                                           startActivity(i);

                                        // error
                                        } else { simpleAlert(e, ctx);
                                  }}}); // ./ XSGetPointer
                               } // ./ If

                            } // ./ If

                      }});




                      //-----------------------------------------------
                      // MARK - LIKE BUTTON
                      //-----------------------------------------------
                      likeButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            // USER IS NOT LOGGED IN...
                            if (currentUser == null) {
                               startActivity(new Intent(ctx, Intro.class));

                            // USER IS LOGGED IN!
                            } else {
                               // Refresh aObj data
                               XSRefreshObjectData((Activity) ctx, ANSWERS_TABLE_NAME, aObj, new XServerSDK.XSRefreshObjectDataHandler() {
                                   @Override
                                   public void done(final JSON aObj, String error) {
                                      if (error == null) {

                                         final List<String> likedBy = XSGetArrayFromJSONArray(aObj.key(ANSWERS_LIKED_BY).getJsonArray());

                                         // UNLIKE ----------------------------------
                                         if (likedBy.contains(currentUser.key("ID_id").stringValue())){
                                            likedBy.remove(currentUser.key("ID_id").stringValue());

                                            RequestParams params = new RequestParams();
                                            params.put("tableName", ANSWERS_TABLE_NAME);
                                            params.put("ID_id", aObj.key("ID_id").stringValue());
                                            params.put(ANSWERS_LIKED_BY, XSGetStringFromArray(likedBy));

                                            XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                               if (e == null) {
                                                  likesTxt.setText(roundLargeNumber(likedBy.size()));
                                                  likeButton.setBackgroundResource(R.drawable.like_butt);
                                                  // error
                                               } else { simpleAlert(e, ctx);
                                            }}});// ./ XSObject



                                         // LIKE ----------------------------------
                                         } else {
                                            likedBy.add(currentUser.key("ID_id").stringValue());
                                            RequestParams params = new RequestParams();
                                            params.put("tableName", ANSWERS_TABLE_NAME);
                                            params.put("ID_id", aObj.key("ID_id").stringValue());
                                            params.put(ANSWERS_LIKED_BY, XSGetStringFromArray(likedBy));

                                            XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                               if (e == null) {
                                                  likesTxt.setText(roundLargeNumber(likedBy.size()));
                                                  likeButton.setBackgroundResource(R.drawable.liked_butt);
                                                  // error
                                               } else { simpleAlert(e, ctx);
                                            }}});// ./ XSObject


                                            // Get Pointer
                                            XSGetPointer((Activity)ctx, aObj.key(ANSWERS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                                               @Override public void done(final JSON userPointer, String e) {
                                                  if (userPointer != null) {

                                                     final String pushMessage = currentUser.key(USERS_FULLNAME).stringValue() + " liked your answer: " + aObj.key(ANSWERS_ANSWER).stringValue();

                                                     // Send Android Push Notification
                                                     XSSendAndroidPush((Activity)ctx, pushMessage, userPointer.key("ST_androidDeviceToken").stringValue(), "", new XServerSDK.XSAndroidPushHandler() {
                                                          @Override
                                                          public void done(boolean success, String e) {
                                                             if (e == null) {
                                                                Log.i(TAG, "ANDROID PUSH SENT TO: " + userPointer.key(USERS_USERNAME).stringValue() + " -> MESSAGE: " + pushMessage);
                                                              // error
                                                              } else { Log.i(TAG, e); }
                                                     }});// ./ XSSendAndroidPush

                                                     // Send iOS Push Notification
                                                     XSSendiOSPush((Activity)ctx, pushMessage, userPointer.key("ST_iosDeviceToken").stringValue(), "", new XServerSDK.XSiOSPushHandler() {
                                                         @Override
                                                         public void done(boolean success, String e) {
                                                             if (e == null) {
                                                                 Log.i(TAG, "iOS PUSH SENT TO: " + userPointer.key(USERS_USERNAME).stringValue() + " -> MESSAGE: " + pushMessage);
                                                             // error
                                                             } else { Log.i(TAG, e); }
                                                     }});// ./ XSSendiOSPush


                                                     // Save Notification
                                                     RequestParams params2 = new RequestParams();
                                                     params2.put("tableName", NOTIFICATIONS_TABLE_NAME);
                                                     params2.put(NOTIFICATIONS_CURRENT_USER, currentUser.key("ID_id").stringValue());
                                                     params2.put(NOTIFICATIONS_OTHER_USER, userPointer.key("ID_id").stringValue());
                                                     params2.put(NOTIFICATIONS_TEXT, pushMessage);

                                                     XSObject((Activity)ctx, params2, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                        if (e != null) { simpleAlert(e, ctx);
                                                     }}});// ./ XSObject

                                                  // error
                                                  } else { simpleAlert(e, ctx);
                                            }}}); // ./ XSGetPointer


                                         } //./ If

                                   // error
                                   } else { hideHUD(); simpleAlert(error, ctx); }
                               }});//./ XSRefreshObjectData

                         } //./ If
                      }});


                      //-----------------------------------------------
                      // MARK - DISLIKE BUTTON
                      //-----------------------------------------------
                      dislikeButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            // USER IS NOT LOGGED IN...
                            if (currentUser == null) {
                               startActivity(new Intent(ctx, Intro.class));

                            // USER IS LOGGED IN!
                            } else {

                               XSRefreshObjectData((Activity) ctx, ANSWERS_TABLE_NAME, aObj, new XServerSDK.XSRefreshObjectDataHandler() {
                                  @Override
                                  public void done(JSON aObj, String error) {
                                     if (error == null) {

                                        final List<String> dislikedBy = XSGetArrayFromJSONArray(aObj.key(ANSWERS_DISLIKED_BY).getJsonArray());

                                        // UN-DISLIKE
                                        if (dislikedBy.contains(currentUser.key("ID_id").stringValue())) {
                                           dislikedBy.remove(currentUser.key("ID_id").stringValue());

                                           RequestParams params = new RequestParams();
                                           params.put("tableName", ANSWERS_TABLE_NAME);
                                           params.put("ID_id", aObj.key("ID_id").stringValue());
                                           params.put(ANSWERS_DISLIKED_BY, XSGetStringFromArray(dislikedBy));

                                           XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                                              @Override
                                              public void done(String e, JSON obj) {
                                                 if (e == null) {
                                                    dislikesTxt.setText(roundLargeNumber(dislikedBy.size()));
                                                    dislikeButton.setBackgroundResource(R.drawable.dislike_butt);
                                                 // error
                                                 } else { simpleAlert(e, ctx);
                                           }}});// ./ XSObject


                                        // DISLIKE
                                        } else {
                                           dislikedBy.add(currentUser.key("ID_id").stringValue());

                                           RequestParams params = new RequestParams();
                                           params.put("tableName", ANSWERS_TABLE_NAME);
                                           params.put("ID_id", aObj.key("ID_id").stringValue());
                                           params.put(ANSWERS_DISLIKED_BY, XSGetStringFromArray(dislikedBy));

                                           XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                                              @Override
                                              public void done(String e, JSON obj) {
                                                 if (e == null) {
                                                    dislikesTxt.setText(roundLargeNumber(dislikedBy.size()));
                                                    dislikeButton.setBackgroundResource(R.drawable.disliked_butt);
                                                 // error
                                                 } else { simpleAlert(e, ctx);
                                           }}});// ./ XSObject

                                        }

                                     // error
                                     } else { hideHUD(); simpleAlert(error, ctx); }
                               }});//./ XSRefreshObjectData

                            } // ./ If
                      }});



                      //-----------------------------------------------
                      // MARK - REPORT ANSWER BUTTON
                      //-----------------------------------------------
                      reportAnswerButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            // USER IS NOT LOGGED IN...
                            if (currentUser == null) {
                               startActivity(new Intent(ctx, Intro.class));

                            // USER IS LOGGED IN!
                            } else {

                               final List<String> reportedBy = XSGetArrayFromJSONArray(aObj.key(ANSWERS_REPORTED_BY).getJsonArray());

                               // Fire alert
                               AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                               alert.setMessage("Do you want to report this Answer as inappropriate/abusive to the admin?")
                                     .setTitle(R.string.app_name)
                                     .setPositiveButton("Report Answer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                           reportedBy.add(currentUser.key("ID_id").stringValue());

                                           RequestParams params = new RequestParams();
                                           params.put("tableName", ANSWERS_TABLE_NAME);
                                           params.put("ID_id", aObj.key("ID_id").stringValue());
                                           params.put(ANSWERS_REPORTED_BY, XSGetStringFromArray(reportedBy));

                                           XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                              if (e == null) {

                                                 // Fire alert
                                                 AlertDialog.Builder alert2 = new AlertDialog.Builder(ctx, R.style.alert);
                                                 alert2.setMessage("Thanks for reporting this Answer. We'll take action within 24h")
                                                       .setTitle(R.string.app_name)
                                                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialogInterface, int i) {
                                                             queryAnswers();
                                                       }})
                                                       .setCancelable(false)
                                                       .setIcon(R.drawable.logo)
                                                       .create().show();

                                              // error
                                              } else { simpleAlert(e, ctx);
                                           }}});// ./ XSObject

                                      }})
                                     .setNegativeButton("Cancel", null)
                                     .setCancelable(false)
                                     .setIcon(R.drawable.logo)
                                     .create().show();

                            }// ./ If

                         }
                      });





                      //-----------------------------------------------
                      // MARK - EDIT ANSWER BUTTON
                      //-----------------------------------------------
                      editAnswerButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            final boolean isAnonymous = aObj.key(ANSWERS_IS_ANONYMOUS).booleanValue();

                            // Fire alert
                            List<String> buttons = new ArrayList<>();
                            buttons.add("Delete Answer");
                            buttons.add("Edit Answer");
                            new ActionSheet(ctx, buttons)
                                  .setTitle("Select option")
                                  .setCancelTitle("Cancel")
                                  .setColorTitle(Color.parseColor(GRAY))
                                  .setColorTitleCancel(Color.parseColor(GRAY))
                                  .setColorData(Color.parseColor(MAIN_COLOR))
                                  .create(new ActionSheetCallBack() {
                                     @Override
                                     public void data(@NotNull String data, int position) {
                                        switch (position) {

                                           // Delete Answer
                                           case 0:

                                              showHUD(ctx);
                                              XSDelete((Activity) ctx, ANSWERS_TABLE_NAME, aObj.key("ID_id").stringValue(), new XServerSDK.XSDeleteHandler() {
                                                 @Override
                                                 public void done(boolean success, String error) {
                                                    if (error == null) {

                                                       // Update qObj's answers
                                                       RequestParams params = new RequestParams();
                                                       params.put("tableName", QUESTIONS_TABLE_NAME);
                                                       params.put("ID_id", qObj.key("ID_id").stringValue());
                                                       params.put(QUESTIONS_ANSWERS, String.valueOf(qObj.key(QUESTIONS_ANSWERS).intValue() - 1));

                                                       XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                          if (e == null) {
                                                             hideHUD();
                                                             mustReload = true;

                                                             // Recall query
                                                             showQuestionDetails();

                                                          // error
                                                          } else { hideHUD(); simpleAlert(e, ctx);
                                                       }}});// ./ XSObject

                                                    // error
                                                    } else { hideHUD(); simpleAlert(error, ctx); }
                                              }});//./ XSDelete

                                           break;


                                           // Edit Answer
                                           case 1:
                                              Intent i = new Intent(ctx, PostScreen.class);
                                              Bundle extras = new Bundle();

                                              extras.putBoolean("isAnonymous", isAnonymous);
                                              extras.putBoolean("isQuestion", false);
                                              extras.putBoolean("isEditingMode", true);
                                              extras.putString("qObject", String.valueOf(qObj));
                                              extras.putString("aObject", String.valueOf(aObj));

                                              i.putExtras(extras);
                                              startActivity(i);
                                              break;
                                  }}});

                      }});





                      //-----------------------------------------------
                      // MARK - SET BEST ANSWER BUTTON
                      //-----------------------------------------------
                      setBestAnswerButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            if (currentUser == null){
                               startActivity(new Intent(ctx, Intro.class));

                            } else {

                               // userPointer
                               XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                                  @Override public void done(final JSON userPointer, String e) {
                                     if (userPointer != null) {

                                          // CURRENT USER OWNS THIS QUESTION!
                                          if (userPointer.key("ID_id").stringValue().matches(currentUser.key("ID_id").stringValue())) {

                                             // fire alert
                                             AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
                                             alert.setMessage("Are you sure you want to set this answer as best? Once set, you can no longer change it.")
                                                   .setTitle(R.string.app_name)
                                                   .setPositiveButton("Best Answer", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialog, int which) {
                                                         showHUD(ctx);

                                                         // Update aObj
                                                         RequestParams params = new RequestParams();
                                                         params.put("tableName", ANSWERS_TABLE_NAME);
                                                         params.put("ID_id", aObj.key("ID_id").stringValue());
                                                         params.put(ANSWERS_IS_BEST, "1");

                                                         XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                            if (e == null) {

                                                               // Update qObj
                                                               RequestParams params2 = new RequestParams();
                                                               params2.put("tableName", QUESTIONS_TABLE_NAME);
                                                               params2.put("ID_id", qObj.key("ID_id").stringValue());
                                                               params2.put(QUESTIONS_HAS_BEST_ANSWER, "1");

                                                               XSObject((Activity)ctx, params2, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                                  if (e == null) {
                                                                     hideHUD();

                                                                     // Refresh qObj and query Answers
                                                                     XSRefreshObjectData((Activity) ctx, QUESTIONS_TABLE_NAME, qObj, new XServerSDK.XSRefreshObjectDataHandler() {
                                                                         @Override
                                                                         public void done(JSON object, String error) {
                                                                            if (error == null) {
                                                                               // Update object
                                                                               qObj = object;

                                                                               // Recall query
                                                                               queryAnswers();

                                                                         // error
                                                                         } else { simpleAlert(error, ctx); }
                                                                     }});//./ XSRefreshObjectData


                                                                  // error
                                                                  } else { hideHUD(); simpleAlert(e, ctx);
                                                               }}});// ./ XSObject

                                                            // error
                                                            } else { hideHUD(); simpleAlert(e, ctx);
                                                         }}});// ./ XSObject

                                                   }})
                                                   .setNegativeButton("Cancel", null)
                                                   .setCancelable(false)
                                                   .setIcon(R.drawable.logo)
                                                   .create().show();


                                          // CURRENT USER DOESN'T OWN THIS QUESTION...
                                          } else { simpleAlert("Only the User who made this question is allowed to choose the best answer!", ctx); }


                                          // error
                                       } else { simpleAlert(e, ctx);
                                       }}}); // ./ XSGetPointer


                                     }// ./ If

                      }});



                      // error
                   } else { simpleAlert(e, ctx);
                   }}}); // ./ XSGetPointer



         return cell;
         }
         @Override public int getCount() { return answersArray.size(); }
         @Override public Object getItem(int position) { return answersArray.get(position); }
         @Override public long getItemId(int position) { return position; }
      }

      // Set Adapter
      answersListView.setAdapter(new ListAdapter(ctx));
   }




}// ./ end
