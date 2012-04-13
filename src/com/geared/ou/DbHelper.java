/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geared.ou;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 *
 * @author David
 */
public class DbHelper extends SQLiteOpenHelper {
    Context context;
    private static final String DB_NAME = "userdata.db";
    private static final int DB_VERSION = 1;
    
    public static final String T_CLASSES = "classes";
    public static final String C_ID = BaseColumns._ID;
    public static final String C_USER = "user";
    public static final String C_NAME = "name";
    public static final String C_COLLEGE_ABVR = "college_abvr";
    public static final String C_URL = "url";
    public static final String C_LAST_UPDATE = "last_update";
    public static final String C_OUID = "ou_id";
    
    public static final String T_CONTENT = "content";
    public static final String C_CON_ID = BaseColumns._ID;
    public static final String C_CON_USER = "user";
    public static final String C_CON_NAME = "name";
    public static final String C_CON_LINK = "link";
    public static final String C_CON_CATEGORY = "category";
    public static final String C_CON_LAST_UPDATE = "last_update";
    public static final String C_CON_OUID = "ou_id";
    public static final String C_CON_TYPE = "type";
    
    public static final String T_GRADES = "grades";
    public static final String C_GRA_ID = "id";
    public static final String C_GRA_USER = "user";
    public static final String C_GRA_NAME = "name";
    public static final String C_GRA_CATEGORY = "category";
    public static final String C_GRA_SCORE = "score";
    public static final String C_GRA_OUID = "ou_id";
    public static final String C_GRA_LAST_UPDATE = "last_update";
    
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateClassTable = String.format(context.getString(R.string.sqlSetupClassList),
                T_CLASSES, C_ID, C_USER, C_NAME,
                C_URL, C_COLLEGE_ABVR, C_LAST_UPDATE,
                C_OUID);
        String sqlCreateContentTable = String.format(context.getString(R.string.sqlSetupContent),
                T_CONTENT, C_CON_ID, C_CON_USER, C_CON_NAME,
                C_CON_LINK, C_CON_CATEGORY,
                C_CON_LAST_UPDATE, C_CON_OUID,
                C_CON_TYPE);
        String sqlCreateGradesTable = String.format(context.getString(R.string.sqlSetupGrades), 
                T_GRADES, C_GRA_ID,
                C_GRA_USER, C_GRA_NAME,
                C_GRA_CATEGORY, C_GRA_SCORE,
                C_GRA_OUID, C_GRA_LAST_UPDATE);
        db.execSQL(sqlCreateClassTable);
        db.execSQL(sqlCreateContentTable);
        db.execSQL(sqlCreateGradesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + T_CLASSES);
        db.execSQL("drop table if exists " + T_CONTENT);
        db.execSQL("drop table if exists " + T_GRADES);
        this.onCreate(db);
    }
    
}
