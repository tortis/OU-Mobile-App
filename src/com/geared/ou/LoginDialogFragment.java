package com.geared.ou;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class LoginDialogFragment extends SherlockDialogFragment implements View.OnClickListener {
	
	OUApplication app;
	private Context c;
    private SlidingFragmentActivity a;
    
    private static final int LOGOUT_BUTTON = 1;
    private static final int NEVERMIND_BUTTON = 2;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		a = (SlidingFragmentActivity)getActivity();
		c = a.getApplicationContext();
		app = (OUApplication) a.getApplication();
		getDialog().setTitle("Would you like to logout?");
		LinearLayout tlc = new LinearLayout(c);
		tlc.setOrientation(LinearLayout.HORIZONTAL);
		tlc.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Button logout = new Button(c);
		Button nevermind = new Button(c);
		logout.setText("Logout");
		logout.setPadding(0, 15, 0, 15);
		nevermind.setText("Neverind");
		LayoutParams lp = new LayoutParams(tlc.getWidth()/2, LayoutParams.WRAP_CONTENT);
		logout.setLayoutParams(lp);
		nevermind.setLayoutParams(lp);
		logout.setOnClickListener(this);
		logout.setTextColor(Color.BLACK);
		nevermind.setOnClickListener(this);
		logout.setId(LOGOUT_BUTTON);
		nevermind.setId(NEVERMIND_BUTTON);
		logout.setBackgroundResource(R.drawable.content_list_button_selector);
		//tlc.addView(logout);
		//tlc.addView(nevermind);
		return logout;
	}

	public void onClick(View v) {
		if (v.getId() == LOGOUT_BUTTON)
		{
			SharedPreferences prefs = app.getPrefs();
	    	SharedPreferences.Editor mEditor = prefs.edit();
	    	mEditor.putString("username", "");
	    	mEditor.putString("password", "");
	    	mEditor.commit();
	    	
	    	((NewsActivity)getActivity()).logout();
	    	
	    	NewsFragment newsFragment = new NewsFragment();
			FragmentTransaction fragNewsTrans = getActivity().getSupportFragmentManager().beginTransaction();
			fragNewsTrans.replace(R.id.top_level_container, newsFragment, "main_fragment");
			fragNewsTrans.commit();
	    	
	    	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	    	LoginDialogFragment prev = (LoginDialogFragment)getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
	        if (prev != null) {
	            ft.remove(prev);
	            prev.dismiss();
	        }
		}
		else
		{
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	    	LoginDialogFragment prev = (LoginDialogFragment)getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
	        if (prev != null) {
	            ft.remove(prev);
	            prev.dismiss();
	        }
		}
	}
	
}
