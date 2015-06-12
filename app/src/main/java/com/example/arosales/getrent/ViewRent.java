package com.example.arosales.getrent;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ViewRent extends AppCompatActivity {

    private static final String TYPE = "com.example.arosales.getrent.TYPE";
    private static final String DESCRIPTION = "com.example.arosales.getrent.DESCRIPTION";
    private static final String LOCATION = "com.example.arosales.getrent.LOCATION";
    private static final String COST = "com.example.arosales.getrent.POSITION";
    private static final String SIZE = "com.example.arosales.getrent.INDUSTRY";
    private static final String TAGS = "com.example.arosales.getrent.SALARY";
    private static final String EDIT = "com.example.arosales.getrent.EDIT";
    private static final int REQUEST_IMAGE_GET = 1;

    private Spinner typeView;
    private EditText descriptionView;
    private EditText locationView;
    private EditText costView;
    private EditText sizeView;
    private EditText tagsView;

    private ArrayAdapter<String> adapterType;

    //private Rent receivedRent;
    private String rentId;

    private ArrayList<byte[]> photosBitmap;
    private List<ParseFile> photos;
    private boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rent);

        Intent intent = getIntent();
        //receivedRent = (Rent) intent.getSerializableExtra(RentAdapter.RENT);
        rentId = (String) intent.getStringExtra(RentAdapter.RENT);

        descriptionView = (EditText) findViewById(R.id.textDescription);
        locationView = (EditText) findViewById(R.id.textLocation);
        costView = (EditText) findViewById(R.id.textCost);
        sizeView = (EditText) findViewById(R.id.textSize);
        tagsView = (EditText) findViewById(R.id.textTags);
        typeView = (Spinner) findViewById(R.id.spinnerType);

        adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.arrayType));
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeView.setAdapter(adapterType);

        ParseQuery rentQuery = new ParseQuery("Rent");
        //rentQuery.include("OwnerId");
        //rentQuery.whereEqualTo("objectId", rentId);
        try {
            //ParseObject receivedRent = rentQuery.getFirst();
            ParseObject receivedRent = rentQuery.get(rentId);
            typeView.setSelection(adapterType.getPosition(receivedRent.getString("Type")));
            if (receivedRent.getString("Description") != null) {
                descriptionView.setText(receivedRent.getString("Description"));
                locationView.setText(receivedRent.getString("Location"));
                costView.setText(receivedRent.getNumber("Cost").toString());
                sizeView.setText(receivedRent.getNumber("Size").toString());
                if (receivedRent.get("Tags") != null) {
                    ArrayList<String> tags = (ArrayList<String>) receivedRent.get("Tags");
                    if(tags.size()>0)
                        tagsView.setText(tags.toString().substring(1, tags.toString().length() - 1));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //typeView.setSelection(adapterType.getPosition(receivedRent.getType()));
        typeView.setFocusable(false);
        typeView.setFocusableInTouchMode(false);
        typeView.setClickable(false);
        typeView.setEnabled(false);

        /*if (receivedRent.getDescription() != null) {
            descriptionView.setText(receivedRent.getDescription());
        }*/
        descriptionView.setFocusable(false);
        descriptionView.setFocusableInTouchMode(false);
        descriptionView.setClickable(false);

        //locationView.setText(receivedRent.getLocation());
        locationView.setFocusable(false);
        locationView.setFocusableInTouchMode(false);
        locationView.setClickable(false);

        //costView.setText(receivedRent.getCost().toString());
        costView.setFocusable(false);
        costView.setFocusableInTouchMode(false);
        costView.setClickable(false);

        //sizeView.setText(receivedRent.getSize().toString());
        sizeView.setFocusable(false);
        sizeView.setFocusableInTouchMode(false);
        sizeView.setClickable(false);

        /*if (receivedRent.getTags() != null){
            if(receivedRent.getTags().size()>0)
                tagsView.setText(receivedRent.getTags().toString().substring(1, receivedRent.getTags().toString().length() - 1));
        }*/
        tagsView.setFocusable(false);
        tagsView.setFocusableInTouchMode(false);
        tagsView.setClickable(false);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setClickable(false);
        saveButton.setFocusable(false);
        saveButton.setFocusableInTouchMode(false);

        TextView uploadPhotos = (TextView) findViewById(R.id.rentAddPhotos);
        uploadPhotos.setClickable(false);
        uploadPhotos.setFocusable(false);
        uploadPhotos.setFocusableInTouchMode(false);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        descriptionView = (EditText) findViewById(R.id.textDescription);
        locationView = (EditText) findViewById(R.id.textLocation);
        costView = (EditText) findViewById(R.id.textCost);
        sizeView = (EditText) findViewById(R.id.textSize);
        tagsView = (EditText) findViewById(R.id.textTags);
        typeView = (Spinner) findViewById(R.id.spinnerType);

        if(!typeView.getSelectedItem().toString().equals("-")) {
            outState.putString(TYPE, typeView.getSelectedItem().toString());
        }
        outState.putString(DESCRIPTION, descriptionView.getText().toString());
        outState.putString(LOCATION, locationView.getText().toString());
        outState.putString(COST, costView.getText().toString());
        outState.putString(SIZE, sizeView.getText().toString());
        outState.putString(TAGS, tagsView.getText().toString());

        if(edit) {
            outState.putBoolean(EDIT, true);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

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

        if(savedInstanceState.containsKey(EDIT)){
            Button saveButton = (Button) findViewById(R.id.saveButton);
            saveButton.setClickable(true);
            saveButton.setFocusable(true);
            saveButton.setFocusableInTouchMode(true);

            TextView uploadPhotos = (TextView) findViewById(R.id.rentAddPhotos);
            uploadPhotos.setClickable(true);
            uploadPhotos.setFocusable(true);
            uploadPhotos.setFocusableInTouchMode(true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_rent, menu);
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

    public void editRent(View view) {
        Button editButton= (Button) findViewById(R.id.editButton);
        editButton.setVisibility(View.GONE);

        edit = true;

        typeView.setFocusable(true);
        typeView.setFocusableInTouchMode(true);
        typeView.setClickable(true);
        typeView.setEnabled(true);

        descriptionView.setFocusable(true);
        descriptionView.setFocusableInTouchMode(true);
        descriptionView.setClickable(true);

        locationView.setFocusable(true);
        locationView.setFocusableInTouchMode(true);
        locationView.setClickable(true);

        costView.setFocusable(true);
        costView.setFocusableInTouchMode(true);
        costView.setClickable(true);

        sizeView.setFocusable(true);
        sizeView.setFocusableInTouchMode(true);
        sizeView.setClickable(true);

        tagsView.setFocusable(true);
        tagsView.setFocusableInTouchMode(true);
        tagsView.setClickable(true);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setClickable(true);
        saveButton.setFocusable(true);
        saveButton.setFocusableInTouchMode(true);

        TextView uploadPhotos = (TextView) findViewById(R.id.rentAddPhotos);
        uploadPhotos.setClickable(true);
        uploadPhotos.setFocusable(true);
        uploadPhotos.setFocusableInTouchMode(true);

    }

    public void saveRent(View view) {

        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getResources().getString(R.string.error_intro)+"\n");
        if (typeView.getSelectedItem().toString().equals("-")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_rent_type) + "\n");
        }
        if (locationView.getText().toString().equals("")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_location) + "\n");
        }
        if (costView.getText().toString().equals("")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_cost) + "\n");
        }
        if (sizeView.getText().toString().equals("")) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_size) + "\n");
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(ViewRent.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dlg = new ProgressDialog(ViewRent.this);
        dlg.setTitle(R.string.publication_progress);
        dlg.setMessage(getString(R.string.publish_rent_message));
        dlg.show();
        //save the data to parse.com


        ParseQuery rentQuery = new ParseQuery("Rent");
        rentQuery.include("OwnerId");
        //rentQuery.whereEqualTo("objectId", receivedRent.getId());
        rentQuery.whereEqualTo("objectId", rentId);
        try {
            ParseObject resultRent = rentQuery.getFirst();

            resultRent.put("Type", typeView.getSelectedItem().toString());

            if(!descriptionView.getText().toString().equals(""))
                resultRent.put("Description", descriptionView.getText().toString());
            else
                resultRent.remove("Description");

            resultRent.put("Location", locationView.getText().toString());
            //TODO
            //Modify remove or leave Geopoints in parse
            //receivedRent.put("Point",p.getParseGeoPoint("Point"));

            resultRent.put("Cost", Double.valueOf(costView.getText().toString()));
            resultRent.put("Size", Double.valueOf(sizeView.getText().toString()));

            if(!tagsView.getText().toString().equals("")){
                final List<String> listTags = Arrays.asList(tagsView.getText().toString().split(","));
                resultRent.put("Tags", listTags);
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
            else
                resultRent.remove("Tags");

            if(photosBitmap!=null && photosBitmap.size()>0){
                createPhotoFiles();
                resultRent.add("Photos", photos);
            }

            resultRent.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dlg.dismiss();
                    edit = false;
                    Intent intent = new Intent(ViewRent.this, ViewRent.class);
                    //intent.putExtra(RentAdapter.RENT, receivedRent.getId());
                    intent.putExtra(RentAdapter.RENT, rentId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void deleteRent(View view) {

        try {
            //
                /*Parse.initialize(this, "H9NFC1K9LmahxGcCrMOdT0qMaE0lDGT6BgbrSOAc", "4K2VfxRGIyk69KlQJ2B8NMnD71llrlkEPLdTNh9M");
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", "2AM7fmxH5Sk");
                ParseUser user = query.getFirst();*/
            //

            ParseQuery<ParseObject> queryStudent = ParseQuery.getQuery("Student");
            queryStudent.include("StudentId");
            queryStudent.whereEqualTo("StudentId", ParseUser.getCurrentUser());

            ParseObject student = queryStudent.getFirst();

            ParseQuery<ParseObject> queryApplyJob = ParseQuery.getQuery("ApplyJob");
            queryApplyJob.include("StudentId");
            queryApplyJob.whereEqualTo("StudentId", student);

            List<ParseObject> resultsApplyJob = queryApplyJob.find();
            for (ParseObject p : resultsApplyJob) {
                p.delete();
            }

            ParseQuery<ParseObject> querySavedCompany = ParseQuery.getQuery("SavedCompany");
            querySavedCompany.include("StudentId");
            querySavedCompany.whereEqualTo("StudentId", student);

            List<ParseObject> resultsSavedCompany = querySavedCompany.find();
            for (ParseObject p : resultsSavedCompany) {
                p.delete();
            }

            ParseQuery<ParseObject> querySavedJobOffer = ParseQuery.getQuery("SavedJobOffer");
            querySavedJobOffer.include("StudentId");
            querySavedJobOffer.whereEqualTo("StudentId", student);

            List<ParseObject> resultsSavedJobOffer = querySavedJobOffer.find();
            for (ParseObject p : resultsSavedJobOffer) {
                p.delete();
            }

            ParseQuery<ParseObject> querySavedStudent = ParseQuery.getQuery("SavedStudent");
            querySavedStudent.include("StudentId");
            querySavedStudent.whereEqualTo("StudentId", student);

            List<ParseObject> resultsSavedStudent = querySavedStudent.find();
            for (ParseObject p : resultsSavedStudent) {
                p.delete();
            }

            ParseQuery<ParseObject> queryMessage = ParseQuery.getQuery("Message");
            queryMessage.whereEqualTo("SenderId", ParseUser.getCurrentUser().getObjectId());
            List<ParseObject> resultsMessage = queryMessage.find();
            for (ParseObject p : resultsMessage) {
                p.delete();
            }

            queryMessage = ParseQuery.getQuery("Message");
            queryMessage.whereEqualTo("ReceiverId", ParseUser.getCurrentUser().getObjectId());
            resultsMessage = queryMessage.find();
            for (ParseObject p : resultsMessage) {
                p.delete();
            }

            student.delete();
            ParseUser.getCurrentUser().delete();
            //String objectId = ParseUser.getCurrentUser().getObjectId();
            ParseUser.logOut();



            /*ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", objectId);
            ParseUser user = query.getFirst();
            user.delete();*/

            startActivity(new Intent(this, ManageSession.class));


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void goBack(View view) {
        Intent intent = new Intent(this, ListRent.class);
        startActivity(intent);
    }

    public void viewPhotos(View view) {
        Intent intent = new Intent(this, ListPhotos.class);
        intent.putExtra(RentAdapter.RENT, rentId);
        startActivity(intent);
    }

    public void uploadPhotos(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            //paths = new ArrayList<>();
            photosBitmap = new ArrayList<>();
            if(data.getData() != null){
                Uri selectedImage = data.getData();
                //String path = getRealPathFromURI(selectedImage);
                /*
                if(path==null)
                    path=selectedImage.getPath();
                paths.add(path);*/
                Log.d("Selected image ", "Only one selected image");
                final int THUMBNAIL_SIZE = 64;

                try {
                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                    byte[] imageData = baos.toByteArray();
                    photosBitmap.add(imageData);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                //If uploaded with the new Android Photos gallery
                ClipData clipData = data.getClipData();
                for(int i = 0; i < clipData.getItemCount(); i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    //String path = getRealPathFromURI(selectedImage);
                    /*
                    if(path==null)
                        path=uri.getPath();
                    paths.add(path);*/
                    Log.d("Selected image ", "Only one selected image");
                    final int THUMBNAIL_SIZE = 64;

                    try {
                        InputStream is = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                        byte[] imageData = baos.toByteArray();
                        photosBitmap.add(imageData);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void createPhotoFiles() {
        try
        {
            photos = new ArrayList<>();
            for(int i=0;i<photosBitmap.size();i++){
                //for(int i=0;i<paths.size();i++){
                //To get filename
                //File file= new File(paths.get(i));
                /*String fileName = paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1);
                final int THUMBNAIL_SIZE = 64;

                FileInputStream fis = new FileInputStream(fileName);
                Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();*/
                // Create the ParseFile and Upload the image into Parse Cloud
                //final ParseFile parseFile = new ParseFile(fileName, imageData);
                final String filename = "Image"+i+".JPEG";
                final ParseFile parseFile = new ParseFile(filename, photosBitmap.get(i));
                /*parseFile.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        // If successful add file to user and signUpInBackground
                        if (null == e)
                            Log.d("File stored ", filename);
                            photos.add(parseFile);
                    }
                });*/
                parseFile.save();
                Log.d("File stored ", filename);
                //
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
