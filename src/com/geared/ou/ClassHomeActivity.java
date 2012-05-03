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
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.geared.ou.ClassesData.Course;

/**
 * 
 * This is the generic home activity of a course. From this page the user can
 * rech the grades, content, and roster activite. This activity should also
 * display class info/news in the future.
 */

public class ClassHomeActivity extends Activity {
    private TextView titleBar;
    int classId;
    Course course;
    ClassHomeData news;
    LinearLayout layoutContent;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.class_home);
        
        /* Get context. */
        classId = getIntent().getIntExtra("classId", 0);
        OUApplication app = (OUApplication) this.getApplication();
        course = app.getClasses().getCourse(classId);
        news = course.getNews();
        
        /* XML Poop */
        titleBar = (TextView) findViewById(R.id.classHomeTitle);
        titleBar.setText(course.getName()+" ("+course.getPrefix()+")");
        layoutContent = (LinearLayout) findViewById(R.id.content);
        
        /* Pull news from D2L or database and display */
        if (news.needsUpdate()) {
            updateDisplay(false);
            setStatusTextViewToUpdating();
            new update().execute();
        } else {
            Log.d("OU", "Doesn't need update. displaying.");
            updateDisplay(false);
        }
    }
    
    private class update extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... s) {
            return news.update();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update percentage
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            setStatusTextViewToNormal();
            if (result) {
                updateDisplay(false);
            }
            else {
                Log.d("OU", "Update failed?");
                updateDisplay(true);
                Toast.makeText(ClassHomeActivity.this, R.string.failedSourceUpdate, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public void onClick(View v) {
        if (v.getId() == R.id.newsbutton)
        {
            Log.d("OU", "News button pressed.");
            Intent myIntent = new Intent(this, NewsActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        else if (v.getId() == R.id.emailbutton)
        {
            Log.d("OU", "Email button pressed.");
            Intent myIntent = new Intent(this, EmailActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(myIntent);
        }
    }
    
    public void addSpacer(LinearLayout l, int color, int height) {
        TextView t = new TextView(this);
        t.setWidth(l.getWidth());
        t.setHeight(height);
        t.setBackgroundColor(color);
        l.addView(t);
    }
    
    private void updateDisplay(Boolean updateFailed) {
        layoutContent.removeViews(1, layoutContent.getChildCount()-1);
        for (ClassHomeData.NewsItem n: news.getNewsItems()) {
            addSpacer(layoutContent, Color.BLACK, 2);
            
            /* Title of n Item */
            LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            TextView t = new TextView(this);
            t.setLayoutParams(lparams);
            t.setText(n.getName());
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setTextColor(Color.DKGRAY);
            t.setTextSize(13);
            t.setTypeface(null, Typeface.BOLD);
            t.setPadding(10, 4, 10, 4);
            t.setHorizontalFadingEdgeEnabled(true);
            t.setFadingEdgeLength(35);
            t.setSingleLine(true);
            layoutContent.addView(t);
            
            addSpacer(layoutContent, Color.BLACK, 2);
            
            /* Content of news item */
            TextView tv = new TextView(this);
            tv.setLayoutParams(lparams);
            tv.setText(n.getContent());
            tv.setGravity(Gravity.TOP | Gravity.LEFT);
            tv.setTextColor(Color.argb(255, 75, 25, 25));
            tv.setTextSize(11);
            tv.setPadding(10, 5, 10, 5);
            layoutContent.addView(tv);
        }
        addSpacer(layoutContent, Color.BLACK, 1);
        TextView t = new TextView(this);
         if (news.needsUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
        LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        t.setText(getString(R.string.lastUpdateTitle)+" "+news.getLastUpdate().toLocaleString());
        t.setGravity(Gravity.TOP);
        t.setLayoutParams(lparams);
        t.setPadding(7, 3, 3, 3);
        t.setTextColor(Color.GRAY);
        t.setTextSize(12);
        t.setId(R.id.updateTextView);
        layoutContent.addView(t);
    }
    
    protected void setStatusTextViewToUpdating() {
        final AnimationDrawable img = new AnimationDrawable();
        img.addFrame(getResources().getDrawable(R.drawable.loading1), 150);
        img.addFrame(getResources().getDrawable(R.drawable.loading2), 150);
        img.addFrame(getResources().getDrawable(R.drawable.loading3), 150);
        img.addFrame(getResources().getDrawable(R.drawable.loading4), 150);
        img.setBounds(0, 0, 30, 30);
        img.setOneShot(false);
        final AnimationDrawable img2 = new AnimationDrawable();
        img2.addFrame(getResources().getDrawable(R.drawable.loading1), 150);
        img2.addFrame(getResources().getDrawable(R.drawable.loading2), 150);
        img2.addFrame(getResources().getDrawable(R.drawable.loading3), 150);
        img2.addFrame(getResources().getDrawable(R.drawable.loading4), 150);
        img2.setBounds(0, 0, 30, 30);
        img2.setOneShot(false);
        TextView updateTV = (TextView)findViewById(R.id.updateTextView);
        TextView TitleTV = (TextView) findViewById(R.id.classHomeTitle);
        TitleTV.setCompoundDrawables(img2, null, null, null);
        if (updateTV == null) {
            updateTV = new TextView(this);
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
            @Override
            public void run() {
                img.start();
            }
        });
        TitleTV.post(new Runnable() {
            @Override public void run() {
                img2.start();
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemPrefs:
                startActivity(new Intent(this, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                break;
            case R.id.refreshClasses:
                news.forceNextUpdate();
                setStatusTextViewToUpdating();
                new update().execute();
                break;
        }
        return true;
    }
    
    protected void setStatusTextViewToNormal() {
        TextView TitleTV = (TextView) findViewById(R.id.classHomeTitle);
        TitleTV.setCompoundDrawables(null, null, null, null);
    }
    
    public void goToGrades(View v)
    {
        Intent myIntent = new Intent(this, GradesActivity.class);
        myIntent.putExtra("classId", classId);
        startActivity(myIntent);
    }
    
    public void goToContent(View v)
    {
        Intent myIntent = new Intent(this, ContentActivity.class);
        myIntent.putExtra("classId", classId);
        startActivity(myIntent);
    }
    
    public void goToRoster(View v)
    {
        Intent myIntent = new Intent(this, RosterActivity.class);
        myIntent.putExtra("classId", classId);
        startActivity(myIntent);
    }
}
