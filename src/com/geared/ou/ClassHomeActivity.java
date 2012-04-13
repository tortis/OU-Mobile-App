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
import android.widget.TextView;

import com.geared.ou.ClassesData.Course;

/**
 *
 * @author David
 */
public class ClassHomeActivity extends Activity {
    private TextView titleBar;
    int classId;
    Course course;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.class_home);
        
        
        classId = getIntent().getIntExtra("classId", 0);
        Log.d("OU", "get classid: "+classId);
        OUApplication app = (OUApplication) this.getApplication();
        course = app.getClasses().getCourse(classId);
        
        titleBar = (TextView) findViewById(R.id.classHomeTitle);
        titleBar.setText(course.getName()+" ("+course.getPrefix()+")");
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
    }
    
    public void goToGrades(View v)
    {
        Intent myIntent = new Intent(this, GradesActivity.class);
        myIntent.putExtra("classId", classId);
        startActivity(myIntent);
    }
    
    public void goToContent(View v)
    {
        Intent myIntent = new Intent(this, ContentActivity.class);
        myIntent.putExtra("classId", classId);
        startActivity(myIntent);
    }
    
    public void goToRoster(View v)
    {
        
    }
}
