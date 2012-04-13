/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geared.ou;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class NewsActivity extends MapActivity {
    private LinearLayout buttonClasses;
    private LinearLayout buttonEmail;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.news);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(new GeoPoint(35211098, -97447894));
        points.add(new GeoPoint(35203866, -97441263));
        setMapBoundsToPois(points,0.0,0.0,mapView);
        
        
        // Get Action Buttons
        //buttonClasses = (LinearLayout) findViewById(R.id.classesbutton);
        //buttonEmail = (LinearLayout) findViewById(R.id.emailbutton);
        //buttonClasses.setOnClickListener(this);
        //buttonEmail.setOnClickListener(this);
    }
    
    public void setMapBoundsToPois(List<GeoPoint> items, double hpadding, double vpadding, MapView mv) {
        MapController mapController = mv.getController();
        // If there is only on one result
        // directly animate to that location

        if (items.size() == 1) { // animate to the location
            mapController.animateTo(items.get(0));
        } else {
            // find the lat, lon span
            int minLatitude = Integer.MAX_VALUE;
            int maxLatitude = Integer.MIN_VALUE;
            int minLongitude = Integer.MAX_VALUE;
            int maxLongitude = Integer.MIN_VALUE;

            // Find the boundaries of the item set
            for (GeoPoint item : items) {
                int lat = item.getLatitudeE6(); int lon = item.getLongitudeE6();

                maxLatitude = Math.max(lat, maxLatitude);
                minLatitude = Math.min(lat, minLatitude);
                maxLongitude = Math.max(lon, maxLongitude);
                minLongitude = Math.min(lon, minLongitude);
            }

            // leave some padding from corners
            // such as 0.1 for hpadding and 0.2 for vpadding
            maxLatitude = maxLatitude + (int)((maxLatitude-minLatitude)*hpadding);
            minLatitude = minLatitude - (int)((maxLatitude-minLatitude)*hpadding);

            maxLongitude = maxLongitude + (int)((maxLongitude-minLongitude)*vpadding);
            minLongitude = minLongitude - (int)((maxLongitude-minLongitude)*vpadding);

            // Calculate the lat, lon spans from the given pois and zoom
            mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math
    .abs(maxLongitude - minLongitude));

            // Animate to the center of the cluster of points
            mapController.animateTo(new GeoPoint(
                  (maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
        }
    }

    /*public void onClick(View v) {
        if (v.getId() == R.id.classesbutton)
        {
            Log.d("OU", "Classes button pressed.");
            Intent myIntent = new Intent(this, ClassesActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        if (v.getId() == R.id.emailbutton)
        {
            Log.d("OU", "Email button pressed.");
            Intent myIntent = new Intent(this, EmailActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
    }*/
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
}
