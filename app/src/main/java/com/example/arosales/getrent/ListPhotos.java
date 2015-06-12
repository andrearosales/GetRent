package com.example.arosales.getrent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ListPhotos extends AppCompatActivity {

    private String rentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photos);

        Intent intent = getIntent();
        //receivedRent = (Rent) intent.getSerializableExtra(RentAdapter.RENT);
        rentId = (String) intent.getSerializableExtra(RentAdapter.RENT);

        //try {
            //ParseQuery<ParseObject> queryRent = ParseQuery.getQuery("Rent");
            //queryRent.include("Photos");
            //queryRent.whereEqualTo("objectId", rentId);

            //ParseObject rent = queryRent.getFirst();


            new RetrieveFromDatabase().execute(rentId);

        //} catch (ParseException e) {
          //  e.printStackTrace();
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_photos, menu);
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

    public void goBack(View view) {
        finish();
    }

    private class RetrieveFromDatabase extends AsyncTask<String,Void,ArrayList<Photo>> {

        private ProgressDialog progressDialog= new ProgressDialog(ListPhotos.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loading photos");
            if(!progressDialog.isShowing()){
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<Photo> doInBackground(String... params) {
            ArrayList<Photo> result_photos=new ArrayList<Photo>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Rent");
            query.include("OwnerId");
            query.include("Photos");
            query.whereEqualTo("objectId", params[0]);
            try {
                ParseObject result=query.getFirst();
                /*for(ParseObject p:result){
                    Rent rent = new Rent();
                    rent.setId(p.getObjectId());
                    rent.setType(p.getString("Type"));
                    if(p.getString("Description")!=null)
                        rent.setDescription(p.getString("Description"));
                    rent.setLocation(p.getString("Location"));
                    //TODO
                    //Modify remove or leave Geopoints in parse
                    //rent.setPoint(p.getParseGeoPoint("Point"));
                    rent.setCost(p.getDouble("Cost"));
                    rent.setSize(p.getDouble("Size"));
                    if(p.get("Tags")!=null){
                        ArrayList<String> tags = (ArrayList<String>)p.get("Tags");
                        rent.setTags(tags);
                    }
                    if(p.get("Photos")!=null){
                        ArrayList<ParseFile> photos = (ArrayList<ParseFile>)p.get("Photos");
                        rent.setPhotos(photos);
                    }
                    rent.setInadequate(p.getBoolean("Inadequate"));
                    result_photos.add(rent);
                }*/
                ArrayList<ParseFile> photos = (ArrayList<ParseFile>) result.get("Photos");
                if(photos!=null){
                    for(int i=0; i<photos.size();i++){
                        ParseFile photoFile = photos.get(i);
                        Photo newPhoto = new Photo();
                        newPhoto.setFile(photoFile);
                        result_photos.add(newPhoto);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return result_photos;
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> photos) {
            super.onPostExecute(photos);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            PhotoAdapter photoAdapter= new PhotoAdapter(ListPhotos.this, photos, "Owner");

            ListView list_photos= (ListView) findViewById(R.id.listPhotos);

            final Button goBack = new Button(ListPhotos.this);
            Drawable background = getResources().getDrawable(R.drawable.background_color);

            if (android.os.Build.VERSION.SDK_INT >= 16)
                goBack.setBackground(background);
            else
                goBack.setBackgroundDrawable(background);

            goBack.setHeight(getResources().getDimensionPixelSize(R.dimen.button_height));
            goBack.setWidth(getResources().getDimensionPixelSize(R.dimen.width_buttons));
            goBack.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size));
            goBack.setTextColor(Color.WHITE);
            goBack.setTypeface(null, Typeface.BOLD);
            goBack.setText(getResources().getString(R.string.goBack_button));

            goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack(goBack);
                }
            });

            list_photos.addFooterView(goBack);

            list_photos.setAdapter(photoAdapter);
            list_photos.setEmptyView(findViewById(R.id.emptyViewPhotos));

        }
    }
}
