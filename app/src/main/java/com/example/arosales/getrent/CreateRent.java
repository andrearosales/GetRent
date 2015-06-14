package com.example.arosales.getrent;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CreateRent extends AppCompatActivity {

    private static final String TYPE = "com.example.arosales.getrent.TYPE";
    private static final String DESCRIPTION = "com.example.arosales.getrent.DESCRIPTION";
    private static final String LOCATION = "com.example.arosales.getrent.LOCATION";
    private static final String COST = "com.example.arosales.getrent.POSITION";
    private static final String SIZE = "com.example.arosales.getrent.INDUSTRY";
    private static final String TAGS = "com.example.arosales.getrent.SALARY";
    private static final String PHOTO = "com.example.arosales.getrent.PHOTO";
    private static final int REQUEST_IMAGE_GET = 1;

    private ArrayAdapter<String> adapterType;
    private ImageView image;
    private String path = "";
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rent);

        image = (ImageView) findViewById(R.id.rentImage);

        Spinner type = (Spinner) findViewById(R.id.spinnerType);
        adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.arrayType));
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapterType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_rent, menu);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Spinner type = (Spinner) findViewById(R.id.spinnerType);
        EditText description = (EditText) findViewById(R.id.textDescription);
        EditText location = (EditText) findViewById(R.id.textLocation);
        EditText cost = (EditText) findViewById(R.id.textCost);
        EditText size = (EditText) findViewById(R.id.textSize);
        EditText tags = (EditText) findViewById(R.id.textTags);

        if(!type.getSelectedItem().toString().equals("-")) {
            outState.putString(TYPE, type.getSelectedItem().toString());
        }
        outState.putString(DESCRIPTION, description.getText().toString());
        outState.putString(LOCATION, location.getText().toString());
        outState.putString(COST, cost.getText().toString());
        outState.putString(SIZE, size.getText().toString());
        outState.putString(TAGS, tags.getText().toString());
        if(bitmap!=null){
            outState.putParcelable(PHOTO, bitmap);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ImageView photo = (ImageView) findViewById(R.id.rentImage);
        Spinner type = (Spinner) findViewById(R.id.spinnerType);
        EditText description = (EditText) findViewById(R.id.textDescription);
        EditText location = (EditText) findViewById(R.id.textLocation);
        EditText cost = (EditText) findViewById(R.id.textCost);
        EditText size = (EditText) findViewById(R.id.textSize);
        EditText tags = (EditText) findViewById(R.id.textTags);

        if(savedInstanceState.containsKey(TYPE)) {
            type.setSelection(adapterType.getPosition(savedInstanceState.getString(TYPE)));
        }
        description.setText(savedInstanceState.getString(DESCRIPTION));
        location.setText(savedInstanceState.getString(LOCATION));
        cost.setText(savedInstanceState.getString(COST));
        size.setText(savedInstanceState.getString(SIZE));
        tags.setText(savedInstanceState.getString(TAGS));

        if(savedInstanceState.containsKey(PHOTO)){
            bitmap = savedInstanceState.getParcelable(PHOTO);
            photo.setImageBitmap(bitmap);
        }

    }

    public void publishRent(View view){

        Spinner type = (Spinner) findViewById(R.id.spinnerType);
        EditText description = (EditText) findViewById(R.id.textDescription);
        EditText location = (EditText) findViewById(R.id.textLocation);
        EditText cost = (EditText) findViewById(R.id.textCost);
        EditText size = (EditText) findViewById(R.id.textSize);
        EditText tags = (EditText) findViewById(R.id.textTags);
        ParseGeoPoint geoPoint = null;

        // Validate the inserted data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getResources().getString(R.string.error_intro)+"\n");
        if (type.getSelectedItem().toString().equals("-")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_rent_type)+"\n");
        }
        if (location.getText().toString().equals("")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_location)+"\n");
        }
        else {
            Geocoder geocoder = new Geocoder(CreateRent.this);
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(location.getText().toString(), 1);
                if(addresses.size() > 0) {
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();
                    geoPoint = new ParseGeoPoint(latitude, longitude);
                }
                else {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_wrong_location)+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cost.getText().toString().equals("")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_cost)+"\n");
        }
        if (size.getText().toString().equals("")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_size)+"\n");
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(CreateRent.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(CreateRent.this);
        dlg.setTitle(R.string.publication_progress);
        dlg.setMessage(getString(R.string.publish_rent_message));
        dlg.show();

        //save the data to parse.com

        try {
            ParseObject rent = new ParseObject("Rent");
            ParseQuery ownerQuery= new ParseQuery("Owner");
            ownerQuery.whereEqualTo("OwnerId", ParseUser.getCurrentUser());
            ParseObject owner=ownerQuery.getFirst();
            rent.put("OwnerId", owner);

            rent.put("Type", type.getSelectedItem().toString());

            if(!description.getText().toString().equals(""))
                rent.put("Description", description.getText().toString());

            rent.put("Location", location.getText().toString());
            rent.put("Point", geoPoint);

            rent.put("Cost", Double.valueOf(cost.getText().toString()));
            rent.put("Size", Double.valueOf(size.getText().toString()));

            if(!tags.getText().toString().equals("")){
                final List<String> listTags = Arrays.asList(tags.getText().toString().split(","));
                rent.put("Tags", listTags);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> resultList, ParseException e) {
                        if (e == null) {
                            if (resultList != null) {
                                if (resultList.size() > 0) {
                                    for (int i = 0; i < listTags.size(); i++) {
                                        boolean found = false;
                                        for (int j = 0; j < resultList.size(); j++) {
                                            String name = resultList.get(j).getString("Name");
                                            if (name.equalsIgnoreCase(listTags.get(i))) {
                                                found = true;
                                            }
                                        }
                                        if (!found) {
                                            ParseObject category = new ParseObject("Category");
                                            category.put("Name", listTags.get(i));
                                            try {
                                                category.save();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }

            if(bitmap!=null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                byte[] imageData = baos.toByteArray();
                ParseFile parseFile = new ParseFile("Photo", imageData);
                parseFile.save();
                rent.put("Photos",parseFile);
            }

            rent.put("Inadequate", false);

            rent.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    Log.d("Save", "It saved");
                    dlg.dismiss();
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, ListRent.class);
        startActivity(intent);
    }

    public void cancel(View view){
        finish();
    }

    public void uploadPhotos(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            if(data.getData() != null){
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path = cursor.getString(columnIndex);
                cursor.close();
                Log.d("Selected image ", "Only one selected image");
                final int THUMBNAIL_SIZE = 64;

                try {
                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                    ExifInterface ei = new ExifInterface(path);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    Matrix matrix = new Matrix();
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            break;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
