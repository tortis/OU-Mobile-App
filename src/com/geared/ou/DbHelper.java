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
    // Deployed version: 3
    private static final int DB_VERSION = 5;
    
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
    	values.put(C_MD_DESC, "Adams Center is the first of three 12-story housing centers built during the mid-1960s expansion of University residence halls. Adams Center which opened in 1964 to accommodate 816 residents is named for K. S. “Boots” Adams, a former Chairman and Chief Executive Officer of the Phillips Petroleum Company. There is a campus restaurant located on the first floor. There is also a 24-hour monitored quiet study lounge within the tower.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Adams Hall
    	values.put(C_MD_ID, 1);
    	values.put(C_MD_NAME, "Adams Hall");
    	values.put(C_MD_X, 35207762);
    	values.put(C_MD_Y, -97444231);
    	values.put(C_MD_DESC, "Adams Hall debuted on the campus in 1936 as the Business Administration Building named for Arthur B. Adams the first dean of the College of Business Administration who served OU from 1913 to 1948. In addition to offices and classrooms Adams Hall houses computer laboratories and the JC Penney Leadership Center. It is home to OU's first endowed college the Michael F. Price College of Business named in honor of Price a 1973 OU graduate and outstanding national business leader and financier. The main lobby features a 1936 mural by artist Craig Sheppard highlighting different fields of commerce and industry. On the exterior of the south building entrance are sculptures representing industry and commerce by sculptor Julius Struppeck.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Al Velie Rugby Football Complex
    	values.put(C_MD_ID, 2);
    	values.put(C_MD_NAME, "Al Velie Rugby Football Complex");
    	values.put(C_MD_X, 35185778);
    	values.put(C_MD_Y, -97448796);
    	values.put(C_MD_DESC, "Al Velie Rugby Football Complex is named in honor of David Ross Boyd Professor of English Alan Velie. Velie is the founding faculty advisor and was also a member of the OU Rugby team. Rugby has been a club sport at OU for over 30 years. OU's field is one of the top collegiate fields in the country allowing the team to host a number of major collegiate tournaments.");
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
    	values.put(C_MD_X, 35205755);
    	values.put(C_MD_Y, -97443630);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Barry Switzer Center
    	values.put(C_MD_ID, 7);
    	values.put(C_MD_NAME, "Barry Switzer Center");
    	values.put(C_MD_X, 35204683);
    	values.put(C_MD_Y, -97441808);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Biological Survey
    	values.put(C_MD_ID, 8);
    	values.put(C_MD_NAME, "Biological Survey");
    	values.put(C_MD_X, 35189672);
    	values.put(C_MD_Y, -97439959);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Bizzell Memorial Library
    	values.put(C_MD_ID, 9);
    	values.put(C_MD_NAME, "Bizzell Memorial Library");
    	values.put(C_MD_X, 35208062);
    	values.put(C_MD_Y, -97445749);
    	values.put(C_MD_DESC, "Bizzell Memorial Library built in 1929 and later named for William Bennett Bizzell the University's fifth president is located in the heart of the Norman campus. A north wing was added in 1958 and the Doris W. Neustadt Wing was constructed in 1982 creating a new main entrance on the west while retaining the original south entrance. Bizzell Memorial Library is the main library building in the University Libraries. The University of Oklahoma Libraries holds the largest research collections in the state including more than 4.7 million volumes 31000 print and electronic serials 180 electronic databases and 259000 electronic books. Also housed in the main library are distinguished special collections such as the Harry W. Bass Collections in Business History the Bizzell Bible Collection the John and Mary Nichols Rare Books and Special Collections and the History of Science Collections. The John and Mary Nichols Collections is composed of rare books and special materials in British and American literature dating from the 15th century to the present. The collections feature a number of first-edition works by Charles Dickens. The History of Science Collections is one of the largest collections of rare scientific books and documents in the United States and includes Galileo's handwritten corrections to one of his first editions. The Bass Business History Collection houses close to 2000 rare books and manuscripts including the archives of J. and W. Seligman Company a historic l9th century financial firm in New York City. The beautifully decorated Evelyena D. Honeymon Anteroom provides a spectacular entrance to the Peggy V. Helmerich Great Reading Room. This architectural masterpiece remains a favorite student study area. The walls of the Great Reading Room are lined with beautiful carved bookcases that hold theses and dissertations of OU graduates and the ceiling features intricately carved angels. The School of Library and Information Studies which occupies the south end of the original library building was formally established in 1929 the same year the library was built. The school's quarters combine the architecture and carved woodwork of the original building with the latest in information technology.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Boomer Outreach Building
    	values.put(C_MD_ID, 10);
    	values.put(C_MD_NAME, "Boomer Outreach Building");
    	values.put(C_MD_X, 35197260);
    	values.put(C_MD_Y, -97445864);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Boren Hall
    	values.put(C_MD_ID, 11);
    	values.put(C_MD_NAME, "Boren Hall");
    	values.put(C_MD_X, 35203113);
    	values.put(C_MD_Y, -97444850);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Boyd House
    	values.put(C_MD_ID, 12);
    	values.put(C_MD_NAME, "Boyd House");
    	values.put(C_MD_X, 35211600);
    	values.put(C_MD_Y, -97446118);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Bruce Drake Golf Clubhouse
    	values.put(C_MD_ID, 13);
    	values.put(C_MD_NAME, "Bruce Drake Golf Clubhouse");
    	values.put(C_MD_X, 35194522);
    	values.put(C_MD_Y, -97431921);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Buchanan Hall
    	values.put(C_MD_ID, 14);
    	values.put(C_MD_NAME, "Buchanan Hall");
    	values.put(C_MD_X, 35208450);
    	values.put(C_MD_Y, -97444450);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Bud Wilkinson House
    	values.put(C_MD_ID, 15);
    	values.put(C_MD_NAME, "Bud Wilkinson House");
    	values.put(C_MD_X, 35204387);
    	values.put(C_MD_Y, -97440794);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Burton Hall
    	values.put(C_MD_ID, 16);
    	values.put(C_MD_NAME, "Burton Hall");
    	values.put(C_MD_X, 35208960);
    	values.put(C_MD_Y, -97448474);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Carnegie Building
    	values.put(C_MD_ID, 17);
    	values.put(C_MD_NAME, "Carnegie Building");
    	values.put(C_MD_X, 35208844);
    	values.put(C_MD_Y, -97445030);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Carpenter Hall
    	values.put(C_MD_ID, 18);
    	values.put(C_MD_NAME, "Carpenter Hall");
    	values.put(C_MD_X, 35210322);
    	values.put(C_MD_Y, -97444258);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Carson  Center
    	values.put(C_MD_ID, 19);
    	values.put(C_MD_NAME, "Carson  Center");
    	values.put(C_MD_X, 35210720);
    	values.put(C_MD_Y, -97442782);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Cate Center
    	values.put(C_MD_ID, 20);
    	values.put(C_MD_NAME, "Cate Center");
    	values.put(C_MD_X, 35203125);
    	values.put(C_MD_Y, -97445937);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Catlett Music Center
    	values.put(C_MD_ID, 21);
    	values.put(C_MD_NAME, "Catlett Music Center");
    	values.put(C_MD_X, 35210492);
    	values.put(C_MD_Y, -97448433);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ceramics Department
    	values.put(C_MD_ID, 22);
    	values.put(C_MD_NAME, "Ceramics Department");
    	values.put(C_MD_X, 35187768);
    	values.put(C_MD_Y, -97436484);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Charlie Coe Golf Learning Center
    	values.put(C_MD_ID, 23);
    	values.put(C_MD_NAME, "Charlie Coe Golf Learning Center");
    	values.put(C_MD_X, 35191799);
    	values.put(C_MD_Y, -97433441);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Chemistry Annex
    	values.put(C_MD_ID, 24);
    	values.put(C_MD_NAME, "Chemistry Annex");
    	values.put(C_MD_X, 35209378);
    	values.put(C_MD_Y, -97446649);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Chemistry Building
    	values.put(C_MD_ID, 25);
    	values.put(C_MD_NAME, "Chemistry Building");
    	values.put(C_MD_X, 35209356);
    	values.put(C_MD_Y, -97446208);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Chilled Water Plant
    	values.put(C_MD_ID, 26);
    	values.put(C_MD_NAME, "Chilled Water Plant");
    	values.put(C_MD_X, 35186895);
    	values.put(C_MD_Y, -97436598);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Coats Hall
    	values.put(C_MD_ID, 27);
    	values.put(C_MD_NAME, "Coats Hall");
    	values.put(C_MD_X, 35195964);
    	values.put(C_MD_Y, -97446667);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Collums Building
    	values.put(C_MD_ID, 28);
    	values.put(C_MD_NAME, "Collums Building");
    	values.put(C_MD_X, 35204329);
    	values.put(C_MD_Y, -97439632);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Copeland Hall
    	values.put(C_MD_ID, 29);
    	values.put(C_MD_NAME, "Copeland Hall");
    	values.put(C_MD_X, 35204819);
    	values.put(C_MD_Y, -97446551);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Couch Restaurants
    	values.put(C_MD_ID, 30);
    	values.put(C_MD_NAME, "Couch Restaurants");
    	values.put(C_MD_X, 35200305);
    	values.put(C_MD_Y, -97445632);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Couch Tower
    	values.put(C_MD_ID, 31);
    	values.put(C_MD_NAME, "Couch Tower");
    	values.put(C_MD_X, 35200214);
    	values.put(C_MD_Y, -97444755);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Craddock Hall, Air Force ROTC
    	values.put(C_MD_ID, 32);
    	values.put(C_MD_NAME, "Craddock Hall, Air Force ROTC");
    	values.put(C_MD_X, 35210148);
    	values.put(C_MD_Y, -97442178);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Cross Center
    	values.put(C_MD_ID, 33);
    	values.put(C_MD_NAME, "Cross Center");
    	values.put(C_MD_X, 35198778);
    	values.put(C_MD_Y, -97442282);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Dale Hall
    	values.put(C_MD_ID, 34);
    	values.put(C_MD_NAME, "Dale Hall");
    	values.put(C_MD_X, 35204300);
    	values.put(C_MD_Y, -97446521);
    	values.put(C_MD_DESC, "Dale Hall was completed in 1967 and named for a distinguished OU professor and noted historian of the West Edward Everett Dale. Dale Hall is dedicated to the social sciences and contains some of the largest classroom facilities on campus. In addition the building houses laboratories and some faculty offices.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Dale Hall Tower
    	values.put(C_MD_ID, 35);
    	values.put(C_MD_NAME, "Dale Hall Tower");
    	values.put(C_MD_X, 35204265);
    	values.put(C_MD_Y, -97447154);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Devon Energy Hall
    	values.put(C_MD_ID, 36);
    	values.put(C_MD_NAME, "Devon Energy Hall");
    	values.put(C_MD_X, 35210738);
    	values.put(C_MD_Y, -97441758);
    	values.put(C_MD_DESC, "Devon Energy Hall was designed as a collaborative learning environment for The College of Engineering and opened in 2010. The facilities include classrooms, research space, teaching labs, collaborative space, study space, an outdoor terrace and a model shop.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ellison Hall
    	values.put(C_MD_ID, 37);
    	values.put(C_MD_NAME, "Ellison Hall");
    	values.put(C_MD_X, 35207894);
    	values.put(C_MD_Y, -97447320);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Ellsworth Collings Hall
    	values.put(C_MD_ID, 38);
    	values.put(C_MD_NAME, "Ellsworth Collings Hall");
    	values.put(C_MD_X, 35205566);
    	values.put(C_MD_Y, -97446473);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Elm Avenue Parking Facility
    	values.put(C_MD_ID, 39);
    	values.put(C_MD_NAME, "Elm Avenue Parking Facility");
    	values.put(C_MD_X, 35209577);
    	values.put(C_MD_Y, -97448420);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	// Laboratory
    	values.put(C_MD_ID, 117);
    	values.put(C_MD_NAME, "Engineering Laboratory");
    	values.put(C_MD_X, 35209327);
    	values.put(C_MD_Y, -97443099);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Evans Hall
    	values.put(C_MD_ID, 40);
    	values.put(C_MD_NAME, "Evans Hall");
    	values.put(C_MD_X, 35208451);
    	values.put(C_MD_Y, -97445609);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Everest Training Center
    	values.put(C_MD_ID, 41);
    	values.put(C_MD_NAME, "Everest Training Center");
    	values.put(C_MD_X, 35204436);
    	values.put(C_MD_Y, -97438679);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//ExxonMobil / Lawrence G. Rawl Engineering Practice Facility
    	values.put(C_MD_ID, 42);
    	values.put(C_MD_NAME, "ExxonMobil / Lawrence G. Rawl Engineering Practice Facility");
    	values.put(C_MD_X, 35210230);
    	values.put(C_MD_Y, -97441672);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Felgar Hall
    	values.put(C_MD_ID, 43);
    	values.put(C_MD_NAME, "Felgar Hall");
    	values.put(C_MD_X, 35210160);
    	values.put(C_MD_Y, -97442938);
    	values.put(C_MD_DESC, "Felgar Hall is named for the first dean of engineering James H. Felgar who served from 1909 to 1937. It was built in 1925 to house the College of Engineering. This hall now contains the School of Aerospace and Mechanical Engineering Williams Student Services Center laboratories and classrooms.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Fine Arts Center, Drama
    	values.put(C_MD_ID, 44);
    	values.put(C_MD_NAME, "Fine Arts Center, Drama");
    	values.put(C_MD_X, 35210042);
    	values.put(C_MD_Y, -97447390);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Fred Jones Jr. Art Center
    	values.put(C_MD_ID, 45);
    	values.put(C_MD_NAME, "Fred Jones Jr. Art Center");
    	values.put(C_MD_X, 35210651);
    	values.put(C_MD_Y, -97446510);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Fred Jones Jr. Museum of Art
    	values.put(C_MD_ID, 46);
    	values.put(C_MD_NAME, "Fred Jones Jr. Museum of Art");
    	values.put(C_MD_X, 35210668);
    	values.put(C_MD_Y, -97447304);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gaylord Family Oklahoma Memorial Stadium
    	values.put(C_MD_ID, 47);
    	values.put(C_MD_NAME, "Gaylord Family Oklahoma Memorial Stadium");
    	values.put(C_MD_X, 35205961);
    	values.put(C_MD_Y, -97442542);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gaylord Hall
    	values.put(C_MD_ID, 48);
    	values.put(C_MD_NAME, "Gaylord Hall");
    	values.put(C_MD_X, 35204405);
    	values.put(C_MD_Y, -97445137);
    	values.put(C_MD_DESC, "Gaylord Hall has been the home of the Gaylord College of Journalism and Mass Communication since the fall of 2004. The building dedicated November 30 2004 was made possible by a gift of $22 million from the late Edward L. Gaylord editor and publisher of The Oklahoman and his family. The 6400-foot facility includes multimedia labs computer labs audio and video editing suites faculty offices classrooms and the Edith K. Gaylord Library and Resource Center named for the sister of Mr. Gaylord. The Gaylord gift enabled the University to elevate H. H. Hebert School of Journalism to a freestanding college including the H. H. Herbert School named for its founder.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//George Lynn Cross Hall
    	values.put(C_MD_ID, 49);
    	values.put(C_MD_NAME, "George Lynn Cross Hall");
    	values.put(C_MD_X, 35206785);
    	values.put(C_MD_Y, -97444906);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gittinger Hall
    	values.put(C_MD_ID, 50);
    	values.put(C_MD_NAME, "Gittinger Hall");
    	values.put(C_MD_X, 35206610);
    	values.put(C_MD_Y, -97446301);
    	values.put(C_MD_DESC, "Gittinger Hall was part of the post-World War II campus expansion to accommodate a burgeoning enrollment. Built in 1951 the building is named for Roy Gittinger a 1902 graduate who served OU as a professor of history registrar and dean of undergraduates graduates administration and admissions. Occupants are the Department of English and its faculty offices classrooms and a computer laboratory.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Goddard Health Center
    	values.put(C_MD_ID, 51);
    	values.put(C_MD_NAME, "Goddard Health Center");
    	values.put(C_MD_X, 35208376);
    	values.put(C_MD_Y, -97448501);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Gould Hall
    	values.put(C_MD_ID, 52);
    	values.put(C_MD_NAME, "Gould Hall");
    	values.put(C_MD_X, 35205426);
    	values.put(C_MD_Y, -97444939);
    	values.put(C_MD_DESC, "Gould Hall bears the name of the founder of geological studies in Oklahoma and OU's first professor of geology Charles Newton Gould who was also the director of the Oklahoma Geological Survey. OU had one of the first schools of petroleum geology and petroleum engineering in the United States. Constructed in 1951 the building was furnished and equipped with donations from alumni and friends in the petroleum industry. Since the School of Geology and Geophysics moved in 1986 Gould Hall has been occupied by the College of Architecture and its faculty offices studios and classrooms. The IT Service Center is located in Gould Hall, Room B10, and is open from 9 am to 5 pm on weekdays. IT Service Center Phone: (405) 325-5981");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Headington Family Tennis Center
    	values.put(C_MD_ID, 53);
    	values.put(C_MD_NAME, "Headington Family Tennis Center");
    	values.put(C_MD_X, 35187322);
    	values.put(C_MD_Y, -97448775);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Henderson - Tolson Cultural Center
    	values.put(C_MD_ID, 54);
    	values.put(C_MD_NAME, "Henderson - Tolson Cultural Center");
    	values.put(C_MD_X, 35202748);
    	values.put(C_MD_Y, -97443844);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Hester Hall
    	values.put(C_MD_ID, 55);
    	values.put(C_MD_NAME, "Hester Hall");
    	values.put(C_MD_X, 35206973);
    	values.put(C_MD_Y, -97447465);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Huston Huffman Physical Fitness Center
    	values.put(C_MD_ID, 56);
    	values.put(C_MD_NAME, "Huston Huffman Physical Fitness Center");
    	values.put(C_MD_X, 35201411);
    	values.put(C_MD_Y, -97442713);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jacobs Track and Field Facility
    	values.put(C_MD_ID, 57);
    	values.put(C_MD_NAME, "Jacobs Track and Field Facility");
    	values.put(C_MD_X, 35205417);
    	values.put(C_MD_Y, -97438958);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jacobson Hall, Visitor Center
    	values.put(C_MD_ID, 58);
    	values.put(C_MD_NAME, "Jacobson Hall, Visitor Center");
    	values.put(C_MD_X, 35210283);
    	values.put(C_MD_Y, -97444855);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jefferson House
    	values.put(C_MD_ID, 59);
    	values.put(C_MD_NAME, "Jefferson House");
    	values.put(C_MD_X, 35204120);
    	values.put(C_MD_Y, -97440770);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jimmie Austin University of Oklahoma Golf Course
    	values.put(C_MD_ID, 60);
    	values.put(C_MD_NAME, "Jimmie Austin University of Oklahoma Golf Course");
    	values.put(C_MD_X, 35194472);
    	values.put(C_MD_Y, -97432139);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Jones House
    	values.put(C_MD_ID, 61);
    	values.put(C_MD_NAME, "Jones House");
    	values.put(C_MD_X, 35204738);
    	values.put(C_MD_Y, -97440631);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Joseph K. Lester Oklahoma Police Department
    	values.put(C_MD_ID, 62);
    	values.put(C_MD_NAME, "Joseph K. Lester Oklahoma Police Department");
    	values.put(C_MD_X, 35189750);
    	values.put(C_MD_Y, -97435257);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Kaufman Hall
    	values.put(C_MD_ID, 63);
    	values.put(C_MD_NAME, "Kaufman Hall");
    	values.put(C_MD_X, 35206057);
    	values.put(C_MD_Y, -97446522);
    	values.put(C_MD_DESC, "Kaufman Hall honors the service of Kenneth C. Kaufman a distinguished veteran professor of modern languages. Built in 1949 to provide classroom and faculty office space the building contains the departments of Modern Languages Literatures and Linguistics; Sociology; and Classics and Letters; the Student Teaching/ Field Experience office; faculty offices and classrooms; the Language Laboratory; and radio station KGOU. A mural titled Pan-American Family by famed OU art professor Emilio Amero is located on the east stairwell of the second floor.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Kraettli Apartments
    	values.put(C_MD_ID, 64);
    	values.put(C_MD_NAME, "Kraettli Apartments");
    	values.put(C_MD_X, 35192109);
    	values.put(C_MD_Y, -97443142);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//L. Dale Mitchell Baseball Park
    	values.put(C_MD_ID, 65);
    	values.put(C_MD_NAME, "L. Dale Mitchell Baseball Park");
    	values.put(C_MD_X, 35190219);
    	values.put(C_MD_Y, -97446891);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Landscaping Department
    	values.put(C_MD_ID, 66);
    	values.put(C_MD_NAME, "Landscaping Department");
    	values.put(C_MD_X, 35187821);
    	values.put(C_MD_Y, -97438496);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Lissa and Cy Wagner Hall
    	values.put(C_MD_ID, 67);
    	values.put(C_MD_NAME, "Lissa and Cy Wagner Hall");
    	values.put(C_MD_X, 35208348);
    	values.put(C_MD_Y, -97443082);
    	values.put(C_MD_DESC, "Wagner Hall houses the Student Academic Services Center which includes the Writing Center, the Graduation Office and Project Threshold. It is at the center of a developing student services district in the middle of campus seeking to improve student success, retention and graduation rates. Wagner Hall facilities include classrooms (full A/V), student study rooms (with whiteboards and/or SmartBoards), the Wagner Learning Center, a Computer Laboratory with printing and study lounges.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Lloyd Noble Center
    	values.put(C_MD_ID, 68);
    	values.put(C_MD_NAME, "Lloyd Noble Center");
    	values.put(C_MD_X, 35187348);
    	values.put(C_MD_Y, -97444365);
    	values.put(C_MD_DESC, "Lloyd Noble Center was originally known as the house Alvan Adams built Wayman Tisdale filled and where Billy Tubbs rebuilt the Sooner men's basketball program. Now the names of Kelvin Sampson and Sherri Coale must be added to the description as those Sooner coaches have taken OU basketball to new levels reaching the NCAA Final Four in 2002 in mens and women's basketball. That marked only the third time in NCAA history for a school to have both teams make the Final Four in the same season. Dedicated in 1975 the Lloyd Noble Center has become one of the top sports arenas in the country. Named after Lloyd Noble of Ardmore a longtime supporter and regent of the University of Oklahoma and Oklahoma philanthropist it has served as the host for NCAA mens and womens basketball tournament games the NCAA Wrestling Championship the NCAA Men's Gymnastics Championships conference championships in wrestling womens basketball mens gymnastics and women's gymnastics and several events from the 1989 U.S. Olympic Festival. Since its opening more than four million fans have come through the doors to cheer on their favorite teams usually the home standing Sooners. Attendance records continue to be broken by both the men's and women's programs. Sellouts once reserved for the Sooner men have happened with the OU women as well in recent years with more than 11000 fans attending the Sooners game with Villanova in the 2002 NCAA second round and the game with the Tennessee Lady Vols in December 2003. Display cases highlighting the careers of Wayman Tisdale Alvan Adams Sooners drafted by the NBA as well as the Final Four appearances three for the men and one for the women are featured on the upper concourse. The Lloyd Noble Center also was the first arena in the Big 12 Conference to feature a state-of-the-art video scoreboard that debuted in 1997. Recently the center underwent a $17.1 million renovation that added more than 70000 square feet on the south side of the facility. Included in the addition are the Mary Jane Noble Womens Basketball Complex the Bob and Ann Coleman Men's Basketball Complex and the Bruce Drake Men's Basketball Practice Court. Each side of the addition is a mirror image of the other with a full size practice court coaches offices film rooms spa/sauna rooms team areas and team locker rooms. The addition includes a sports medicine facility and strength and conditioning center that are used by both teams. Also within the structure are the Kerr McGee Courtside Club that provides seating for up to 250 for meetings and banquets and the TipIn Club Lobby that features displays on the history of both programs. The addition gives the center more flexibility to host events other than athletic competitions. OU and area high schools have held commencement ceremonies in the center and it has hosted a number of concerts and large meetings. As part of the renovation project the parking lot was enlarged additional lighting in the parking lot was added the arena's ceiling was renovated and the electrical systems were modernized. The end result is an arena that is second to none in the country and is the envy collegiate basketball programs nationwide.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//McCasland Field House
    	values.put(C_MD_ID, 69);
    	values.put(C_MD_NAME, "McCasland Field House");
    	values.put(C_MD_X, 35208003);
    	values.put(C_MD_Y, -97442273);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Monnet Hall
    	values.put(C_MD_ID, 70);
    	values.put(C_MD_NAME, "Monnet Hall");
    	values.put(C_MD_X, 35209353);
    	values.put(C_MD_Y, -97445014);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Mosier Indoor Athletic Facility
    	values.put(C_MD_ID, 71);
    	values.put(C_MD_NAME, "Mosier Indoor Athletic Facility");
    	values.put(C_MD_X, 35204795);
    	values.put(C_MD_Y, -97437960);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Murray Case Sells Swim Center
    	values.put(C_MD_ID, 72);
    	values.put(C_MD_NAME, "Murray Case Sells Swim Center");
    	values.put(C_MD_X, 35197523);
    	values.put(C_MD_Y, -97443635);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//National Weather Center
    	values.put(C_MD_ID, 73);
    	values.put(C_MD_NAME, "National Weather Center");
    	values.put(C_MD_X, 35181451);
    	values.put(C_MD_Y, -97439322);
    	values.put(C_MD_DESC, "The National Weather Center a $67 million facility is a unique confederation of federal state and University of Oklahoma organizations that work together to improve understanding of events occurring in the earth's atmosphere. The 17 organizations comprising the NWC include such National Oceanic and Atmospheric Administration entities as the National Severe Storms Laboratory Storm Prediction Center National Weather Service Norman Forecast Office Warning Decision Training Branch and part of the Radar Operations Center. Organizations in the center work with a wide range of federal state and local government agencies to help reduce loss of life and property to hazardous weather ensure wise use of water resources enhance agricultural production and develop renewable energy sources. They also work with private sector partners to develop new applications of weather and regional climate information that provide competitive advantage in the marketplace.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Nielsen Hall
    	values.put(C_MD_ID, 74);
    	values.put(C_MD_NAME, "Nielsen Hall");
    	values.put(C_MD_X, 35207092);
    	values.put(C_MD_Y, -97446715);
    	values.put(C_MD_DESC, "Nielsen Hall later renamed for J. Rud Nielsen a distinguished member of the OU physics faculty was originally the Research Institute Building. Built in 1948 Nielsen Hall houses the Homer L. Dodge Department of Physics and Astronomy offices laboratories and classrooms. In the late 1990s it underwent a $4.8 million expansion to include large classrooms and lecture demonstration rooms. A second expansion completed in 2004 contains offices for the Physics and Astronomy faculty as well as a large teaching laboratory. A Foucault pendulum suspended between this expansion and the 1948 building can be viewed from the South Oval.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Noble Electron Microscopy Laboratory
    	values.put(C_MD_ID, 75);
    	values.put(C_MD_NAME, "Noble Electron Microscopy Laboratory");
    	values.put(C_MD_X, 35206274);
    	values.put(C_MD_Y, -97445024);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Nuclear Engineering Laboratory
    	values.put(C_MD_ID, 76);
    	values.put(C_MD_NAME, "Nuclear Engineering Laboratory");
    	values.put(C_MD_X, 35208961);
    	values.put(C_MD_Y, -97443289);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Administration Building
    	values.put(C_MD_ID, 77);
    	values.put(C_MD_NAME, "OCCE Administration Building");
    	values.put(C_MD_X, 35197996);
    	values.put(C_MD_Y, -97444601);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Cross Center Main
    	values.put(C_MD_ID, 78);
    	values.put(C_MD_NAME, "OCCE Cross Center Main");
    	values.put(C_MD_X, 35198649);
    	values.put(C_MD_Y, -97441940);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE McCarter Hall of Advanced Studies
    	values.put(C_MD_ID, 79);
    	values.put(C_MD_NAME, "OCCE McCarter Hall of Advanced Studies");
    	values.put(C_MD_X, 35198726);
    	values.put(C_MD_Y, -97444831);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Office Annex
    	values.put(C_MD_ID, 80);
    	values.put(C_MD_NAME, "OCCE Office Annex");
    	values.put(C_MD_X, 35192289);
    	values.put(C_MD_Y, -97432284);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Sooner Suites
    	values.put(C_MD_ID, 81);
    	values.put(C_MD_NAME, "OCCE Sooner Suites");
    	values.put(C_MD_X, 35198851);
    	values.put(C_MD_Y, -97446827);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//OCCE Thurman J. White Forum Building
    	values.put(C_MD_ID, 82);
    	values.put(C_MD_NAME, "OCCE Thurman J. White Forum Building");
    	values.put(C_MD_X, 35197926);
    	values.put(C_MD_Y, -97445314);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Observatory and Landscape Department
    	values.put(C_MD_ID, 83);
    	values.put(C_MD_NAME, "Observatory and Landscape Department");
    	values.put(C_MD_X, 35202450);
    	values.put(C_MD_Y, -97443678);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Oklahoma Geological Survey
    	values.put(C_MD_ID, 84);
    	values.put(C_MD_NAME, "Oklahoma Geological Survey");
    	values.put(C_MD_X, 35190202);
    	values.put(C_MD_Y, -97440449);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Oklahoma Memorial Union
    	values.put(C_MD_ID, 85);
    	values.put(C_MD_NAME, "Oklahoma Memorial Union");
    	values.put(C_MD_X, 35209134);
    	values.put(C_MD_Y, -97444378);
    	values.put(C_MD_DESC, "Oklahoma Memorial Union is the companion tribute with Oklahoma Memorial Stadium to OU students killed in World War I. A popular gathering place for generations of students since 1928 the Union underwent extensive renovation beginning in 1995 to improve its ability to serve the campus community. The Union features the Molly Shi Boren Ballroom; Meacham Auditorium (named for arts and sciences dean Edgar Meacham); restored Beaird Lounge (named for Union director Ted Beaird); and the Stuart Landing which features a 6-foot bronze OU seal (a gift from the Class of 1998) and a 5-foot 2- inch bronze sculpture titled The Seed Sower which appears on the official OU seal. The face on the figure is a likeness of OU's first president David Ross Boyd. Both pieces were sculpted by Paul Moore OU sculptor-in-residence. Two murals on the east wall of the main entrance depict a World War I soldier and nurse. The Will Rogers Room features an 83-foot mural highlighting the life of Oklahoma's favorite son Will Rogers. It was painted by Mary Scofield in 1950. A new 72-foot mural titled The 20th Century at OU by faculty member Louise Jones adds special artistic interest to the food court area. In addition the Union houses a branch of the Visitor Center; the Clarke-Anderson Will Rogers Justice Alma Wilson John M. Houchin Louise Houchin Weitzenhoffer and David F. Schrage OU Traditions rooms; David L. Boren Lounge; the Flint Learning Center computer lab; a food court; a branch bookstore; and a broadcasting booth for the student radio station. The Union is a center for student activities and contains offices for Student Affairs the Center for Student Life Career Services the University of Oklahoma Alumni Association the University Club the Henry W. Sr. and Claudine Browne Game Room Crossroads restaurant a post office and the Archie W. Dunham-Conoco Student Leadership Center completed in 2000 that brings together 60 major student organizations.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Oklahoma Memorial Union Parking Center
    	values.put(C_MD_ID, 86);
    	values.put(C_MD_NAME, "Oklahoma Memorial Union Parking Center");
    	values.put(C_MD_X, 35210070);
    	values.put(C_MD_Y, -97444351);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Old Faculty Club
    	values.put(C_MD_ID, 87);
    	values.put(C_MD_NAME, "Old Faculty Club");
    	values.put(C_MD_X, 35211749);
    	values.put(C_MD_Y, -97446438);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Old Science Hall
    	values.put(C_MD_ID, 88);
    	values.put(C_MD_NAME, "Old Science Hall");
    	values.put(C_MD_X, 35208926);
    	values.put(C_MD_Y, -97446298);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//One Partners' Place
    	values.put(C_MD_ID, 89);
    	values.put(C_MD_NAME, "One Partners' Place");
    	values.put(C_MD_X, 35182543);
    	values.put(C_MD_Y, -97436871);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Our Children's World Learning Center
    	values.put(C_MD_ID, 90);
    	values.put(C_MD_NAME, "Our Children's World Learning Center");
    	values.put(C_MD_X, 35190991);
    	values.put(C_MD_Y, -97444134);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Physical Plant Complex
    	values.put(C_MD_ID, 91);
    	values.put(C_MD_NAME, "Physical Plant Complex");
    	values.put(C_MD_X, 35209185);
    	values.put(C_MD_Y, -97441891);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Physical Sciences Center
    	values.put(C_MD_ID, 92);
    	values.put(C_MD_NAME, "Physical Sciences Center");
    	values.put(C_MD_X, 35209332);
    	values.put(C_MD_Y, -97447092);
    	values.put(C_MD_DESC, "Physical Sciences Center commonly called the Blender because of its shape was designed to be riot-proof in 1969 a time of nationwide campus demonstrations protesting the Vietnam War. The building’s occupants include the departments of Mathematics History of Science and Human Relations; the Women’s Studies Program; the Chemistry and Mathematics Library; and the Arts and Sciences Computer Laboratory.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Price Hall
    	values.put(C_MD_ID, 93);
    	values.put(C_MD_NAME, "Price Hall");
    	values.put(C_MD_X, 35207892);
    	values.put(C_MD_Y, -97443589);
    	values.put(C_MD_DESC, "Price Hall named in honor of Wall Street investor and OU alumnus Michael F. Price opened in the summer of 2005. Designed to match the collegiate Gothic style of Adams Hall Price Hall blends traditional beauty with advanced technological capabilities and is dedicated entirely to student use activities and support including classrooms advising offices and computer labs. The interior areas have natural light high ceilings and natural wood tones. The 55000-square-foot addition has eight study rooms and nine new high-tech classrooms in addition to the expanded Business Communication Center and the Graduate Student Support Center. Adams Hall and Price Hall house the Price College of Business.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Reaves' Park Building (City of Norman)
    	values.put(C_MD_ID, 94);
    	values.put(C_MD_NAME, "Reaves' Park Building (City of Norman)");
    	values.put(C_MD_X, 35194555);
    	values.put(C_MD_Y, -97437898);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Reynolds Performing Arts Center
    	values.put(C_MD_ID, 95);
    	values.put(C_MD_NAME, "Reynolds Performing Arts Center");
    	values.put(C_MD_X, 35210164);
    	values.put(C_MD_Y, -97446333);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Rhyne Hall
    	values.put(C_MD_ID, 96);
    	values.put(C_MD_NAME, "Rhyne Hall");
    	values.put(C_MD_X, 35208385);
    	values.put(C_MD_Y, -97440695);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Richards Hall
    	values.put(C_MD_ID, 97);
    	values.put(C_MD_NAME, "Richards Hall");
    	values.put(C_MD_X, 35207118);
    	values.put(C_MD_Y, -97444568);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Robertson Hall
    	values.put(C_MD_ID, 98);
    	values.put(C_MD_NAME, "Robertson Hall");
    	values.put(C_MD_X, 35206198);
    	values.put(C_MD_Y, -97447487);
    	values.put(C_MD_DESC, "Robertson Hall constructed in 1926 as a women's dormitory is named for Ann Worcester Robertson a missionary to the Creek Nation. She is credited with translating the entire New Testament of the Bible and most of the Old Testament into the Creek language. Converted to office spaces in 1972 its current occupants are the offices of the Graduate College, Parking and Transit Services, and Cleveland Area Rapid Transit.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sam Noble Oklahoma Museum of Natural History
    	values.put(C_MD_ID, 99);
    	values.put(C_MD_NAME, "Sam Noble Oklahoma Museum of Natural History");
    	values.put(C_MD_X, 35194318);
    	values.put(C_MD_Y, -97449065);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sam Viersen Gymnastics Center
    	values.put(C_MD_ID, 100);
    	values.put(C_MD_NAME, "Sam Viersen Gymnastics Center");
    	values.put(C_MD_X, 35190338);
    	values.put(C_MD_Y, -97443801);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sarkeys Energy Center
    	values.put(C_MD_ID, 101);
    	values.put(C_MD_NAME, "Sarkeys Energy Center");
    	values.put(C_MD_X, 35210440);
    	values.put(C_MD_Y, -97440353);
    	values.put(C_MD_DESC, "Sarkeys Energy Center was completed in 1991 at that time the most ambitious construction project ever attempted in Oklahoma higher education. Its $50 million cost was underwritten by public funds combined with private gifts from alumni and industry founders corporations foundations and a major grant from the Sarkeys Foundation of Norman. The center is organized into several component institutes and programs. Collectively it represents important neutral ground where investigatory dealings with all aspects of energy can pursue research at the crossroads of disciplines. The center also houses the College of Geosciences the Oklahoma School of Geology and Geophysics School of Meteorology and Department of Geography. The engineering schools of Chemical Engineering and Materials Science, the Mewbourne School of Petroleum and Geological Engineering, the Oklahoma Geological Survey, and the Laurence S. Youngblood Energy Library are also located in the Center.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Soccer Complex/John Crain Field
    	values.put(C_MD_ID, 102);
    	values.put(C_MD_NAME, "Soccer Complex/John Crain Field");
    	values.put(C_MD_X, 35188453);
    	values.put(C_MD_Y, -97448798);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Soccer Practice Field
    	values.put(C_MD_ID, 103);
    	values.put(C_MD_NAME, "Soccer Practice Field");
    	values.put(C_MD_X, 35186684);
    	values.put(C_MD_Y, -97448793);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Softball Complex/Marita Hynes Field
    	values.put(C_MD_ID, 104);
    	values.put(C_MD_NAME, "Softball Complex/Marita Hynes Field");
    	values.put(C_MD_X, 35194954);
    	values.put(C_MD_Y, -97442026);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Stephenson Life Sciences Research Center
    	values.put(C_MD_ID, 105);
    	values.put(C_MD_NAME, "Stephenson Life Sciences Research Center");
    	values.put(C_MD_X, 35185919);
    	values.put(C_MD_Y, -97440572);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Stephenson Research and Technology Center
    	values.put(C_MD_ID, 106);
    	values.put(C_MD_NAME, "Stephenson Research and Technology Center");
    	values.put(C_MD_X, 35183718);
    	values.put(C_MD_Y, -97440310);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Sutton Hall
    	values.put(C_MD_ID, 107);
    	values.put(C_MD_NAME, "Sutton Hall");
    	values.put(C_MD_X, 35208828);
    	values.put(C_MD_Y, -97447183);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Theta M. Dempsey Transportation Operation Center
    	values.put(C_MD_ID, 108);
    	values.put(C_MD_NAME, "Theta M. Dempsey Transportation Operation Center");
    	values.put(C_MD_X, 35188974);
    	values.put(C_MD_Y, -97434719);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Three Partners' Place
    	values.put(C_MD_ID, 109);
    	values.put(C_MD_NAME, "Three Partners' Place");
    	values.put(C_MD_X, 35183718);
    	values.put(C_MD_Y, -97438067);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Traditions Square East
    	values.put(C_MD_ID, 110);
    	values.put(C_MD_NAME, "Traditions Square East");
    	values.put(C_MD_X, 35192902);
    	values.put(C_MD_Y, -97446258);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Traditions Square West
    	values.put(C_MD_ID, 111);
    	values.put(C_MD_NAME, "Traditions Square West");
    	values.put(C_MD_X, 35190575);
    	values.put(C_MD_Y, -97450856);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Two Partners' Place
    	values.put(C_MD_ID, 112);
    	values.put(C_MD_NAME, "Two Partners' Place");
    	values.put(C_MD_X, 35182885);
    	values.put(C_MD_Y, -97435996);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//University of Oklahoma Foundation Building
    	values.put(C_MD_ID, 113);
    	values.put(C_MD_NAME, "University of Oklahoma Foundation Building");
    	values.put(C_MD_X, 35195747);
    	values.put(C_MD_Y, -97444655);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Wagner Dining Facility
    	values.put(C_MD_ID, 114);
    	values.put(C_MD_NAME, "Wagner Dining Facility");
    	values.put(C_MD_X, 35204234);
    	values.put(C_MD_Y, -97440321);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Walker Tower
    	values.put(C_MD_ID, 115);
    	values.put(C_MD_NAME, "Walker Tower");
    	values.put(C_MD_X, 35201481);
    	values.put(C_MD_Y, -97444773);
    	values.put(C_MD_DESC, "Walker Center was the third component of the 1967 residence hall expansion project. Originally called Couch North the tower was renamed for E. A. Walker a prominent Oklahoma City banker and a major donor to the University. Walker accommodates 1300 residents in a suite system of two rooms with adjoining bath. This 12-story facility contains a computer lab and offices for Housing and Food Services main office. Study lounges are located on each floor with a laundry facility in the basement. There is a convenience store located on the first floor. There is also a 24-hour monitored quiet study lounge within the tower. Special-interest living options include a National Scholars floor Quiet life-styles and a Scholastic floor. A Faculty-In-Residence apartment was added in the fall of 1998.");
    	db.insert(T_MAP_DATA, null, values);
    	values.clear();
    	//Whitehand Hall
    	values.put(C_MD_ID, 116);
    	values.put(C_MD_NAME, "Whitehand Hall");
    	values.put(C_MD_X, 35211703);
    	values.put(C_MD_Y, -97445260);
    	values.put(C_MD_DESC, "tmp");
    	db.insert(T_MAP_DATA, null, values);
    }
    
}
