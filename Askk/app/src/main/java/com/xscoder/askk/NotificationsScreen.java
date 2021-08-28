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
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.amirs.JSON;

import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_CREATED_AT;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_CURRENT_USER;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_OTHER_USER;
import static com.xscoder.askk.XServerSDK.NOTIFICATIONS_TEXT;
import static com.xscoder.askk.XServerSDK.USERS_AVATAR;
import static com.xscoder.askk.XServerSDK.USERS_FULLNAME;

import static com.xscoder.askk.XServerSDK.USERS_TABLE_NAME;
import static com.xscoder.askk.XServerSDK.XSCurrentUser;
import static com.xscoder.askk.XServerSDK.XSGetPointer;
import static com.xscoder.askk.XServerSDK.XSQuery;
import static com.xscoder.askk.XServerSDK.XSRemoveDuplicatesFromArray;
import static com.xscoder.askk.XServerSDK.hideHUD;
import static com.xscoder.askk.XServerSDK.popBold;
import static com.xscoder.askk.XServerSDK.popRegular;
import static com.xscoder.askk.XServerSDK.showHUD;
import static com.xscoder.askk.XServerSDK.simpleAlert;

public class NotificationsScreen extends AppCompatActivity {

   // VIEWS //
   TextView titleTxt;
   ListView notificationsListView;


   // VARIABLES //
   Context ctx = this;
   JSON currentUser;
   List<JSON> notificationsArray = new ArrayList<>();







   //-----------------------------------------------
   // MARK - ON CREATE
   //-----------------------------------------------
   @SuppressLint("SourceLockedOrientationActivity")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.notifications_screen);
      super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      // Hide ActionBar
      Objects.requireNonNull(getSupportActionBar()).hide();


      //-----------------------------------------------
      // MARK - INITIALIZE VIEWS
      //-----------------------------------------------
      titleTxt = findViewById(R.id.notTitleTxt);
      titleTxt.setTypeface(popBold);
      notificationsListView = findViewById(R.id.notNotificationsListView);


      //-----------------------------------------------
      // MARK - TAB BAR BUTTONS
      //-----------------------------------------------
      Button tab1 = findViewById(R.id.tab1);
      Button tab2 = findViewById(R.id.tab3);

      tab1.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(ctx, Home.class));
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


      // Get Current User
      XSCurrentUser((Activity)ctx, new XServerSDK.XSCurrentUserHandler() { @Override public void done(final JSON currUser) {
          // Current User IS LOGGED IN!
          if (currUser != null) {
             currentUser = currUser;
             queryNotifications();

          // Current User IS LOGGED OUT
          } else { startActivity(new Intent(ctx, Intro.class)); }
      }}); // ./ XSCurrentUser


   } // ./ onCreate





   //-----------------------------------------------
   // MARK - QUERY NOTIFICATIONS
   //-----------------------------------------------
   void queryNotifications() {
      showHUD(ctx);
      notificationsArray = new ArrayList<>();
      notificationsListView.invalidateViews();
      notificationsListView.refreshDrawableState();


      XSQuery((Activity)ctx, NOTIFICATIONS_TABLE_NAME, NOTIFICATIONS_CREATED_AT, "", new XServerSDK.XSQueryHandler() {
         @Override public void done(JSON objects, String error) {
            if (error == null) {
               for (int i = 0; i < objects.count(); i++) {
                  JSON obj = objects.index(i);

                  if (obj.key(NOTIFICATIONS_OTHER_USER).stringValue().matches(currentUser.key("ID_id").stringValue())
                  ){ notificationsArray.add(obj); }

                  // [Finalize array of objects]
                  if (i == objects.count()-1) { notificationsArray = XSRemoveDuplicatesFromArray(notificationsArray); }
               } // ./ For


               // There area some objects
               if (notificationsArray.size() != 0) {
                  hideHUD();
                  notificationsListView.setVisibility(View.VISIBLE);
                  showDataInListView();

               // NO objects
               } else {
                  hideHUD();
                  notificationsListView.setVisibility(View.INVISIBLE);
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
               cell = inflater.inflate(R.layout.cell_notification, null);
            }

            //-----------------------------------------------
            // MARK - INITIALIZE VIEWS
            //-----------------------------------------------
            final CircleImageView avatarImg = cell.findViewById(R.id.cnotAvatarImg);
            final TextView fullnameTxt = cell.findViewById(R.id.cnotFullnameTxt);
            fullnameTxt.setTypeface(popBold);
            final TextView notificationTxt = cell.findViewById(R.id.cnotNotificationtxt);
            notificationTxt.setTypeface(popRegular);


            // Obj
            final JSON nObj = notificationsArray.get(position);

            // userPointer
            XSGetPointer((Activity)ctx, nObj.key(NOTIFICATIONS_CURRENT_USER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
               @Override public void done(final JSON userPointer, String e) {
                  if (userPointer != null) {

                     // User is anonymous!
                     String notifText = nObj.key(NOTIFICATIONS_TEXT).stringValue();
                     assert notifText != null;
                     if (notifText.contains("(Anonymous)")) {
                        fullnameTxt.setText("(Anonymous)");
                        avatarImg.setImageResource(R.drawable.anonymous_avatar);

                        // User is not anonymous
                     } else {
                        fullnameTxt.setText(userPointer.key(USERS_FULLNAME).stringValue());
                        Glide.with(ctx).load(userPointer.key(USERS_AVATAR).stringValue()).into(avatarImg);
                     }
                     notificationTxt.setText(notifText);

                  // error
                  } else { simpleAlert(e, ctx);
            }}}); // ./ XSGetPointer


         return cell;
         }
         @Override public int getCount() { return notificationsArray.size(); }
         @Override public Object getItem(int position) { return notificationsArray.get(position); }
         @Override public long getItemId(int position) { return position; }
      }

      // Set Adapter
      notificationsListView.setAdapter(new ListAdapter(ctx));



      //-----------------------------------------------
      // MARK - TAP CELL -> SHOW USER'S PROFILE
      //-----------------------------------------------
      notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // Obj
            final JSON nObj = notificationsArray.get(position);

            // userPointer
            XSGetPointer((Activity)ctx, nObj.key(NOTIFICATIONS_CURRENT_USER).stringValue(), USERS_TABLE_NAME, new XServerSDK.XSPointerHandler() {
               @Override public void done(final JSON userPointer, String e) {
                  if (userPointer != null) {

                     // User is Anonymous!
                     String notifText = nObj.key(NOTIFICATIONS_TEXT).stringValue();
                     assert notifText != null;
                     if (notifText.contains("(Anonymous)")) {
                        simpleAlert("You are not allowed to see this Profile", ctx);

                        // User is not Anonymous...
                     } else {
                        Intent i = new Intent(ctx, Account.class);
                        Bundle extras = new Bundle();

                        if (userPointer.key("ID_id").stringValue().matches(currentUser.key("ID_id").stringValue())){
                           extras.putBoolean("isCurrentUser", true);
                        } else {
                           extras.putBoolean("isCurrentUser", false);
                           extras.putString("userObj", String.valueOf(userPointer));
                        }
                        extras.putBoolean("showBackButton", true);

                        i.putExtras(extras);
                        startActivity(i);
                     } // ./ If

                  // error
                  } else { simpleAlert(e, ctx);
            }}}); // ./ XSGetPointer

      }});
   }




}// ./ end
