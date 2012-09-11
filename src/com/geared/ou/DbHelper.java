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

import android.content.ContentValues;
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
    private Context context;
    private static final String DB_NAME = "userdata.db";
    private static final int DB_VERSION = 4;
    
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
    
    public static final String T_ROSTER = "roster";
    public static final String C_ROS_ID = "id";
    public static final String C_ROS_USER = "user";
    public static final String C_ROS_OUID = "ou_id";
    public static final String C_ROS_FIRST_NAME = "first_name";
    public static final String C_ROS_LAST_NAME = "last_name";
    public static final String C_ROS_ROLE = "role";
    public static final String C_ROS_LAST_UPDATE = "last_update";
    
    public static final String T_MAP_DATA = "map_data";
    public static final String C_MD_ID = "id";
    public static final String C_MD_NAME = "name";
    public static final String C_MD_X = "x";
    public static final String C_MD_Y = "y";
    public static final String C_MD_DESC = "description";
    
    
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
        String sqlCreateRosterTable = String.format(context.getString(R.string.sqlSetupRoster),
                T_ROSTER, C_ROS_ID,
                C_ROS_OUID, C_ROS_USER,
                C_ROS_FIRST_NAME, C_ROS_LAST_NAME,
                C_ROS_ROLE, C_ROS_LAST_UPDATE);
        
        String sqlCreateMapDataTable = String.format(context.getString(R.string.sqlSetupMap),
        		T_MAP_DATA, C_MD_ID, C_MD_NAME, C_MD_X, C_MD_Y,
        		C_MD_DESC);
        
        db.execSQL(sqlCreateClassTable);
        db.execSQL(sqlCreateContentTable);
        db.execSQL(sqlCreateGradesTable);
        db.execSQL(sqlCreateCourseNewsTable);
        db.execSQL(sqlCreateRosterTable);
        db.execSQL(sqlCreateMapDataTable);
        loadMapData(db);
    }
    
    

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if (oldVersion < 3)
    	{
	        db.execSQL("drop table if exists " + T_CLASSES);
	        db.execSQL("drop table if exists " + T_CONTENT);
	        db.execSQL("drop table if exists " + T_GRADES);
	        db.execSQL("drop table if exists " + T_COURSENEWS);
	        this.onCreate(db);
    	}
    	else
    	{
    		String sqlCreateMapDataTable = String.format(context.getString(R.string.sqlSetupMap),
            		T_MAP_DATA, C_MD_ID, C_MD_NAME, C_MD_X, C_MD_Y,
            		C_MD_DESC);
    		db.execSQL(sqlCreateMapDataTable);
    		loadMapData(db);
    	}
    }
    
    private void loadMapData(SQLiteDatabase db)
    {
    	ContentValues values = new ContentValues();
    	//Adams Center
    	values.put(C_MD_ID, 0);
    	values.put(C_MD_NAME, "Adams Center");
    	values.put(C_MD_X, 35201446);
    	values.put(C_MD_Y, -97446415);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Adams Hall
    	values.put(C_MD_ID, 1);
    	values.put(C_MD_NAME, "Adams Hall");
    	values.put(C_MD_X, 35207762);
    	values.put(C_MD_Y, -97444231);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Al Velie Rugby Football Complex
    	values.put(C_MD_ID, 2);
    	values.put(C_MD_NAME, "Al Velie Rugby Football Complex");
    	values.put(C_MD_X, 35185778);
    	values.put(C_MD_Y, -97448796);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ann & Henry Zarrow Hall - School of Social Work
    	values.put(C_MD_ID, 3);
    	values.put(C_MD_NAME, "Ann & Henry Zarrow Hall - School of Social Work");
    	values.put(C_MD_X, 35207285);
    	values.put(C_MD_Y, -97448475);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Archaeological Survey
    	values.put(C_MD_ID, 4);
    	values.put(C_MD_NAME, "Archaeological Survey");
    	values.put(C_MD_X, 35189715);
    	values.put(C_MD_Y, -97440604);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Armory
    	values.put(C_MD_ID, 5);
    	values.put(C_MD_NAME, "Armory");
    	values.put(C_MD_X, 35206697);
    	values.put(C_MD_Y, -97443711);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Asp Avenue Parking Facility
    	values.put(C_MD_ID, 6);
    	values.put(C_MD_NAME, "Asp Avenue Parking Facility");
    	values.put(C_MD_X, 3520568);
    	values.put(C_MD_Y, -97443657);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Barry Switzer Center
    	values.put(C_MD_ID, 7);
    	values.put(C_MD_NAME, "Barry Switzer Center");
    	values.put(C_MD_X, 35204659);
    	values.put(C_MD_Y, -974418);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Biological Survey
    	values.put(C_MD_ID, 8);
    	values.put(C_MD_NAME, "Biological Survey");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Bizzell Memorial Library
    	values.put(C_MD_ID, 9);
    	values.put(C_MD_NAME, "Bizzell Memorial Library");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Boomer Outreach Building
    	values.put(C_MD_ID, 10);
    	values.put(C_MD_NAME, "Boomer Outreach Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Boren Hall
    	values.put(C_MD_ID, 11);
    	values.put(C_MD_NAME, "Boren Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Boyd House
    	values.put(C_MD_ID, 12);
    	values.put(C_MD_NAME, "Boyd House");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Bruce Drake Golf Clubhouse
    	values.put(C_MD_ID, 13);
    	values.put(C_MD_NAME, "Bruce Drake Golf Clubhouse");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Buchanan Hall
    	values.put(C_MD_ID, 14);
    	values.put(C_MD_NAME, "Buchanan Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Bud Wilkinson House
    	values.put(C_MD_ID, 15);
    	values.put(C_MD_NAME, "Bud Wilkinson House");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Burton Hall
    	values.put(C_MD_ID, 16);
    	values.put(C_MD_NAME, "Burton Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Carnegie Building
    	values.put(C_MD_ID, 17);
    	values.put(C_MD_NAME, "Carnegie Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Carpenter Hall
    	values.put(C_MD_ID, 18);
    	values.put(C_MD_NAME, "Carpenter Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Carson Engineering Center
    	values.put(C_MD_ID, 19);
    	values.put(C_MD_NAME, "Carson Engineering Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Cate Center
    	values.put(C_MD_ID, 20);
    	values.put(C_MD_NAME, "Cate Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Catlett Music Center
    	values.put(C_MD_ID, 21);
    	values.put(C_MD_NAME, "Catlett Music Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ceramics Department
    	values.put(C_MD_ID, 22);
    	values.put(C_MD_NAME, "Ceramics Department");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Charlie Coe Golf Learning Center
    	values.put(C_MD_ID, 23);
    	values.put(C_MD_NAME, "Charlie Coe Golf Learning Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Chemistry Annex
    	values.put(C_MD_ID, 24);
    	values.put(C_MD_NAME, "Chemistry Annex");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Chemistry Building
    	values.put(C_MD_ID, 25);
    	values.put(C_MD_NAME, "Chemistry Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Chilled Water Plant
    	values.put(C_MD_ID, 26);
    	values.put(C_MD_NAME, "Chilled Water Plant");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Coats Hall
    	values.put(C_MD_ID, 27);
    	values.put(C_MD_NAME, "Coats Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Collums Building
    	values.put(C_MD_ID, 28);
    	values.put(C_MD_NAME, "Collums Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Copeland Hall
    	values.put(C_MD_ID, 29);
    	values.put(C_MD_NAME, "Copeland Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Couch Restaurants
    	values.put(C_MD_ID, 30);
    	values.put(C_MD_NAME, "Couch Restaurants");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Couch Tower
    	values.put(C_MD_ID, 31);
    	values.put(C_MD_NAME, "Couch Tower");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Craddock Hall, Air Force ROTC
    	values.put(C_MD_ID, 32);
    	values.put(C_MD_NAME, "Craddock Hall, Air Force ROTC");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Cross Center
    	values.put(C_MD_ID, 33);
    	values.put(C_MD_NAME, "Cross Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Dale Hall
    	values.put(C_MD_ID, 34);
    	values.put(C_MD_NAME, "Dale Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Dale Hall Tower
    	values.put(C_MD_ID, 35);
    	values.put(C_MD_NAME, "Dale Hall Tower");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Devon Energy Hall
    	values.put(C_MD_ID, 36);
    	values.put(C_MD_NAME, "Devon Energy Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ellison Hall
    	values.put(C_MD_ID, 37);
    	values.put(C_MD_NAME, "Ellison Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ellsworth Collings Hall
    	values.put(C_MD_ID, 38);
    	values.put(C_MD_NAME, "Ellsworth Collings Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Elm Avenue Parking Facility
    	values.put(C_MD_ID, 39);
    	values.put(C_MD_NAME, "Elm Avenue Parking Facility");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Engineering Laboratory
    	values.put(C_MD_ID, 117);
    	values.put(C_MD_NAME, "Engineering Laboratory");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Evans Hall
    	values.put(C_MD_ID, 40);
    	values.put(C_MD_NAME, "Evans Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Everest Training Center
    	values.put(C_MD_ID, 41);
    	values.put(C_MD_NAME, "Everest Training Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//ExxonMobil / Lawrence G. Rawl Engineering Practice Facility
    	values.put(C_MD_ID, 42);
    	values.put(C_MD_NAME, "ExxonMobil / Lawrence G. Rawl Engineering Practice Facility");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Felgar Hall
    	values.put(C_MD_ID, 43);
    	values.put(C_MD_NAME, "Felgar Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Fine Arts Center, Drama
    	values.put(C_MD_ID, 44);
    	values.put(C_MD_NAME, "Fine Arts Center, Drama");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Fred Jones Jr. Art Center
    	values.put(C_MD_ID, 45);
    	values.put(C_MD_NAME, "Fred Jones Jr. Art Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Fred Jones Jr. Museum of Art
    	values.put(C_MD_ID, 46);
    	values.put(C_MD_NAME, "Fred Jones Jr. Museum of Art");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gaylord Family Oklahoma Memorial Stadium
    	values.put(C_MD_ID, 47);
    	values.put(C_MD_NAME, "Gaylord Family Oklahoma Memorial Stadium");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gaylord Hall
    	values.put(C_MD_ID, 48);
    	values.put(C_MD_NAME, "Gaylord Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//George Lynn Cross Hall
    	values.put(C_MD_ID, 49);
    	values.put(C_MD_NAME, "George Lynn Cross Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gittinger Hall
    	values.put(C_MD_ID, 50);
    	values.put(C_MD_NAME, "Gittinger Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Goddard Health Center
    	values.put(C_MD_ID, 51);
    	values.put(C_MD_NAME, "Goddard Health Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gould Hall
    	values.put(C_MD_ID, 52);
    	values.put(C_MD_NAME, "Gould Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Headington Family Tennis Center
    	values.put(C_MD_ID, 53);
    	values.put(C_MD_NAME, "Headington Family Tennis Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Henderson - Tolson Cultural Center
    	values.put(C_MD_ID, 54);
    	values.put(C_MD_NAME, "Henderson - Tolson Cultural Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Hester Hall
    	values.put(C_MD_ID, 55);
    	values.put(C_MD_NAME, "Hester Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Huston Huffman Physical Fitness Center
    	values.put(C_MD_ID, 56);
    	values.put(C_MD_NAME, "Huston Huffman Physical Fitness Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jacobs Track and Field Facility
    	values.put(C_MD_ID, 57);
    	values.put(C_MD_NAME, "Jacobs Track and Field Facility");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jacobson Hall, Visitor Center
    	values.put(C_MD_ID, 58);
    	values.put(C_MD_NAME, "Jacobson Hall, Visitor Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jefferson House
    	values.put(C_MD_ID, 59);
    	values.put(C_MD_NAME, "Jefferson House");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jimmie Austin University of Oklahoma Golf Course
    	values.put(C_MD_ID, 60);
    	values.put(C_MD_NAME, "Jimmie Austin University of Oklahoma Golf Course");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jones House
    	values.put(C_MD_ID, 61);
    	values.put(C_MD_NAME, "Jones House");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Joseph K. Lester Oklahoma Police Department
    	values.put(C_MD_ID, 62);
    	values.put(C_MD_NAME, "Joseph K. Lester Oklahoma Police Department");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Kaufman Hall
    	values.put(C_MD_ID, 63);
    	values.put(C_MD_NAME, "Kaufman Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Kraettli Apartments
    	values.put(C_MD_ID, 64);
    	values.put(C_MD_NAME, "Kraettli Apartments");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//L. Dale Mitchell Baseball Park
    	values.put(C_MD_ID, 65);
    	values.put(C_MD_NAME, "L. Dale Mitchell Baseball Park");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Landscaping Department
    	values.put(C_MD_ID, 66);
    	values.put(C_MD_NAME, "Landscaping Department");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Lissa and Cy Wagner Hall
    	values.put(C_MD_ID, 67);
    	values.put(C_MD_NAME, "Lissa and Cy Wagner Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Lloyd Noble Center
    	values.put(C_MD_ID, 68);
    	values.put(C_MD_NAME, "Lloyd Noble Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//McCasland Field House
    	values.put(C_MD_ID, 69);
    	values.put(C_MD_NAME, "McCasland Field House");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Monnet Hall
    	values.put(C_MD_ID, 70);
    	values.put(C_MD_NAME, "Monnet Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Mosier Indoor Athletic Facility
    	values.put(C_MD_ID, 71);
    	values.put(C_MD_NAME, "Mosier Indoor Athletic Facility");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Murray Case Sells Swim Center
    	values.put(C_MD_ID, 72);
    	values.put(C_MD_NAME, "Murray Case Sells Swim Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//National Weather Center
    	values.put(C_MD_ID, 73);
    	values.put(C_MD_NAME, "National Weather Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Nielsen Hall
    	values.put(C_MD_ID, 74);
    	values.put(C_MD_NAME, "Nielsen Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Noble Electron Microscopy Laboratory
    	values.put(C_MD_ID, 75);
    	values.put(C_MD_NAME, "Noble Electron Microscopy Laboratory");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Nuclear Engineering Laboratory
    	values.put(C_MD_ID, 76);
    	values.put(C_MD_NAME, "Nuclear Engineering Laboratory");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Administration Building
    	values.put(C_MD_ID, 77);
    	values.put(C_MD_NAME, "OCCE Administration Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Cross Center Main
    	values.put(C_MD_ID, 78);
    	values.put(C_MD_NAME, "OCCE Cross Center Main");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE McCarter Hall of Advanced Studies
    	values.put(C_MD_ID, 79);
    	values.put(C_MD_NAME, "OCCE McCarter Hall of Advanced Studies");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Office Annex
    	values.put(C_MD_ID, 80);
    	values.put(C_MD_NAME, "OCCE Office Annex");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Sooner Suites
    	values.put(C_MD_ID, 81);
    	values.put(C_MD_NAME, "OCCE Sooner Suites");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Thurman J. White Forum Building
    	values.put(C_MD_ID, 82);
    	values.put(C_MD_NAME, "OCCE Thurman J. White Forum Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Observatory and Landscape Department
    	values.put(C_MD_ID, 83);
    	values.put(C_MD_NAME, "Observatory and Landscape Department");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Oklahoma Geological Survey
    	values.put(C_MD_ID, 84);
    	values.put(C_MD_NAME, "Oklahoma Geological Survey");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Oklahoma Memorial Union
    	values.put(C_MD_ID, 85);
    	values.put(C_MD_NAME, "Oklahoma Memorial Union");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Oklahoma Memorial Union Parking Center
    	values.put(C_MD_ID, 86);
    	values.put(C_MD_NAME, "Oklahoma Memorial Union Parking Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Old Faculty Club
    	values.put(C_MD_ID, 87);
    	values.put(C_MD_NAME, "Old Faculty Club");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Old Science Hall
    	values.put(C_MD_ID, 88);
    	values.put(C_MD_NAME, "Old Science Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//One Partners' Place
    	values.put(C_MD_ID, 89);
    	values.put(C_MD_NAME, "One Partners' Place");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Our Children's World Learning Center
    	values.put(C_MD_ID, 90);
    	values.put(C_MD_NAME, "Our Children's World Learning Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Physical Plant Complex
    	values.put(C_MD_ID, 91);
    	values.put(C_MD_NAME, "Physical Plant Complex");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Physical Sciences Center
    	values.put(C_MD_ID, 92);
    	values.put(C_MD_NAME, "Physical Sciences Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Price Hall
    	values.put(C_MD_ID, 93);
    	values.put(C_MD_NAME, "Price Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Reaves' Park Building (City of Norman)
    	values.put(C_MD_ID, 94);
    	values.put(C_MD_NAME, "Reaves' Park Building (City of Norman)");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Reynolds Performing Arts Center
    	values.put(C_MD_ID, 95);
    	values.put(C_MD_NAME, "Reynolds Performing Arts Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Rhyne Hall
    	values.put(C_MD_ID, 96);
    	values.put(C_MD_NAME, "Rhyne Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Richards Hall
    	values.put(C_MD_ID, 97);
    	values.put(C_MD_NAME, "Richards Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Robertson Hall
    	values.put(C_MD_ID, 98);
    	values.put(C_MD_NAME, "Robertson Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sam Noble Oklahoma Museum of Natural History
    	values.put(C_MD_ID, 99);
    	values.put(C_MD_NAME, "Sam Noble Oklahoma Museum of Natural History");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sam Viersen Gymnastics Center
    	values.put(C_MD_ID, 100);
    	values.put(C_MD_NAME, "Sam Viersen Gymnastics Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sarkeys Energy Center
    	values.put(C_MD_ID, 101);
    	values.put(C_MD_NAME, "Sarkeys Energy Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Soccer Complex/John Crain Field
    	values.put(C_MD_ID, 102);
    	values.put(C_MD_NAME, "Soccer Complex/John Crain Field");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Soccer Practice Field
    	values.put(C_MD_ID, 103);
    	values.put(C_MD_NAME, "Soccer Practice Field");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Softball Complex/Marita Hynes Field
    	values.put(C_MD_ID, 104);
    	values.put(C_MD_NAME, "Softball Complex/Marita Hynes Field");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Stephenson Life Sciences Research Center
    	values.put(C_MD_ID, 105);
    	values.put(C_MD_NAME, "Stephenson Life Sciences Research Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Stephenson Research and Technology Center
    	values.put(C_MD_ID, 106);
    	values.put(C_MD_NAME, "Stephenson Research and Technology Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sutton Hall
    	values.put(C_MD_ID, 107);
    	values.put(C_MD_NAME, "Sutton Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Theta M. Dempsey Transportation Operation Center
    	values.put(C_MD_ID, 108);
    	values.put(C_MD_NAME, "Theta M. Dempsey Transportation Operation Center");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Three Partners' Place
    	values.put(C_MD_ID, 109);
    	values.put(C_MD_NAME, "Three Partners' Place");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Traditions Square East
    	values.put(C_MD_ID, 110);
    	values.put(C_MD_NAME, "Traditions Square East");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Traditions Square West
    	values.put(C_MD_ID, 111);
    	values.put(C_MD_NAME, "Traditions Square West");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Two Partners' Place
    	values.put(C_MD_ID, 112);
    	values.put(C_MD_NAME, "Two Partners' Place");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//University of Oklahoma Foundation Building
    	values.put(C_MD_ID, 113);
    	values.put(C_MD_NAME, "University of Oklahoma Foundation Building");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Wagner Dining Facility
    	values.put(C_MD_ID, 114);
    	values.put(C_MD_NAME, "Wagner Dining Facility");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Walker Tower
    	values.put(C_MD_ID, 115);
    	values.put(C_MD_NAME, "Walker Tower");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Whitehand Hall
    	values.put(C_MD_ID, 116);
    	values.put(C_MD_NAME, "Whitehand Hall");
    	values.put(C_MD_X, 2);
    	values.put(C_MD_Y, 2);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    }
    
}
