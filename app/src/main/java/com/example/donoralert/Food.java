package com.example.donoralert;

import com.google.firebase.firestore.GeoPoint;

public class Food {

    String name, description;
    GeoPoint location;
    String uri;

    public Food (String name, String description, GeoPoint location, String uri)
    {
        this.uri = uri;
        this.location = location;
        this.name = name;
        this.description = description;
    }

}
