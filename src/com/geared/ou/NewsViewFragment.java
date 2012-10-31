package com.geared.ou;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class NewsViewFragment extends SherlockFragment {
	private SlidingFragmentActivity a;
	private Context c;
	private OUApplication app;
	private WebView webView;
	private String uri;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		a = (SlidingFragmentActivity)getActivity();
        c = a.getApplicationContext();
        app = (OUApplication) a.getApplication();
        app.setCurrentFragment(OUApplication.FRAGMENT_VIEW_NEWS);
        setHasOptionsMenu(false);
        setRetainInstance(true);
        
        uri = getArguments().getString("url");
        
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle(R.string.oudaily);
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
        webView = new WebView(c);
        webView.getSettings().setBuiltInZoomControls(true); 
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);   
        webView.getSettings().setAllowFileAccess(false); 
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(uri);
        
        LinearLayout v = new LinearLayout(c);
        v.addView(webView);
        
        return v;
    }
}
