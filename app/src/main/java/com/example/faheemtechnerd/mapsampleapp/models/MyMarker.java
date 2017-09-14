package com.example.faheemtechnerd.mapsampleapp.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by faheem.technerd on 9/14/17.
 */

public class MyMarker {

    protected LatLng latLng;
    protected String title;

    public MyMarker(LatLng latLng, String title) {
        this.latLng = latLng;
        this.title = title;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
