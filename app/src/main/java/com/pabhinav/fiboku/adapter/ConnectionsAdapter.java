package com.pabhinav.fiboku.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.pabhinav.fiboku.R;

import java.util.List;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class ConnectionsAdapter extends android.widget.BaseAdapter {

    private Context context;
    private Cursor cursor;
    private int inflateLayoutId;
    private LayoutInflater layoutInflater;
    List<String> rpnData;
    @Setter
    ConnectionItemClickListener connectionItemClickListener;

    public ConnectionsAdapter(Context context, Cursor cursor, int inflateLayoutId, List<String> rpnData){
        this.context = context;
        this.cursor = cursor;
        this.inflateLayoutId = inflateLayoutId;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.rpnData = rpnData;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** Holder class for this adapter **/
    class ConnectionAdapterHolder {

        /**
         * The circular Image View for setting display icon
         */
        CircularImageView circularImageView;

        /**
         * Text View to show name of the contact
         */
        TextView nameTextView;

        /**
         * Text View to show phone number of the contact
         */
        TextView phoneNumberTextView;

        /**
         * Text View denoting whether a connection is using is app or not
         */
        TextView usingAppTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /** Get the convertView **/
        View view = convertView;

        /** Holder instance **/
        ConnectionAdapterHolder connectionAdapterHolder;

        /** Moving cursor **/
        cursor.moveToPosition(position);

        /** if convertView is null, its first inflation for list item view **/
        if(view == null){
            view = layoutInflater.inflate(inflateLayoutId, parent, false);
            connectionAdapterHolder = new ConnectionAdapterHolder();
            connectionAdapterHolder.circularImageView = (CircularImageView) view.findViewById(R.id.contact_photo);
            connectionAdapterHolder.nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            connectionAdapterHolder.phoneNumberTextView = (TextView) view.findViewById(R.id.phone_number);
            connectionAdapterHolder.usingAppTextView = (TextView) view.findViewById(R.id.using_app_text_view);
            view.setTag(connectionAdapterHolder);
        } else {
            /** else, get the holder from the view's tag **/
            connectionAdapterHolder = (ConnectionAdapterHolder) view.getTag();
        }

        /** Try to get the image for a given contact **/
        try {
            connectionAdapterHolder.circularImageView.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                            context.getContentResolver(),
                            Uri.parse(cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI)))
                    )
            );
        } catch (Exception e){

            /** If can't get the image, set the default image **/
            connectionAdapterHolder.circularImageView.setImageResource(R.drawable.contact);
        }

        /** Set the phone number **/
        connectionAdapterHolder.phoneNumberTextView.setText(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));

        /** Set the name **/
        connectionAdapterHolder.nameTextView.setText(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));

        /** If phone number is in rpn data, mark connection as using this app **/
        String phoneNumber = connectionAdapterHolder.phoneNumberTextView.getText().toString();
        if(rpnData.contains(phoneNumber)
                || (phoneNumber.length() >0 && rpnData.contains(phoneNumber.substring(3)))){
            connectionAdapterHolder.usingAppTextView.setVisibility(View.VISIBLE);
        } else {
            connectionAdapterHolder.usingAppTextView.setVisibility(View.GONE);
        }

        if(connectionItemClickListener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectionItemClickListener.onItemClicked(v);
                }
            });
        }

        /** return the inflated view **/
        return view;
    }


    public interface ConnectionItemClickListener{
        void onItemClicked(View v);
    }
}
