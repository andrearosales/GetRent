package com.example.arosales.getrent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea Rosales on 12/06/2015.
 */
public class PhotoAdapter extends BaseAdapter {
    public static final String RENT = "com.example.arosales.getrent.RENT";
    public static final String SEARCH_TYPE = "com.example.arosales.getrent.SEARCH_TYPE";

    private LayoutInflater inflater;
    private Activity activity;
    private List<Photo> listPhotos;
    private String searchType;
    private BaseAdapter adapter;


    public PhotoAdapter(Activity activity, ArrayList list, String searchType) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.listPhotos = list;
        this.searchType = searchType;
        this.adapter=this;
    }


    @Override
    public int getCount() {
        return listPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return listPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        ViewHolder vholder;
        View v = convertView;

        if (listPhotos.size() > 0) {
            if (v == null) {
                v = inflater.inflate(R.layout.photo_row, parent, false);
                vholder = new ViewHolder();
                vholder.image = (ParseImageView) v.findViewById(R.id.rentPhoto);
                v.setTag(vholder);
            } else {
                vholder = (ViewHolder) v.getTag();
            }

            ParseFile file;
            vholder.image.setParseFile(listPhotos.get(position).getFile());
            vholder.image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {

                }
            });

        }

        return v;
    }

    public static class ViewHolder {
        public ParseImageView image;
    }
}
