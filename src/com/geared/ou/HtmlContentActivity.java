package com.geared.ou;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class HtmlContentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html_content);
		TextView text = (TextView)findViewById(R.id.htmlContentText);
		if (text == null)
			Log.d("OU", "text is null?");
		String html = getIntent().getStringExtra("html");
		if (html == null)
			Log.d("OU", "html is null");
		text.setText(Html.fromHtml(html));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.html_content, menu);
		return true;
	}

}
