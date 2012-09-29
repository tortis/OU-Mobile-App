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
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class RosterFragment extends SherlockFragment implements OnNavigationListener {
	private LinearLayout layoutContent;
    private int classId;
    private Course course;
    private RosterData roster;
    
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
		roster = course.getRoster();
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
        	ab.setSelectedNavigationItem(3);
        }
        
        ScrollView scroll = new ScrollView(c);
        layoutContent = new LinearLayout(c);
        layoutContent.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(layoutContent);
        
        /* Pull content from D2L or DB and display. */
        if (roster.needsUpdate()) {
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
    
    private void updateDisplay(Boolean updateFailed) {
        layoutContent.removeAllViews();
        ArrayList<RosterData.Person> people = roster.getRoster();
        if (people == null)
            return;
        
        for (RosterData.Person p : people) {
            addSpacer(layoutContent, Color.BLACK, 1);
            TextView t = new TextView(c);
            LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
        TextView t = new TextView(c);
        LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        t.setLayoutParams(lparams);
         if (roster.needsUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
        t.setText(getString(R.string.lastUpdateTitle)+" "+roster.getLastUpdate().toString());
        t.setGravity(Gravity.TOP);
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
    
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.classes_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.refreshClasses:
            	roster.forceNextUpdate();
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
				GradesFragment gradesFragment = new GradesFragment();
				FragmentTransaction fragGradesTrans = fragManager.beginTransaction();
				fragGradesTrans.replace(R.id.top_level_container, gradesFragment, "main_fragment");
				fragGradesTrans.commit();
				break;
				
			case 3:			// Roster
				
				break;
			default:
				break;
		}
		return false;
	}
}
