package com.teamtreehouse.ribbit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 			  This class supports the MainActivity.java class by providing
 * 			  setting up the inbox fragment that displays a user's list
 * 			  of inbox messages. This includes restarting upon app resume.
 *
 * 			  This project was created while following the teamtreehouse.com
 * 			  Build a Self-Destructing Message Android App project
 *
 * @version   Completed Feb 18, 2014
 * @author    Thomas Hervey <thomasahervey@gmail.com>
 */
public class InboxFragment extends ListFragment {
	
	protected List<ParseObject> mMessages;

    /**
     * Generate fragment view
     *
     * @param  inflater
     * @param  container
     * @param  savedInstanceState
     * @return View rootView
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox,
				container, false);

		return rootView;
	}

    /**
     * On application resume, set up friends fragment as if
     * the application had been running
     *
     * @param
     * @return none
     */
	@Override
	public void onResume() {
		super.onResume();
		
		getActivity().setProgressBarIndeterminateVisibility(true);
		
		// Query parse object
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
		// look at all IDs in the KEY_RECIPIENTS_IDS list, and see if there is a match 
		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, com.parse.ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				
				// We found messages
				if (e == null) {
					mMessages = messages;
					
					String[] usernames = new String[mMessages.size()];
					int i = 0;
					for(ParseObject message : mMessages) {
						usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					// create message adapter
					if (getListView().getAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
						setListAdapter(adapter);
					}
					// refill the adapter
					else {
						((MessageAdapter)getListView().getAdapter()).refill(mMessages);
					}
				}
			}
		});
	}

    /**
     * When an inbox item is clicked, the self-destructing message and its information
     * is retrieved. A new intent for the message is started, the message runs, and
     * then is deleted locally and remotely.
     *
     * @param  l
     * @param  v
     * @param  position
     * @param  id
     * @return none
     */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		
		// get parse file for this message
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		Uri fileUri = Uri.parse(file.getUrl());
		// view the image in new activity
		if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
			Intent intent = new Intent(getActivity(), ViewImageActivity.class);
			intent.setData(fileUri);
			startActivity(intent);
		}
		// view the video
		else {
			Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
			intent.setDataAndType(fileUri, "video/*");
			startActivity(intent);
		}
		// delete the message
		
		// check recipients
		List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
		// last recipient, delete the whole message
		if (ids.size() == 1) {
			message.deleteInBackground();
		}
		// remove the recipient and save
		else {
			// remove locally
			ids.remove(ParseUser.getCurrentUser().getObjectId());
			// remove remotely by updating recipients array
			ArrayList<String> idsToRemove = new ArrayList<String>();
			idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
			message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
			message.saveInBackground();
		}
	}
}
