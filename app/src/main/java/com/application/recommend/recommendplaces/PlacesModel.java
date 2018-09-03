package com.application.recommend.recommendplaces;

import java.io.Serializable;

/**
 * Created by Akarsh on 11-01-2018.
 */

public class PlacesModel implements Serializable{

    private String id;
    private String name;
    private String lat;
    private String longi;
    private String add1 = "";
    private String price;
    private String rating;
    private String ratingColor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAdd1() {
        return add1;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public String getRatingColor() {
        return ratingColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setRatingColor(String ratingColor) {
        this.ratingColor = ratingColor;
    }
}
