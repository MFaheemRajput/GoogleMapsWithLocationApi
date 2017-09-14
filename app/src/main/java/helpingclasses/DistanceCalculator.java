package helpingclasses;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by faheem.technerd on 9/14/17.
 */

public class DistanceCalculator {


    private ArrayList<Marker> sortListbyDistance(ArrayList<Marker> markers, final LatLng location){
        Collections.sort(markers, new Comparator<Marker>() {
            @Override
            public int compare(Marker marker2, Marker marker1) {
                //
                if(getDistanceBetweenPoints(marker1.getPosition(),location)>getDistanceBetweenPoints(marker2.getPosition(),location)){
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return markers;
    }


    private float getDistanceBetweenPoints(LatLng firstLatLng, LatLng secondLatLng) {
        float[] results = new float[1];
        Location.distanceBetween(firstLatLng.latitude, firstLatLng.longitude, secondLatLng.latitude, secondLatLng.longitude, results);
        return results[0];
    }


    public ArrayList<Marker> calculateDistance(ArrayList<Marker> markersParam, final LatLng currentLocation){

        markersParam = DistanceCalculator.this.sortListbyDistance(markersParam, currentLocation);
        return markersParam;

    }



}
