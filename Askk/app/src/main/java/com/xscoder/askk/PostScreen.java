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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.mb3364.http.RequestParams;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.ANSWERS_ANSWER;
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
import static com.xscoder.askk.XServerSDK.MULTIPLE_PERMISSIONS;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_CURRENT_USER;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_OTHER_USER;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_TEXT;
import static com.xscoder.askk.XServerSDK.QUESTIONS_ANSWERS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_CATEGORY;
import static com.xscoder.askk.XServerSDK.QUESTIONS_COLOR;
import static com.xscoder.askk.XServerSDK.QUESTIONS_HAS_BEST_ANSWER;
import static com.xscoder.askk.XServerSDK.QUESTIONS_IMAGE;
import static com.xscoder.askk.XServerSDK.QUESTIONS_IS_ANONYMOUS;
import static com.xscoder.askk.XServerSDK.QUESTIONS_QUESTION;
import static com.xscoder.askk.XServerSDK.QUESTIONS_REPORTED_BY;
import static com.xscoder.askk.XServerSDK.QUESTIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.QUESTIONS_USER_POINTER;
import static com.xscoder.askk.XServerSDK.QUESTIONS_VIEWS;
import static com.xscoder.askk.XServerSDK.TAG;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.USERS_USERNAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSGetPointer;
import static com.xscoder.askk.XServerSDK.XSGetStringFromArray;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSSendAndroidPush;
import static com.xscoder.askk.XServerSDK.XSSendiOSPush;
import static com.xscoder.askk.XServerSDK.XSUploadFile;
import static com.xscoder.askk.XServerSDK.categoriesArray;
import static com.xscoder.askk.XServerSDK.colorsArray;
import static com.xscoder.askk.XServerSDK.getFilePathFromURI;
import static com.xscoder.askk.XServerSDK.getImageUri;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.mustReload;
import static com.xscoder.askk.XServerSDK.permissions;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.scaleBitmapToMaxSize;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class PostScreen extends AppCompatActivity {

   // VIEWS //
   TextView titleTxt, postTitleTxt;
   EditText postTxt;
   ImageView attachmentImg;
   Button addImageButton, removePictureButton, postButton, dismissButton;



   // VARIABLES //
   Context ctx = this;
   JSON currentUser;
   JSON qObj;
   JSON aObj;
   boolean isAnonymous = false;
   boolean isQuestion = false;
   boolean isEditingMode = false;




   //-----------------------------------------------
   // MARK - ON CREATE
   //-----------------------------------------------
   @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.post_screen);
      super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      // Hide ActionBar
      Objects.requireNonNull(getSupportActionBar()).hide();



      //-----------------------------------------------
      // MARK - INITIALIZE VIEWS
      //-----------------------------------------------
      titleTxt = findViewById(R.id.psTitleTxt);
      titleTxt.setTypeface(popBold);
      postTitleTxt = findViewById(R.id.psPostTitleTxt);
      postTitleTxt.setTypeface(popBold);
      postTxt = findViewById(R.id.psPostTxt);
      postTxt.setTypeface(popRegular);
      postButton = findViewById(R.id.psPostButton);
      postButton.setTypeface(popBold);
      attachmentImg = findViewById(R.id.psAttachmentImg);
      addImageButton = findViewById(R.id.psAddImageButton);
      dismissButton = findViewById(R.id.psDismissButton);
      removePictureButton = findViewById(R.id.psRemovePictureButton);
      removePictureButton.setVisibility(View.INVISIBLE);


      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
          // Current User IS LOGGED IN!
          if (currUser != null) {
             currentUser = currUser;
      }}}); // ./ XSCurrentUser


      // Get Extras
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
         qObj = new JSON(extras.getString("qObject"));
         aObj = new JSON(extras.getString("aObject"));
         isAnonymous = extras.getBoolean("isAnonymous");
         isQuestion = extras.getBoolean("isQuestion");
         isEditingMode = extras.getBoolean("isEditingMode");

         Log.i(TAG, "IS EDITING MODE: " + isEditingMode);
         Log.i(TAG, "IS ANONYMOUS: " + isAnonymous);
         Log.i(TAG, "IS QUESTION: " + isQuestion);
         Log.i(TAG, "QUESTION OPBJ: " + qObj);
         Log.i(TAG, "ANSWER OBJ: " + aObj);

         // IT'S A QUESTION
         if (isQuestion) {
            if (isAnonymous) { titleTxt.setText("Ask something\n(anonymous)");
            } else { titleTxt.setText("Ask something"); }


         // IT'S AN ANSWER
         } else {
            if (isAnonymous) { titleTxt.setText("Give an answer\n(anonymous)");
            } else { titleTxt.setText("Give an answer"); }
            postTitleTxt.setText(qObj.key(QUESTIONS_QUESTION).stringValue());
         }


         // IS EDITING MODE
         if (isEditingMode) {
            postTxt.setBackgroundColor(Color.WHITE);

            // isQuestion
            if (isQuestion) {
               postTxt.setText(qObj.key(QUESTIONS_QUESTION).stringValue());

               String imageFile = qObj.key(QUESTIONS_IMAGE).stringValue();
               if (!imageFile.matches("")) {
                  removePictureButton.setVisibility(View.VISIBLE);
                  Glide.with(ctx).load(qObj.key(QUESTIONS_IMAGE).stringValue()).into(attachmentImg);
               }

            // isAnswer
            } else {
               postTxt.setText(aObj.key(ANSWERS_ANSWER).stringValue());
               String imageFile = aObj.key(QUESTIONS_IMAGE).stringValue();
               if (!imageFile.matches("")) {
                  removePictureButton.setVisibility(View.VISIBLE);
                  Glide.with(ctx).load(aObj.key(ANSWERS_IMAGE).stringValue()).into(attachmentImg);
               }
            }
         }

      } // ./ If [extras != null]





      // ------------------------------------------------
      // MARK: - POST QUESTION/ANSWER BUTTON
      // ------------------------------------------------
      postButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           final String isAnonymousStr;
           if (isAnonymous){ isAnonymousStr = "1"; } else { isAnonymousStr = "0"; }


           if (!postTxt.getText().toString().matches("") ){

              // [POST A QUESTION] ---------------------------------------------------------
              if (isQuestion) {

                 final List<String> tempCatArr = new ArrayList<>();
                 Collections.addAll(tempCatArr, categoriesArray);
                 final List<String> tempColorsArr = new ArrayList<>();
                 Collections.addAll(tempColorsArr, colorsArray);

                 for (int i = 0; i < 3; i++) {
                    tempCatArr.remove(0);
                    tempColorsArr.remove(0);
                 }

                 // Make tempCatArr's items UPPERCASE
                 for (int i = 0, l = tempCatArr.size(); i < l; ++i) {
                    tempCatArr.set(i, tempCatArr.get(i).toUpperCase());
                 }

                 // ActionSheet
                 new ActionSheet(ctx, tempCatArr)
                         .setTitle("Select a Category")
                         .setCancelTitle("Cancel")
                         .setColorTitle(Color.parseColor(GRAY))
                         .setColorTitleCancel(Color.parseColor(GRAY))
                         .setColorData(Color.parseColor(MAIN_COLOR))
                         .create(new ActionSheetCallBack() {
                            @Override
                            public void data(@NotNull String catName, int position) {

                               showHUD(ctx);
                               dismissKeyboard();

                               // Prepare data
                               final RequestParams params = new RequestParams();
                               params.put("tableName", QUESTIONS_TABLE_NAME);

                               // New Question Obj
                               if (!isEditingMode) {
                                  params.put(QUESTIONS_ANSWERS, "0");
                                  params.put(QUESTIONS_VIEWS, "0");
                                  params.put(QUESTIONS_REPORTED_BY, XSGetStringFromArray(new ArrayList<String>()));
                                  params.put(QUESTIONS_HAS_BEST_ANSWER, "0");

                               // Edit Question Obj
                               } else { params.put("ID_id", qObj.key("ID_id").stringValue()); }

                               params.put(QUESTIONS_QUESTION, postTxt.getText().toString());
                               params.put(QUESTIONS_COLOR, tempColorsArr.get(position));
                               params.put(QUESTIONS_CATEGORY, catName.toLowerCase());
                               params.put(QUESTIONS_IS_ANONYMOUS, isAnonymousStr);
                               params.put(QUESTIONS_USER_POINTER, currentUser.key("ID_id").stringValue());

                               // Attach Image
                               if (attachmentImg.getDrawable() != null){

                                  // Get Image path
                                  attachmentImg.invalidate();
                                  Bitmap bmp = ((BitmapDrawable) attachmentImg.getDrawable()).getBitmap();
                                  String filePath = getFilePathFromURI(getImageUri(bmp, ctx), ctx);

                                  XSUploadFile(filePath, "image.jpg", (Activity)ctx, new XServerSDK.XSFileHandler() {
                                      @Override public void done(String fileURL, String e) {
                                          if (fileURL != null) {
                                              Log.i(TAG, "FILE URL: " + fileURL);
                                              params.put(QUESTIONS_IMAGE, fileURL);

                                              // Save
                                              XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                                 if (e == null) {
                                                    hideHUD();
                                                    mustReload = true;
                                                    finish();
                                                 // error
                                                 } else { hideHUD(); simpleAlert(e, ctx);
                                              }}});// ./ XSObject

                                          // error
                                          } else { hideHUD(); simpleAlert(e, ctx); }
                                  }});// ./ XSUploadFile


                               // NO Attachment Image
                               } else {
                                  params.put(QUESTIONS_IMAGE, "");

                                  // Save
                                  XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                     if (e == null) {
                                        hideHUD();
                                        mustReload = true;
                                        finish();
                                        // error
                                     } else { hideHUD(); simpleAlert(e, ctx);
                                  }}});// ./ XSObject

                               } //./ If

                 }}); //./ ActionSheet




              // [POST AN ANSWER] -----------------------------------------------------------------
              } else {
                 showHUD(ctx);
                 dismissKeyboard();

                 // Prepare data
                 final RequestParams params = new RequestParams();
                 params.put("tableName", ANSWERS_TABLE_NAME);

                 // New Answer Obj
                 if (!isEditingMode){
                    params.put(ANSWERS_IS_BEST, "0");
                    params.put(ANSWERS_LIKED_BY, XSGetStringFromArray(new ArrayList<String>()));
                    params.put(ANSWERS_DISLIKED_BY, XSGetStringFromArray(new ArrayList<String>()));
                    params.put(ANSWERS_REPORTED_BY, XSGetStringFromArray(new ArrayList<String>()));

                 // Edit Answer Obj
                 } else { params.put("ID_id", aObj.key("ID_id").stringValue()); }

                 params.put(ANSWERS_ANSWER, postTxt.getText().toString());
                 params.put(ANSWERS_QUESTION_POINTER, qObj.key("ID_id").stringValue());
                 params.put(ANSWERS_USER_POINTER, currentUser.key("ID_id").stringValue());
                 params.put(ANSWERS_IS_ANONYMOUS, isAnonymousStr);

                 if (attachmentImg.getDrawable() != null){

                    // Get Image path
                    attachmentImg.invalidate();
                    Bitmap bmp = ((BitmapDrawable) attachmentImg.getDrawable()).getBitmap();
                    String filePath = getFilePathFromURI(getImageUri(bmp, ctx), ctx);

                    XSUploadFile(filePath, "image.jpg", (Activity)ctx, new XServerSDK.XSFileHandler() {
                       @Override public void done(String fileURL, String e) {
                          if (fileURL != null) {
                             Log.i(TAG, "FILE URL: " + fileURL);
                             params.put(ANSWERS_IMAGE, fileURL);

                             // Save
                             XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                if (e != null) { hideHUD(); simpleAlert(e, ctx);
                             }}});// ./ XSObject

                          // error
                          } else { hideHUD(); simpleAlert(e, ctx); }
                    }});// ./ XSUploadFile


                 // NO Attachment Image
                 } else {
                    params.put(ANSWERS_IMAGE, "");

                    // Save
                    XSObject((Activity)ctx, params, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                       if (e != null) { hideHUD(); simpleAlert(e, ctx);
                    }}});// ./ XSObject

                 } //./ If


                 // Send Push Notification
                 XSGetPointer((Activity)ctx, qObj.key(QUESTIONS_USER_POINTER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
                    @Override public void done(final JSON userPointer, String e) {
                       if (userPointer != null) {

                          // Send Push Notification
                          String userFullname;
                          if (!isAnonymous){ userFullname = currentUser.key(USERS_FULLNAME).stringValue();
                          } else { userFullname = "(Anonymous)"; }

                          final String pushMessage = userFullname + " answered to your question: '" + qObj.key(QUESTIONS_QUESTION).stringValue() + "'";

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
                             if (e == null) {
                                hideHUD();

                                // Increment Question's answers
                                if (!isEditingMode){
                                   RequestParams params3 = new RequestParams();
                                   params3.put("tableName", QUESTIONS_TABLE_NAME);
                                   params3.put("ID_id", qObj.key("ID_id").stringValue());
                                   params3.put(QUESTIONS_ANSWERS, String.valueOf(qObj.key(QUESTIONS_ANSWERS).intValue() + 1));

                                   XSObject((Activity)ctx, params3, new XServerSDK.XSObjectHandler() { @Override public void done(String e, JSON obj) {
                                      if (e == null) {
                                         hideHUD();
                                         mustReload = true;
                                         finish();
                                         // error
                                      } else { hideHUD(); simpleAlert(e, ctx);
                                   }}}); //./ XSObject

                                } else {
                                   hideHUD();
                                   mustReload = true;
                                   finish();
                                } //./ If

                             // error
                             } else { hideHUD(); simpleAlert(e, ctx);
                          }}});// ./ XSObject

                       // error
                       } else { simpleAlert(e, ctx);
                 }}}); // ./ XSGetPointer


              } // ./ If [isQuestion]


           // postTxt is empty!
           } else { simpleAlert("Please type something!", ctx); }// ./ If
      }});






      //-----------------------------------------------
      // MARK - ADD AN IMAGE BUTTON
      //-----------------------------------------------
      addImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

           if (!checkPermissions()) { checkPermissions(); }

           AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
           alert.setTitle("Select source")
                   .setIcon(R.drawable.logo)
                   .setItems(new CharSequence[] {
                                   "Take a photo",
                                   "Pick from Gallery"
                   }, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           switch (which) {

                               // OPEN CAMERA
                               case 0:
                                   openCamera();
                                   break;

                               // OPEN GALLERY
                               case 1:
                                   openGallery();
                                   break;

                           }}})
                   .setNegativeButton("Cancel", null);
           alert.create().show();

      }});



      //-----------------------------------------------
      // MARK - REMOVE PICTURE BUTTON
      //-----------------------------------------------
      removePictureButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if (attachmentImg.getDrawable() != null){
              attachmentImg.setImageResource(0); // .setImageBitmap(null);
              removePictureButton.setVisibility(View.INVISIBLE);
           }
      }});




      //-----------------------------------------------
      // MARK - DISMISS BUTTON
      //-----------------------------------------------
      dismissButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) { finish(); }});


   }// ./ onCreate







   //-----------------------------------------------
   // MARK - IMAGE PICKER DELEGATE FUNCTIONS
   //-----------------------------------------------
   int CAMERA = 0;
   int GALLERY = 1;
   Uri imageURI;
   File file;


   // OPEN CAMERA
   public void openCamera() {
       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       file = new File(getExternalCacheDir().getAbsolutePath() + "image.jpg");
       imageURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file);
       intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
       startActivityForResult(intent, CAMERA);
   }

   // OPEN GALLERY
   public void openGallery() {
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY);
   }


   // IMAGE/VIDEO PICKED DELEGATE ------------------------------
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       if (resultCode == Activity.RESULT_OK) {

           // • IMAGE FROM CAMERA
           if (requestCode == CAMERA) {
               try {
                   File f = file;
                   ExifInterface exif = new ExifInterface(f.getPath());
                   int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                   int angle = 0;
                   if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                       angle = 90;
                   } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                       angle = 180;
                   } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                       angle = 270;
                   }
                   // Log.i("log-", "ORIENTATION: " + orientation);

                   Matrix mat = new Matrix();
                   mat.postRotate(angle);

                   Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                   assert bmp != null;
                   bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                   bmp = scaleBitmapToMaxSize(800, bmp);
                   attachmentImg.setImageBitmap(bmp);
                   removePictureButton.setVisibility(View.VISIBLE);

               } catch (IOException | OutOfMemoryError e) { Log.i("log-", Objects.requireNonNull(e.getMessage())); }



           // • IMAGE FROM GALLERY
           } else if (requestCode == GALLERY) {
              try {
                 Bitmap bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                 bmp = scaleBitmapToMaxSize(800, bmp);
                 attachmentImg.setImageBitmap(bmp);
                 removePictureButton.setVisibility(View.VISIBLE);

              } catch (IOException e) { e.printStackTrace(); }
           }

       }
   }





   //-----------------------------------------------
   // MARK - DISMISS KEYBOARD
   //-----------------------------------------------
   void dismissKeyboard() {
       InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
       assert imm != null;
       imm.hideSoftInputFromWindow(postTxt.getWindowToken(), 0);
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
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == MULTIPLE_PERMISSIONS) {
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "ALL PERMISSIONS GRANTED!");
         }
      }
   }



}// ./ end
