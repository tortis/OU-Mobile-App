package com.geared.ou;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.geared.ou.ClassesData.Course;
import com.geared.ou.GradesData.Category;
import com.geared.ou.GradesData.Grade;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class GradesFragment extends SherlockFragment implements OnNavigationListener {
	private LinearLayout layoutContent;
    private int classId;
    private Course course;
    private GradesData grades;
    
    private OUApplication app;
    private Context c;
    private SlidingFragmentActivity a;
    private update updateThread;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	a = (SlidingFragmentActivity)getActivity();
		c = a.getApplicationContext();
		app = (OUApplication) a.getApplication();
		app.setCurrentFragment(OUApplication.FRAGMENT_CONTENT);
		classId = app.getCurrentClass();
		course = app.getClasses().getCourse(classId);
		grades = course.getGrades();
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setTitle(course.getPrefix());
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        	SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(c, R.array.class_nav, R.layout.drop_down_nav_item);
        	ab.setListNavigationCallbacks(mSpinnerAdapter, this);
        	ab.setSelectedNavigationItem(2);
        }
        
        ScrollView scroll = new ScrollView(c);
        layoutContent = new LinearLayout(c);
        layoutContent.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(layoutContent);
        
        /* Pull content from D2L or DB and display. */
        if (grades.needsUpdate()) {
            updateDisplay(false);
            setStatusTextViewToUpdating();
            updateThread = new update();
            updateThread.execute();
        } else {
            updateDisplay(false);
        }
        
        return scroll;
    }
    
    private class update extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... s) {
            return grades.update();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update percentage
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                updateDisplay(false);
            }
            else {
                Log.d("OU", "Update failed?");
                updateDisplay(true);
                Toast.makeText(c, R.string.failedSourceUpdate, Toast.LENGTH_LONG).show();
            }
        }
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
            	grades.forceNextUpdate();
                setStatusTextViewToUpdating();
                updateThread = new update();
                updateThread.execute();
                break;
            default:
            	break;
        }
        return true;
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
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		FragmentManager fragManager = a.getSupportFragmentManager();
		switch(itemPosition)
		{
			case 0:			// Home
				ClassHomeFragment classHomeFragment = new ClassHomeFragment();
				FragmentTransaction fragHomeTrans = fragManager.beginTransaction();
				fragHomeTrans.replace(R.id.top_level_container, classHomeFragment, "main_fragment");
				fragHomeTrans.commit();
				break;
			case 1:			// Content
				ContentFragment contentFragment = new ContentFragment();
				FragmentTransaction fragContentTrans = fragManager.beginTransaction();
				fragContentTrans.replace(R.id.top_level_container, contentFragment, "main_fragment");
				fragContentTrans.commit();
				break;
			case 2:			// Grades
				
				break;
				
			case 3:			// Roster
				RosterFragment rosterFragment = new RosterFragment();
				FragmentTransaction fragRosterTrans = fragManager.beginTransaction();
				fragRosterTrans.replace(R.id.top_level_container, rosterFragment, "main_fragment");
				fragRosterTrans.commit();
				break;
			default:
				break;
		}
		return false;
	}
	
	private void updateDisplay(Boolean updateFailed) {
        layoutContent.removeAllViews();
        ArrayList<Category> g = grades.getGrades();
        if (g == null)
            return;
        
        for (Category cat : g) {
            addSpacer(layoutContent, Color.BLACK, 2);
            TextView t = new TextView(c);
            t.setText(cat.getName()+": "+cat.getScore());
            Log.d("OU", "c.getName(): "+cat.getName());
            t.setWidth(layoutContent.getWidth());
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setTextColor(Color.argb(255, 75, 25, 25));
            t.setTextSize(15);
            t.setTypeface(null, Typeface.BOLD);
            t.setPadding(10, 6, 10, 6);
            t.setHorizontalFadingEdgeEnabled(true);
            t.setFadingEdgeLength(35);
            t.setSingleLine(true);
            layoutContent.addView(t);
            addSpacer(layoutContent, Color.BLACK, 1);
            for (Grade x : cat.getGrades()) {
                addSpacer(layoutContent, Color.BLACK, 1);
                //LinearLayout l = new LinearLayout(this);
                //l.setLayoutParams(new LayoutParams(layoutContent.getWidth(), LayoutParams.WRAP_CONTENT));
                TextView tl = new TextView(c);
                TextView tr = new TextView(c);
                tl.setText(x.getName()+": "+x.getScore());
                tl.setWidth(layoutContent.getWidth());
                tl.setHeight(LayoutParams.WRAP_CONTENT);
                tl.setGravity(Gravity.CENTER_VERTICAL);
                tl.setTextColor(Color.DKGRAY);
                tl.setTextSize(15);
                tl.setPadding(12, 15, 10, 15);
                tl.setHorizontalFadingEdgeEnabled(true);
                tl.setFadingEdgeLength(35);
                tl.setSingleLine(true);
                tr.setText(x.getScore());
                tr.setWidth(layoutContent.getWidth()*2/3);
                tr.setHeight(LayoutParams.WRAP_CONTENT);
                tr.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                tr.setTextColor(Color.DKGRAY);
                tr.setTextSize(15);
                tr.setPadding(12, 15, 10, 15);
                tr.setHorizontalFadingEdgeEnabled(true);
                tr.setFadingEdgeLength(35);
                tr.setSingleLine(true);
                //l.setBackgroundResource(R.drawable.transparent);
                //l.setOrientation(LinearLayout.HORIZONTAL);
                //l.addView(tl);
                //l.addView(tr);
                layoutContent.addView(tl);
            }
        }
        addSpacer(layoutContent, Color.BLACK, 1);
        TextView t = new TextView(c);
         if (grades.needsUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
        t.setText(getString(R.string.lastUpdateTitle)+" "+grades.getLastUpdate().toString());
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
    
    public void addSpacer(LinearLayout l, int color, int height) {
        TextView t = new TextView(c);
        t.setWidth(l.getWidth());
        t.setHeight(height);
        t.setBackgroundColor(color);
        l.addView(t);
    }
}
