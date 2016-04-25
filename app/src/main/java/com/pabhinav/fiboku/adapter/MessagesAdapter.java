package com.pabhinav.fiboku.adapter;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.alertDialogs.FibokuAlertMessageDialog;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.models.Message;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author pabhinav
 */
public class MessagesAdapter extends BaseAdapter<Message> {

    private Context context;

    public MessagesAdapter(int childLayoutResId, ArrayList<Message> mDataset, Context context) {
        super(childLayoutResId, mDataset, context, false, -1);
        this.context = context;
    }

    @Override
    public DataObjectHolder onCreateInitializeDataObjectHolder(View v) {
        return new MessageDataObjectHolder(v);
    }

    @Override
    public DataObjectHolder onCreateFooterInitializeDataObjectHolder(View v) {
        return null;
    }

    @Override
    public void onBindItemViewHolder(DataObjectHolder holder, int position, Message dataObj) {
        MessageDataObjectHolder messageDataObjectHolder = (MessageDataObjectHolder) holder;
        messageDataObjectHolder.messageBody.setText(dataObj.getMessage());
        messageDataObjectHolder.messageTime.setText(new Date(Long.parseLong(dataObj.getTimeForMessage())).toString());
        String currentPhoneNumber = FibokuApplication.getInstance().getPhoneNumber();
        if(currentPhoneNumber.equals(dataObj.getPhoneNumber())
                || currentPhoneNumber.equals(dataObj.getPhoneNumber().substring(3))){
            messageDataObjectHolder.messageSender.setVisibility(View.GONE);
            messageDataObjectHolder.leftFab.setVisibility(View.GONE);
            messageDataObjectHolder.rightFab.setVisibility(View.VISIBLE);
            messageDataObjectHolder.messageReceiver.setVisibility(View.VISIBLE);
        } else {
            messageDataObjectHolder.rightFab.setVisibility(View.GONE);
            messageDataObjectHolder.messageReceiver.setVisibility(View.GONE);
            messageDataObjectHolder.leftFab.setVisibility(View.VISIBLE);
            messageDataObjectHolder.messageSender.setVisibility(View.VISIBLE);
            String displayName = getContactName(context, dataObj.getPhoneNumber());
            if(displayName == null
                    || displayName.length() == 0){
                messageDataObjectHolder.messageSender.setText(dataObj.getPhoneNumber());
            } else {
                messageDataObjectHolder.messageSender.setText(displayName);
            }
        }
    }

    @Override
    public void onBindFooterItemViewHolder(DataObjectHolder holder) {}

    /**
     * Static View Holder Class
     */
    public static class MessageDataObjectHolder extends DataObjectHolder {

        TextView messageSender;
        TextView messageBody;
        TextView messageTime;
        TextView messageReceiver;
        FloatingActionButton leftFab;
        FloatingActionButton rightFab;

        /**
         * Constructor with root view
         *
         * @param itemView root view
         **/
        public MessageDataObjectHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(View rootView) {
            messageSender = (TextView) rootView.findViewById(R.id.message_sender_name_text_view);
            messageBody = (TextView) rootView.findViewById(R.id.message_body_text_view);
            messageTime = (TextView) rootView.findViewById(R.id.time_for_message_text_view);
            messageReceiver = (TextView)rootView.findViewById(R.id.message_receiver_name_text_view);
            leftFab = (FloatingActionButton) rootView.findViewById(R.id.left_fab);
            rightFab = (FloatingActionButton) rootView.findViewById(R.id.right_fab);
        }
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}
