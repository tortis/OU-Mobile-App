package com.geared.ou;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class GetStringDialogFragment extends SherlockDialogFragment implements View.OnClickListener {
	
	OUApplication app;
	private Context c;
    private NewsActivity a;
    
    String title;
    String oldData;
    EditText mTextField;
    
    private static final int OK_BUTTON = 1;
    private static final int CANCEL_BUTTON = 1;
    
    public void setTitle(String title, String oldData)
    {
		this.title = title;
		this.oldData = oldData;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		a = (NewsActivity)getActivity();
		c = a.getApplicationContext();
		app = (OUApplication) a.getApplication();
		getDialog().setTitle(title);
		
		mTextField = new EditText(c);
		mTextField.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 55));
		mTextField.setTextSize(20);
		mTextField.setPadding(10, 0, 5, 0);
		mTextField.setText(oldData);
		mTextField.setTextColor(Color.BLACK);
		if (title == getResources().getString(R.string.password))
			mTextField.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		TextView ok = new TextView(c);
		TextView cancel = new TextView(c);
		
		ok.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f));
		cancel.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f));
		
		ok.setBackgroundResource(R.drawable.content_list_button_selector);
		ok.setText(R.string.ok);
		ok.setOnClickListener(this);
		ok.setId(OK_BUTTON);
		ok.setClickable(true);
		ok.setTextColor(Color.BLACK);
		ok.setTextSize(14);
		ok.setGravity(Gravity.CENTER);
		ok.setPadding(0, 20, 0, 20);
		
		cancel.setBackgroundResource(R.drawable.content_list_button_selector);
		cancel.setText(R.string.cancel);
		cancel.setOnClickListener(this);
		cancel.setId(CANCEL_BUTTON);
		cancel.setClickable(true);
		cancel.setTextColor(Color.BLACK);
		cancel.setTextSize(14);
		cancel.setGravity(Gravity.CENTER);
		cancel.setPadding(0, 20, 0, 20);
		
		LinearLayout buttonContainer = new LinearLayout(c);
		buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout tlc = new LinearLayout(c);
		tlc.setOrientation(LinearLayout.VERTICAL);
		
		View horizontalSpacer = new View(c);
		horizontalSpacer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		horizontalSpacer.setBackgroundColor(Color.LTGRAY);
		
		View horizontalSpacerBlank = new View(c);
		horizontalSpacerBlank.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10));
		
		View verticalSpacer = new View(c);
		verticalSpacer.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT));
		verticalSpacer.setBackgroundColor(Color.LTGRAY);
		
		buttonContainer.addView(cancel);
		buttonContainer.addView(verticalSpacer);
		buttonContainer.addView(ok);
		
		tlc.addView(mTextField);
		tlc.addView(horizontalSpacerBlank);
		tlc.addView(horizontalSpacer);
		tlc.addView(buttonContainer);
		
		return tlc;
	}
    
	public void onClick(View v) {
		if (v.getId() == OK_BUTTON)
		{
			SharedPreferences prefs = app.getPrefs();
	    	SharedPreferences.Editor mEditor = prefs.edit();
	    	if (title == getResources().getString(R.string.username))
	    		mEditor.putString("username", mTextField.getText().toString());
	    	if (title == getResources().getString(R.string.password))
	    		mEditor.putString("password", mTextField.getText().toString());
	    	mEditor.commit();
			this.dismiss();
		}
		else
		{
			this.dismiss();
		}
	}

	@Override
	public void onDetach() {
		SharedPreferences prefs = app.getPrefs();
    	SharedPreferences.Editor mEditor = prefs.edit();
    	if (title == getResources().getString(R.string.username))
    		mEditor.putString("username", mTextField.getText().toString());
    	if (title == getResources().getString(R.string.password))
    		mEditor.putString("password", mTextField.getText().toString());
    	mEditor.commit();
		super.onDetach();
	}
	
	

}
