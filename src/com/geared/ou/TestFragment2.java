package com.geared.ou;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestFragment2 extends SherlockFragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context c = getActivity().getApplicationContext();
        LinearLayout l = new LinearLayout(c);
        TextView tv = new TextView(c);
        tv.setText("bar");
        l.addView(tv);

        return l;
    }

}
