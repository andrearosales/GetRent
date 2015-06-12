package com.example.arosales.getrent;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CreateRent extends AppCompatActivity {

    private static final String TYPE = "com.example.arosales.getrent.TYPE";
    private static final String DESCRIPTION = "com.example.arosales.getrent.DESCRIPTION";
    private static final String LOCATION = "com.example.arosales.getrent.LOCATION";
    private static final String COST = "com.example.arosales.getrent.POSITION";
    private static final String SIZE = "com.example.arosales.getrent.INDUSTRY";
    private static final String TAGS = "com.example.arosales.getrent.SALARY";
    private static final int REQUEST_IMAGE_GET = 1;

    private ArrayAdapter<String> adapterType;
    private List<ParseFile> photos;
    private ArrayList<String> paths;
    private ArrayList<byte[]> photosBitmap;
    private ArrayList<Bitmap> bitmapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rent);

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

    }

    public void publishRent(View view){

        Spinner type = (Spinner) findViewById(R.id.spinnerType);
        EditText description = (EditText) findViewById(R.id.textDescription);
        EditText location = (EditText) findViewById(R.id.textLocation);
        EditText cost = (EditText) findViewById(R.id.textCost);
        EditText size = (EditText) findViewById(R.id.textSize);
        EditText tags = (EditText) findViewById(R.id.textTags);

        // Validate the sign up data
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
            //TODO
            //Modify remove or leave Geopoints in parse
            //rent.put("Point",p.getParseGeoPoint("Point"));

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

            /*if(paths!=null && paths.size()>0){
                createPhotoFiles();
                rent.put("Photos", photos);
            }*/

            if(photosBitmap!=null && photosBitmap.size()>0){
                createPhotoFiles();
                rent.put("Photos", photos);
            }

            /*if(bitmapList!=null && bitmapList.size()>0){
                createPhotoFiles();
                rent.put("Photos", photos);
            }*/

            rent.put("Inadequate", false);

            rent.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    Log.d("Save", "It saved");
                    dlg.dismiss();
                }
            });
            //rent.save();
            //Log.d("Save", "It saved");
            //rent.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, ListRent.class);
        startActivity(intent);
    }

    private void createPhotoFiles() {
        try
        {
            photos = new ArrayList<>();
            for(int i=0;i<photosBitmap.size();i++){
            //for(int i=0;i<paths.size();i++){
            //for(int i=0;i<bitmapList.size();i++){
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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                Bitmap bitmap = bitmapList.get(i);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                final String filename = "Image"+i+".JPEG";
                //final ParseFile parseFile = new ParseFile(filename, photosBitmap.get(i));
                final ParseFile parseFile = new ParseFile(filename, image);
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

    public void cancel(View view){
        finish();
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
            bitmapList = new ArrayList<>();
            if(data.getData() != null){
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();

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
                /*try {
                    ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
                    byte[] imageData = exif.getThumbnail();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
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
                    bitmapList.add(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


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
                    Log.d("Selected image ", "Selected several images");
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
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getRealPathFromURI(Uri contentUri) {
        String [] projection = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(
                getApplicationContext(),
                contentUri,
                projection,
                null,   //selection
                null,   //selectionArgs
                null   //sortOrder
        );

        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

}
