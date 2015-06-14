package com.example.arosales.getrent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea Rosales on 11/06/2015.
 */
public class RentAdapter extends BaseAdapter{
    public static final String RENT = "com.example.arosales.getrent.RENT";
    public static final String SEARCH_TYPE = "com.example.arosales.getrent.SEARCH_TYPE";

    private LayoutInflater inflater;
    private Activity activity;
    private List<Rent> listRents;
    private String searchType;
    private BaseAdapter adapter;


    public RentAdapter(Activity activity, ArrayList list, String searchType) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.listRents = list;
        this.searchType = searchType;
        this.adapter=this;
    }


    @Override
    public int getCount() {
        return listRents.size();
    }

    @Override
    public Object getItem(int position) {
        return listRents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        ViewHolder vholder;
        View v = convertView;

        if (listRents.size() > 0) {
            if (v == null) {
                v = inflater.inflate(R.layout.rent_row, parent, false);
                vholder = new ViewHolder();
                vholder.textType = (TextView) v.findViewById(R.id.rentType);
                vholder.textDescription = (TextView) v.findViewById(R.id.rentDescription);
                vholder.textLocation = (TextView) v.findViewById(R.id.rentLocation);
                vholder.textPrice = (TextView) v.findViewById(R.id.rentPrice);
                v.setTag(vholder);
            } else {
                vholder = (ViewHolder) v.getTag();
            }

            vholder.textType.setText(listRents.get(position).getType());
            if(listRents.get(position).getDescription()!=null)
                vholder.textDescription.setText(listRents.get(position).getDescription().substring(0, 1).toUpperCase() + listRents.get(position).getDescription().substring(1));
            vholder.textLocation.setText(listRents.get(position).getLocation());
            vholder.textPrice.setText(listRents.get(position).getCost().toString());
            if(listRents.get(position).isInadequate()) {
                v.setClickable(false);
                v.setFocusable(false);
                v.setFocusableInTouchMode(false);
                vholder.textType.setText("INADEQUATE - "+listRents.get(position).getType().substring(0, 1).toUpperCase() + listRents.get(position).getType().substring(1));
            }else {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (searchType.equals("Bookmarks") || searchType.equals("Search")) {
                            Intent intent = new Intent(activity, RentDescription.class);
                            intent.putExtra(SEARCH_TYPE, searchType);
                            intent.putExtra(RENT, listRents.get(position).getId());
                            activity.startActivity(intent);
                        } else if (searchType.equals("Owner")) {
                            Intent intent = new Intent(activity, ViewRent.class);
                            intent.putExtra(SEARCH_TYPE, searchType);
                            //intent.putExtra(RENT, listRents.get(position));
                            intent.putExtra(RENT, listRents.get(position).getId());
                            activity.startActivity(intent);
                        }
                    }
                });
            }
        }

        return v;
    }

    public static class ViewHolder {
        public TextView textType;
        public TextView textDescription;
        public TextView textPrice;
        public TextView textLocation;
    }
}
