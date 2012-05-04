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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geared.ou.D2LSourceGetter.SGError;
import com.geared.ou.GradesData.Category;
import com.geared.ou.GradesData.Grade;
import java.util.ArrayList;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * This is a data wrapper class that supports the GradesActivity class. It is
 * initialized in the Application object. It stores all of the grade data that
 * is either pulled from D2L or the local database.
 * 
 */
public class RosterData {
    
    public class Person {
        protected String role;
        protected String lastName;
        protected String firstName;
        protected int id;
        
        public Person(String firstName, String lastName, String role, int id) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public String getRole() {
            return role;
        }
    }
    
    private ArrayList<Person> people;
    private String rosterSource;
    private String preRosterSource;
    private String allTabIds;
    protected Date lastUpdate;
    protected Boolean force;
    protected static final String PRE_ROSTER_URL_PRE = "http://learn.ou.edu/d2l/lms/classlist/classlist.d2l?ou=";
    protected static final String PRE_ROSTER_URL_SUF = "&tabid=1";
    protected ClassesData.Course course;
    protected OUApplication app;
    private static final long UPDATE_INTERVAL = 2678400000L;
    
    public RosterData(OUApplication app, ClassesData.Course course) {
        force = false;
        this.app = app;
        this.course = course;
        lastUpdate = new Date();
        lastUpdate.setTime(lastUpdate.getTime() - UPDATE_INTERVAL);
        people = new ArrayList<Person>();
    }
    
    public Boolean update() {
        app.updateSGCredentials();
        D2LSourceGetter sg = app.getSourceGetter();
        // If this update was force, unset force
        if (force) {
            force = false;
        }
        
        /* Get the list of student IDs from the roster page. This list of IDs
         * can be sent to a print ids url to get an unabridged list of people.
         */
        SGError result = sg.pullSource(PRE_ROSTER_URL_PRE+course.getOuId()+PRE_ROSTER_URL_SUF);
        if (result != SGError.NO_ERROR)
            return false;
        preRosterSource = sg.getPulledSource();
        if (!pullIdsFromPreRoster())
            return false;
        
        /* Now use the IDs to get the full roster. */
        String ROSTER_URL = "http://learn.ou.edu/d2l/lms/classlist/admin/classlist_print.d2l?ou="+course.getOuId()+"&tabid=2&tabname=All&isalltab=1&gc=&gn=&sort=LastName&sortdir=asc&ulst="+allTabIds+"&d2l_body_type=4";
        result = sg.pullSource(ROSTER_URL);
        if (result != SGError.NO_ERROR)
            return false;
        rosterSource = sg.getPulledSource();
        if (!pullRoster())
            return false;
        return true;
    }
    
    private Boolean pullIdsFromPreRoster() {
        Document doc = Jsoup.parse(preRosterSource);
        preRosterSource = null;
        Elements es = doc.getElementsByAttributeValueMatching("name", "HDN_tabUserIds"); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! LITERAL
        allTabIds = es.first().attr("value"); //!!!!!!!!!!!!!!!!!!!! LITERAL
        if (allTabIds == null)
            return false;
        return true;
    }
    
    private Boolean pullRoster() {
        Document doc = Jsoup.parse(rosterSource);
        rosterSource = null;
        people.clear();
        
        /***********************************************************************
         *                      START specialized code
         **********************************************************************/
        
        Elements results = doc.getElementsByAttributeValueMatching("summary", ".*class participants.*");
        if (results.size() != 1) {
            return false; // There should only be one table with this summary.
        }
        Elements studentTrs = results.first().children().first().children();
                Log.d("OU", "test: "+studentTrs.size());
        if (studentTrs.size() < 3) // The first two elemts are not people.
        {
            if (studentTrs.size() > 0) { // If there are less than 3 elements, empty roster.
                people.add(new Person("The roster for this course appears to be empty. That's weird...", "", "Student", 0));
            }
            else // Something went wrong.
                return false;
        }
        
        for (int i = 2; i < studentTrs.size(); i++) {
            Elements studentDataTds = studentTrs.get(i).children();
            if (studentDataTds.size() < 3)
                return false;
            String n = studentDataTds.get(1).text();
            String r = studentDataTds.get(2).text();
            String[] fnln = n.split(",");
            if (fnln.length != 2)
                return false;
            Person p = new Person(fnln[1].trim(), fnln[0].trim(), r, course.getOuId()+i);
            people.add(p);
        }
        
        /***********************************************************************
         *                       END specialized code
         **********************************************************************/
        
        lastUpdate = new Date();
        writeToDb();
        return true;
    }
    
    private Boolean populateFromDb() {
        people.clear();
        SQLiteDatabase db = app.getDb();
        Cursor result = db.rawQuery("select * from roster where user='"+app.getUser()+"' and ou_id="+course.getOuId(), null);
        if (result.getCount() < 1) {
            return false;
        }
        int counter = 0;
        while(result.moveToNext())
        {
            if(counter == 0)        
                lastUpdate.setTime(((long)(result.getInt(result.getColumnIndex(DbHelper.C_ROS_LAST_UPDATE))))*1000);
            String firstName = result.getString(result.getColumnIndex(DbHelper.C_ROS_FIRST_NAME));
            String lastName = result.getString(result.getColumnIndex(DbHelper.C_ROS_LAST_NAME));
            String role = result.getString(result.getColumnIndex(DbHelper.C_ROS_ROLE));
            int id = result.getInt(result.getColumnIndex(DbHelper.C_ROS_ID));
            Person p = new Person(firstName, lastName, role, id);
            people.add(p);
            counter++;
        }
        
        db.close();
        return true;
    }
    
    public Boolean needsUpdate() {
        if (people.isEmpty())
            populateFromDb();
        if (people.isEmpty())
            return true;
        if ((new Date().getTime() - lastUpdate.getTime() > UPDATE_INTERVAL))
            return true;
        
        return false;
    }
    
    private Boolean writeToDb() {
        SQLiteDatabase db = app.getDb();
        db.rawQuery("delete from roster where user='"+app.getUser()+"' and ou_id="+course.getOuId(), null);
        ContentValues values = new ContentValues();
        
        for (Person p : people) {
            values.clear();
            values.put(DbHelper.C_ROS_ID, p.getId());
            values.put(DbHelper.C_ROS_FIRST_NAME, p.getFirstName());
            values.put(DbHelper.C_ROS_USER, app.getUser());
            values.put(DbHelper.C_ROS_ROLE, p.getRole());
            values.put(DbHelper.C_ROS_OUID, course.getOuId());
            values.put(DbHelper.C_ROS_LAST_UPDATE, (int)((lastUpdate.getTime())/1000));
            values.put(DbHelper.C_ROS_LAST_NAME, p.getLastName());
            db.insert(DbHelper.T_ROSTER, null, values);
        }
        db.close();
        return true;
    }
    
    public void forceNextUpdate() {
        force = true;
    }
    
    public ArrayList<Person> getRoster() {
        return people;
    }
    
    public Date getLastUpdate() {
        return lastUpdate;
    }
}
