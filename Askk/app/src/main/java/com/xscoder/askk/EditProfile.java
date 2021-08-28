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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mb3364.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_EDUCATION;
import static com.xscoder.askk.XServerSDK.USERS_EMAIL;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;
import static com.xscoder.askk.XServerSDK.USERS_LOCATION;
import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.USERS_USERNAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSObject;
import static com.xscoder.askk.XServerSDK.XSUploadFile;
import static com.xscoder.askk.XServerSDK.getFilePathFromURI;
import static com.xscoder.askk.XServerSDK.getImageUri;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.scaleBitmapToMaxSize;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;
import static com.xscoder.askk.XServerSDK.TAG;

public class EditProfile extends AppCompatActivity {

   // VIEWS //
   TextView titleTxt;
   Button backbutton, updateProfileButton;
   CircleImageView avatarImg;
   EditText fullnameTxt, usernameTxt, emailTxt, locationTxt, educationTxt;



   // VARIABLES //
   Context ctx = this;
   JSON currentUser;

   

   //-----------------------------------------------
   // MARK - ON CREATE
   //-----------------------------------------------
   @SuppressLint("SourceLockedOrientationActivity")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.edit_profile);
      super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      // Hide ActionBar
      Objects.requireNonNull(getSupportActionBar()).hide();


      //-----------------------------------------------
      // MARK - INITIALIZE VIEWS
      //-----------------------------------------------
      titleTxt = findViewById(R.id.epTitleTxt);
      titleTxt.setTypeface(popBold);
      backbutton = findViewById(R.id.epBackButton);
      updateProfileButton = findViewById(R.id.epUpdateProfileButton);
      updateProfileButton.setTypeface(popBold);
      avatarImg = findViewById(R.id.epAvatarImg);
      fullnameTxt = findViewById(R.id.epFullnameTxt);
      fullnameTxt.setTypeface(popRegular);
      usernameTxt = findViewById(R.id.epUsernameTxt);
      usernameTxt.setTypeface(popRegular);
      emailTxt = findViewById(R.id.epEmailTxt);
      emailTxt.setTypeface(popRegular);
      locationTxt = findViewById(R.id.epLocationTxt);
      locationTxt.setTypeface(popRegular);
      educationTxt = findViewById(R.id.epEducationTxt);
      educationTxt.setTypeface(popRegular);


      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
          // Current User IS LOGGED IN!
          if (currUser != null) {
             currentUser = currUser;

             // Call function
             showUserDetails();
      }}}); // ./ XSCurrentUser





      //-----------------------------------------------
      // MARK - CHANGE AVATAR BUTTON
      //-----------------------------------------------
      avatarImg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder alert  = new AlertDialog.Builder(ctx);
            alert.setTitle("Select source")
                  .setIcon(R.drawable.logo)
                  .setItems(new CharSequence[] {
                        "Take a photo",
                        "Pick from Gallery"
                  }, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                           // Open Camera
                           case 0:
                              openCamera();
                              break;

                           // Open Gallery
                           case 1:
                              openGallery();
                              break;
                  }}})
                  .setNegativeButton("Cancel", null)
                  .create().show();
      }});



      //-----------------------------------------------
      // MARK - UPDATE PROFILE BUTTON
      //-----------------------------------------------
      updateProfileButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

           if (!fullnameTxt.getText().toString().matches("") || !usernameTxt.getText().toString().matches("") || !emailTxt.getText().toString().matches("")){
              dismissKeyboard();
              showHUD(ctx);

              // Get Image path
              avatarImg.invalidate();
              Bitmap bmp = ((BitmapDrawable) avatarImg.getDrawable()).getBitmap();
              String filePath = getFilePathFromURI(getImageUri(bmp, ctx), ctx);

              // Upload image
              XSUploadFile(filePath, "avatar.jpg", (Activity)ctx, new XServerSDK.XSFileHandler() {
                  @Override public void done(String fileURL, String e) {
                      if (fileURL != null) {
                         Log.i(TAG, "FILE URL: " + fileURL);

                         // Update data
                         RequestParams params = new RequestParams();
                         params.put("tableName", USERS_TABLE_NAME);
                         params.put("ID_id", currentUser.key("ID_id").stringValue());
                         params.put(USERS_FULLNAME, fullnameTxt.getText().toString());
                         params.put(USERS_USERNAME, usernameTxt.getText().toString());
                         params.put(USERS_EMAIL, emailTxt.getText().toString());
                         params.put(USERS_LOCATION, locationTxt.getText().toString());
                         params.put(USERS_EDUCATION, educationTxt.getText().toString());
                         params.put(USERS_AVATAR, fileURL);

                         XSObject((Activity) ctx, params, new XServerSDK.XSObjectHandler() {
                            @Override
                            public void done(String e, JSON obj) {
                               if (e == null) {
                                  hideHUD();
                                  simpleAlert("Your Profile has been updated.", ctx);
                               // error
                               } else { hideHUD(); simpleAlert(e, ctx);
                         }}});// ./ XSObject


                      // error
                      } else { hideHUD(); simpleAlert(e, ctx); }
                  }});// ./ XSUploadFile


           // Fields are empty...
           } else { simpleAlert("Full Name, Username and Email address are mandatory.", ctx); }
      }});




      //-----------------------------------------------
      // MARK - BACK BUTTON
      //-----------------------------------------------
      backbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) { finish(); }});


   }// ./ onCreate()





   // ------------------------------------------------
   // MARK: - SHOW USER DETAILS
   // ------------------------------------------------
   void showUserDetails() {

      Glide.with(ctx).load(currentUser.key(USERS_AVATAR).stringValue()).into(avatarImg);
      fullnameTxt.setText(currentUser.key(USERS_FULLNAME).stringValue());
      usernameTxt.setText(currentUser.key(USERS_USERNAME).stringValue());
      emailTxt.setText(currentUser.key(USERS_EMAIL).stringValue());
      locationTxt.setText(currentUser.key(USERS_LOCATION).stringValue());
      educationTxt.setText(currentUser.key(USERS_EDUCATION).stringValue());
   }






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
          Bitmap bmp;

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

                   bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                   assert bmp != null;
                   bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                   bmp = scaleBitmapToMaxSize(300, bmp);

                   avatarImg.setImageBitmap(bmp);

               } catch (IOException | OutOfMemoryError e) { Log.i("log-", Objects.requireNonNull(e.getMessage())); }



           // • IMAGE FROM GALLERY
           } else if (requestCode == GALLERY) {
               try {
                   bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                   bmp = scaleBitmapToMaxSize(300, bmp);

                   avatarImg.setImageBitmap(bmp);

               } catch (IOException e) { e.printStackTrace(); }
           }

       }// ./ If
   }





   //-----------------------------------------------
   // MARK - DISMISS KEYBOARD
   //-----------------------------------------------
   void dismissKeyboard() {
      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      assert imm != null;
      imm.hideSoftInputFromWindow(fullnameTxt.getWindowToken(), 0);
      imm.hideSoftInputFromWindow(usernameTxt.getWindowToken(), 0);
      imm.hideSoftInputFromWindow(emailTxt.getWindowToken(), 0);
      imm.hideSoftInputFromWindow(locationTxt.getWindowToken(), 0);
      imm.hideSoftInputFromWindow(educationTxt.getWindowToken(), 0);
   }


}// ./ end
