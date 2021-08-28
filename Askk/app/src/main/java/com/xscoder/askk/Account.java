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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mb3364.http.RequestParams;

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
import static com.xscoder.askk.XServerSDK.ANSWERS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.ANSWERS_QUESTION_POINTER;
import static com.xscoder.askk.XServerSDK.ANSWERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.ANSWERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.ANSWERS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.BLACK_COLOR;
import static com.xscoder.askk.XServerSDK.MAIN_COLOR;
import static com.xscoder.askk.XServerSDK.QUESTIONS_ANSWERS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_CATEGORY;
import static com.xscoder.askk.XServerSDK.QUESTIONS_COLOR;
import static com.xscoder.askk.XServerSDK.QUESTIONS_CREATED_AT;
import static com.xscoder.askk.XServerSDK.QUESTIONS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_QUESTION;
import static com.xscoder.askk.XServerSDK.QUESTIONS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.QUESTIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.QUESTIONS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.QUESTIONS_VIEWS;
import static com.xscoder.askk.XServerSDK.TAG;
import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_EDUCATION;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_LOCATION;
import static com.xscoder.askk.XServerSDK.USERS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.USERS_USERNAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSGetArrayFromJSONArray;
import static com.xscoder.askk.XServerSDK.XSGetDateFromString;
import static com.xscoder.askk.XServerSDK.XSGetPointer;
import static com.xscoder.askk.XServerSDK.XSGetStringFromArray;
import static com.xscoder.askk.XServerSDK.XSLogout;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSQuery;
import static com.xscoder.askk.XServerSDK.XSRemoveDuplicatesFromArray;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.roundLargeNumber;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class Account extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

   // VIEWS //
   Button backButton, questionsButton, answersButton, editProfileButton, reportUserButton, logoutButton;
   CircleImageView avatarImg;
   TextView fullnameTxt, usernameTxt, locationTxt, educationTxt;
   ListView QAListView;
   SwipeRefreshLayout refreshControl;
   RelativeLayout tabBar;


   // VARIABLES //
   Context ctx = this;
   JSON currentUser;
   boolean isCurrentUser = true;
   boolean showBackbutton = false;
   boolean isQuestions = true;
   JSON userObj;
   List<JSON>QAArray = new ArrayList<>();





   //-----------------------------------------------
   // MARK - ON START
   //-----------------------------------------------
   @Override
   protected void onStart() {
      super.onStart();

      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
         // Current User IS LOGGED IN!
         if (currUser != null) {
            currentUser = currUser;

            // Call function
            if (isCurrentUser) { showUserDetails(currentUser);
            } else { showUserDetails(userObj); }

            Log.i(TAG, "MY_VARIABLE: " + isCurrentUser);

            if (mustReload){
               mustReload = false;

               // Call query
               callQuery();
            }

         // Current User IS LOGGED OUT
         } else { startActivity(new Intent(ctx, Intro.class)); }
      }}); // ./ XSCurrentUser

   }




   //-----------------------------------------------
   // MARK - ON CREATE
   //-----------------------------------------------
   @SuppressLint("SourceLockedOrientationActivity")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.account);
      super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      // Hide ActionBar
      Objects.requireNonNull(getSupportActionBar()).hide();


      //-----------------------------------------------
      // MARK - INITIALIZE VIEWS
      //-----------------------------------------------
      backButton = findViewById(R.id.accBackButton);
      questionsButton = findViewById(R.id.accQuestionsButton);
      questionsButton.setTypeface(popBold);
      answersButton = findViewById(R.id.accAnswersButton);
      answersButton.setTypeface(popBold);
      editProfileButton = findViewById(R.id.accEditProfileButton);
      reportUserButton = findViewById(R.id.accReportButton);
      avatarImg = findViewById(R.id.accAvatarImg);
      fullnameTxt = findViewById(R.id.accFullnameTxt);
      fullnameTxt.setTypeface(popBold);
      usernameTxt = findViewById(R.id.accusernameTxt);
      usernameTxt.setTypeface(popRegular);
      locationTxt = findViewById(R.id.accLocationTxt);
      locationTxt.setTypeface(popRegular);
      educationTxt = findViewById(R.id.accEducationTxt);
      educationTxt.setTypeface(popRegular);
      logoutButton = findViewById(R.id.accLogoutButton);
      QAListView = findViewById(R.id.accQAListView);
      tabBar = findViewById(R.id.tabBar);

      // Refresh Control
      refreshControl = findViewById(R.id.refreshControl);
      refreshControl.setOnRefreshListener(this);


      //-----------------------------------------------
      // MARK - TAB BAR BUTTONS
      //-----------------------------------------------
      Button tab1 = findViewById(R.id.tab1);
      Button tab2 = findViewById(R.id.tab2);

      tab1.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(ctx, Home.class));
      }});

      tab2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(ctx, NotificationsScreen.class));
      }});



      // Get extras
      Bundle extras = getIntent().getExtras();
      if(extras != null) {
         isCurrentUser = extras.getBoolean("isCurrentUser");
         showBackbutton = extras.getBoolean("showBackButton");
         userObj = new JSON(extras.getString("userObj"));
      }

      // Show back button
      if (showBackbutton) {
         backButton.setVisibility(View.VISIBLE);
         tabBar.setVisibility(View.GONE);
      } else {
         backButton.setVisibility(View.INVISIBLE);
         tabBar.setVisibility(View.VISIBLE);
      }


      Log.i(TAG, "IS CURRENT USER: " + isCurrentUser);
      Log.i(TAG, "SHOW BACK BUTTON: " + showBackbutton);


      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
         // Current User IS LOGGED IN!
         if (currUser != null) {
            currentUser = currUser;

            // Call function
            if (isCurrentUser) {
               showUserDetails(currentUser);
               editProfileButton.setVisibility(View.VISIBLE);
               reportUserButton.setVisibility(View.INVISIBLE);

            } else {
               showUserDetails(userObj);
               editProfileButton.setVisibility(View.INVISIBLE);
               reportUserButton.setVisibility(View.VISIBLE);

            }

            callQuery();

         // Current User IS LOGGED OUT
         } else { startActivity(new Intent(ctx, Intro.class)); }
      }}); // ./ XSCurrentUser





      //-----------------------------------------------
      // MARK - QUESTIONS BUTTON
      //-----------------------------------------------
      questionsButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           isQuestions = true;
           questionsButton.setBackgroundResource(R.drawable.rounded_main_butt);
           questionsButton.setTextColor(Color.WHITE);
           answersButton.setBackgroundColor(Color.TRANSPARENT);
           answersButton.setTextColor(Color.parseColor(MAIN_COLOR));

           callQuery();
      }});



      //-----------------------------------------------
      // MARK - ANSWERS BUTTON
      //-----------------------------------------------
      answersButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           isQuestions = false;
           questionsButton.setBackgroundColor(Color.TRANSPARENT);
           questionsButton.setTextColor(Color.parseColor(MAIN_COLOR));
           answersButton.setBackgroundResource(R.drawable.rounded_main_butt);
           answersButton.setTextColor(Color.WHITE);

           callQuery();
      }});




      //-----------------------------------------------
      // MARK - EDIT PROFILE BUTTON
      //-----------------------------------------------
      editProfileButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            startActivity(new Intent(ctx, EditProfile.class));
      }});



      //-----------------------------------------------
      // MARK - REPORT USER BUTTON
      //-----------------------------------------------
      reportUserButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            // Fire alert
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
            alert.setMessage("Are you sure you want to report this User to the Admin?")
                   .setTitle(R.string.app_name)
                   .setPositiveButton("Report User", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {

                         List<String> reportedBy = XSGetArrayFromJSONArray(userObj.key(USERS_REPORTED_BY).getJsonArray());
                         reportedBy.add(currentUser.key("ID_id").stringValue());
                         showHUD(ctx);

                         RequestParams params = new RequestParams();
                         params.put("tableName", USERS_TABLE_NAME);
                         params.put("ID_id", userObj.key("ID_id").stringValue());
                         params.put(USERS_REPORTED_BY, XSGetStringFromArray(reportedBy));

                         XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                            @Override
                            public void done(String e, JSON obj) {
                               if (e == null) {
                                  hideHUD();

                                  // 1. Query all Questions of this user and report them (if any)
                                  XSQuery((Activity) ctx, QUESTIONS_TABLE_NAME, "", "", new XServerSDK.XSQueryHandler() {
                                     @Override
                                     public void done(JSON objects, String error) {
                                        if (error == null) {
                                           if (objects.count() != 0) {
                                              // For
                                              for (int i = 0; i < objects.count(); i++) {
                                                 JSON obj = objects.index(i);

                                                 if (obj.key(QUESTIONS_USER_POINTER).stringValue().matches(userObj.key("ID_id").stringValue())) {

                                                    List<String> qReportedBy = XSGetArrayFromJSONArray(obj.key(QUESTIONS_REPORTED_BY).getJsonArray());
                                                    qReportedBy.add(currentUser.key("ID_id").stringValue());
                                                    RequestParams params = new RequestParams();
                                                    params.put("tableName", QUESTIONS_TABLE_NAME);
                                                    params.put("ID_id", obj.key("ID_id").stringValue());
                                                    params.put(QUESTIONS_REPORTED_BY, XSGetStringFromArray(qReportedBy));

                                                    XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                                                       @Override
                                                       public void done(String e, JSON obj) {
                                                          if (e != null) { simpleAlert(e, ctx);
                                                    }}});// ./ XSObject

                                                 } //./ If
                                              } //./ For

                                           } //./ If

                                        // error
                                        } else { simpleAlert(error, ctx);
                                  }}});// /. XSQuery


                                  // 2. Query all Answers of this user and report them (if any)
                                  XSQuery((Activity) ctx, ANSWERS_TABLE_NAME, "", "", new XServerSDK.XSQueryHandler() {
                                     @Override
                                     public void done(JSON objects, String error) {
                                        if (error == null) {
                                           if (objects.count() != 0) {
                                              // For
                                              for (int i = 0; i < objects.count(); i++) {
                                                 JSON obj = objects.index(i);

                                                 if (obj.key(ANSWERS_USER_POINTER).stringValue().matches(userObj.key("ID_id").stringValue())) {

                                                    List<String> aReportedBy = XSGetArrayFromJSONArray(obj.key(ANSWERS_REPORTED_BY).getJsonArray());
                                                    aReportedBy.add(currentUser.key("ID_id").stringValue());
                                                    RequestParams params = new RequestParams();
                                                    params.put("tableName", ANSWERS_TABLE_NAME);
                                                    params.put("ID_id", obj.key("ID_id").stringValue());
                                                    params.put(ANSWERS_REPORTED_BY, XSGetStringFromArray(aReportedBy));

                                                    XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                                                       @Override
                                                       public void done(String e, JSON obj) {
                                                          if (e != null) { simpleAlert(e, ctx);
                                                    }}});// ./ XSObject

                                                 } //./ If
                                              } //./ For

                                           } //./ If
                                        // error
                                        } else { simpleAlert(error, ctx);
                                  }}});// /. XSQuery


                                  // Fire alert
                                  AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
                                  alert.setMessage("Thanks for reporting @" + userObj.key(USERS_USERNAME) + " to us. We'll take action for it within 24h.")
                                          .setTitle(R.string.app_name)
                                          .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(ctx, Home.class));
                                             }
                                          })
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

      }});




      //-----------------------------------------------
      // MARK - LOGOUT BUTTON
      //-----------------------------------------------
      logoutButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

           // Fire alert
           AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.alert);
           alert.setMessage("Are you sure you want to logout?")
                 .setTitle(R.string.app_name)
                 .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       showHUD(ctx);

                       XSLogout(new XServerSDK.LogoutHandler() { @Override public void done(boolean success) {
                           if (success) {
                              GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                              GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(ctx, gso);
                              googleSignInClient.signOut();

                              hideHUD();
                              startActivity(new Intent(ctx, Home.class));

                           // error
                           } else { hideHUD(); simpleAlert("Something went wrong. Try again", ctx);
                       }}});// ./ XSLogout
                 }})
                 .setNegativeButton("Cancel", null)
                 .setCancelable(false)
                 .setIcon(R.drawable.logo)
                 .create().show();
      }});



      //-----------------------------------------------
      // MARK - BACK BUTTON
      //-----------------------------------------------
      backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) { finish(); }});



   }// ./ onCreate







   // ------------------------------------------------
   // MARK: - SHOW USER DETAILS
   // ------------------------------------------------
   @SuppressLint("SetTextI18n")
   void showUserDetails(JSON uObj) {
      fullnameTxt.setText(uObj.key(USERS_FULLNAME).stringValue());
      usernameTxt.setText("@" + uObj.key(USERS_USERNAME).stringValue());
      Glide.with(ctx).load(uObj.key(USERS_AVATAR).stringValue()).into(avatarImg);
      if (!uObj.key(USERS_LOCATION).stringValue().matches("")) { locationTxt.setText(uObj.key(USERS_LOCATION).stringValue());
      } else { locationTxt.setText("N/A"); }
      if (!uObj.key(USERS_EDUCATION).stringValue().matches("")) { educationTxt.setText(uObj.key(USERS_EDUCATION).stringValue());
      } else { educationTxt.setText("N/A"); }
   }




   // ------------------------------------------------
   // MARK: - CALL QUERY
   // ------------------------------------------------
   void callQuery() {
      // Call query
      if (isQuestions){ queryQuestions();
      } else { queryAnswers(); }
   }



   // ------------------------------------------------
   // MARK: - QUERY QUESTIONS
   // ------------------------------------------------
   void queryQuestions() {
      showHUD(ctx);
      QAArray = new ArrayList<>();
      QAListView.invalidateViews();
      QAListView.refreshDrawableState();


      XSQuery((Activity)ctx, QUESTIONS_TABLE_NAME, QUESTIONS_CREATED_AT, "", new XServerSDK.XSQueryHandler() {
         @Override public void done(JSON objects, String error) {
            if (error == null) {
               for (int i = 0; i < objects.count(); i++) {
                  JSON obj = objects.index(i);

                  if (isCurrentUser){
                     if (obj.key(QUESTIONS_USER_POINTER).stringValue().matches(currentUser.key("ID_id").stringValue())
                     ){ QAArray.add(obj); }
                  } else {
                     if (obj.key(QUESTIONS_USER_POINTER).stringValue().matches(userObj.key("ID_id").stringValue())
                         && !obj.key(QUESTIONS_IS_ANONYMOUS).booleanValue()
                     ){ QAArray.add(obj); }
                  }

                  // [Finalize array of objects]
                  if (i == objects.count()-1) { QAArray = XSRemoveDuplicatesFromArray(QAArray); }
               } // ./ For


               // There area some objects
               if (QAArray.size() != 0) {
                  hideHUD();
                  QAListView.setVisibility(View.VISIBLE);
                  showDataInListView();

                  // NO objects
               } else {
                  hideHUD();
                  QAListView.setVisibility(View.INVISIBLE);
               }

            // error
            } else { hideHUD(); simpleAlert(error, ctx); }
      }});// /. XSQuery

   }



   // ------------------------------------------------
   // MARK: -  QUERY ANSWERS
   // ------------------------------------------------
   void queryAnswers() {
      showHUD(ctx);
      QAArray = new ArrayList<>();
      QAListView.invalidateViews();
      QAListView.refreshDrawableState();

      XSQuery((Activity)ctx, ANSWERS_TABLE_NAME, ANSWERS_CREATED_AT, "", new XServerSDK.XSQueryHandler() {
         @Override public void done(JSON objects, String error) {
            if (error == null) {
               for (int i = 0; i < objects.count(); i++) {
                  JSON obj = objects.index(i);

                  if (isCurrentUser){
                     if (obj.key(ANSWERS_USER_POINTER).stringValue().matches(currentUser.key("ID_id").stringValue())
                     ){ QAArray.add(obj); }
                  } else {
                     if (obj.key(ANSWERS_USER_POINTER).stringValue().matches(userObj.key("ID_id").stringValue())
                         && !obj.key(ANSWERS_IS_ANONYMOUS).booleanValue()
                     ){ QAArray.add(obj); }
                  }

                  // [Finalize array of objects]
                  if (i == objects.count()-1) { QAArray = XSRemoveDuplicatesFromArray(QAArray); }
               } // ./ For


               // There area some objects
               if (QAArray.size() != 0) {
                  hideHUD();
                  QAListView.setVisibility(View.VISIBLE);
                  showDataInListView();

               // NO objects
               } else {
                  hideHUD();
                  QAListView.setVisibility(View.INVISIBLE);
               }

            // error
            } else { hideHUD(); simpleAlert(error, ctx); }
      }});// /. XSQuery

   }



   //-----------------------------------------------
   // MARK - SHOW DATA IN LISTVIEW
   //-----------------------------------------------
   void showDataInListView() {
      @SuppressLint("SetTextI18n")
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
               cell = inflater.inflate(R.layout.cell_question, null);
            }

            //-----------------------------------------------
            // MARK - INITIALIZE VIEWS
            //-----------------------------------------------
            final RelativeLayout aCell = cell.findViewById(R.id.ccatCell);
            final TextView dateTxt = cell.findViewById(R.id.ccatDateTxt);
            dateTxt.setTypeface(popRegular);
            final TextView categoryTxt = cell.findViewById(R.id.ccatCategoryTxt);
            categoryTxt.setTypeface(popRegular);
            final ImageView categoryImg = cell.findViewById(R.id.ccatCategoryImg);
            final CircleImageView avatarImg = cell.findViewById(R.id.ccatAvatarImg);
            final TextView questionTxt = cell.findViewById(R.id.ccatQuestionTxt);
            questionTxt.setTypeface(popBold);
            final TextView answeredByTxt = cell.findViewById(R.id.ccatAnsweredByTxt);
            answeredByTxt.setTypeface(popRegular);


            // [SHOW QUESTIONS] ------------------------------------------------------------------
            if (isQuestions) {

               // Obj
               final JSON qObj = QAArray.get(position);

               // Get Pointer
               XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                  @Override public void done(final JSON userPointer, String e) {
                     if (userPointer != null) {

                        // isAnonymous
                        boolean isAnonymuos = qObj.key(QUESTIONS_IS_ANONYMOUS).booleanValue();

                        // Color
                        aCell.setBackgroundColor(Color.parseColor(qObj.key(QUESTIONS_COLOR).stringValue()));

                        // Category Image • Name
                        categoryImg.setVisibility(View.VISIBLE);
                        categoryTxt.setVisibility(View.VISIBLE);
                        int imgID = getResources().getIdentifier(qObj.key(QUESTIONS_CATEGORY).stringValue(), "drawable", getPackageName());
                        categoryImg.setImageResource(imgID);
                        categoryTxt.setText(Objects.requireNonNull(qObj.key(QUESTIONS_CATEGORY).stringValue()).toUpperCase());

                        // Fullname • Date
                        Date aDate = XSGetDateFromString(qObj.key(QUESTIONS_CREATED_AT).stringValue());
                        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        String dateStr = df.format(aDate);

                        if (isAnonymuos) { dateTxt.setText("(Anonymously) • " + dateStr);
                        } else { dateTxt.setText(dateStr); }

                        // Avatar
                        if (isAnonymuos) { avatarImg.setImageResource(R.drawable.anonymous_avatar);
                        } else { Glide.with(ctx).load(userPointer.key(USERS_AVATAR).stringValue()).into(avatarImg); }

                        // Question
                        questionTxt.setText(qObj.key(QUESTIONS_QUESTION).stringValue());

                        // Answers & Views
                        int answers = qObj.key(QUESTIONS_ANSWERS).intValue();
                        int views = qObj.key(QUESTIONS_VIEWS).intValue();
                        answeredByTxt.setText(roundLargeNumber(answers) + " answers • " + roundLargeNumber(views) + " views");



                     // error
                     } else { simpleAlert(e, ctx);
               }}}); // ./ XSGetPointer


            // [SHOW ANSWERS] --------------------------------------------------------------------
            } else {
               // Obj
               final JSON aObj = QAArray.get(position);

               // Get Pointer
               XSGetPointer((Activity)ctx, aObj.key(ANSWERS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                  @Override public void done(final JSON userPointer, String e) {
                     if (userPointer != null) {

                        // isAnonymous
                        boolean isAnonymuos = aObj.key(ANSWERS_IS_ANONYMOUS).booleanValue();

                        // Color
                        aCell.setBackgroundColor(Color.parseColor(BLACK_COLOR));

                        // Hide Category Image • Name
                        categoryImg.setVisibility(View.INVISIBLE);
                        categoryTxt.setVisibility(View.INVISIBLE);

                        // Fullname • Date
                        Date aDate = XSGetDateFromString(aObj.key(ANSWERS_CREATED_AT).stringValue());
                        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        String dateStr = df.format(aDate);

                        if (isAnonymuos){ dateTxt.setText("(Anonymously) • " + dateStr);
                        } else{ dateTxt.setText(dateStr); }

                        // Avatar
                        if (isAnonymuos){ avatarImg.setImageResource(R.drawable.anonymous_avatar);
                        } else { Glide.with(ctx).load(userPointer.key(USERS_AVATAR).stringValue()).into(avatarImg); }

                        // Answer
                        questionTxt.setText(aObj.key(ANSWERS_ANSWER).stringValue());

                        // questionPointer
                        XSGetPointer((Activity)ctx, aObj.key(ANSWERS_QUESTION_POINTER).stringValue(), QUESTIONS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                           @Override public void done(final JSON questionPointer, String e) {
                              if (questionPointer != null) {
                                 answeredByTxt.setText("In: '" + questionPointer.key(QUESTIONS_QUESTION).stringValue() + "'");
                              // error
                              } else { simpleAlert(e, ctx);
                        }}}); // ./ XSGetPointer

                     // error
                     } else { simpleAlert(e, ctx);
               }}}); // ./ XSGetPointer
            }


         return cell;
         }
         @Override public int getCount() { return QAArray.size(); }
         @Override public Object getItem(int position) { return QAArray.get(position); }
         @Override public long getItemId(int position) { return position; }
      }

      // Set Adapter
      QAListView.setAdapter(new ListAdapter(ctx));



      //-----------------------------------------------
      // MARK - TAP CELL -> SEE QUESTIONS OR ANSWERS
      //-----------------------------------------------
      QAListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            if (isQuestions) {
               // Obj
               JSON qObj = QAArray.get(position);

               Intent i = new Intent(ctx, QuestionScreen.class);
               Bundle extras = new Bundle();
               extras.putString("object", String.valueOf(qObj));
               i.putExtras(extras);
               startActivity(i);

            } else {
               // Obj
               JSON aObj = QAArray.get(position);

               // Get Pointer
               XSGetPointer((Activity)ctx, aObj.key(ANSWERS_QUESTION_POINTER).stringValue(), QUESTIONS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                  @Override public void done(final JSON questionPointer, String e) {
                     if (questionPointer != null) {

                        Intent i = new Intent(ctx, QuestionScreen.class);
                        Bundle extras = new Bundle();
                        extras.putString("object", String.valueOf(questionPointer));
                        i.putExtras(extras);
                        startActivity(i);

                     // error
                     } else { simpleAlert(e, ctx);
               }}}); // ./ XSGetPointer

            }// :/ If

      }});
   }







   //-----------------------------------------------
   // MARK - REFRESH DATA
   //-----------------------------------------------
   @Override
   public void onRefresh() {
      callQuery();
      if (refreshControl.isRefreshing()) { refreshControl.setRefreshing(false); }
   }


}// ./ end
