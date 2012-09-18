package com.geared.ou;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;

public class NewsFragment extends SherlockFragment implements View.OnClickListener {
	
	private LinearLayout layoutContent;
    private OUApplication app;
    private List<?> items;
    SyndFeed feed;
    private Context c;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        c = getActivity().getApplicationContext();
        ScrollView scroll = new ScrollView(c);
        layoutContent = new LinearLayout(c);
        layoutContent.setOrientation(LinearLayout.VERTICAL);
        //layoutContent.setBackgroundColor(Color.argb(255, 255, 253, 208));
        scroll.addView(layoutContent);
        
        app = (OUApplication) getActivity().getApplication();
        feed = app.getFeed();
        if (feed == null) {
            setStatusTextViewToUpdating();
            new Load().execute();
        }
        else {
            updateDisplay();
        }

        return scroll;
    }
	
	private class Load extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... s) {
            feed = getMostRecentNews("http://www.oudaily.com/rss/headlines/front/");
            app.setFeed(feed);
            if (feed == null)
                return false;
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update percentage
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            updateDisplay();
        }
    }
    
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.refreshClasses:
            	setStatusTextViewToUpdating();
                new Load().execute();
                break;
            case R.id.itemPrefs:
                startActivity(new Intent(c, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                break;
            default:
            	break;
        }
        return true;
    }
    
    protected void updateDisplay() {
        layoutContent.removeAllViews();
        LayoutParams lparam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        items = feed.getEntries();
        Log.d("OU", "num items: "+items.size());
        Iterator<?> i = items.iterator();
        addSpacer(layoutContent, Color.BLACK, 1, -1, View.VISIBLE);
        int counter = 0;
        while (i.hasNext()) {
            SyndEntryImpl entry = (SyndEntryImpl) i.next();
            TextView tv = new TextView(c);
            tv.setLayoutParams(lparam);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTextColor(Color.parseColor("#80100e"));
            tv.setTextSize(15);
            tv.setId(counter);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setPadding(10, 12, 12, 10);
            tv.setText(entry.getTitle().trim());
            tv.setOnClickListener(this);
            tv.setClickable(true);
            tv.setBackgroundResource(R.drawable.content_list_button_selector);
            Drawable img = getActivity().getResources().getDrawable(R.drawable.ic_menu_more);
            img.setBounds(0, 0, 48, 48);
            tv.setCompoundDrawables(null, null, img, null);
            layoutContent.addView(tv);
            addSpacer(layoutContent, Color.BLACK, 1, -1, View.VISIBLE);
            TextView tvc = new TextView(c);
            tvc.setLayoutParams(lparam);
            tvc.setGravity(Gravity.CENTER_VERTICAL);
            tvc.setTextColor(Color.argb(255, 75, 25, 25));
            tvc.setTextSize(13);
            tvc.setId(counter+100);
            tvc.setPadding(10, 5, 10, 5);
            tvc.setVisibility(View.GONE);
            tvc.setText(entry.getDescription().getValue());
            layoutContent.addView(tvc);
            addSpacer(layoutContent, Color.BLACK, 1, counter+200, View.GONE);
            counter++;
        }
    }
    
    public void addSpacer(LinearLayout l, int color, int height, int id, int vis) {
        TextView t = new TextView(c);
        t.setWidth(l.getWidth());
        t.setHeight(height);
        t.setId(id);
        t.setVisibility(vis);
        t.setBackgroundColor(color);
        l.addView(t);
    }
    
    protected SyndFeed getMostRecentNews( final String feedUrl )
    {
        try
        {
            return retrieveFeed( feedUrl );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
    
    private SyndFeed retrieveFeed( final String feedUrl ) throws IOException, FeedException, FetcherException
    {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
        return feedFetcher.retrieveFeed( new URL( feedUrl ) );
    }
    
    public void onClick(View v) {
        Log.d("OU", "Something was clicked");
        SyndEntryImpl i = (SyndEntryImpl)items.get(v.getId());
        if (i == null) {
            Log.d("OU", "Could not get the clicked view.");
            return;
        }
        TextView tv = (TextView)layoutContent.findViewById(v.getId()+100);
        TextView sp = (TextView)layoutContent.findViewById(v.getId()+200);
        if (tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
        }
        else {
            tv.setVisibility(View.GONE);
            sp.setVisibility(View.GONE);
        }
    }
    
    private void setStatusTextViewToUpdating() {
        final AnimationDrawable img = new AnimationDrawable();
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading1), 150);
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading2), 150);
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading3), 150);
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading4), 150);
        img.setBounds(0, 0, 30, 30);
        img.setOneShot(false);
        TextView updateTV = (TextView)layoutContent.findViewById(R.id.updateTextView);
        if (updateTV == null) {
            updateTV = new TextView(c);
            updateTV.setText(" Updating...");
            updateTV.setCompoundDrawables(img, null, null, null);
            updateTV.setGravity(Gravity.TOP);
            updateTV.setWidth(layoutContent.getWidth());
            updateTV.setPadding(15, 3, 3, 3);
            updateTV.setTextColor(Color.BLACK);
            updateTV.setTextSize(13);
            updateTV.setId(R.id.updateTextView);
            layoutContent.addView(updateTV);
        } else {
            updateTV.setCompoundDrawables(img, null, null, null);
            updateTV.setText(" Updating...");
            updateTV.setTextColor(Color.BLACK);
        }
        updateTV.post(new Runnable() {
            public void run() {
                img.start();
            }
        });
    }

}
