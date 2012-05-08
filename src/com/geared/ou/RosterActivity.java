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
import java.util.ArrayList;

/**
 *
 * The RosterActivity is accessed from the ClassHomeActivity, and displays all
 * of the students and teachers for the selected class from D2L. It is supported by the 
 * RosterData class. When calling an Intent for this class it must be passed an
 * integer id specifying which class. (note: this can only pull grades if the 
 * given class ID belongs to the user that is currently logged in).
 * 
 */

public class RosterActivity extends Activity {
    private TextView titleBar;
    private int classId;
    protected Course course;
    private LinearLayout layoutContent;
    protected OUApplication app;
    private RosterData roster;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.roster);
        
        /* Get the context */
        classId = getIntent().getIntExtra("classId", 0); // The class id that was passed in the intent.
        app = (OUApplication) this.getApplication(); // Get the Application object.
        course = app.getClasses().getCourse(classId); // From the classes list get this course.
        roster = course.getRoster(); //From the course get the roster object.. phew
        
        /* Modify XML crap */
        titleBar = (TextView) findViewById(R.id.classHomeTitle);
        titleBar.setText(course.getName()+" ("+course.getPrefix()+")");
        layoutContent = (LinearLayout) findViewById(R.id.content);
        
        /* Pull grades data from D2L or database and then display it. */
        if (roster.needsUpdate()) {
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
            return roster.update();
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
                Toast.makeText(RosterActivity.this, R.string.failedSourceUpdate, Toast.LENGTH_LONG).show();
            }
        }
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
                roster.forceNextUpdate();
                setStatusTextViewToUpdating();
                new update().execute();
                break;
        }
        return true;
    }
    
    
    private void updateDisplay(Boolean updateFailed) {
        // Remove all elements from the content layout, except the first one.
        layoutContent.removeViews(1, layoutContent.getChildCount()-1);
        ArrayList<RosterData.Person> people = roster.getRoster();
        if (people == null)
            return;
        
        for (RosterData.Person p : people) {
            addSpacer(layoutContent, Color.BLACK, 1);
            TextView t = new TextView(this);
            LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            t.setText(p.getFirstName()+" "+p.getLastName());
            t.setLayoutParams(lparams);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setTextColor(Color.argb(255, 75, 25, 25));
            t.setTextSize(15);
            if (p.getRole().equals("Instructor"))
                t.setBackgroundColor(R.drawable.transparent_gold);
            t.setTypeface(null, Typeface.BOLD);
            t.setPadding(10, 6, 10, 6);
            t.setHorizontalFadingEdgeEnabled(true);
            t.setFadingEdgeLength(35);
            t.setSingleLine(true);
            layoutContent.addView(t);
        }
        addSpacer(layoutContent, Color.BLACK, 1);
        TextView t = new TextView(this);
        LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        t.setLayoutParams(lparams);
         if (roster.needsUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
        t.setText(getString(R.string.lastUpdateTitle)+" "+roster.getLastUpdate().toLocaleString());
        t.setGravity(Gravity.TOP);
        t.setPadding(7, 3, 3, 3);
        t.setTextColor(Color.GRAY);
        t.setTextSize(13);
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
    
    protected void setStatusTextViewToNormal() {
        TextView TitleTV = (TextView) findViewById(R.id.classHomeTitle);
        TitleTV.setCompoundDrawables(null, null, null, null);
    }
    
    public void addSpacer(LinearLayout l, int color, int height) {
        TextView t = new TextView(this);
        t.setWidth(l.getWidth());
        t.setHeight(height);
        t.setBackgroundColor(color);
        l.addView(t);
    }
    
    public void goToMap(View v)
    {
        Intent myIntent = new Intent(this, CampusMapActivity.class);
        startActivity(myIntent);
    }
    
    public void goToNews(View v)
    {
        Intent myIntent = new Intent(this, NewsActivity.class);
        startActivity(myIntent);
    }
}
