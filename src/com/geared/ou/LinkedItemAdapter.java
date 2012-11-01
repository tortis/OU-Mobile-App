package com.geared.ou;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LinkedItemAdapter extends BaseAdapter {

	   private OnClickListener listener;
	   private ArrayList<ListItem> items;
	   private Context c;

	   private class ViewHolder {
	      TextView nameTextView = null;
	   }
	   

	   public LinkedItemAdapter(Context context, ArrayList<ListItem> items, OnClickListener listener) {
	      c = context;
	      this.listener = listener;
	      this.items = items;
	   }

	   public int getCount() {
	      return items.size();
	   }

	   public ListItem getItem(int position) {
	      return items.get(position);
	   }

	   public long getItemId(int position) {
	      return position;
	   }

	   public View getView(int position, View convertView, ViewGroup parent) {
	      ViewHolder holder = null;
    	  holder = new ViewHolder();
    	  TextView t = new TextView(c);
    	  t.setText(items.get(position).name);
    	  t.setId(position);
    	  t.setClickable(true);
    	  t.setOnClickListener(listener);
    	  t.setPadding(10, 20, 10, 20);
    	  t.setTextSize(17);
    	  t.setTextColor(Color.BLACK);
    	  t.setBackgroundResource(R.drawable.content_list_button_selector);
    	  holder.nameTextView = t;
	      return holder.nameTextView;
	   }
}