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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 *
 * This is a top level activity that displays News that is read from oudaily.com
 * to the user. Currently this is just a framework, and none of the real
 * functionality has been implemented.
 * 
 */
public class NewsActivity extends SlidingFragmentActivity {
	
    FragmentManager fragmentManager;
    NewsFragment newsFragment;
    ClassesFragment classesFragment;
    Fragment mapFragment;
    OUApplication app;
    
    LinearLayout tlc;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        app = (OUApplication)getApplication();
        
        setContentView(R.layout.news);
        setBehindContentView(R.layout.side_nav);
        tlc = (LinearLayout)findViewById(R.id.top_level_container);
        
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindWidth(300);
        
        fragmentManager = getSupportFragmentManager();
        newsFragment = new NewsFragment();
        
        if (fragmentManager.findFragmentByTag("main_fragment") == null)
        {
        	switch(app.getCurrentFragment())
        	{
        		case OUApplication.FRAGMENT_NEWS:
        			FragmentTransaction fragNewsTrans = fragmentManager.beginTransaction();
        			fragNewsTrans.add(R.id.top_level_container, newsFragment, "main_fragment");
        			fragNewsTrans.commit();
        			break;
        		case OUApplication.FRAGMENT_CLASSES:
        			if (classesFragment == null)
        				classesFragment = new ClassesFragment();
        			FragmentTransaction fragClassesTrans = fragmentManager.beginTransaction();
        			fragClassesTrans.add(R.id.top_level_container, classesFragment, "main_fragment");
        			fragClassesTrans.commit();
        			break;
    			default:
    				FragmentTransaction fragDefaultTrans = fragmentManager.beginTransaction();
    				fragDefaultTrans.add(R.id.top_level_container, newsFragment, "main_fragment");
    				fragDefaultTrans.commit();
    				break;
        	}
        }
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case android.R.id.home:
        		Log.d("OU", "Up button pressed");
        		toggle();
        		break;
        	case R.id.itemPrefs:
        		startActivity(new Intent(this, PrefsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void sideNavItemSelected(View v)
    {
    	switch(v.getId())
    	{
    		case R.id.news_button:
    			app.setCurrentFragment(OUApplication.FRAGMENT_NEWS);
    			FragmentTransaction fragNewsTrans = fragmentManager.beginTransaction();
    			fragNewsTrans.replace(R.id.top_level_container, newsFragment, "main_fragment");
    			fragNewsTrans.commit();
    			break;
    		case R.id.classes_button:
    			if (classesFragment == null)
    				classesFragment = new ClassesFragment();
    			app.setCurrentFragment(OUApplication.FRAGMENT_CLASSES);
    			FragmentTransaction fragClassesTrans = fragmentManager.beginTransaction();
    			fragClassesTrans.replace(R.id.top_level_container, classesFragment, "main_fragment");
    			fragClassesTrans.commit();
    			break;
    		case R.id.map_button:
    			startActivity(new Intent(this, CampusMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
    			break;
			default:
				break;
    	}
    	toggle();
    }

}
