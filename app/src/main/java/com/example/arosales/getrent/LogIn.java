package com.example.arosales.getrent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LogIn extends AppCompatActivity {

    public static final String APPLICATION_ID = "1Jos8ZbAEwRqhqcsOLwXsRxGkClzr5FkNIHAuALC";
    public static final String CLIENT_KEY = "eXCaQag6GThrxtX4AkST4fsfROYfAqTwKFNpeGq4";
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(getApplicationContext(), APPLICATION_ID, CLIENT_KEY);

        /*ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();*/

        init();
    }

    public void onRegisterClick(View v)
    {
        startActivity(new Intent(this,Registration.class));
    }
    public void onLoginClick(View v)
    {
        // Check for input data validation error, display the error
        if(validateRegisterInput()) {
            // Set up a progress dialog
            final ProgressDialog dlg = new ProgressDialog(LogIn.this);
            dlg.setTitle("Please wait.");
            dlg.setMessage("Logging in.  Please wait.");
            dlg.show();
            // Call the Parse login method
            ParseUser.logInInBackground(username.getText().toString(), password.getText()
                    .toString(), new LogInCallback() {

                @Override
                public void done(ParseUser user, ParseException e) {
                    dlg.dismiss();
                    if (e != null) {
                        // Show the error message
                        Toast.makeText(LogIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        // Start an intent for the dispatch activity
                        Intent intent = new Intent(LogIn.this, ManageSession.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void init()
    {
        username = (EditText) findViewById(R.id.loginUsernameTxt);
        password = (EditText) findViewById(R.id.loginPasswordTxt);
    }

    //use type value = true for student and false for company data
    private boolean validateRegisterInput()
    {
        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage =
                new StringBuilder(getResources().getString(R.string.error_intro)+"\n");
        if (isEmpty(username)) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_username)+"\n");
        }
        if (isEmpty(password)) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_password)+"\n");
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(getBaseContext(), validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            return false;//invalid input
        }
        return true;//valid input
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
