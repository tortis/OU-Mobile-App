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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 *
 * This class manages the local sqlite database where data that is pulled from
 * D2L is stored. The use of this database allows the Application to run more
 * smoothly. If a user opens an activity that should display data from D2L, first
 * the data found in this database should be loaded. Then if this data is old, or
 * if the user manually requests it, D2L will be queried for up-to-date data.
 * 
 */
public class DbHelper extends SQLiteOpenHelper {
    Context context;
    private static final String DB_NAME = "userdata.db";
    private static final int DB_VERSION = 2;
    
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
    
    public static final String T_COURSENEWS = "course_news";
    public static final String C_CN_ID = "id";
    public static final String C_CN_USER = "user";
    public static final String C_CN_NAME = "name";
    public static final String C_CN_OUID = "ou_id";
    public static final String C_CN_CONTENT = "content";
    public static final String C_CN_LAST_UPDATE = "last_update";
    
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
        String sqlCreateCourseNewsTable = String.format(context.getString(R.string.sqlSetupCourseNews),
                T_COURSENEWS, C_CN_ID,
                C_CN_USER, C_CN_NAME,
                C_CN_OUID, C_CN_CONTENT,
                C_CN_LAST_UPDATE);
        
        db.execSQL(sqlCreateClassTable);
        db.execSQL(sqlCreateContentTable);
        db.execSQL(sqlCreateGradesTable);
        db.execSQL(sqlCreateCourseNewsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + T_CLASSES);
        db.execSQL("drop table if exists " + T_CONTENT);
        db.execSQL("drop table if exists " + T_GRADES);
        this.onCreate(db);
    }
    
}
