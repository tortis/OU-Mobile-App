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

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
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
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.news);
        setBehindContentView(R.layout.side_nav);
        
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindWidth(300);
        
        ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
        	Log.d("OU", "ab not null");
        	ab.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.crimson));
        	ab.setTitle("News");
        	ab.setDisplayHomeAsUpEnabled(true);
        }
        
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentById(R.id.main_fragment);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.main_fragment, newFragment);
		fragmentTransaction.addToBackStack(null);

		// Commit the transaction
		fragmentTransaction.commit();*/
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.classes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case android.R.id.home:
        		Log.d("OU", "Up button pressed");
        		toggle();
        		break;
            default:
            	break;
        }
        return true;
    }
    
    public void sideNavItemSelected(View v)
    {
    	toggle();
    }

}
