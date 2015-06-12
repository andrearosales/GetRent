package com.example.arosales.getrent;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchRents extends AppCompatActivity {

    public final static String INFO_HASH = "com.example.arosales.arosales.HASH";
    public final static String INFO_SEARCHTYPE = "com.example.arosales.arosales.SEARCHTYPE";
    public final static String INFO_LOCATION = "com.example.arosales.arosales.LOCATION";
    public final static String INFO_CATEGORY = "com.example.arosales.arosales.CATEGORY";
    public final static String INFO_COST = "com.example.arosales.arosales.COST";
    public final static String INFO_SIZE = "com.example.arosales.arosales.SIZE";

    private ArrayAdapter<String> adapterCategory;
    private ArrayAdapter<String> adapterPrice;
    private ArrayAdapter<String> adapterSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_rents);

        Spinner category = (Spinner) findViewById(R.id.spinnerCategory);
        Spinner price = (Spinner) findViewById(R.id.spinnerCost);
        Spinner size = (Spinner) findViewById(R.id.spinnerSize);

        final ArrayList<String> categories = new ArrayList<>();
        categories.add("-");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> categoryList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i <categoryList.size(); i++) {
                        ParseObject category = categoryList.get(i);
                        categories.add(category.getString("Name"));
                    }
                }
            }
        });
        adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPrice = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.arrayPrice));
        adapterPrice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSize = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.arraySize));
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category.setAdapter(adapterCategory);
        price.setAdapter(adapterPrice);
        size.setAdapter(adapterSize);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_rents, menu);
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

    public void search(View view){
        Intent intent = new Intent(this, SearchResults.class);

        HashMap<String,String> search_filters= new HashMap<String,String>();

        EditText locationFilter= (EditText) findViewById(R.id.textLocation);
        Spinner category = (Spinner) findViewById(R.id.spinnerCategory);
        Spinner price = (Spinner) findViewById(R.id.spinnerCost);
        Spinner size = (Spinner) findViewById(R.id.spinnerSize);

        search_filters.put(INFO_SEARCHTYPE,"Search");

        if(!locationFilter.getText().toString().equals("")){
            search_filters.put(INFO_LOCATION, locationFilter.getText().toString().toLowerCase());
        }

        if(!category.getSelectedItem().toString().equals("-")){
            search_filters.put(INFO_CATEGORY,category.getSelectedItem().toString());
        }

        if(!price.getSelectedItem().toString().equals("Maximum price")){
            search_filters.put(INFO_COST,price.getSelectedItem().toString());
        }

        if(!size.getSelectedItem().toString().equals("Minimum size")){
            search_filters.put(INFO_SIZE,size.getSelectedItem().toString());
        }

        Bundle b = new Bundle();
        b.putSerializable(INFO_HASH,search_filters);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        EditText locationFilter= (EditText) findViewById(R.id.textLocation);
        Spinner category = (Spinner) findViewById(R.id.spinnerCategory);
        Spinner price = (Spinner) findViewById(R.id.spinnerCost);
        Spinner size = (Spinner) findViewById(R.id.spinnerSize);

        outState.putString(INFO_LOCATION,locationFilter.getText().toString());
        outState.putString(INFO_CATEGORY,category.getSelectedItem().toString());
        outState.putString(INFO_COST,price.getSelectedItem().toString());
        outState.putString(INFO_SIZE,size.getSelectedItem().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        EditText locationFilter= (EditText) findViewById(R.id.textLocation);
        Spinner category = (Spinner) findViewById(R.id.spinnerCategory);
        Spinner price = (Spinner) findViewById(R.id.spinnerCost);
        Spinner size = (Spinner) findViewById(R.id.spinnerSize);

        locationFilter.setText(savedInstanceState.getString(INFO_LOCATION));
        category.setSelection(adapterCategory.getPosition(savedInstanceState.getString(INFO_CATEGORY)));
        price.setSelection(adapterPrice.getPosition(savedInstanceState.getString(INFO_COST)));
        size.setSelection(adapterSize.getPosition(savedInstanceState.getString(INFO_SIZE)));

    }
}