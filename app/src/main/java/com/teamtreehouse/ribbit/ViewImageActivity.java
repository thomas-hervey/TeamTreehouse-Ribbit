package com.teamtreehouse.ribbit;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 			  This class supports the MainActivity.java class by providing
 * 			  the list of 'predicted' crystal ball responses and randomly
 * 			  assigning one.
 *
 * 			  This project was created while following the teamtreehouse.com
 * 			  Build a Self-Destructing Message Android App project
 *
 * @version   Completed Feb 18, 2014
 * @author    Thomas Hervey <thomasahervey@gmail.com>
 */
public class ViewImageActivity extends Activity {

    /**
     * Initial create method setting up self-destruct
     * characteristics including a the image view,
     * image uri, and a timer. Here picasso is used.
     *
     * @param  savedInstanceState
     * @return none
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		// Show the Up button in the action bar.
		setupActionBar();
		
		ImageView imageView = (ImageView)findViewById(R.id.imageView);
		
		Uri imageUri = getIntent().getData();
		// Picasso, with the current context, load this image into this image view
		Picasso.with(this).load(imageUri.toString()).into(imageView);
		
		// setup timer and delay for message
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				finish();
			}
		}, 10*1000);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

    /**
     * Menu option to return home
     *
     * @param  item
     * @return boolean
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			// 
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
