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
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * This is a data wrapper class that supports the ContentActivity class
 * it is initialized by the Activity object and loads a course content index from
 * either d2l or the local database. It also has functions to allow users to download
 * content files from d2l. Viewing the of the files is not handled by this applicaton.
 * 
 */
public class ContentData {
    
    public class ContentItem {
        
        String name;
        String link;
        String category;
        String type;
        int ouId;
        int id;
        
        public ContentItem(String name, String link, String category, int ouId) {
            type = "";
            this.name = name;
            this.link = link;
            this.category = category;
            this.ouId = ouId;
            setIdFromLink();
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public void setName(String s) {
            name = s;
        }
        
        public String getLink() {
            return link;
        }
        
        public int getOuId() {
            return ouId;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String s) {
            category = s;
        }
        
        public int getId() {
            return id;
        }
        
       private void setIdFromLink() {
           String[] split = link.split("=");
           String sId = split[split.length-1];
           id = Integer.parseInt(sId);
       }
    }
    
    private Date lastUpdate;
    private OUApplication app;
    private ClassesData.Course course;
    private String contentSource;
    private Boolean force;
    private static final long UPDATE_INTERVAL = 86400000L;
    private static final String CONTENT_URL = "https://learn.ou.edu/d2l/lms/content/home.d2l?ou=";
    
    //content maps String: category_name to a List of all ContentItems in that cat.
    private Map<String,ArrayList<ContentItem>> content;
    private ArrayList<String> categories;
    
    public ContentData(OUApplication app, ClassesData.Course course) {
        contentSource = "";
        categories = new ArrayList<String>();
        force = false;
        this.app = app;
        this.course = course;
        lastUpdate = new Date();
        lastUpdate.setTime(lastUpdate.getTime() - UPDATE_INTERVAL);
        content = new HashMap<String,ArrayList<ContentItem>>();
    }
    
