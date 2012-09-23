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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingMapActivity;

/**
 *
 * This is a top level activity that displays News that is read from oudaily.com
 * to the user. Currently this is just a framework, and none of the real
 * functionality has been implemented.
 * 
 */
public class CampusMapActivity extends SlidingMapActivity {
	
	private MapView mapView;
	OUApplication app;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        app = (OUApplication)getApplication();
        
        setContentView(R.layout.map);
        setBehindContentView(R.layout.side_nav);
        
        ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle("Map");
        	ab.setDisplayHomeAsUpEnabled(true);
        }
        
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindWidth(350);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(new GeoPoint(35211098, -97447894));
        points.add(new GeoPoint(35203866, -97441263));
        setMapBoundsToPois(points,0.0,0.0,mapView);
        
        //Overlays
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker);
        CampusMapOverlay itemizedoverlay = new CampusMapOverlay(drawable,this);
        GeoPoint point = new GeoPoint(35208814,-97442315);
        OverlayItem overlayitem = new OverlayItem(point, "Laissez les bon temps rouler!", "I'm in Louisiana!");

        GeoPoint point2 = new GeoPoint(17385812,78480667);
        OverlayItem overlayitem2 = new OverlayItem(point2, "Namashkaar!", "I'm in Hyderabad, India!");

        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay.addOverlay(overlayitem2);

        mapOverlays.add(itemizedoverlay);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case android.R.id.home:
        		toggle();
        		break;
        	default:
            	break;
        }
        return super.onOptionsItemSelected(item);
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
	
	public void sideNavItemSelected(View v)
    {
    	switch(v.getId())
    	{
    		case R.id.news_button:
    			app.setCurrentFragment(OUApplication.FRAGMENT_NEWS);
    			startActivity(new Intent(this, NewsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
    			break;
    		case R.id.classes_button:
    			app.setCurrentFragment(OUApplication.FRAGMENT_CLASSES);
    			startActivity(new Intent(this, NewsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
    			break;
    		case R.id.map_button:
    			startActivity(new Intent(this, CampusMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
    			break;
			default:
				break;
    	}
    	toggle();
    }
       
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
}
