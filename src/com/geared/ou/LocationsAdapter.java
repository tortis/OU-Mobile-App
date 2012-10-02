package com.geared.ou;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;

public class LocationsAdapter extends BaseAdapter {
	private LayoutInflater inflater = null;
	   private ArrayList<OverlayItem> locations = null;
	   private OnClickListener listener;

	   private class ViewHolder {
	      TextView locationTextView = null;
	   }

	   public LocationsAdapter(Context context, CampusLocations campusLocations, OnClickListener listener) {
	      inflater = LayoutInflater.from(context);
	      this.listener = listener;
	      this.locations = campusLocations.getFilteredLocations();
	   }

	   public int getCount() {
	      return locations.size();
	   }

	   public OverlayItem getItem(int position) {
	      return locations.get(position);
	   }

	   public long getItemId(int position) {
	      return position;
	   }

	   public View getView(int position, View convertView, ViewGroup parent) {
	      ViewHolder holder = null;
	      if(convertView == null) {
	    	  holder = new ViewHolder();
	    	  convertView = inflater.inflate(R.layout.location_item, null);
	    	  holder.locationTextView = (TextView) convertView.findViewById(R.id.locationText);
	    	  convertView.setTag(holder);
	      } else {
	         holder = (ViewHolder) convertView.getTag();
	      }
	      holder.locationTextView.setText(locations.get(position).getTitle());
	      holder.locationTextView.setId(position);
	      holder.locationTextView.setOnClickListener(listener);
	      return convertView;
	   }
	}