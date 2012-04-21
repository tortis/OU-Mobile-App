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
 * Please email me at: thefindley@gmail.com with questions.
 * 
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
 * This is the generic home activity of a course. From this page the user can
 * rech the grades, content, and roster activite. This activity should also
 * display class info/news in the future.
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
