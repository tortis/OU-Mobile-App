package com.geared.ou;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class CampusMapFragment extends SherlockFragment {
	
	private Context c;
	private OUApplication app;
	private SlidingFragmentActivity a;
	private MapView mapView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		a = (SlidingFragmentActivity)getActivity();
		c = a.getApplicationContext();
		app = (OUApplication)a.getApplication();
		mapView = new MapView(c,"0Z4bcHWH4mxxi6O69Ny7vTYFCH9CemuLE9phijg");
		mapView.isClickable();
		mapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mapView.setBuiltInZoomControls(true);
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(new GeoPoint(35211098, -97447894));
        points.add(new GeoPoint(35203866, -97441263));
        setMapBoundsToPois(points,0.0,0.0,mapView);
        
        //Overlays
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker);
        CampusMapOverlay itemizedoverlay = new CampusMapOverlay(drawable,c);
        GeoPoint point = new GeoPoint(35208814,-97442315);
        OverlayItem overlayitem = new OverlayItem(point, "Laissez les bon temps rouler!", "I'm in Louisiana!");

        GeoPoint point2 = new GeoPoint(17385812,78480667);
        OverlayItem overlayitem2 = new OverlayItem(point2, "Namashkaar!", "I'm in Hyderabad, India!");

        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay.addOverlay(overlayitem2);

        mapOverlays.add(itemizedoverlay);
		return mapView;
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
	
	/*@Override
    protected boolean isRouteDisplayed() {
        return false;
    }*/
}
