package com.geared.ou;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CampusLocations {
	private ArrayList<OverlayItem> mLocations;
	
	public CampusLocations()
	{
		mLocations = new ArrayList<OverlayItem>(150);
	}
	
	public boolean loadLocations(SQLiteDatabase db)
	{
		mLocations.clear();
        Cursor result = db.rawQuery("select * from map_data", null);
        if (result == null)
        	return false;
        while(result.moveToNext())
        {
        	String name = result.getString(result.getColumnIndex(DbHelper.C_MD_NAME));
        	String description = result.getString(result.getColumnIndex(DbHelper.C_MD_DESC));
        	int x = result.getInt(result.getColumnIndex(DbHelper.C_MD_X));
        	int y = result.getInt(result.getColumnIndex(DbHelper.C_MD_Y));
        	GeoPoint point = new GeoPoint(x,y);
        	mLocations.add(new OverlayItem(point, name, description));
        }
        db.close();
        Log.d("OU", mLocations.size()+" locations loaded.");
		return true;
	}
	
	public ArrayList<OverlayItem> getAllLocations()
	{
		return mLocations;
	}
	
	public OverlayItem getLocationById(int id)
	{
		return mLocations.get(id);
	}
}
