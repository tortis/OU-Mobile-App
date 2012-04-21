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

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

/**
 * 
 * Most of the work done by this prefs activity is handled in XML. This activity
 * allows the user to set his D2L username and password so that the application
 * will remember them. Currently they are stored in shared prefs, and this
 * probably isn't very secure. Consider securing credentials.
 */
public class PrefsActivity extends PreferenceActivity implements OnClickListener{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.layout.prefs);
        setContentView(R.layout.pref_layout);
        setTitle(R.string.titlePrefs);
    }

    public void onClick(DialogInterface arg0, int arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void goToClassesActivity(View v)
    {
        OUApplication app = (OUApplication)this.getApplication();
        app.getClasses().forceNextUpdate();
        startActivity(new Intent(this, ClassesActivity.class));
    }
    
}
