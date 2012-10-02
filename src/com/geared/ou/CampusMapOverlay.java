package com.geared.ou;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CampusMapOverlay extends ItemizedOverlay<OverlayItem>
{
	private ArrayList<OverlayItem> mOverlays= new ArrayList<OverlayItem>();
	Context mContext;
	
	public CampusMapOverlay(Drawable defaultMarker, Context context)
	{
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		populate();
	}
	
	public CampusMapOverlay(Drawable defaultMarker)
	{
		super(boundCenterBottom(defaultMarker));
	}
	
	public void addOverlay(OverlayItem overlay)
	{
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i)
	{
		return mOverlays.get(i);
	}
	
	public void pop()
	{
		mOverlays.remove(mOverlays.size()-1);
	}
	
	public void removeAllItems()
	{
		mOverlays.clear();
	}
	
	@Override
	public int size()
	{
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
}
