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
public class MessageAdapter extends ArrayAdapter<ParseObject> {

	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
	}
	
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
	
	private static class ViewHolder {
		ImageView iconImageView;
		TextView  nameLabel;
	}
	
	public void refill(List<ParseObject> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}
}
