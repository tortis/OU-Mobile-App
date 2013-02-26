package com.geared.ou;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.geared.ou.ClassesData.Course;
import com.geared.ou.ContentData.ContentItem;
import com.geared.ou.D2LSourceGetter.SGError;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ContentFragment extends SherlockFragment implements OnNavigationListener, View.OnClickListener {
	
	private LinearLayout layoutContent;
    private int classId;
    private Course course;
    private ContentData content;
    
    private Boolean busy;
    ProgressDialog dlDialog;
    
    private OUApplication app;
    private Context c;
    private SlidingFragmentActivity a;
    private update updateThread;
    private Download downloadThread;
    
    private ClipDrawable downloadingBackground;
    private String htmlContent;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	a = (SlidingFragmentActivity)getActivity();
		c = a.getApplicationContext();
		app = (OUApplication) a.getApplication();
		app.setCurrentFragment(OUApplication.FRAGMENT_CONTENT);
		classId = app.getCurrentClass();
		course = app.getClasses().getCourse(classId);
		content = course.getContent();
		setRetainInstance(true);
		busy = false;
		
		setHasOptionsMenu(true);
		
		ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setTitle(course.getPrefix());
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        	SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(c, R.array.class_nav, R.layout.drop_down_nav_item);
        	ab.setListNavigationCallbacks(mSpinnerAdapter, this);
        	ab.setSelectedNavigationItem(1);
        }
        
        ScrollView scroll = new ScrollView(c);
        layoutContent = new LinearLayout(c);
        layoutContent.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(layoutContent);
        
        /* Pull content from D2L or DB and display. */
        if (content.needsUpdate()) {
            updateDisplay(false);
            setStatusTextViewToUpdating();
            updateThread = new update();
            updateThread.execute();
        } else {
            updateDisplay(false);
        }
        
        return scroll;
    }
	
	/**
	 * When the user navigates to a different fragment we need to cancel the update
	 * and download threads, otherwise the app may crash.
	 */
	@Override
	public void onDetach() {
		if (updateThread != null)
		{
			if (updateThread.getStatus() == AsyncTask.Status.RUNNING)
				updateThread.cancel(false);
		}
		if (downloadThread != null)
		{
			if (downloadThread.getStatus() == AsyncTask.Status.RUNNING)
				downloadThread.cancel(false);
		}
		super.onDetach();
	}
	
	/**
	 * This method will be called when the user navigates away from the content fragment.
	 * If a user opens one of the files, then a new activity will be launched. In this case the fragment
	 * will not be detached. So here we need to cancel the updateThread if it is still running.
	 * Eg. A user navigates to the content fragment and immediately clicks a link before it finishes updating.
	 * If we do not cancel the updateTread, then it will crash when it tries to reutrn.
	 */
	public void onStop()
	{
		if (updateThread != null)
		{
			if (updateThread.getStatus() == AsyncTask.Status.RUNNING)
				updateThread.cancel(false);
		}
		super.onStop();
	}
	
	
	private class update extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... s) {
            return content.update();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update percentage
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                updateDisplay(false);
            }
            else {
                Toast.makeText(c, R.string.failedSourceUpdate, Toast.LENGTH_LONG).show();
            }
        }
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.classes_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.refreshClasses:
            	content.forceNextUpdate();
                setStatusTextViewToUpdating();
                updateThread = new update();
                updateThread.execute();
                break;
            default:
            	break;
        }
        return true;
    }
	
	public class Download extends AsyncTask<Integer, Double, Integer> {
        private File file;
        private int dlSize;
        private double percent;
        private int id;
        
        Context context;
        
        private Download(Context context) {
            this.context = context.getApplicationContext();
        }
        
        @Override
        protected Integer doInBackground(Integer... ids) {
            percent = 0.0D;
            id = ids[0];
            ContentItem ci = content.getItem(id);
            TextView tmp = (TextView) layoutContent.findViewById(ci.getId());
            Log.d("OU", "width: "+tmp.getWidth());
            // Get direct link and extract the file name.
            String dLink = "";
            dLink = getDirectLink(ci);
            Log.d("OU", dLink);
            if (dLink.equals("=-1")) {
            	if (htmlContent == null)
            		return 1;
            	else
            	{
            		return 3;
            	}
            }
            String fileName = ci.getId()+getFileNameFromDirectLink(dLink);

            // Create file on SD card.
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/ou"); // Literal! =(
            dir.mkdirs();
            file = new File(dir, fileName);

            if (!file.exists()) {
                Log.d("OU", "The file does not exist, downlading...");
                SGError e = app.getSourceGetter().downloadFile(file, dLink, this);
                if (e != SGError.NO_ERROR) {
                    return 2;
                }
            }
            file.setReadable(true);
            return 0;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            downloadingBackground.setLevel(1000 + (int)(9000*values[0]));
        }
        
        public void publicProgressUpdate(int p) {
            if ((double)p/(double)dlSize - percent > 0.01D) {
                percent = (double)p/(double)dlSize;
                publishProgress(percent);
            }
        }
        
        public void setTotalDownloadSize(int s) {
            dlSize = s;
            //dlDialog.setMax(s);
            //dlDialog.setIndeterminate(false);
        }

        @Override
        protected void onPostExecute(Integer result) {
            busy = false;
            super.onPostExecute(result);
            TextView clickedItem = (TextView)layoutContent.findViewById(id);
            clickedItem.setBackgroundResource(R.drawable.content_list_button_selector);
            if (result == 1) {
                Toast.makeText(c, R.string.failedToGetDLink, Toast.LENGTH_SHORT).show();
            }
            else if (result == 2) {
                Toast.makeText(c, R.string.downloadFailed, Toast.LENGTH_SHORT).show();
            }
            else if (result == 3)
            {
            	Intent displayHtml = new Intent(context, HtmlContentActivity.class);
            	displayHtml.putExtra("html", htmlContent);
            	displayHtml.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	context.startActivity(displayHtml);
            }
            else
            {
                // Try to open the file!
                try {
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String type = fileNameMap.getContentTypeFor(Uri.fromFile(file).toString());
                    Log.d("OU", "File type: "+type);
                    Intent openFile = new Intent(Intent.ACTION_VIEW);
                    openFile.setDataAndType(Uri.fromFile(file), type);
                    startActivity(openFile);
                }
                catch (ActivityNotFoundException e1) {
                    Log.d("OU", "No application found to open: "+file.toString());
                    Toast.makeText(c, R.string.noAppToOpenFile, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
	
	public void onClick(View v) {
        if (!busy) {
            v.setBackgroundResource(R.drawable.download_progress);
            downloadingBackground = (ClipDrawable)v.getBackground();
            downloadingBackground.setLevel(1000);
            downloadThread = new Download(c);
            downloadThread.execute(v.getId());
            busy = true;
        }
    }

	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		FragmentManager fragManager = a.getSupportFragmentManager();
		switch(itemPosition)
		{
			case 0:			// Home
				ClassHomeFragment classHomeFragment = new ClassHomeFragment();
				FragmentTransaction fragHomeTrans = fragManager.beginTransaction();
				fragHomeTrans.replace(R.id.top_level_container, classHomeFragment, "main_fragment");
				fragHomeTrans.commit();
				break;
			case 1:			// Content
				break;
			case 2:			// Grades
				GradesFragment gradesFragment = new GradesFragment();
				FragmentTransaction fragGradesTrans = fragManager.beginTransaction();
				fragGradesTrans.replace(R.id.top_level_container, gradesFragment, "main_fragment");
				fragGradesTrans.commit();
				break;
				
			case 3:			// Roster
				RosterFragment rosterFragment = new RosterFragment();
				FragmentTransaction fragRosterTrans = fragManager.beginTransaction();
				fragRosterTrans.replace(R.id.top_level_container, rosterFragment, "main_fragment");
				fragRosterTrans.commit();
				break;
			default:
				break;
		}
		return false;
	}
	
	private String getDirectLink(ContentItem ci) {
        /***********************************************************************
         *                      START specialized code
         **********************************************************************/
        String src;
        String r = "=-1";
        SGError e = app.getSourceGetter().pullSource("http://learn.ou.edu/d2l/m/le/content/"+ci.getOuId()+"/topic/view/"+ci.getId());
        Log.d("OU", "CID: "+ci.getId());
        if (e != SGError.NO_ERROR)
            Log.d("OU", "There was an error!");
        src = app.getSourceGetter().getPulledSource();
        Document doc = Jsoup.parse(src);
        Elements finds = doc.getElementsByAttributeValueContaining("href", "/content/enforced/"); // Literal! =(
        if (finds.size()!=1) {
            finds = doc.getElementsByAttributeValueContaining("src", "/content/enforced");
            if (finds.size() == 1) {
                r = "http://learn.ou.edu"+finds.first().attr("src");
            }
            else
            {
            	htmlContent="";
            	Element contentBody = doc.getElementsByTag("body").first();
            	int count = 0;
            	for (Element ele:contentBody.children())
            	{
            		if (count != 0 || count != 1)
            		{
            			htmlContent += ele.html();
            		}
            		
            		++count;
            	}
            }
        }
        else {
            r = "http://learn.ou.edu"+finds.first().attr("href");
        }
        r = r.replaceAll(" ", "%20");
        return r;
        /***********************************************************************
         *                       END specialized code
         **********************************************************************/
    }
	
	private String getFileNameFromDirectLink(String dLink) {
        String[] t = dLink.split("/");
        String a = t[t.length-1];
        a = a.replace(' ', '_');
        a = a.replaceAll("%20", "_");
        return a;
    }
	
	private void updateDisplay(Boolean updateFailed) {
        // Remove all elements from the content layout, except the first one.
        layoutContent.removeAllViews();
        Map<String,ArrayList<ContentItem>> cont = content.getContent();
        ArrayList<String> cat = content.getCategories();
        
        if (cont == null || cat == null)
            return;
        for (String s : cat) {
            addSpacer(layoutContent, Color.BLACK, 2);
            TextView t = new TextView(c);
            t.setText(s);
            t.setWidth(layoutContent.getWidth());
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setTextColor(Color.argb(255, 75, 25, 25));
            t.setTextSize(15);
            t.setTypeface(null, Typeface.BOLD);
            t.setPadding(10, 6, 10, 6);
            t.setHorizontalFadingEdgeEnabled(true);
            t.setFadingEdgeLength(35);
            t.setSingleLine(true);
            layoutContent.addView(t);
            addSpacer(layoutContent, Color.BLACK, 1);
            for (ContentItem ci : cont.get(s)) {
                addSpacer(layoutContent, Color.BLACK, 1);
                TextView tv = new TextView(c);
                tv.setText(ci.getName());
                tv.setId(ci.getId());
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(Color.DKGRAY);
                tv.setTextSize(15);
                tv.setPadding(12, 15, 10, 15);
                tv.setHorizontalFadingEdgeEnabled(true);
                tv.setFadingEdgeLength(35);
                tv.setSingleLine(true);
                if (ci.hasLink())
                {
	                tv.setOnClickListener(this);
	                tv.setClickable(true);
                }
                tv.setBackgroundResource(R.drawable.content_list_button_selector);
                Drawable img = getResources().getDrawable(getIconForType(ci.getType()));
                img.setBounds(0, 0, 45, 40);
                tv.setCompoundDrawables(img, null, null, null);
                layoutContent.addView(tv);
            }
        }
        addSpacer(layoutContent, Color.BLACK, 1);
        TextView t = new TextView(c);
         if (content.needsUpdate() || updateFailed) {
             Drawable img = getResources().getDrawable(R.drawable.ic_small_alert);
             img.setBounds(0, 0, 30, 25);
             t.setCompoundDrawables(img, null, null, null);
         }
        t.setText(getString(R.string.lastUpdateTitle)+" "+content.getLastUpdate().toString());
        t.setGravity(Gravity.TOP);
        t.setWidth(layoutContent.getWidth());
        t.setPadding(7, 3, 3, 3);
        t.setTextColor(Color.GRAY);
        t.setTextSize(13);
        t.setId(R.id.updateTextView);
        layoutContent.addView(t);
    }
	
	protected void setStatusTextViewToUpdating() {
        final AnimationDrawable img = new AnimationDrawable();
        img.addFrame(a.getResources().getDrawable(R.drawable.loading1), 150);
        img.addFrame(a.getResources().getDrawable(R.drawable.loading2), 150);
        img.addFrame(a.getResources().getDrawable(R.drawable.loading3), 150);
        img.addFrame(a.getResources().getDrawable(R.drawable.loading4), 150);
        img.setBounds(0, 0, 30, 30);
        img.setOneShot(false);
        
        TextView updateTV = (TextView)layoutContent.findViewById(R.id.updateTextView);
        if (updateTV == null) {
            updateTV = new TextView(c);
            updateTV.setText(R.string.updating);
            updateTV.setCompoundDrawables(img, null, null, null);
            updateTV.setGravity(Gravity.TOP);
            updateTV.setWidth(layoutContent.getWidth());
            updateTV.setPadding(15, 3, 3, 3);
            updateTV.setTextColor(Color.BLACK);
            updateTV.setTextSize(13);
            updateTV.setId(R.id.updateTextView);
            layoutContent.addView(updateTV);
        } else {
            updateTV.setCompoundDrawables(img, null, null, null);
            updateTV.setText(R.string.updating);
            updateTV.setTextColor(Color.BLACK);
        }
        updateTV.post(new Runnable() {
            public void run() {
                img.start();
            }
        });
        
    }
	
	public int getIconForType(String type) {
        if (type.equals("Adobe Acrobat Document"))
            return R.drawable.pdf_icon;
        else if (type.equals("Word Document"))
            return R.drawable.doc_icon;
        else if (type.equals("PowerPoint Presentation"))
            return R.drawable.ppt_icon;
        else if (type.equals("Video File"))
            return R.drawable.video_icon;
        else if (type.equals("Flash File"))
            return R.drawable.swf_icon;
        else if (type.equals("Web Page"))
            return R.drawable.web_icon;
        else if (type.equals("WMV File"))
            return R.drawable.video_icon;
        else
            return R.drawable.unknown_icon;
    }
	
	private void addSpacer(LinearLayout l, int color, int height) {
        TextView t = new TextView(c);
        t.setWidth(l.getWidth());
        t.setHeight(height);
        t.setBackgroundColor(color);
        l.addView(t);
    }
}
