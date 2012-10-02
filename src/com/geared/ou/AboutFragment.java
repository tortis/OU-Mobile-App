package com.geared.ou;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class AboutFragment extends SherlockFragment {
	
	private OUApplication app;
    private Context c;
    private SlidingFragmentActivity a;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		a = (SlidingFragmentActivity)getActivity();
        c =a.getApplicationContext();
        app = (OUApplication) a.getApplication();
        app.setCurrentFragment(OUApplication.FRAGMENT_ABOUT);
        
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle(R.string.aboutTitle);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
    	TextView test = new TextView(c);
    	test.setText("About....");
    	test.setPadding(9, 5, 9, 5);
    	test.setTextColor(Color.BLACK);
        return test;
    }
}
/*
OU Mobile App
This android app was developed for free by David Findley for use by students at the University of Oklahoma.
It lets users read OU Daily News, search for buildings on the campus map, and gives partial D2L access.  This is not
an official University of Oklahoma application, nor is it sponsored by the University of Oklahoma.

Special Thanks
Design input: Vincent Do, Hoai Nguyen
Art asset contribution: John Dewberry
Testing device: Vincent Do

License
The OU Mobile App by David findley is licensed under a Creative Commons Attribution-NonCommercial 3.0 Unported License.(link)
Based on work at github.com(link).

Libraries Used
This application uses several third-party opensource libraries in order implement it's features.

	Apache HttpClient (link) is a library that lets developers interact with Web servers in a very robust way. OU Mobile App
	uses this library to pull user specific Webpages from OU's D2L Website. This library is licensed under the
	Apache License, version 2.0 (link).
	
	jsoup Html parser(link) is a library for reading and modifying html source files. OU Mobile App uses this 
	library to extract user specific data from D2L Webpages. This library is licensed under the MIT License(link).
	
	ActionBarSherlock(link) is a library that extends the compatibility library in order to facilitate use of 
	the action bar design patter across all versions of Android with a single API. This library is licensed 
	under the Apache License, version 2.0 (link).
	
	SlidingMenu(link) is a library that allows Android apps to easily implement the sliding menu design pattern.
	This library is licensed under Apache License, version 2.0 (link).
	