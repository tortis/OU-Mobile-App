/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author David
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
