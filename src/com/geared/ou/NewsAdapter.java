package com.geared.ou;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;

public class NewsAdapter extends BaseAdapter {

	   private LayoutInflater inflater = null;
	   private ArrayList<SyndEntryImpl> news = null;
	   private OnClickListener listener;

	   private class ViewHolder {
		  LinearLayout titleLayout = null;
	      TextView titleTextView = null;
	      TextView dateTextView = null;
	      TextView summaryTextView = null;
	      ImageView moreIcon = null;
	   }

	   public NewsAdapter(Context context, ArrayList<SyndEntryImpl> news, OnClickListener listener) {
	      inflater = LayoutInflater.from(context);
	      this.listener = listener;
	      this.news = news;
	   }

	   public int getCount() {
	      return news.size();
	   }

	   public SyndEntryImpl getItem(int position) {
	      return news.get(position);
	   }

	   public long getItemId(int position) {
	      return position;
	   }

	   public View getView(int position, View convertView, ViewGroup parent) {
	      ViewHolder holder = null;
	      if(convertView == null) {
	    	  holder = new ViewHolder();
	    	  convertView = inflater.inflate(R.layout.news_list_item, null);
	    	  holder.titleLayout = (LinearLayout) convertView.findViewById(R.id.newsTitleLink);
	    	  holder.titleTextView = (TextView) convertView.findViewById(R.id.newsTitleText);
	    	  holder.dateTextView = (TextView) convertView.findViewById(R.id.newsDateText);
	    	  holder.summaryTextView = (TextView) convertView.findViewById(R.id.newsSummaryText);
	    	  holder.moreIcon = (ImageView) convertView.findViewById(R.id.newsMoreIcon);
	    	  convertView.setTag(holder);
	      } else {
	         holder = (ViewHolder) convertView.getTag();
	      }
	      
	      String title = news.get(position).getTitle().trim();
	      title = title.replaceFirst("COLUMN: ", "");
	      title = title.replaceFirst("EDITORIAL: ", "");
	      holder.titleTextView.setText(title);
	      holder.dateTextView.setText(news.get(position).getPublishedDate().toLocaleString());
	      holder.summaryTextView.setText(news.get(position).getDescription().getValue());
	      holder.summaryTextView.setId(position + 200);
	      holder.moreIcon.setImageResource(R.drawable.ic_menu_more);
	      holder.moreIcon.setId(position+100);
	      holder.moreIcon.setOnClickListener(listener);
	      holder.titleLayout.setId(position);
	      holder.titleLayout.setOnClickListener(listener);
	      return convertView;
	   }
}