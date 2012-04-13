/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geared.ou;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.geared.ou.D2LSourceGetter.SGError;

/**
 *
 * @author David
 */
public class OUApplication extends Application {
    private ClassesData classes;
    private SharedPreferences prefs;
    private D2LSourceGetter sourceGetter;
    private DbHelper dbHelper;
    
    @Override
    public void onCreate() {
        super.onCreate();
        classes = new ClassesData(this);
        sourceGetter = new D2LSourceGetter();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sourceGetter.setCredentials(prefs.getString("username", ""), prefs.getString("password", ""));
        dbHelper = new DbHelper(this);
    }
    
    public SGError updateClasses(ClassesActivity context) //Async this function!
    {
        return classes.update(sourceGetter);
    }
    
    public SQLiteDatabase getDb()
    {
        return dbHelper.getWritableDatabase();
    }
    
    public SharedPreferences getPrefs()
    {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    public void updateSGCredentials()
    {
        SharedPreferences p = getPrefs();
        sourceGetter.setCredentials(p.getString("username", ""), p.getString("password",""));
    }
    
    public ClassesData getClasses()
    {
        return classes;
    }
    
    public D2LSourceGetter getSourceGetter()
    {
        return sourceGetter;
    }
    
    public String getUser()
    {
        SharedPreferences p = getPrefs();
        return p.getString("username", "");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        dbHelper.close();
    }
}