    public Boolean update() {
        app.updateSGCredentials();
        D2LSourceGetter sg = app.getSourceGetter();
        // If this update was force, unset force
        if (force) {
            force = false;
        }
        Log.d("OU", CONTENT_URL+course.getOuId());
        SGError result = sg.pullSource(CONTENT_URL+course.getOuId());
        if (result != SGError.NO_ERROR)
            return false;
        contentSource = sg.getPulledSource();
        if (!pullContent())
            return false;
        return true;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // This function is highly specialized to interface with D2L, and is      //
    // likley to break with changes to the structure of the D2L Web page.     //
    ////////////////////////////////////////////////////////////////////////////
    private Boolean pullContent() {
        Document doc = Jsoup.parse(contentSource);
        contentSource = null;
        
        /***********************************************************************
         *                      START specialized code
         **********************************************************************/
        
        // Get the element which is the table containing the content.
        Elements tables = doc.getElementsByAttributeValue("summary", "Course Content");
        if (tables.size() != 1)
            return false;
        Element table = tables.get(0);
        Elements tdsOfInterest = table.getElementsByAttributeValue("class", "d_gn");
        if (tdsOfInterest.isEmpty()) {
            categories.add("Empty");
            content.get(categories.get(categories.size()-1)).add(new ContentItem("No content", "#", "Empty", course.getOuId()));
            return true;
        }
        
        content.clear();
        categories.clear();
        for (Element e : tdsOfInterest) {
            Elements as;
            // If there was no <a> tag, then td was a category, so add arraylist for it.
            if ((as=e.getElementsByTag("a")).isEmpty()) {
                content.put(e.text().trim().replace("&nbsp;", ""), new ArrayList<ContentItem>());
                categories.add(e.text().trim().replace("&nbsp;", ""));
            }
            // Otherwie the <td> contains an <a> tag representing a ContentItem.
            else {
                Element a = as.get(0);
                ContentItem ci = new ContentItem(a.ownText().trim(), "https://learn.ou.edu"+a.attr("href"), categories.get(categories.size()-1), course.getOuId());
                Element img = a.getElementsByTag("img").first();
                ci.setType(img.attr("alt"));
                content.get(categories.get(categories.size()-1)).add(ci);
            }
	}
        cleanContent();
        
        /***********************************************************************
         *                       END specialized code
         **********************************************************************/
        
        lastUpdate = new Date();
        if (!writeToDb())
            return false;
        
        return true;
    }
    
    public Boolean needsUpdate() {
        if (content.isEmpty() && !populateFromDb())
            return true;
        if ((new Date().getTime() - lastUpdate.getTime() > UPDATE_INTERVAL))
            return true;
        
        return false;
    }
    
    private Boolean populateFromDb() {        
        content.clear();
        categories.clear();
        SQLiteDatabase db = app.getDb();
        Cursor result = db.rawQuery("select * from content where user='"+app.getUser()+"' and ou_id="+course.getOuId(), null);
        if (result.getCount() < 1)
            return false;
        int counter = 0;
        while(result.moveToNext())
        {
            if(counter == 0)        
                lastUpdate.setTime(((long)(result.getInt(result.getColumnIndex(DbHelper.C_CON_LAST_UPDATE))))*1000);
            String link = result.getString(result.getColumnIndex(DbHelper.C_CON_LINK));
            String name = result.getString(result.getColumnIndex(DbHelper.C_CON_NAME));
            String category = result.getString(result.getColumnIndex(DbHelper.C_CON_CATEGORY));
            int ouId = result.getInt(result.getColumnIndex(DbHelper.C_CON_OUID));
            String type = result.getString(result.getColumnIndex(DbHelper.C_CON_TYPE));
            
            ContentItem ci = new ContentItem(name, link, category, ouId);
            ci.setType(type);
            
            // If no categories are loaded yet, aka this is the first row, then add category:
            if (categories.isEmpty()) {
                content.put(category, new ArrayList<ContentItem>());
                categories.add(category);
            }
            // If the category of the current row does not equal the most recently
            // added category, then create the new category.
            else if (!categories.get(categories.size()-1).equals(category)) {
                content.put(category, new ArrayList<ContentItem>());
                categories.add(category);
            }
            // Finially add the ContentItem to the current category:
            content.get(categories.get(categories.size()-1)).add(ci);
            counter++;
        }
        
        db.close();
        return true;
    }
    
    private void cleanContent() {
        for (String s : categories) {
            for (ContentItem ci : content.get(s)) {
                ci.setCategory(ci.getCategory().replace("&amp;", "&"));
                ci.setName(ci.getName().replace("&amp;", "&"));
            }
        }
    }
    
    private Boolean writeToDb() {
        SQLiteDatabase db = app.getDb();
        db.rawQuery("delete from content where user='"+app.getUser()+"' and ou_id="+course.getOuId(), null);
        
        ContentValues values = new ContentValues();
        
        for (String s : categories) {
            for (ContentItem ci : content.get(s)) {
                values.clear();
                values.put(DbHelper.C_CON_ID, ci.getId());
                values.put(DbHelper.C_CON_USER, app.getUser());
                values.put(DbHelper.C_CON_NAME, ci.getName());
                values.put(DbHelper.C_CON_LINK, ci.getLink());
                values.put(DbHelper.C_CON_CATEGORY, ci.getCategory());
                values.put(DbHelper.C_CON_LAST_UPDATE, (int)((new Date().getTime())/1000));
                values.put(DbHelper.C_CON_OUID, course.getOuId());
                values.put(DbHelper.C_CON_TYPE, ci.getType());
                db.insert(DbHelper.T_CONTENT, null, values);
            }
        }
        db.close();
        return true;
    }
    
    public void forceNextUpdate() {
        force = true;
    }
    
    //TMP
    public String getSource() {
        return contentSource;
    }
    
    public Map<String,ArrayList<ContentItem>> getContent() {
        return content;
    }
    
    public ArrayList<String> getCategories() {
        return categories;
    }
    
    public Date getLastUpdate() {
        return lastUpdate;
    }
    
    public ContentItem getItem(int id) {
        for (String s : categories) {
            for (ContentItem ci : content.get(s)) {
                if (ci.getId() == id)
                    return ci;
            }
        }
        return new ContentItem("bad", "bad", "bad", -1);
    }
}
