package com.teamtreehouse.ribbit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * 			  This class supports the MainActivity.java class by helping
 * 			  with fragment adaption incuding changing minor details such
 * 			  as title.
 *
 * 			  This project was created while following the teamtreehouse.com
 * 			  Build a Self-Destructing Message Android App project
 *
 * @version   Completed Feb 18, 2014
 * @author    Thomas Hervey <thomasahervey@gmail.com>
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	protected Context mContext;

	public SectionsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
	}

    /**
     * getItem is called to instantiate the fragment for the given page.
     * Return a DummySectionFragment (defined as a static inner class
     * below) with the page number as its lone argument.
     *
     * @param  position -
     * @return none
     */
	@Override
	public Fragment getItem(int position) {
		
		switch(position) {
			case 0:
				return new InboxFragment();
			case 1:
				return new FriendsFragment();
		}

		return null;
	}

    /**
     * ?
     *
     * @param
     * @return int count
     */
	@Override
	public int getCount() {
		return 2;
	}

    /**
     * Retrieve and set the title based on which fragment is displayed
     *
     * @param  position
     * @return none
     */
	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mContext.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return mContext.getString(R.string.title_section2).toUpperCase(l);
		}
		return null;
	}
}