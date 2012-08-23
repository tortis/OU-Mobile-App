/**
 *
 * @author David Findley (ThinksInBits)
 * 
 * The source for this application may be found in its entirety at 
 * https://github.com/ThinksInBits/OU-Mobile-App
 * 
 * This application is published on the Google Play Store under
 * the title: OU Mobile Alpha:
 * https://play.google.com/store/apps/details?id=com.geared.ou
 * 
 * If you want to follow the official development of this application
 * then check out my Trello board for the project at:
 * https://trello.com/board/ou-app/4f1f697a28390abb75008a97
 * 
 * Please email me at: thefindley@gmail.com with questions.
 * 
 */

package com.geared.ou;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/**
 *
 * This is a top level activity that displays News that is read from oudaily.com
 * to the user. Currently this is just a framework, and none of the real
 * functionality has been implemented.
 * 
 */
public class CampusMapActivity extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.map);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(new GeoPoint(35211098, -97447894));
        points.add(new GeoPoint(35203866, -97441263));
        setMapBoundsToPois(points,0.0,0.0,mapView);
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
    
    public void gotoClasses(View v) {
        Intent myIntent = new Intent(this, ClassesActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }
    
    public void gotoNews(View v) {
        Intent myIntent = new Intent(this, NewsActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
}
