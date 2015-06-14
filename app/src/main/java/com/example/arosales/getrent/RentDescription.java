package com.example.arosales.getrent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;


public class RentDescription extends ActionBarActivity {

    private String rentId;
    private String descriptionType;
    public static HashMap<String, String> hash;
    public final static String INFO_HASH = "com.example.arosales.arosales.HASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_description);

        Intent intent = getIntent();
        rentId = (String) intent.getStringExtra(RentAdapter.RENT);
        descriptionType = intent.getStringExtra(RentAdapter.SEARCH_TYPE);
        if(descriptionType.equals("Search")) {
            Bundle b = intent.getExtras();
            hash = (HashMap<String, String>) b.getSerializable(SearchRents.INFO_HASH);
        }

        TextView typeView = (TextView) findViewById(R.id.rentType);
        TextView descriptionView = (TextView) findViewById(R.id.rentDescription);
        TextView locationView = (TextView) findViewById(R.id.rentLocation);
        TextView costView = (TextView) findViewById(R.id.rentPrice);
        TextView sizeView = (TextView) findViewById(R.id.rentSize);
        TextView tagsView = (TextView) findViewById(R.id.rentTags);
        ImageView image = (ImageView) findViewById(R.id.rentImage);

        ParseQuery rentQuery = new ParseQuery("Rent");
        rentQuery.include("OwnerId");
        rentQuery.whereEqualTo("objectId", rentId);
        try {
            ParseObject receivedRent = rentQuery.getFirst();
            typeView.setText(receivedRent.getString("Type").toUpperCase());
            if (receivedRent.getString("Description") != null)
                descriptionView.setText(receivedRent.getString("Description"));
            locationView.setText(locationView.getText().toString()+": "+receivedRent.getString("Location"));
            costView.setText(costView.getText().toString()+": "+receivedRent.getNumber("Cost").toString());
            sizeView.setText(sizeView.getText().toString()+": "+receivedRent.getNumber("Size").toString());
            if (receivedRent.get("Tags") != null) {
                ArrayList<String> tags = (ArrayList<String>) receivedRent.get("Tags");
                tagsView.setText(tagsView.getText().toString() + "\n");
                if(tags.size()>0)
                    tagsView.setText(tags.toString().substring(1, tags.toString().length() - 1));
            }
            if(receivedRent.get("Photos")!=null){
                ParseFile photo = receivedRent.getParseFile("Photos");
                Bitmap bitmap = BitmapFactory.decodeByteArray(photo.getData(), 0, photo.getData().length);;
                image = (ImageView) findViewById(R.id.rentImage);
                image.setImageBitmap(bitmap);
            }
            ParseObject owner = receivedRent.getParseObject("OwnerId");
            if(owner.getString("Telephone")!=null){
                TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
                SpannableString content = new SpannableString(owner.getString("Telephone").toString());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                phoneNumber.setText(content);
                //phoneNumber.setText(owner.getString("Telephone").toString());
            }
            if(owner.getString("Email")!=null){
                TextView emailOwner = (TextView) findViewById(R.id.emailOwner);
                SpannableString content = new SpannableString(owner.getString("Email").toString());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                emailOwner.setText(content);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button bookmark = (Button) findViewById(R.id.BookmarkButton);
        if(descriptionType.equals("Search")){
            bookmark.setText(getResources().getString(R.string.bookmark_button));
        }
        else {
            bookmark.setText(getResources().getString(R.string.unbookmark_button));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rent_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bookmarks) {
            Intent intent = new Intent(this, ListBookmark.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchRents.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent intent= new Intent(this,LogIn.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goBack(View view) {
        finish();
    }

    public void bookmarkRent(View view) {
        try {
            ParseQuery<ParseObject> queryStudent = ParseQuery.getQuery("Student");
            queryStudent.include("StudentId");
            queryStudent.whereEqualTo("StudentId", ParseUser.getCurrentUser());
            ParseQuery<ParseObject> queryRent = ParseQuery.getQuery("Rent");
            queryRent.whereEqualTo("objectId", rentId);

            ParseObject student = queryStudent.getFirst();
            ParseObject rent = queryRent.getFirst();

            ParseQuery<ParseObject> queryBookmark = ParseQuery.getQuery("Bookmark");
            queryBookmark.whereEqualTo("StudentId", student);
            queryBookmark.whereEqualTo("RentId", rent);

            if(descriptionType.equals("Search")) {
                String message = null;

                if (queryBookmark.count() == 0) {
                    ParseObject bookmark = new ParseObject("Bookmark");
                    bookmark.put("StudentId", student);
                    bookmark.put("RentId", rent);
                    bookmark.saveInBackground();
                    message = getString(R.string.addedBookmarkMessage);

                } else {
                    message = getString(R.string.existingBookmarkMessage);

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save bookmark");
                builder.setMessage(message);
                builder.setCancelable(true);
                builder.setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
            else{
                queryBookmark.getFirst().deleteInBackground();
                Intent intent = new Intent(this, ListBookmark.class);
                startActivity(intent);

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void dialPhoneNumber(View view) {
        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        if(phoneNumber.getText()!=null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber.getText().toString()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    public void sendEmail(View view) {
        TextView email = (TextView) findViewById(R.id.emailOwner);
        TextView type = (TextView) findViewById(R.id.rentType);
        String subject = "Contact for rent: "+type.getText().toString();
        if(email.getText()!=null) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email.getText().toString()));
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    public void flagInadequate(View view) {
        try {
            ParseQuery<ParseObject> queryRent = ParseQuery.getQuery("Rent");
            queryRent.whereEqualTo("objectId", rentId);
            ParseObject rent = queryRent.getFirst();
            if (descriptionType.equals("Search")) {
                rent.put("Inadequate", true);
                rent.save();
                Intent intent = new Intent(this, SearchResults.class);
                Bundle b = new Bundle();
                b.putSerializable(INFO_HASH,hash);
                intent.putExtras(b);
                startActivity(intent);
            } else {

                ParseQuery<ParseObject> queryStudent = ParseQuery.getQuery("Student");
                queryStudent.include("StudentId");
                queryStudent.whereEqualTo("StudentId", ParseUser.getCurrentUser());

                ParseObject student = queryStudent.getFirst();

                ParseQuery<ParseObject> queryBookmark = ParseQuery.getQuery("Bookmark");
                queryBookmark.whereEqualTo("StudentId", student);
                queryBookmark.whereEqualTo("RentId", rent);

                rent.put("Inadequate", true);
                rent.saveInBackground();

                queryBookmark.getFirst().deleteInBackground();
                Intent intent = new Intent(this, ListBookmark.class);
                startActivity(intent);

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
