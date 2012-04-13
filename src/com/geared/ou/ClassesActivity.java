package com.geared.ou;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geared.ou.ClassesData.Course;
import com.geared.ou.D2LSourceGetter.SGError;


public class ClassesActivity extends Activity implements OnClickListener
{
    private LinearLayout buttonNews;
    private LinearLayout buttonEmail;
    private LinearLayout layoutContent;
    
    private ClassesData classes;
    private String username="";
    private String password="";
    private Activity a;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes);
        a = ClassesActivity.this;
        
        // Get Action Buttons
        buttonNews = (LinearLayout) findViewById(R.id.newsbutton);
        buttonEmail = (LinearLayout) findViewById(R.id.emailbutton);
        layoutContent = (LinearLayout) findViewById(R.id.content);
        buttonNews.setOnClickListener(this);
        buttonEmail.setOnClickListener(this);
        
        SharedPreferences prefs = ((OUApplication)this.getApplication()).getPrefs();
        classes = ((OUApplication)this.getApplication()).getClasses();

        username = prefs.getString("username", "");
        password = prefs.getString("password", "");

        /* If a username or password was not found, kick to Prefs Activity. */
        if (password.isEmpty() || username.isEmpty()) {
            startActivity(new Intent(this, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            if(classes.needUpdate()) {
                displayClassData(false);
                setStatusTextViewToUpdating();
                new update().execute(((OUApplication)this.getApplication()).getSourceGetter());
            }
            else
                displayClassData(false);
        }
            
    }
    
    private class update extends AsyncTask<D2LSourceGetter, Integer, SGError> {
        @Override
        protected SGError doInBackground(D2LSourceGetter... sg) {
            return classes.update(sg[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update percentage
        }

        @Override
        protected void onPostExecute(SGError result) {
            super.onPostExecute(result);
            if (result == SGError.NO_ERROR)
                displayClassData(false);
            else if (result == SGError.NO_CONNECTION) {
                Toast.makeText(a, R.string.failedSourceUpdate, Toast.LENGTH_LONG).show();
                displayClassData(true);
            }
            else if (result == SGError.NO_CREDENTIALS)
                startActivity(new Intent(a, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            else if (result == SGError.NO_DATA)
                Toast.makeText(a, R.string.noCoursesFound, Toast.LENGTH_LONG).show();
            else if (result == SGError.BAD_CREDENTIALS) {
                Toast.makeText(a, R.string.badLogin, Toast.LENGTH_LONG).show();
                displayClassData(true);
            }
            else {
                Toast.makeText(a, R.string.unknownError, Toast.LENGTH_LONG).show();
                displayClassData(true);
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
        // User must have clicked on a course link, and the views id will be the
        // same as the course's id.
        else
        {
            Intent myIntent = new Intent(this, ClassHomeActivity.class);
            myIntent.putExtra("classId", v.getId());
            Log.d("OU", "Put extra classId: " +v.getId());
            startActivity(myIntent);
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
                classes.forceNextUpdate();
                setStatusTextViewToUpdating();
                new update().execute(((OUApplication)this.getApplication()).getSourceGetter());
                break;
        }
        return true;
    }
    
    public void displayClassData(Boolean updateFailed)
    {
        Course c;
        ArrayList<Course> courses = classes.getCourseList();
        layoutContent.removeAllViews();
        if (courses == null)
            return;
        for (Iterator<Course> i = courses.iterator(); i.hasNext();)
        {
            c = i.next();
            TextView t = new TextView(this);
            t.setId(c.getId());
            t.setText(c.getName());
            t.setWidth(layoutContent.getWidth());
            t.setHeight(70);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setTextColor(Color.DKGRAY);
            t.setTextSize(18);
            t.setPadding(10, 0, 10, 0);
            t.setClickable(true);
            t.setHorizontalFadingEdgeEnabled(true);
            t.setFadingEdgeLength(35);
            t.setBackgroundResource(R.drawable.class_button_bg_selector);
            t.setSingleLine(true);
            t.setOnClickListener(this);
            layoutContent.addView(t);

            TextView spacer = new TextView(this);
            spacer.setWidth(layoutContent.getWidth());
            spacer.setHeight(2);
            spacer.setBackgroundColor(Color.BLACK);
            layoutContent.addView(spacer);
         }
         TextView t = new TextView(this);
         if (classes.needUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
         t.setText(getString(R.string.lastUpdateTitle)+" "+classes.getLastSourceUpdate().toLocaleString());
         t.setGravity(Gravity.TOP);
         t.setWidth(layoutContent.getWidth());
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
        TextView updateTV = (TextView)findViewById(R.id.updateTextView);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = ((OUApplication)this.getApplication()).getPrefs();
        
        username = prefs.getString("username", "");
        password = prefs.getString("password", "");

        /* If a username or password was not found, kick to Prefs Activity. */
        if (password.isEmpty() || username.isEmpty()) {
            startActivity(new Intent(this, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
            return;
        }
    }
    
    
}
