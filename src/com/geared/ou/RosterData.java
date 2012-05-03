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
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.geared.ou.D2LSourceGetter.SGError;
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
        //Build Roster URL
        String ROSTER_URL = "http://learn.ou.edu/d2l/common/popup/popup.d2l?ou="+<CLASSID>+"&queryString=ou%3D"+<CLASSID>+"%26tabid%3D2%26tabname%3DAll%26isalltab%3D1%26gc%3D%26gn%3D%26sort%3DLastName%26sortdir%3Dasc%26ulst%3D"+<IDLIST>+"&footerMsg=&popBodySrc=/d2l/lms/classlist/admin/classlist_print.d2l&popFooterSrc=footer.d2l&width=550&height=580&hasStatusBar=false&hasAutoScroll=false";
        //result = sg.pullSource(ROSTER_URL);
        //if (result != SGError.NO_ERROR)
        //    return false;
        //rosterSource = sg.getPulledSource();
        //if (!pullRoster())
        //    return false;
        return true;
    }
    
    private Boolean pullIdsFromPreRoster() {
        Document doc = Jsoup.parse(preRosterSource);
        preRosterSource = null;
        Elements es = doc.getElementsByAttributeValueMatching("name", "HDN_tabUserIds"); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! LITERAL
        allTabIds = es.first().attr("value"); //!!!!!!!!!!!!!!!!!!!! LITERAL
        Log.d("OU", allTabIds);
        if (allTabIds == null)
            return false;
        return true;
    }
    
    private Boolean pullRoster() {
        /*Document doc = Jsoup.parse(rosterSource);
        rosterSource = null;*/
        
        /***********************************************************************
         *                      START specialized code
         **********************************************************************/
        
        /*Elements results = doc.getElementsContainingOwnText("Grade Items");
        if (results.size() != 1)
            return false;
        Element gradesDiv = results.first().nextElementSibling().child(0);
        Elements categoryList = gradesDiv.children();
        categories.clear();
        // Loop through each category
        int counter = 0;
        for (Element categoryLi : categoryList) {
            String categoryName = categoryLi.children().first().children().first().children().first().text();
            Category c = new Category(categoryName);
            // Loop through each item:
            if (categoryLi.children().size() > 1) {
                for (Element itemLi : categoryLi.child(1).children()) {
                    String itemName = itemLi.children().first().children().first().text();
                    String itemGrade = itemLi.children().first().child(1).text();
                    Grade g = new Grade(itemName, itemGrade, (course.getOuId()+counter));
                    Log.d("OU", ""+course.getId());
                    c.addGrade(g);
                    counter++;
                }
            }
            categories.add(c);
        }
        
        /***********************************************************************
         *                       END specialized code
         **********************************************************************/
        
        /*lastUpdate = new Date();
        writeToDb();*/
        return true;
    }
    
    private Boolean populateFromDb() {
        people.clear();
        /*SQLiteDatabase db = app.getDb();
        Cursor result = db.rawQuery("select * from grades where user='"+app.getUser()+"' and ou_id="+course.getOuId(), null);
        if (result.getCount() < 1) {
            return false;
        }
        int counter = 0;
        while(result.moveToNext())
        {
            if(counter == 0)        
                lastUpdate.setTime(((long)(result.getInt(result.getColumnIndex(DbHelper.C_GRA_LAST_UPDATE))))*1000);
            String name = result.getString(result.getColumnIndex(DbHelper.C_GRA_NAME));
            String grade = result.getString(result.getColumnIndex(DbHelper.C_GRA_SCORE));
            String category = result.getString(result.getColumnIndex(DbHelper.C_GRA_CATEGORY));
            int id = result.getInt(result.getColumnIndex(DbHelper.C_GRA_ID));
            Log.d("OU", "item name: "+name);
            Grade g = new Grade(name, grade, id);
            
            // If no categories are loaded yet, aka this is the first row, then add category:
            if (categories.isEmpty()) {
                categories.add(new Category(category));
            }
            // If the category of the current row does not equal the most recently
            // added category, then create the new category.
            else if (!categories.get(categories.size()-1).getName().equals(category)) {
                categories.add(new Category(category));
            }
            // Finially add the ContentItem to the current category:
            categories.get(categories.size()-1).addGrade(g);
            counter++;
        }
        
        db.close();*/
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
