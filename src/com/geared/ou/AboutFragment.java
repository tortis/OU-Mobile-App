package com.geared.ou;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/** This is the fragment that displays the application info, such as licensing,
 * special thanks, and version.
 * @author David Findley
 *
 */
public class AboutFragment extends SherlockFragment {
	
	/** The Application object, used to set persistant data.
	 */
	private OUApplication app;
	
	/** The SlidingFragmentActivity in which this fragment
	 * is displayed. This is mostly used to get a context.
	 */
    private SlidingFragmentActivity a;
    
    /** The View that contains the content for this fragment.
     */
    private View mainView;
	
    /** In this method, the ActionBarSherlock ActionBar is setup, persistent 
     * application data is retrieved, the layout is loaded, and setup to
     * interpret html links.
     */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		/* Store any easily accessible reference to the activity.*/
		a = (SlidingFragmentActivity)getActivity();
		
		/* Use the activity to get a reference to the application object. */
        app = (OUApplication) a.getApplication();
        
        /* Every fragment should set the current fragment to itself in the
         * application object.
         */
        app.setCurrentFragment(OUApplication.FRAGMENT_ABOUT);
        
        /* Setup the action bar for this fragment. */
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle(R.string.aboutTitle);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
        /* Inflate the layout from resource.*/
        mainView = inflater.inflate(R.layout.about, container, false);
        
        /* Get the version TextView and set it to the current app version. */
    	TextView version = (TextView) mainView.findViewById(R.id.versionText);
    	PackageInfo pInfo;
		try {
			pInfo = a.getPackageManager().getPackageInfo(a.getPackageName(), 0);
			version.setText("version: "+pInfo.versionName);
		} catch (NameNotFoundException e) {
			version.setText("version: ???");
			e.printStackTrace();
		}
		
		/* Setup the TextViews so that hyperlinks are clickable. */
		TextView aboutLicenseContent = (TextView) mainView.findViewById(R.id.aboutLicenseContent);
		aboutLicenseContent.setText(a.getResources().getText(R.string.aboutLicenseContent));
		aboutLicenseContent.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView aboutLibsContent = (TextView) mainView.findViewById(R.id.aboutLibsContent);
		aboutLibsContent.setText(a.getResources().getText(R.string.aboutLibsContent));
		aboutLibsContent.setMovementMethod(LinkMovementMethod.getInstance());
		
        return mainView;
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
	the action bar design pattern across all versions of Android with a single API. This library is licensed 
	under the Apache License, version 2.0 (link).
	
	SlidingMenu(link) is a library that allows Android apps to easily implement the sliding menu design pattern.
	This library is licensed under Apache License, version 2.0 (link).
	*/