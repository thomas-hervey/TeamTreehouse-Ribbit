package com.teamtreehouse.ribbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * 			  This class supports the MainActivity.java class by handling
 * 			  a message that is being viewed. When a message is clicked
 * 			  to be viewed, a new view is created (or updated).
 *
 * 			  This project was created while following the teamtreehouse.com
 * 			  Build a Self-Destructing Message Android App project
 *
 * @version   Completed Feb 18, 2014
 * @author    Thomas Hervey <thomasahervey@gmail.com>
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

	protected Context mContext;
	protected List<ParseObject> mMessages;

    /**
     * Constructor to set up
     * @param context
     * @param messages
     */
	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
	}

    /**
     * Get the new view of the selected message to be viewed.
     *
     * @param  position
     * @param  convertView
     * @param  parent
     * @return View convertView - updated view of the current message
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		// first time, create new view
		if (convertView == null) {
			// Inflate layout with icon and label
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView)convertView.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView)convertView.findViewById(R.id.senderLabel);
			convertView.setTag(holder);
		}
		// reuse view
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		// set data in view
		ParseObject message = mMessages.get(position);
		// set as image or video
		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
			holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
		} else {
			holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
		}
		// set text
		holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
		
		return convertView;
	}

    // generate icon image view and text view that messages will hold
	private static class ViewHolder {
		ImageView iconImageView;
		TextView  nameLabel;
	}

    /**
     * Refill the inbox messages and update the system.
     *
     * @param  messages - inbox messages
     * @return none
     */
	public void refill(List<ParseObject> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}
}
