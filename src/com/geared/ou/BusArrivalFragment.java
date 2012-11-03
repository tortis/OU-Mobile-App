package com.geared.ou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

/** This fragment shows the estimated arrival time for a bus
 * on a given route and stop. The data is pulled from cartgps.com
 * and the route and stop are set in the application object by
 * different fragments.
 * @author David Findley
 *
 */
public class BusArrivalFragment extends SherlockFragment {
	
	/** The SlidingFragmentActivity that displays this fragment. */
	private SlidingFragmentActivity a;
	
	/** The display context for this fragment. */
	private Context c;
	
	/** The application object for persistent data. */
	private OUApplication app;
	
	/** The top level view for this fragment. */
	private LinearLayout tlc;
	
	/** The an instance of the Load object which, when executed,
	 * pulls the bus arrival data. */
	private Load updateThread;
	
	/** This is the base url where the arrival data will be pulled from.
	 * The actual url is of the form: http://cartgps.com/simple/routes/{route#}/stops/{stop#} */
	private static final String url = "http://cartgps.com/simple/routes/";
	
	/** The TextView that will hold the arrival data string pulled from the Web. */
	private TextView arrivalTextView;
	private String arrivalString;
	
	/** In this method the persistent app object is retrieved to get the current route and
	 * and stop, then execute the Load object to get the. The actionbar is also setup here.
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		/* Get some object references for easy access.*/
		a = (SlidingFragmentActivity)getActivity();
        c = a.getApplicationContext();
        app = (OUApplication) a.getApplication();
        app.setCurrentFragment(OUApplication.FRAGMENT_ARRIVAL_TIME);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        
        /* Setup the ActionBar. */
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle(R.string.bus_arrival);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
        /* Get references to views that were inflated. */
        tlc = (LinearLayout)inflater.inflate(R.layout.bus_arrival, container, false);
        arrivalTextView = (TextView) tlc.findViewById(R.id.arrival_text_view);
        
        /* Fetch the data and then display it. */
        setStatusTextViewToUpdating();
        updateThread = new Load();
        updateThread.execute();

        return tlc;
    }
	
	/** This class is an AsyncTask that that fetches the arrival data from the url
	 * using HttpClient. In the onPostExecute method, the fragment view is updated
	 * to show the information.
	 * @author David Findley
	 *
	 */
	private class Load extends AsyncTask<Integer, Integer, Boolean> {
		
		/** This method will run in a different thread, but it can still access
		 * data from the containing class, like the app object. Use the HttpClient
		 * to get the source of the set url.
		 */
        @Override
        protected Boolean doInBackground(Integer... s) {
        	String source = "";
        	try 
        	{
        		/* Create and execute the http request. */
	        	HttpClient httpclient = new DefaultHttpClient();
	        	HttpGet httpget = new HttpGet(url+app.getCurrentRoute()+"/stops/"+app.getCurrentStop());
	            HttpResponse response = httpclient.execute(httpget);
	            
	            /* Create a buffered input stream reader to get the page source. */
	            BufferedReader mReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            
	            /* Read in the source line by line. */
	            String line;
	            while ((line = mReader.readLine()) != null)
	            {
	                source = source + line;
	            }
	            
	            /* Set the retrieved source in the application object. */
	            app.setArrivalSource(source);
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
        
        /** In this method the display is updated if everything went well,
         * otherwise the user is told that the
         * arrival data could not be fetched.
         */
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
	
	/** This method will pull the desired information from the 
	 * Web page source. It is highly dependent on the source and
	 * likely to break if the source changes.
	 */
	private void parseSource()
	{
		Document doc = Jsoup.parse(app.getArrivalSource());
		Elements allULs = doc.getElementsByTag("ul");
		arrivalString = allULs.get(1).children().get(0).text();
	}
	
	/** Inflate the classes_menu options button, which contains a refresh
	 * button.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.classes_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/** When the refresh options button is pressed (the only one),
	 * then execute updateThread again. This will also update the
	 * display automatically.
	 */
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
	
	/** When this fragment is detached, it should check if the 
	 * updateThread object is being executed. If it is, then cancel
	 * the AsyncTask and proceed with detach.
	 */
	@Override
	public void onDetach() {
		if (updateThread != null)
		{
			if (updateThread.getStatus() == AsyncTask.Status.RUNNING)
				updateThread.cancel(false);
		}
		super.onDetach();
	}
	
	/** This method should call the parse source method to extract the
	 * deisred information, then update the Views that were inflated
	 * in onCreateView.
	 */
	protected void updateDisplay() {
		parseSource();
		
		/* If there was a TextView added to show that the fragment was updating,
		 * then remove it at this point. */
    	if (tlc.getChildAt(0).getId() == R.id.updateTextView)
    		tlc.removeViewAt(0);
    	
    	/* Update the textView with the parsed string. */
    	arrivalTextView.setText(arrivalString);
    }
	
	/** This method will add a TextView at the beginning of the top level View
	 * that indicates the information is updating. It also contains an animation.
	 */
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

}
