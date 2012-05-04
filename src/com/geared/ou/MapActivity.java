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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 *
 * This is a top level Activity that would pull the users OU exchange email.
 * This is a tentatively planned release feature, but it is not included in
 * Beta features.
 * 
 */
public class MapActivity extends Activity implements OnClickListener {
    private LinearLayout buttonClasses;
    private LinearLayout buttonNews;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.email);
        
        
        // Get Action Buttons
        buttonClasses = (LinearLayout) findViewById(R.id.classesbutton);
        buttonNews = (LinearLayout) findViewById(R.id.newsbutton);
        buttonClasses.setOnClickListener(this);
        buttonNews.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.classesbutton)
        {
            Log.d("OU", "Classes button pressed.");
            Intent myIntent = new Intent(this, ClassesActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        if (v.getId() == R.id.newsbutton)
        {
            Log.d("OU", "News button pressed.");
            Intent myIntent = new Intent(this, NewsActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
    }
}
