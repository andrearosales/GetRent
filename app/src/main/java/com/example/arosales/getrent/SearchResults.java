package com.example.arosales.getrent;

import android.app.ActivityGroup;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class SearchResults extends AppCompatActivity {

    private TabHost tabHost;
    public static HashMap<String, String> hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        tabHost= (TabHost) findViewById(R.id.tabHostResults);
        tabHost.setup();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        hash = (HashMap<String, String>) b.getSerializable(SearchRents.INFO_HASH);

        String []tabNames = getResources().getStringArray(R.array.tabSearchNames);
        TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator(tabNames[0]);
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator(tabNames[1]);//"Mon", null);//res.getDrawable(R..drawable.tab_icon);
        tabHost.addTab(spec2);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                Log.i("***Selected Tab", "Im currently in tab with index::" + tabHost.getCurrentTab());
                if (tabHost.getCurrentTab() == 0) {
                    new RetrieveFromDatabase().execute(hash);
                } else {

                }
            }
        });

        new RetrieveFromDatabase().execute(hash);
        tabHost.setCurrentTabByTag("tab1");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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

    public void newSearch(View view) {
        Intent intent = new Intent(this, SearchRents.class);
        startActivity(intent);
    }

    private class RetrieveFromDatabase extends AsyncTask<HashMap<String, String>, Void, ArrayList<Rent>> {

        private ProgressDialog progressDialog = new ProgressDialog(SearchResults.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loading rents");
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<Rent> doInBackground(HashMap<String, String>... params) {


            ArrayList<Rent> rents = new ArrayList<Rent>();
            HashMap<String, String> search_data = params[0];
            //Search
            ParseQuery<ParseObject> searchRentQuery = ParseQuery.getQuery("Rent");

            //Location filter
            if(search_data.containsKey(SearchRents.INFO_LOCATION)){
                Geocoder geocoder = new Geocoder(SearchResults.this);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(search_data.get(SearchRents.INFO_LOCATION), 1);
                    if(addresses.size() > 0) {
                        double latitude= addresses.get(0).getLatitude();
                        double longitude= addresses.get(0).getLongitude();
                        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
                        searchRentQuery.whereWithinKilometers("Point", geoPoint, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //Cost filter
            if(search_data.containsKey(SearchRents.INFO_COST)){
                searchRentQuery.whereLessThanOrEqualTo("Cost", Double.valueOf(search_data.get(SearchRents.INFO_COST)));
            }

            //Size filter
            if(search_data.containsKey(SearchRents.INFO_SIZE)){
                searchRentQuery.whereGreaterThanOrEqualTo("Size", Double.valueOf(search_data.get(SearchRents.INFO_SIZE)));
            }

            searchRentQuery.whereEqualTo("Inadequate", false);

            try {
                List<ParseObject> results=searchRentQuery.find();

                for(ParseObject parseRent:results){

                    if(search_data.containsKey(SearchRents.INFO_CATEGORY)){
                        if(parseRent.getString("Type").equalsIgnoreCase(search_data.get(SearchRents.INFO_CATEGORY))){
                            Rent toSave = new Rent();
                            toSave.setId(parseRent.getObjectId());
                            toSave.setType(parseRent.getString("Type"));
                            if(parseRent.getString("Description")!=null)
                                toSave.setDescription(parseRent.getString("Description"));
                            toSave.setLocation(parseRent.getString("Location"));
                            toSave.setPoint(parseRent.getParseGeoPoint("Point"));
                            toSave.setCost(parseRent.getDouble("Cost"));
                            toSave.setSize(parseRent.getDouble("Size"));
                            if(parseRent.get("Tags")!=null){
                                ArrayList<String> tags = (ArrayList<String>)parseRent.get("Tags");
                                toSave.setTags(tags);
                            }
                            if(parseRent.get("Photos")!=null){
                                ParseFile photos = (ParseFile) parseRent.get("Photos");
                                toSave.setPhotos(photos);
                            }
                            toSave.setInadequate(parseRent.getBoolean("Inadequate"));
                            toSave.setCreatedAt(parseRent.getCreatedAt());
                            rents.add(toSave);
                        }
                        else{
                            if(parseRent.get("Tags")!=null){
                                ArrayList<String> tags = (ArrayList<String>)parseRent.get("Tags");
                                for(int i=0; i<tags.size();i++){
                                    if(tags.get(i).equalsIgnoreCase(search_data.get(SearchRents.INFO_CATEGORY))){
                                        Rent toSave = new Rent();
                                        toSave.setId(parseRent.getObjectId());
                                        toSave.setType(parseRent.getString("Type"));
                                        if(parseRent.getString("Description")!=null)
                                            toSave.setDescription(parseRent.getString("Description"));
                                        toSave.setLocation(parseRent.getString("Location"));
                                        toSave.setPoint(parseRent.getParseGeoPoint("Point"));
                                        toSave.setCost(parseRent.getDouble("Cost"));
                                        toSave.setSize(parseRent.getDouble("Size"));
                                        toSave.setTags(tags);
                                        if(parseRent.get("Photos")!=null){
                                            ParseFile photos = (ParseFile) parseRent.get("Photos");
                                            toSave.setPhotos(photos);
                                        }
                                        toSave.setInadequate(parseRent.getBoolean("Inadequate"));
                                        toSave.setCreatedAt(parseRent.getCreatedAt());
                                        rents.add(toSave);
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Rent toSave = new Rent();
                        toSave.setId(parseRent.getObjectId());
                        toSave.setType(parseRent.getString("Type"));
                        if(parseRent.getString("Description")!=null)
                            toSave.setDescription(parseRent.getString("Description"));
                        else
                            toSave.setDescription(null);
                        toSave.setLocation(parseRent.getString("Location"));
                        toSave.setPoint(parseRent.getParseGeoPoint("Point"));
                        toSave.setCost(parseRent.getDouble("Cost"));
                        toSave.setSize(parseRent.getDouble("Size"));
                        if(parseRent.get("Tags")!=null){
                            ArrayList<String> tags = (ArrayList<String>)parseRent.get("Tags");
                            toSave.setTags(tags);
                        }
                        if(parseRent.get("Photos")!=null){
                            ParseFile photos = (ParseFile) parseRent.get("Photos");
                            toSave.setPhotos(photos);
                        }
                        toSave.setInadequate(parseRent.getBoolean("Inadequate"));
                        toSave.setCreatedAt(parseRent.getCreatedAt());
                        rents.add(toSave);
                    }

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return rents;
        }

        @Override
        protected void onPostExecute(ArrayList<Rent> rents) {
            super.onPostExecute(rents);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            final RentAdapter rAdapter = new RentAdapter(SearchResults.this, rents, "Search");

            ListView list_rents = (ListView) findViewById(R.id.listResult);

            Button newSearchButton = new Button(SearchResults.this);

            Drawable background = getResources().getDrawable(R.drawable.background_color);

            if (android.os.Build.VERSION.SDK_INT >= 16)
                newSearchButton.setBackground(background);
            else
                newSearchButton.setBackgroundDrawable(background);


            newSearchButton.setHeight(getResources().getDimensionPixelSize(R.dimen.button_height));
            newSearchButton.setWidth(getResources().getDimensionPixelSize(R.dimen.width_buttons));
            newSearchButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size));
            newSearchButton.setTextColor(Color.WHITE);
            newSearchButton.setTypeface(null, Typeface.BOLD);
            newSearchButton.setText(R.string.new_search_button);

            newSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchResults.this, SearchRents.class);
                    startActivity(intent);
                }
            });

            list_rents.addFooterView(newSearchButton);

            Spinner sort = new Spinner(SearchResults.this);
            ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(SearchResults.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.arraySort));
            adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sort.setAdapter(adapterSort);

            sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==1){
                        rAdapter.sortPriceMinMax();
                    }
                    else if(position==2){
                        rAdapter.sortPriceMaxMin();
                    }
                    else if(position==3){
                        rAdapter.sortSizeMinMax();
                    }
                    else if(position==4){
                        rAdapter.sortSizeMaxMin();
                    }
                    else if(position==5){
                        rAdapter.sortDateNewOld();
                    }
                    else if(position==6){
                        rAdapter.sortDateOldNew();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            list_rents.addHeaderView(sort);

            list_rents.setAdapter(rAdapter);
            list_rents.setEmptyView(findViewById(R.id.emptyView));


        }
    }

}
