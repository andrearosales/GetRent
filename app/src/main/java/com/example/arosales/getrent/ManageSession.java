package com.example.arosales.getrent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by aRosales on 27/05/2015.
 */
public class ManageSession extends Activity {
    public static final String APPLICATION_ID = "1Jos8ZbAEwRqhqcsOLwXsRxGkClzr5FkNIHAuALC";
    public static final String CLIENT_KEY = "eXCaQag6GThrxtX4AkST4fsfROYfAqTwKFNpeGq4";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        Parse.initialize(getApplicationContext(), APPLICATION_ID, CLIENT_KEY);
        manageUserSession();
    }

    private void manageUserSession() {
        // Check if there is current user info
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Start an intent for the logged in activity
            String userType = currentUser.getString("TypeUser");//true for student //false for company
            if(userType.equals("Student")) {
                startActivity(new Intent(this, ListBookmark.class));
            }else if(userType.equals("Owner")) {
                startActivity(new Intent(this, ListRent.class));
            }
            else
                Toast.makeText(getBaseContext(), "Unknown user type!", Toast.LENGTH_LONG).show();
        } else {
            // Start and intent for the logged out activity(home activity of the app)
            startActivity(new Intent(this, LogIn.class));
        }
    }
}