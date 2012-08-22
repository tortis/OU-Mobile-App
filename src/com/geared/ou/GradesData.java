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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * This is a data wrapper class that supports the GradesActivity class. It is
 * initialized in the Application object. It stores all of the grade data that
 * is either pulled from D2L or the local database.
 * 
 */
public class GradesData {
    
    public class Grade {
        protected float totalPoints;
        protected float pointsEarned;
        protected float percentage;
        protected String grade;
        protected String name;
        protected int id;
        
        public Grade(String name, String grade, int id) {
            this.grade = grade;
            this.name = name;
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
        
        public String getScore() {
            return grade;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public class Category {
        protected String name;
        protected ArrayList<Grade> grades;
        protected Float total;
        protected Float earned;
        protected Float percentage;
        
        public Category(String name) {
            this.name = name;
            grades = new ArrayList<Grade>();
        }
        
        public String getName() {
            return name;
        }
        
        public void addGrade(Grade g) {
            grades.add(g);
            recomputePoints();
        }
        
        public Boolean removeGrade(Grade g) {
            //  TO DO
            return true;
        }
        
        public ArrayList<Grade> getGrades() {
            return grades;
        }
        
        private void recomputePoints() {
            for (Iterator<Grade> i = grades.iterator(); i.hasNext();) {
                Grade g = i.next();
                // TO DO
            }
        }
    }
    
    private ArrayList<Category> categories;
    private String gradesSource;
    protected Date lastUpdate;
    protected Boolean force;
    protected static final String GRADES_URL_PRE = "http://learn.ou.edu/d2l/m/le/grades/";
    protected static final String GRADES_URL_SUF = "/list";
    protected ClassesData.Course course;
    protected OUApplication app;
    protected String dump;
    private static final long UPDATE_INTERVAL = 86400000L;
    
    public GradesData(OUApplication app, ClassesData.Course course) {
        force = false;
        this.app = app;
        this.course = course;
        lastUpdate = new Date();
        lastUpdate.setTime(lastUpdate.getTime() - UPDATE_INTERVAL);
        categories = new ArrayList<Category>();
        dump = "";
    }
    
    public Boolean update() {
        app.updateSGCredentials();
        D2LSourceGetter sg = app.getSourceGetter();
        // If this update was force, unset force
        if (force) {
            force = false;
        }
        Log.d("OU", GRADES_URL_PRE+course.getOuId()+GRADES_URL_SUF);
        SGError result = sg.pullSource(GRADES_URL_PRE+course.getOuId()+GRADES_URL_SUF);
        if (result != SGError.NO_ERROR)
            return false;
        gradesSource = sg.getPulledSource();
        if (!pullGrades())
            return false;
        return true;
    }
    
    private Boolean pullGrades() {
        Document doc = Jsoup.parse(gradesSource);
        gradesSource = null;
        
        /***********************************************************************
         *                      START specialized code
         **********************************************************************/
        int test;
        Elements results = doc.getElementsContainingOwnText("Grade Items");
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
        
        lastUpdate = new Date();
        writeToDb();
        return true;
    }
    
    private Boolean populateFromDb() {
        categories.clear();
        SQLiteDatabase db = app.getDb();
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
        
        db.close();
        return true;
    }
    
    public Boolean needsUpdate() {
        if (categories.isEmpty())
            populateFromDb();
        if (categories.isEmpty())
            return true;
        if ((new Date().getTime() - lastUpdate.getTime() > UPDATE_INTERVAL))
            return true;
        
        return false;
    }
    
    private Boolean writeToDb() {
        SQLiteDatabase db = app.getDb();
        db.rawQuery("delete from grades where user='"+app.getUser()+"' and ou_id="+course.getOuId(), null);
        ContentValues values = new ContentValues();
        
        for (Category c : categories) {
            for (Grade g : c.getGrades()) {
                values.clear();
                values.put(DbHelper.C_GRA_ID, g.getId());
                values.put(DbHelper.C_GRA_NAME, g.getName());
                values.put(DbHelper.C_GRA_USER, app.getUser());
                values.put(DbHelper.C_GRA_CATEGORY, c.getName());
                values.put(DbHelper.C_GRA_OUID, course.getOuId());
                values.put(DbHelper.C_GRA_LAST_UPDATE, (int)((lastUpdate.getTime())/1000));
                values.put(DbHelper.C_GRA_SCORE, g.getScore());
                db.insert(DbHelper.T_GRADES, null, values);
            }
        }
        db.close();
        return true;
    }
    
    public void forceNextUpdate() {
        force = true;
    }
    
    public String gradeDump() {
        return dump;
    }
    
    public ArrayList<Category> getGrades() {
        return categories;
    }
    
    public Date getLastUpdate() {
        return lastUpdate;
    }
}
