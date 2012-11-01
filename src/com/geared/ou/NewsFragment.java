package com.geared.ou;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class NewsFragment extends SherlockFragment implements
		View.OnClickListener {

	private OUApplication app;
	private List<SyndEntryImpl> items;
	private ArrayList<SyndEntryImpl> itemsAList;
	private SyndFeed feed;
	private Context c;
	private SlidingFragmentActivity a;
	private Load updateThread;
	private NewsAdapter newsAdapter;
	private ListView newsList;
	private LinearLayout tlc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		a = (SlidingFragmentActivity) getActivity();
		c = a.getApplicationContext();
		app = (OUApplication) a.getApplication();
		app.setCurrentFragment(OUApplication.FRAGMENT_NEWS);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		ActionBar ab = a.getSupportActionBar();
		if (ab != null) {
			ab.setIcon(R.drawable.side_menu_button);
			ab.setTitle(R.string.newsButton);
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}

		tlc = (LinearLayout) inflater.inflate(R.layout.news, container, false);
		newsList = (ListView) tlc.findViewById(R.id.newsList);

		feed = app.getFeed();
		if (feed == null) {
			setStatusTextViewToUpdating();
			updateThread = new Load();
			updateThread.execute();
		} else {
			updateDisplay();
		}

		return tlc;
	}

	private class Load extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... s) {
			Log.d("OU", "Before");
			feed = getMostRecentNews("http://www.oudaily.com/rss/headlines/front/");
			Log.d("OU", "After");
			app.setFeed(feed);
			if (feed == null)
				return false;
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
			else {
				TextView t = new TextView(c);
				t.setTextColor(Color.BLACK);
				t.setTextSize(16);
				t.setPadding(5, 5, 5, 5);
				t.setText("Could not load news feed. :(");
				tlc.removeViewAt(0);
				tlc.addView(t, 0);
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
		switch (item.getItemId()) {
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
		if (updateThread != null) {
			if (updateThread.getStatus() == AsyncTask.Status.RUNNING)
				updateThread.cancel(false);
		}
		super.onDetach();
	}

	protected void updateDisplay() {
		if (tlc.getChildAt(0).getId() == R.id.updateTextView)
			tlc.removeViewAt(0);
		for (Object o: feed.getEntries())
		{
			items.add((SyndEntryImpl) o);
		}
		if (items == null)
			return;
		for (Object o:items)
		{
			itemsAList.add((SyndEntryImpl)o);
		}
		newsAdapter = new NewsAdapter(c, itemsAList, this);
		newsList.setAdapter(newsAdapter);
		Log.d("OU", "num items: " + newsAdapter.getCount());
	}

	public void addSpacer(LinearLayout l, int color, int height, int id, int vis) {
		TextView t = new TextView(c);
		t.setWidth(l.getWidth());
		t.setHeight(height);
		t.setId(id);
		t.setVisibility(vis);
		t.setBackgroundColor(color);
		l.addView(t);
	}

	protected synchronized SyndFeed getMostRecentNews(final String feedUrl) {
		try {
			return retrieveFeed(feedUrl);
		} catch (FetcherException e) {
			return null;
		} catch (FeedException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private SyndFeed retrieveFeed(final String feedUrl) throws IOException,
			FeedException, FetcherException {
		FeedFetcher feedFetcher = new HttpURLFeedFetcher();
		return feedFetcher.retrieveFeed(new URL(feedUrl));
	}

	public void onClick(View v) {
		/* The Title was clicked. */
		if (v.getId() < 100) {
			SyndEntryImpl i = newsAdapter.getItem(v.getId());

			Bundle data = new Bundle();
			data.putString("url", i.getLink());

			FragmentManager fragmentManager = a.getSupportFragmentManager();
			NewsViewFragment newsViewFragment = new NewsViewFragment();
			newsViewFragment.setArguments(data);
			FragmentTransaction fragNewsViewTrans = fragmentManager
					.beginTransaction();
			fragNewsViewTrans.replace(R.id.top_level_container,
					newsViewFragment, "main_fragment");
			fragNewsViewTrans.commit();
		}
		/* Show more/less was clicked. */
		else {
			ImageView showMoreLess = (ImageView) tlc.findViewById(v.getId());
			TextView hidden = (TextView) tlc.findViewById(v.getId() + 100);
			if (hidden != null) {
				if (hidden.getVisibility() == View.GONE) {
					hidden.setVisibility(View.VISIBLE);
					showMoreLess.setImageResource(R.drawable.ic_menu_less);
				} else {
					hidden.setVisibility(View.GONE);
					showMoreLess.setImageResource(R.drawable.ic_menu_more);
				}
			}
		}
		// SyndEntryImpl i = (SyndEntryImpl)items.get(v.getId());
		// if (i == null) {
		// Log.d("OU", "Could not get the clicked view.");
		// return;
		// }
		// TextView tv = (TextView)layoutContent.findViewById(v.getId()+100);
		// TextView sp = (TextView)layoutContent.findViewById(v.getId()+200);
		// int titleIndex = layoutContent.indexOfChild(tv) -2;
		// TextView titleView = (TextView)layoutContent.getChildAt(titleIndex);
		// if (tv.getVisibility() == View.GONE) {
		// tv.setVisibility(View.VISIBLE);
		// sp.setVisibility(View.VISIBLE);
		// titleView.setCompoundDrawables(null, null, showLess, null);
		// }
		// else {
		// tv.setVisibility(View.GONE);
		// sp.setVisibility(View.GONE);
		// titleView.setCompoundDrawables(null, null, showMore, null);
		// }
	}

	private void setStatusTextViewToUpdating() {
		final AnimationDrawable img = new AnimationDrawable();
		img.addFrame(
				getActivity().getResources().getDrawable(R.drawable.loading1),
				150);
		img.addFrame(
				getActivity().getResources().getDrawable(R.drawable.loading2),
				150);
		img.addFrame(
				getActivity().getResources().getDrawable(R.drawable.loading3),
				150);
		img.addFrame(
				getActivity().getResources().getDrawable(R.drawable.loading4),
				150);
		img.setBounds(0, 0, 30, 30);
		img.setOneShot(false);
		TextView updateTV = (TextView) tlc.findViewById(R.id.updateTextView);
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
