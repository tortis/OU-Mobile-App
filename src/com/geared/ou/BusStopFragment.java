package com.geared.ou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BusStopFragment extends SherlockFragment implements View.OnClickListener {
	
	private SlidingFragmentActivity a;
	private Context c;
	private OUApplication app;
	private LinearLayout tlc;
	private ListView stopList;
	private Load updateThread;
	private static String url = "http://cartgps.com/simple/routes/";
	private LinkedItemAdapter stopAdapter;
	private ArrayList<ListItem> stopAList;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		a = (SlidingFragmentActivity)getActivity();
        c = a.getApplicationContext();
        app = (OUApplication) a.getApplication();
        app.setCurrentFragment(OUApplication.FRAGMENT_STOP_LIST);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle(R.string.bus_stops);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
        tlc = (LinearLayout)inflater.inflate(R.layout.bus_routes, container, false);
        stopList = (ListView)tlc.findViewById(R.id.routeList);
        stopAList = new ArrayList<ListItem>();
        
        setStatusTextViewToUpdating();
        updateThread = new Load();
        updateThread.execute();

        return tlc;
    }
	
	private class Load extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... s) {
        	String source = "";
        	try 
        	{
	        	HttpClient httpclient = new DefaultHttpClient();
	        	HttpGet httpget = new HttpGet(url+app.getCurrentRoute()+"/stops/");
	            HttpResponse response = httpclient.execute(httpget);
	            
	            BufferedReader mReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            String line;
	            while ((line = mReader.readLine()) != null)
	            {
	                source = source + line;
	            }
	            app.setStopSource(source);
	        } 
	        catch (IOException ex)
	        {
	            return false;
	        }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update percentage
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result != false)
            	updateDisplay();
            else
            {
            	TextView t = new TextView(c);
            	t.setTextColor(Color.BLACK);
            	t.setTextSize(16);
            	t.setPadding(5, 5, 5, 5);
            	t.setText("Could not load bus data. :(");
            	tlc.removeViewAt(0);
            	tlc.addView(t, 0);
            }
        }
    }
	
	private void parseSource()
	{
		Document doc = Jsoup.parse(app.getStopSource());
		Elements allULs = doc.getElementsByTag("ul");
		Elements items = allULs.get(1).children();
		for (Element e:items)
		{
			String fetchedUrl = e.child(0).attr("href");
			Pattern intsOnly = Pattern.compile("\\d++");
			Matcher makeMatch = intsOnly.matcher(fetchedUrl);
			makeMatch.find();
			makeMatch.find();
			String inputInt = makeMatch.group();
			String name = e.text();
			int id = Integer.parseInt(inputInt);
			Log.d("OU", "id: "+id);
			stopAList.add(new ListItem(name, id));
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
            	setStatusTextViewToUpdating();
                updateThread = new Load();
                updateThread.execute();
                break;
            default:
            	break;
        }
        return true;
    }
	
	@Override
	public void onDetach() {
		if (updateThread != null)
		{
			if (updateThread.getStatus() == AsyncTask.Status.RUNNING)
				updateThread.cancel(false);
		}
		super.onDetach();
	}
	
	protected void updateDisplay() {
		parseSource();
    	if (tlc.getChildAt(0).getId() == R.id.updateTextView)
    		tlc.removeViewAt(0);
        stopAdapter = new LinkedItemAdapter(c, stopAList, this);
        stopList.setAdapter(stopAdapter);
    }
	
	private void setStatusTextViewToUpdating() {
        final AnimationDrawable img = new AnimationDrawable();
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading1), 150);
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading2), 150);
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading3), 150);
        img.addFrame(getActivity().getResources().getDrawable(R.drawable.loading4), 150);
        img.setBounds(0, 0, 30, 30);
        img.setOneShot(false);
        TextView updateTV = (TextView)tlc.findViewById(R.id.updateTextView);
        if (updateTV == null) {
            updateTV = new TextView(c);
            updateTV.setText(R.string.updating);
            updateTV.setCompoundDrawables(img, null, null, null);
            updateTV.setGravity(Gravity.TOP);
            updateTV.setWidth(tlc.getWidth());
            updateTV.setPadding(15, 3, 3, 3);
            updateTV.setTextColor(Color.BLACK);
            updateTV.setTextSize(13);
            updateTV.setId(R.id.updateTextView);
            tlc.addView(updateTV, 0);
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

	public void onClick(View v) {
		int position = v.getId();
		ListItem listItem = stopAList.get(position);
		app.setCurrentStop(listItem.id);
		
		FragmentManager fragmentManager = a.getSupportFragmentManager();
		BusArrivalFragment busArrivalFragment = new BusArrivalFragment();
		FragmentTransaction fragBusStopsTrans = fragmentManager.beginTransaction();
		fragBusStopsTrans.replace(R.id.top_level_container, busArrivalFragment, "main_fragment");
		fragBusStopsTrans.commit();
	}
}
