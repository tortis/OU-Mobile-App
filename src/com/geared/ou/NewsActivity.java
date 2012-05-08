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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 *
 * This is a top level activity that displays News that is read from oudaily.com
 * to the user. Currently this is just a framework, and none of the real
 * functionality has been implemented.
 * 
 */
public class NewsActivity extends Activity implements View.OnClickListener {
    private LinearLayout buttonClasses;
    private LinearLayout buttonEmail;
    private LinearLayout layoutContent;
    List items;
    SyndFeed feed;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.news);

        // Get Action Buttons
        buttonClasses = (LinearLayout) findViewById(R.id.classesbutton);
        buttonEmail = (LinearLayout) findViewById(R.id.emailbutton);
        buttonClasses.setOnClickListener(this);
        buttonEmail.setOnClickListener(this);
        layoutContent = (LinearLayout) findViewById(R.id.content);
        
        feed = getMostRecentNews("http://www.oudaily.com/rss/headlines/front/");
        LayoutParams lparam = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        items = feed.getEntries();
        Iterator i = items.iterator();
        addSpacer(layoutContent, Color.BLACK, 1);
        int counter = 0;
        while (i.hasNext()) {
            SyndEntryImpl entry = (SyndEntryImpl) i.next();
            TextView tv = new TextView(this);
            tv.setLayoutParams(lparam);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTextColor(Color.argb(255, 75, 25, 25));
            tv.setTextSize(13);
            tv.setId(counter);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setPadding(10, 6, 10, 6);
            tv.setHorizontalFadingEdgeEnabled(true);
            tv.setFadingEdgeLength(35);
            tv.setSingleLine(true);
            tv.setText(entry.getTitle());
            tv.setOnClickListener(this);
            tv.setClickable(true);
            tv.setBackgroundResource(R.drawable.content_list_button_selector);
            layoutContent.addView(tv);
            addSpacer(layoutContent, Color.BLACK, 1);
            counter++;
        }
    }
    
    public void addSpacer(LinearLayout l, int color, int height) {
        TextView t = new TextView(this);
        t.setWidth(l.getWidth());
        t.setHeight(height);
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
        if (v.getId() == R.id.classesbutton)
        {
            Log.d("OU", "Classes button pressed.");
            Intent myIntent = new Intent(this, ClassesActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        else if (v.getId() == R.id.emailbutton)
        {
            Log.d("OU", "Email button pressed.");
            Intent myIntent = new Intent(this, CampusMapActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        else {
            SyndEntryImpl i = (SyndEntryImpl)items.get(v.getId());
            if (i == null) {
                Log.d("OU", "Could not get the clicked view.");
                return;
            }
            Animation a = expand(layoutContent.getChildAt(1), false);
            a.start();
            Log.d("OU", "Animation supposidely started.");
        }
    }
    
    public static Animation expand(final View v, final boolean expand) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            m.setAccessible(true);
            m.invoke(
                v,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST)
            );
        } catch (Exception e) {
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
        }
        else {
            v.getLayoutParams().height = initialHeight;
        }
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight;
                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }
                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand)
                    v.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(250);
        return a;
    }
}
