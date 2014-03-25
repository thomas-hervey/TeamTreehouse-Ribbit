package com.teamtreehouse.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 			  This class supports the MainActivity.java class by handling
 * 			  the list of recipients when a message is sent. The list of
 * 			  recipients is generated based on the selected friends
 * 			  from the sender's friends list.
 *
 * 			  This project was created while following the teamtreehouse.com
 * 			  Build a Self-Destructing Message Android App project
 *
 * @version   Completed Feb 18, 2014
 * @author    Thomas Hervey <thomasahervey@gmail.com>
 */
public class RecipientsActivity extends ListActivity {

	public static final String TAG = RecipientsActivity.class.getSimpleName();

    // parse recipient variables
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;	
	protected List<ParseUser> mFriends;	
	protected MenuItem mSendMenuItem;

    // media variables
	protected Uri mMediaUri;
	protected String mFileType;

    /**
     * Initial create method that retrieves intent data about
     * recipient list
     *
     * @param  savedInstanceState
     * @return none
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recipients);
		// Show the Up button in the action bar.
		setupActionBar();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		mMediaUri = getIntent().getData();
		mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
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
		
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		setProgressBarIndeterminateVisibility(true);
		
		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
            setProgressBarIndeterminateVisibility(false);

            // get recipient usernames
            if (e == null) {
                mFriends = friends;

                String[] usernames = new String[mFriends.size()];
                int i = 0;
                for(ParseUser user : mFriends) {
                    usernames[i] = user.getUsername();
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getListView().getContext(),
                        android.R.layout.simple_list_item_checked,
                        usernames);
                setListAdapter(adapter);
            }
            // error
            else {
                Log.e(TAG, e.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                builder.setMessage(e.getMessage())
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

    /**
     * Menu inflater
     *
     * @param  menu
     * @return none
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);
		mSendMenuItem = menu.getItem(0);
		return true;
	}

    /**
     * Menu options to send user home or send message
     *
     * @param  item
     * @return none
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

        // return home
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

        // send message
		case R.id.action_send:
			ParseObject message = createMessage();
			if (message == null) {
				// error
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.error_selecting_file)
					.setTitle(R.string.error_title)
					.setPositiveButton(android.R.string.ok,  null);
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}
			else {
				send(message);
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * Determine menu item visibility based on the number of checked items.
     *
     * @param  l
     * @param  v
     * @param  position
     * @param  id
     * @return none
     */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if (l.getCheckedItemCount() > 0) {
			mSendMenuItem.setVisible(true);
		}
		else {
			mSendMenuItem.setVisible(false);
		}
	}

    /**
     * Create the message to be sent to the user's friend recipients.
     * The message is generated when the file name is set, the parse
     * file is created, and the file is attached to the parse file.
     *
     * @param
     * @return ParseObject message - the message to send
     */
	protected ParseObject createMessage() {

        // add attributes to parse object message
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
		
		byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
		
		if (fileBytes == null) {
			return null;
		}
		else {
			// we have an image
			if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
				// reduce image size
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);
			}
			// Set file name, create parse file, and attach file to parseFile
			String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);
			message.put(ParseConstants.KEY_FILE, file);

			return message;
		}
	}

    /**
     *
     * @return
     */
	protected ArrayList<String> getRecipientIds() {
		ArrayList<String> recipientIds = new ArrayList<String>();
		for (int i = 0; i < getListView().getCount(); i++) {
			if (getListView().isItemChecked(i)) {
				recipientIds.add(mFriends.get(i).getObjectId());
			}
		}
		return recipientIds;
	}

    /**
     *
     * @param message
     */
	protected void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
            if (e == null) {
                // success!
                Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                builder.setMessage(R.string.error_sending_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok,  null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
			}
		});
	}
}






