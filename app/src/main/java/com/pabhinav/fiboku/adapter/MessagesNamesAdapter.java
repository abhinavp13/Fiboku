package com.pabhinav.fiboku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.models.MessageNames;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class MessagesNamesAdapter extends android.widget.BaseAdapter {

    private ArrayList<MessageNames> messageNamesArrayList;
    private int inflateLayoutId;
    private LayoutInflater layoutInflater;
    private HashMap<String, Integer> numberUnreadCountMap;

    @Setter
    MessagesNamesItemClickEvent messagesNamesItemClickEvent;

    public MessagesNamesAdapter(Context context, ArrayList<MessageNames> messageNamesArrayList, int inflateLayoutId, HashMap<String, Integer> numberUnreadCountMap){
        this.messageNamesArrayList = messageNamesArrayList;
        this.inflateLayoutId = inflateLayoutId;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.numberUnreadCountMap = numberUnreadCountMap;
    }

    public void updateNumberUnreadCountMap(HashMap<String, Integer> numberUnreadCountMap){
        this.numberUnreadCountMap = new HashMap<>(numberUnreadCountMap);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messageNamesArrayList.size();
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
    class MessagesNamesAdapterHolder {

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
        RelativeLayout messsageCounterRelativeLayout;
        TextView messageCounterTextView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        /** Get the convertView **/
        View view = convertView;

        /** Holder instance **/
        MessagesNamesAdapterHolder messagesNamesAdapterHolder;
        

        /** if convertView is null, its first inflation for list item view **/
        if(view == null){
            view = layoutInflater.inflate(inflateLayoutId, parent, false);
            messagesNamesAdapterHolder = new MessagesNamesAdapterHolder();
            messagesNamesAdapterHolder.circularImageView = (CircularImageView) view.findViewById(R.id.contact_photo);
            messagesNamesAdapterHolder.nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            messagesNamesAdapterHolder.phoneNumberTextView = (TextView) view.findViewById(R.id.phone_number);
            messagesNamesAdapterHolder.messsageCounterRelativeLayout = (RelativeLayout) view.findViewById(R.id.messsage_counter_relative_layout_in_tile);
            messagesNamesAdapterHolder.messageCounterTextView = (TextView) view.findViewById(R.id.message_count_text_view_in_tile);

            view.setTag(messagesNamesAdapterHolder);
        } else {
            /** else, get the holder from the view's tag **/
            messagesNamesAdapterHolder = (MessagesNamesAdapterHolder) view.getTag();
        }
        
        if(messageNamesArrayList.get(position).getBitmap() == null){
            messagesNamesAdapterHolder.circularImageView.setImageResource(R.drawable.contact);
        } else {
            messagesNamesAdapterHolder.circularImageView.setImageBitmap(messageNamesArrayList.get(position).getBitmap());
        }
        
        /** Set the phone number **/
        messagesNamesAdapterHolder.phoneNumberTextView.setText(messageNamesArrayList.get(position).getPhoneNumber());

        /** Set the name **/
        messagesNamesAdapterHolder.nameTextView.setText(messageNamesArrayList.get(position).getName());

        /** Hides counter **/
        messagesNamesAdapterHolder.messsageCounterRelativeLayout.setVisibility(View.GONE);

        /** Only makes counter visible if number of unread messages are greater than 0 **/
        if(numberUnreadCountMap != null && numberUnreadCountMap.size() != 0){

            String phoneNumber = messageNamesArrayList.get(position).getPhoneNumber();
            if(phoneNumber.length() > 10){
                phoneNumber = phoneNumber.substring(3);
            }

            if(numberUnreadCountMap.keySet().contains(phoneNumber)){
                int count = numberUnreadCountMap.get(phoneNumber);
                if(count != 0){
                    messagesNamesAdapterHolder.messsageCounterRelativeLayout.setVisibility(View.VISIBLE);
                    messagesNamesAdapterHolder.messageCounterTextView.setText(String.valueOf(count));
                }
            }
        }

        view.setOnClickListener(new MessagesNamesItemClickListener(position));

        /** return the inflated view **/
        return view;
    }


    public interface MessagesNamesItemClickEvent{
        void onItemClicked(View v, int position);
    }


    class MessagesNamesItemClickListener implements View.OnClickListener{

        private int position;

        public MessagesNamesItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(messagesNamesItemClickEvent != null) {
                messagesNamesItemClickEvent.onItemClicked(v, position);
            }
        }
    }
}
