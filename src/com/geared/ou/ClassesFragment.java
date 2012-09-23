package com.geared.ou;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.geared.ou.ClassesData.Course;
import com.geared.ou.D2LSourceGetter.SGError;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ClassesFragment extends SherlockFragment implements View.OnClickListener {
	
	private LinearLayout layoutContent;
    private OUApplication app;
    private Context c;
    private SlidingFragmentActivity a;
    private float scale;
    private int spacerHeight;
    
    private ClassesData classes;
    private String username="";
    private String password="";
    
    private update updateThread;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		a = (SlidingFragmentActivity)getActivity();
		c = a.getApplicationContext();
		app = (OUApplication) a.getApplication();
		app.setCurrentFragment(OUApplication.FRAGMENT_CLASSES);
		classes = app.getClasses();
        setHasOptionsMenu(true);
        setRetainInstance(true);
        
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle("Courses: "+classes.getCurrentSemesterString());
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
        scale = c.getResources().getDisplayMetrics().density;
        spacerHeight = (int)(2 * scale + 0.5f);
        
        ScrollView scroll = new ScrollView(c);
        layoutContent = new LinearLayout(c);
        layoutContent.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(layoutContent);
        
        /* SharedPrefs Poop */
        SharedPreferences prefs = app.getPrefs();
        username = prefs.getString("username", "");
        password = prefs.getString("password", "");
        
        /* If a username or password was not found, kick to Prefs Activity. */
        if (password.isEmpty() || username.isEmpty()) {
            startActivity(new Intent(c, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Toast.makeText(c, R.string.msgSetupPrefs, Toast.LENGTH_SHORT).show();
        }
        else
        {
            /* Pull classes from D2L or DB and display them. */
            if(classes.needUpdate()) {
                displayClassData(false);
                setStatusTextViewToUpdating();
                updateThread = new update();
                updateThread.execute(app.getSourceGetter());
            }
            else
                displayClassData(false);
        }

        return scroll;
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
		// Actually want to do a fragment transaction here...
		app.setCurrentClass(v.getId());
		FragmentManager fragmentManager = a.getSupportFragmentManager();
		ClassHomeFragment classHomeFragment = new ClassHomeFragment();
		FragmentTransaction fragClassHomeTrans = fragmentManager.beginTransaction();
		fragClassHomeTrans.replace(R.id.top_level_container, classHomeFragment, "main_fragment");
		//fragClassHomeTrans.addToBackStack("classesPage");
		fragClassHomeTrans.commit();
    }
	
	@Override
	public void onDetach() {
		if (updateThread != null)
		{
			if (updateThread.getStatus() == AsyncTask.Status.RUNNING)
				updateThread.cancel(false);
		}
		super.onDetach();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.classes_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.refreshClasses:
            	classes.forceNextUpdate();
                setStatusTextViewToUpdating();
                updateThread = new update();
                updateThread.execute(app.getSourceGetter());
                break;
            default:
            	break;
        }
        return true;
    }
	
    public void displayClassData(Boolean updateFailed)
    {
        Course course;
        ArrayList<Course> courses = classes.getCourseList();
        layoutContent.removeAllViews();
        if (courses == null)
            return;
        for (Iterator<Course> i = courses.iterator(); i.hasNext();)
        {
            course = i.next();
            TextView t = new TextView(c);
            t.setId(course.getId());
            t.setText(course.getName());
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

            TextView spacer = new TextView(c);
            spacer.setWidth(layoutContent.getWidth());
            spacer.setHeight(spacerHeight);
            spacer.setBackgroundColor(Color.LTGRAY);
            layoutContent.addView(spacer);
         }
         TextView t = new TextView(c);
         if (classes.needUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
         t.setText(getString(R.string.lastUpdateTitle)+" "+classes.getLastSourceUpdate().toString());
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
        img.addFrame(a.getResources().getDrawable(R.drawable.loading1), 150);
        img.addFrame(a.getResources().getDrawable(R.drawable.loading2), 150);
        img.addFrame(a.getResources().getDrawable(R.drawable.loading3), 150);
        img.addFrame(a.getResources().getDrawable(R.drawable.loading4), 150);
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
