package com.geared.ou;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class PrefsFragment extends SherlockFragment implements View.OnClickListener {
	
	LinearLayout setUserName, setPassword;
	
	private OUApplication app;
    private Context c;
    private SlidingFragmentActivity a;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		a = (SlidingFragmentActivity)getActivity();
        c =a.getApplicationContext();
        app = (OUApplication) a.getApplication();
        app.setCurrentFragment(OUApplication.FRAGMENT_PREFS);
        
        ActionBar ab = a.getSupportActionBar();
        if (ab != null)
        {
        	ab.setIcon(R.drawable.side_menu_button);
        	ab.setTitle("Set Credientials");
        	ab.setDisplayHomeAsUpEnabled(true);
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        
        View tlc = inflater.inflate(R.layout.pref_layout, container, false);
        setUserName = (LinearLayout)tlc.findViewById(R.id.setUserName);
        setPassword = (LinearLayout)tlc.findViewById(R.id.setPassword);
        setUserName.setOnClickListener(this);
        setPassword.setOnClickListener(this);

        return tlc;
    }

	public void onClick(View v) {
		if (v.getId() == R.id.setUserName)
		{
			Log.d("OU", "Set username clicked.");
			showDialog("Username", app.getUser());
		}
		else if (v.getId() == R.id.setPassword)
		{
			Log.d("OU", "Set password clicked.");
			showDialog("Password", app.getPrefs().getString("password", ""));
		}
		else
			Log.d("OU", "Unknown ID");
		
	}
	
	private void showDialog(String title, String oldData) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction();
        Fragment prev = a.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        GetStringDialogFragment newFragment = new GetStringDialogFragment();
        newFragment.setTitle(title, oldData);
        newFragment.show(ft, "dialog");
    }
}
