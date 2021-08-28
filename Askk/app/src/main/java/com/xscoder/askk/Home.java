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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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

import static com.xscoder.askk.XServerSDK.ANDROID_DEVICE_TOKEN;
import static com.xscoder.askk.XServerSDK.GRAY;
import static com.xscoder.askk.XServerSDK.MAIN_COLOR;
import static com.xscoder.askk.XServerSDK.MULTIPLE_PERMISSIONS;
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
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSGetArrayFromJSONArray;
import static com.xscoder.askk.XServerSDK.XSGetDateFromString;
import static com.xscoder.askk.XServerSDK.XSGetPointer;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSQuery;
import static com.xscoder.askk.XServerSDK.XSRemoveDuplicatesFromArray;
import static com.xscoder.askk.XServerSDK.categoriesArray;
import static com.xscoder.askk.XServerSDK.colorsArray;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.permissions;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.roundLargeNumber;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class Home extends AppCompatActivity {


    // VIEWS //
    ListView questionsListView;
    TextView categoryTxt;
    EditText searchEditText;
    Button postQuestionButton;



    // VARIABLES //
    Context ctx = this;
    JSON currentUser;
    List<JSON>questionsArray = new ArrayList<>();
    String selectedCategory = categoriesArray[0];
    String searchTxt = "";




    //-----------------------------------------------
    // MARK - ON START
    //-----------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        // currentUser IS LOGGED IN!
        XSCurrentUser(this, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
            // currentUse IS LOGGED IN!
            if (currUser != null) {

                currentUser = currUser;

                // Get deviceToken
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        ANDROID_DEVICE_TOKEN = Objects.requireNonNull(task.getResult()).getToken();
                        Log.i(TAG, "DEVICE TOKEN: " + ANDROID_DEVICE_TOKEN);

                        // Register the device Token - for Push Notifications
                        RequestParams params = new RequestParams();
                        params.put("tableName", "Users");
                        params.put("ID_id", currentUser.key("ID_id").stringValue());
                        params.put("ST_androidDeviceToken", ANDROID_DEVICE_TOKEN);
                        // Reset app icon badge to 0
                        params.put("NU_badge", "0");
                        XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                            @Override
                            public void done(String e, JSON obj) {
                                if (e == null) { Log.i(TAG, "DEVICE TOKEN REGISTERED");
                        }}});
                }});
            } //./ If

            if (mustReload){
                mustReload = false;
                queryQuestions();
            }
        }}); // ./ XSCurrentUser



        // Check Permissions
        if (!checkPermissions()) { checkPermissions(); }

    }






    //-----------------------------------------------
    // MARK - ON CREATE
    //-----------------------------------------------
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();



        //-----------------------------------------------
        // MARK - INITIALIZE VIEWS
        //-----------------------------------------------
        searchEditText = findViewById(R.id.hSearchEditText);
        searchEditText.setTypeface(popRegular);
        categoryTxt = findViewById(R.id.hCategoryTxt);
        categoryTxt.setTypeface(popBold);
        questionsListView = findViewById(R.id.hQuestionsListView);
        postQuestionButton = findViewById(R.id.hPostQuestionButton);


        //-----------------------------------------------
        // MARK - TAB BAR BUTTONS
        //-----------------------------------------------
        Button tab1 = findViewById(R.id.tab2);
        Button tab2 = findViewById(R.id.tab3);

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, NotificationsScreen.class));
        }});

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, Account.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isCurrentUser", true);
                extras.putBoolean("showBackButton", false);
                i.putExtras(extras);
                startActivity(i);
        }});



        //-----------------------------------------------
        // MARK - SEARCH BY KEYWORDS
        //-----------------------------------------------
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // Call query
                    if (!searchEditText.getText().toString().matches("")) {
                        searchTxt = searchEditText.getText().toString();
                        queryQuestions();
                        dismissKeyboard();
                    } else { simpleAlert("Please type something.", ctx); }

                    return true;
                } return false;
        }});


        // Default Category
        categoryTxt.setText(categoriesArray[0].toUpperCase());


        // Call function
        setupCategoriesScrollView();



        // Get Current User
        XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
            // Current User IS LOGGED IN!
            if (currUser != null) { currentUser = currUser; }

            // Call query
            queryQuestions();
        }}); // ./ XSCurrentUser




        //-----------------------------------------------
        // MARK - POST QUESTION BUTTON
        //-----------------------------------------------
        postQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // USER IS NOT LOGGED IN...
                if (currentUser == null) {
                    startActivity(new Intent(ctx, Intro.class));

                // USER IS LOGGED IN!
                } else {
                    final Intent i = new Intent(ctx, PostScreen.class);
                    final Bundle extras = new Bundle();


                    List<String>buttons = new ArrayList<>();
                    buttons.add("Ask with name");
                    buttons.add("Ask anonymously");
                    new ActionSheet(ctx, buttons)
                          .setTitle("How do you want to ask a question?")
                          .setCancelTitle("Cancel")
                          .setColorTitle(Color.parseColor(GRAY))
                          .setColorTitleCancel(Color.parseColor("#333333"))
                          .setColorData(Color.parseColor(MAIN_COLOR))
                          .create(new ActionSheetCallBack() {
                              @Override
                              public void data(@NotNull String data, int position) {
                                  switch (position){

                                      // Ask with name
                                      case 0:
                                          extras.putBoolean("isAnonymous", false);
                                          extras.putBoolean("isQuestion", true);
                                          i.putExtras(extras);
                                          startActivity(i);
                                          break;

                                      // Ask anonymously
                                      case 1:
                                          extras.putBoolean("isAnonymous", true);
                                          extras.putBoolean("isQuestion", true);
                                          i.putExtras(extras);
                                          startActivity(i);
                                          break;
                                  }}});


                }// ./ If
        }});



    }// ./ onCreate





    // ------------------------------------------------
    // MARK: - SETUP CATEGORIES SCROLLVIEW
    // ------------------------------------------------
    void setupCategoriesScrollView() {

        for (int i=0; i<categoriesArray.length; i++) {
            LinearLayout layout = findViewById(R.id.hCategoriesLayout);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            final int finalI = i;

            // Size values
            int W = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            int H = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
            int WH44 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics());
            int WH20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

            // Container View
            RelativeLayout containerView = new RelativeLayout(ctx);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(W, H);
            lp.setMargins(30, 0, 30, 0);
            containerView.setLayoutParams(lp);


            // Category Button (as CircleImageView)
            CircleImageView aButt = new CircleImageView(ctx);
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(WH44, WH44);
            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            aButt.setLayoutParams(lp2);

            int imgID = getResources().getIdentifier(categoriesArray[i], "drawable", getPackageName());
            aButt.setBackgroundColor(Color.parseColor(colorsArray[i]));
            aButt.setImageResource(imgID);
            aButt.setClipToOutline(true);
            containerView.addView(aButt);

            //-----------------------------------------------
            // MARK - CATEGORY BUTTON
            //-----------------------------------------------
            aButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryTxt.setText(categoriesArray[finalI].toUpperCase());
                    selectedCategory = categoriesArray[finalI];

                    if (selectedCategory.matches(categoriesArray[0]) ||
                          selectedCategory.matches(categoriesArray[1]) ||
                          selectedCategory.matches(categoriesArray[2]) ){
                        categoryTxt.setTextColor(Color.BLACK);
                    } else { categoryTxt.setTextColor(Color.parseColor(colorsArray[finalI])); }

                    searchTxt = "";
                    searchEditText.setText("");

                    // Call query
                    queryQuestions();
            }});


            // TextView
            TextView aTxt = new TextView(ctx);
            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(W, WH20);
            lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            aTxt.setLayoutParams(lp3);

            aTxt.setTextColor(Color.parseColor(colorsArray[i]));
            aTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            aTxt.setTextSize(10);
            aTxt.setTypeface(popRegular);
            aTxt.setText(categoriesArray[i].toUpperCase());
            containerView.addView(aTxt);


            // Add containerView to the layout
            layout.addView(containerView);
        }// ./ For

    }



        
    // ------------------------------------------------
    // MARK: - QUERY QUESTIONS
    // ------------------------------------------------
    void queryQuestions() {
        showHUD(ctx);
        questionsArray = new ArrayList<>();
        questionsListView.invalidateViews();
        questionsListView.refreshDrawableState();

        // Set initial conditions
        String colName;
        int limit;

        // Latest
        if (selectedCategory.matches(categoriesArray[0])){
            colName = QUESTIONS_CREATED_AT;
            limit = 100;

        // Trending
        } else if (selectedCategory.matches(categoriesArray[1])){
            colName = QUESTIONS_ANSWERS;
            limit = 20;

        // Need Answers
        } else if (selectedCategory.matches(categoriesArray[2])){
            colName = QUESTIONS_ANSWERS;
            limit = 1000000;

        // Category
        } else {
            colName = QUESTIONS_CREATED_AT;
            limit = 1000000;
        }

        // Search
        if (!searchTxt.matches("")){
            selectedCategory = "";
            colName = QUESTIONS_CREATED_AT;
            limit = 1000000;
        }

        // Launch query
        final int finalLimit = limit;
        XSQuery((Activity)ctx, QUESTIONS_TABLE_NAME, colName, "descending", new XServerSDK.XSQueryHandler() {
           @SuppressLint("SetTextI18n")
           @Override public void done(JSON objects, String error) {
              if (error == null) {
                 for (int i = 0; i < objects.count(); i++) {
                    JSON obj = objects.index(i);

                     List<String> reportedBy = XSGetArrayFromJSONArray(obj.key(QUESTIONS_REPORTED_BY).getJsonArray());

                     if (i < finalLimit){

                         // [Query filters] --------------------------------------------------------
                         if (searchTxt.matches("")){

                             // Latest
                             if (selectedCategory.matches(categoriesArray[0])){
                                 if (currentUser != null){
                                     if (!reportedBy.contains(currentUser.key("ID_id").stringValue())
                                     ){ questionsArray.add(obj); }
                                 } else { questionsArray.add(obj); }

                             // Trending
                             } else if (selectedCategory.matches(categoriesArray[1])){
                                 if (obj.key(QUESTIONS_ANSWERS).intValue() >= 2){
                                     if (currentUser != null){
                                         if (!reportedBy.contains(currentUser.key("ID_id").stringValue())
                                         ){ questionsArray.add(obj); }
                                     } else { questionsArray.add(obj); }
                                 }

                             // Need Answers
                             } else if (selectedCategory.matches(categoriesArray[2])){
                                 if (obj.key(QUESTIONS_ANSWERS).intValue() == 0){
                                     if (currentUser != null){
                                         if (!reportedBy.contains(currentUser.key("ID_id").stringValue())
                                         ){ questionsArray.add(obj); }
                                     } else { questionsArray.add(obj); }
                                 }

                                 // Other Categories
                             } else {
                                 if (obj.key(QUESTIONS_CATEGORY).stringValue().matches(selectedCategory)){
                                     if (currentUser != null){
                                         if (!reportedBy.contains(currentUser.key("ID_id").stringValue())
                                         ){ questionsArray.add(obj); }
                                     } else { questionsArray.add(obj); }
                                 }
                             }



                         // [Search for keywords] ----------------------------------------------
                         } else {
                             String[] keywords = searchTxt.toLowerCase().split(" ");

                             if (currentUser != null){
                                 if (!reportedBy.contains(currentUser.key("ID_id").stringValue())){
                                     for (String keyword : keywords) {
                                         if (obj.key(QUESTIONS_QUESTION).stringValue().toLowerCase().contains(keyword.toLowerCase())
                                         ){ questionsArray.add(obj); }
                                     }// ./ For • Search
                                 }
                             } else {
                                 for (String keyword : keywords) {
                                     if (obj.key(QUESTIONS_QUESTION).stringValue().toLowerCase().contains(keyword.toLowerCase())
                                     ){ questionsArray.add(obj); }
                                 } // ./ For • Search
                             } //./ If

                         } //./ If • [Search for keywords]

                     } //./ If limit


                     // [Finalize array of objects]
                    if (i == objects.count()-1) { questionsArray = XSRemoveDuplicatesFromArray(questionsArray); }
                 } // ./ For


                 // There area some objects
                 if (questionsArray.size() != 0) {
                    hideHUD();
                    questionsListView.setVisibility(View.VISIBLE);
                    showDataInListView();

                    // NO objects
                 } else {
                    hideHUD();
                    questionsListView.setVisibility(View.INVISIBLE);
                 }

                  // Show found questions
                  if (!searchTxt.matches("")){
                      categoryTxt.setText("FOUND " + roundLargeNumber(questionsArray.size()) + " ANSWERS");
                      categoryTxt.setTextColor(Color.BLACK);
                  }

              // error
              } else { hideHUD(); simpleAlert(error, ctx); }
        }});// /. XSQuery


    }
        


    //-----------------------------------------------
    // MARK -  SHOW DATA IN LISTVIEW
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
           public View getView(int position, View cell, final ViewGroup parent) {
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


               // Obj
               final JSON qObj = questionsArray.get(position);

               // Get Pointer
               XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                  @SuppressLint("SetTextI18n")
                  @Override public void done(final JSON userPointer, String e) {
                     if (userPointer != null) {

                           // isAnonymous
                           boolean isAnonymuos = qObj.key(QUESTIONS_IS_ANONYMOUS).booleanValue();

                           // Color
                           aCell.setBackgroundColor(Color.parseColor(qObj.key(QUESTIONS_COLOR).stringValue()));

                           // Category Image • Name
                           int imgID = getResources().getIdentifier(qObj.key(QUESTIONS_CATEGORY).stringValue(), "drawable", getPackageName());
                           categoryImg.setImageResource(imgID);
                           categoryTxt.setText(Objects.requireNonNull(qObj.key(QUESTIONS_CATEGORY).stringValue()).toUpperCase());


                           // Fullname • Date
                           Date aDate = XSGetDateFromString(qObj.key(QUESTIONS_CREATED_AT).stringValue());
                           SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                           String dateStr = df.format(aDate);

                           if (isAnonymuos) { dateTxt.setText("By Anonymous • " + dateStr);
                           } else { dateTxt.setText("By " + userPointer.key(USERS_FULLNAME).stringValue() + " • " + dateStr); }

                           // Avatar
                           if (isAnonymuos) { avatarImg.setImageResource(R.drawable.anonymous_avatar);
                           } else { Glide.with(ctx).load(userPointer.key(USERS_AVATAR).stringValue()).into(avatarImg); }

                           // Question
                           questionTxt.setText(qObj.key(QUESTIONS_QUESTION).stringValue());

                           // Answers & Views
                           int answers = qObj.key(QUESTIONS_ANSWERS).intValue();
                           int views = qObj.key(QUESTIONS_VIEWS).intValue();
                           String answersStr;
                           if (answers == 0) { answersStr = "No answer yet • ";
                           } else { answersStr = roundLargeNumber(answers) + " answers • ";  }
                           answeredByTxt.setText(answersStr + roundLargeNumber(views) + " views");

                     // error
                     } else { simpleAlert(e, ctx);
               }}}); // ./ XSGetPointer


           return cell;
           }
           @Override public int getCount() { return questionsArray.size(); }
           @Override public Object getItem(int position) { return questionsArray.get(position); }
           @Override public long getItemId(int position) { return position; }
        }

        // Set Adapter
        questionsListView.setAdapter(new ListAdapter(ctx));


        //-----------------------------------------------
        // MARK - SELECT QUESTION
        //-----------------------------------------------
        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
              // Obj
              JSON qObj = questionsArray.get(position);

              Intent i = new Intent(ctx, QuestionScreen.class);
              Bundle extras = new Bundle();
              extras.putString("object", String.valueOf(qObj));
              i.putExtras(extras);
              startActivity(i);
        }});
    }






    //-----------------------------------------------
    // MARK - DISMISS KEYBOARD
    //-----------------------------------------------
    void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }



    //-----------------------------------------------
    // MARK - CHECK FOR PERMISSIONS
    //-----------------------------------------------
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "ALL PERMISSIONS GRANTED!");
            }
        }
    }



} // ./ end
