/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author David
 */
public class EmailActivity extends Activity implements OnClickListener {
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
