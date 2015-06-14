package com.example.arosales.getrent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ListBookmark extends AppCompatActivity {

    public static final String APPLICATION_ID = "qT7ozC6SpBUGiaKxHQwZHRyNfT0GX2xECCVsJYyv";
    public static final String CLIENT_KEY = "jLWrQSMm4uC97tkzwPZLeJ3000GQS8cDm65uXQnc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bookmark);

        try {
            ParseQuery<ParseObject> queryStudent = ParseQuery.getQuery("Student");
            queryStudent.include("StudentId");
            queryStudent.whereEqualTo("StudentId", ParseUser.getCurrentUser());

            ParseObject student = queryStudent.getFirst();


            new RetrieveFromDatabase().execute(student);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_bookmark, menu);
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

    private class RetrieveFromDatabase extends AsyncTask<ParseObject,Void,ArrayList<Rent>> {

        private ProgressDialog progressDialog= new ProgressDialog(ListBookmark.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loading bookmarks");
            if(!progressDialog.isShowing()){
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<Rent> doInBackground(ParseObject... params) {
            ArrayList<Rent> result_rents=new ArrayList<Rent>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Bookmark");
            query.include("StudentId");
            query.include("RentId");
            query.whereEqualTo("StudentId", params[0]);
            //query.whereEqualTo("Inadequate", false);
            try {
                List<ParseObject> results=query.find();
                for(ParseObject p:results){
                    ParseObject resultRent = p.getParseObject("RentId");
                    if(!resultRent.getBoolean("Inadequate")) {
                        Rent rent = new Rent();
                        rent.setId(resultRent.getObjectId());
                        rent.setType(resultRent.getString("Type"));
                        if (resultRent.getString("Description") != null)
                            rent.setDescription(resultRent.getString("Description"));
                        rent.setLocation(resultRent.getString("Location"));
                        rent.setPoint(resultRent.getParseGeoPoint("Point"));
                        rent.setCost(resultRent.getDouble("Cost"));
                        rent.setSize(resultRent.getDouble("Size"));
                        if (resultRent.get("Tags") != null) {
                            ArrayList<String> tags = (ArrayList<String>) resultRent.get("Tags");
                            rent.setTags(tags);
                        }
                        if (resultRent.get("Photos") != null) {
                            //ArrayList<ParseFile> photos = (ArrayList<ParseFile>)p.get("Photos");
                            ParseFile photos = (ParseFile) resultRent.get("Photos");
                            rent.setPhotos(photos);
                        }
                        rent.setInadequate(resultRent.getBoolean("Inadequate"));
                        result_rents.add(rent);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return result_rents;
        }

        @Override
        protected void onPostExecute(ArrayList<Rent> rents) {
            super.onPostExecute(rents);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            RentAdapter rAdapter= new RentAdapter(ListBookmark.this, rents, "Bookmarks");

            ListView list_rents= (ListView) findViewById(R.id.listBookmarks);

            list_rents.setAdapter(rAdapter);
            list_rents.setEmptyView(findViewById(R.id.emptyView));

        }
    }

}

