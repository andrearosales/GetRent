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


public class ListRent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rent);

        try {
            ParseQuery<ParseObject> queryOwner = ParseQuery.getQuery("Owner");
            queryOwner.include("OwnerId");
            queryOwner.whereEqualTo("OwnerId", ParseUser.getCurrentUser());

            ParseObject owner = queryOwner.getFirst();


            new RetrieveFromDatabase().execute(owner);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_rent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, ListRent.class);
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

    public void publishRent(View view) {
        Intent intent = new Intent(this, CreateRent.class);
        startActivity(intent);
    }

    private class RetrieveFromDatabase extends AsyncTask<ParseObject,Void,ArrayList<Rent>> {

        private ProgressDialog progressDialog= new ProgressDialog(ListRent.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loading rents");
            if(!progressDialog.isShowing()){
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<Rent> doInBackground(ParseObject... params) {
            ArrayList<Rent> result_rents=new ArrayList<Rent>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Rent");
            query.include("OwnerId");
            query.whereEqualTo("OwnerId", params[0]);
            try {
                List<ParseObject> results=query.find();
                for(ParseObject p:results){
                    Rent rent = new Rent();
                    rent.setId(p.getObjectId().toString());
                    rent.setType(p.getString("Type"));
                    if(p.getString("Description")!=null)
                        rent.setDescription(p.getString("Description"));
                    else
                        rent.setDescription(null);
                    rent.setLocation(p.getString("Location"));
                    rent.setPoint(p.getParseGeoPoint("Point"));
                    rent.setCost(p.getDouble("Cost"));
                    rent.setSize(p.getDouble("Size"));
                    if(p.get("Tags")!=null){
                        ArrayList<String> tags = (ArrayList<String>)p.get("Tags");
                        rent.setTags(tags);
                    }
                    if(p.get("Photos")!=null){
                        ParseFile photos = (ParseFile) p.get("Photos");
                        rent.setPhotos(photos);
                    }
                    rent.setInadequate(p.getBoolean("Inadequate"));
                    rent.setCreatedAt(p.getCreatedAt());
                    result_rents.add(rent);
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

            RentAdapter rAdapter= new RentAdapter(ListRent.this, rents, "Owner");

            ListView list_rents= (ListView) findViewById(R.id.listResult);

            final Button createRent = new Button(ListRent.this);
            Drawable background = getResources().getDrawable(R.drawable.background_color);

            if (android.os.Build.VERSION.SDK_INT >= 16)
                createRent.setBackground(background);
            else
                createRent.setBackgroundDrawable(background);

            createRent.setHeight(getResources().getDimensionPixelSize(R.dimen.button_height));
            createRent.setWidth(getResources().getDimensionPixelSize(R.dimen.width_buttons));
            createRent.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size));
            createRent.setTextColor(Color.WHITE);
            createRent.setTypeface(null, Typeface.BOLD);
            createRent.setText(getResources().getString(R.string.publish_rent));

            createRent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    publishRent(createRent);
                }
            });

            list_rents.addFooterView(createRent);

            list_rents.setAdapter(rAdapter);
            list_rents.setEmptyView(findViewById(R.id.emptyViewOwner));

        }
    }

}
