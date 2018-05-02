package com.alexandrunica.allcabins.map.helper;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.text.DecimalFormat;

public class LocationHelper {
    private Context context;

    public LocationHelper(Context context) {
        this.context = context;
    }


    public float calcRoute(LocationModel location, LocationModel cabinLocation) {
        float[] results = new float[1];
        Location.distanceBetween(location.getDoubleLatitude(), location.getDoubleLongitude(),
                cabinLocation.getDoubleLatitude(), cabinLocation.getDoubleLongitude(), results);
        return results[0];
    }

    public double calculationByDistance(LocationModel location, LocationModel cabinLocation) {
        LatLng startP = new LatLng(location.getDoubleLatitude(), location.getDoubleLongitude());
        LatLng endP = new LatLng(cabinLocation.getDoubleLatitude(), cabinLocation.getDoubleLongitude());
        int Radius = 6371;// radius of earth in Km
        double lat1 = startP.latitude;
        double lat2 = endP.latitude;
        double lon1 = startP.longitude;
        double lon2 = endP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}