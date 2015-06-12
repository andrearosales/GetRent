package com.example.arosales.getrent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;



public class Registration extends AppCompatActivity {

    public final static String CURRENTAB = "com.example.arosales.getrent.CURRENTAB";

    private EditText studentName;
    private EditText studentSurname;
    private EditText studentPassword;

    private EditText ownerName;
    private EditText ownerSurname;
    private EditText ownerMail;
    private EditText ownerPhone;
    private EditText ownerPassword;

    private TabHost tabHost;
    private String defaultTab = null;
    private int defaultTabIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //final TabHost tabHost= (TabHost) findViewById(R.id.mainTabbHost);
        tabHost= (TabHost) findViewById(R.id.mainTabbHost);
        tabHost.setup();

        createTabs(tabHost);
        init();
    }

    private void init()
    {
        studentName = (EditText) findViewById(R.id.studentNameTxt);
        studentSurname = (EditText) findViewById(R.id.studentSrnameTxt);
        studentPassword = (EditText) findViewById(R.id.studentPasswordTxt);

        ownerName = (EditText) findViewById(R.id.ownerNameTxt);
        ownerSurname = (EditText) findViewById(R.id.ownerSrnameTxt);
        ownerMail = (EditText) findViewById(R.id.ownerMailTxt);
        ownerPhone = (EditText) findViewById(R.id.ownerTelephoneTxt);
        ownerPassword = (EditText) findViewById(R.id.ownerPasswordTxt);
    }

    public void onRegisterOwnerClick(View v)
    {
        String []data=  new String[4];
        // Check for input data validation error, display the error
        //false used as a flag to say company
        if (validateRegisterInput("Owner")) {
            data[0] = ownerName.getText().toString();
            data[1] = ownerSurname.getText().toString();
            if(ownerMail.getText().toString()!=null)
                data[2] = ownerMail.getText().toString();
            if(ownerPhone.getText().toString()!=null)
                data[3] = ownerPhone.getText().toString();
            String username=(ownerName.getText().toString().toLowerCase()+ ownerSurname.getText().toString().toLowerCase()).replaceAll("\\s+","");
            registerUser("Owner", username, ownerPassword.getText().toString(), data);
        }
    }

    public void onRegisterStudentClick(View v)
    {
        String []data=  new String[2];
        // Check for input data validation error, display the error
        //true used as a flag to say student
        if (validateRegisterInput("Student")) {
            data[0] = studentName.getText().toString();
            data[1] = studentSurname.getText().toString();
            String username=(studentName.getText().toString().toLowerCase()+studentSurname.getText().toString().toLowerCase()).replaceAll("\\s+","");
            registerUser("Student", username, studentPassword.getText().toString(), data);
        }
    }

    //use type value = true for student and false for company data
    private boolean validateRegisterInput(String type)
    {
        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage =
                new StringBuilder(getResources().getString(R.string.error_intro)+"\n");
        if(type.equals("Student")) {
            if (isEmpty(studentName)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_name)+"\n");
            }
            if (isEmpty(studentSurname)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_surname)+"\n");
            }
            if (isEmpty(studentPassword)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_password)+"\n");
            }
        }
        else if(type.equals("Owner")){
            if (isEmpty(ownerName)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_name)+"\n");
            }
            if (isEmpty(ownerSurname)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_surname)+"\n");
            }
            if (isEmpty(ownerMail) && isEmpty(ownerPhone)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_contact)+"\n");
            }
            if (isEmpty(ownerPassword)) {
                validationError = true;
                validationErrorMessage.append(getResources().getString(R.string.error_blank_password)+"\n");
            }
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(getBaseContext(), validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
            return false;//invalid input
        }
        return true;//valid input
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    private void createTabs(final TabHost tabHost)
    {
        String []tabNames = getResources().getStringArray(R.array.tabNames);
        TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
        spec1.setContent(R.id.scroller1);
        spec1.setIndicator(tabNames[0]);//"Mon", null);//res.getDrawable(R..drawable.tab_icon);
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
        spec2.setContent(R.id.scroller2);
        spec2.setIndicator(tabNames[1]);//"Mon", null);//res.getDrawable(R..drawable.tab_icon);
        tabHost.addTab(spec2);

        tabHost.setCurrentTabByTag("tab1");

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                Log.i("***Selected Tab", "Im currently in tab with index::" + tabHost.getCurrentTab());
            }
        });
        //populateListView(tabHost,"");
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    protected void registerUser(final String type, final String username,String password,String[] data) {
        ParseUser user = new ParseUser();
        final ParseObject registerStudent = new ParseObject("Student");
        final ParseObject registerOwner = new ParseObject("Owner");

        user.setUsername(username);
        user.setPassword(password);
        // String id =  getObjectID(username,password);
        if(type.equals("Student"))
        {
            registerStudent.put("Name", data[0]);//data[0] name of student
            registerStudent.put("Surname", data[1]);//data[1] surname of student
            user.put("TypeUser", "Student");
        }
        else if (type.equals("Owner"))
        {
            registerOwner.put("Name", data[0]);//data[0] name of owner
            registerOwner.put("Surname", data[1]);//data[1] surname of owner
            if(data[2]!=null)
                registerOwner.put("Email", data[2]);//data[2] email of owner
            if(data[3]!=null)
                registerOwner.put("Telephone", data[3]);//data[3] telephone of owner
            user.put("TypeUser","Owner");
        }



        // Set up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(Registration.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Signing up.  Please wait.");
        dlg.show();


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {//Sign up succeed
                    dlg.dismiss();
                    if (type.equals("Student")) {
                        //Toast.makeText(ParseApplication.this, ParseUser.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
                        registerStudent.put("StudentId", ParseUser.getCurrentUser());//act like a foreign key
                        registerStudent.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                                builder.setTitle(R.string.username);
                                builder.setMessage(getString(R.string.title_activity_username) + " " + username);
                                builder.setCancelable(true);
                                builder.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Intent intent = new Intent(Registration.this, ListBookmark.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();


                            }
                        });
                    } else if (type.equals("Owner")) {
                        // Toast.makeText(ParseApplication.this, ParseUser.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
                        registerOwner.put("OwnerId", ParseUser.getCurrentUser());//act like a foreign key
                        registerOwner.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                                builder.setTitle(R.string.username);
                                builder.setMessage(getString(R.string.title_activity_username) + " " + username);
                                builder.setCancelable(true);
                                builder.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Intent intent = new Intent(Registration.this, ListRent.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();


                            }
                        });
                    }
                } else {
                    // Sign up didn't succeed.
                    String errorMessage = "";
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            errorMessage = "Sorry, this username has already been taken.";
                            break;
                        case ParseException.USERNAME_MISSING:
                            errorMessage = "Sorry, you must supply a username to register.";
                            break;
                        case ParseException.PASSWORD_MISSING:
                            errorMessage = "Sorry, you must supply a password to register.";
                            break;
                        default:
                            errorMessage = e.getLocalizedMessage();
                    }
                    Toast.makeText(Registration.this, errorMessage, Toast.LENGTH_LONG).show();
                    dlg.dismiss();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        String currentTabTag = tabHost.getCurrentTabTag();
        if (currentTabTag != null) {
            savedInstanceState.putString(CURRENTAB, currentTabTag);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ensureTabHost();
        String cur = savedInstanceState.getString(CURRENTAB);
        if (cur != null) {
            tabHost.setCurrentTabByTag(cur);
        }
        if (tabHost.getCurrentTab() < 0) {
            if (defaultTab != null) {
                tabHost.setCurrentTabByTag(defaultTab);
            } else if (defaultTabIndex >= 0) {
                tabHost.setCurrentTab(defaultTabIndex);
            }
        }
    }

    private void ensureTabHost() {
        if (tabHost == null) {
            this.setContentView(R.layout.activity_registration);
        }
    }

}
