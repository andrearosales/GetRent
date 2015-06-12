package com.example.arosales.getrent;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Andrea Rosales on 11/06/2015.
 */
public class Rent implements Serializable{

    String id;
    String type;
    String description;
    String location;
    ParseGeoPoint point;
    Double cost;
    Double size;
    ArrayList<String> tags;
    ArrayList<ParseFile> photos;
    boolean inadequate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ParseGeoPoint getPoint() {
        return point;
    }

    public void setPoint(ParseGeoPoint point) {
        this.point = point;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<ParseFile> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<ParseFile> photos) {
        this.photos = photos;
    }

    public boolean isInadequate() {
        return inadequate;
    }

    public void setInadequate(boolean inadequate) {
        this.inadequate = inadequate;
    }
}
