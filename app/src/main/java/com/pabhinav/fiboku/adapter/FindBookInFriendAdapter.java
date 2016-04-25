package com.pabhinav.fiboku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.alertDialogs.FibokuAlertMessageDialog;
import com.pabhinav.fiboku.firebase.MessagesFirebaseHelper;
import com.pabhinav.fiboku.models.FindBookMetaData;

import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class FindBookInFriendAdapter extends android.widget.BaseAdapter {

    private Context context;
    private ArrayList<FindBookMetaData> findBookInFriendArrayList;
    private int inflateLayoutId;
    private LayoutInflater layoutInflater;
    private String lookupBook;
    
    public FindBookInFriendAdapter(Context context, ArrayList<FindBookMetaData> findBookMetaDataArrayList, int inflateLayoutId, String lookupBook){
        this.context = context;
        this.findBookInFriendArrayList = findBookMetaDataArrayList;
        this.inflateLayoutId = inflateLayoutId;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.lookupBook = lookupBook;
    }
    
    @Override
    public int getCount() {
        return findBookInFriendArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return findBookInFriendArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** Holder class for this adapter **/
    class FindBookInFriendAdapterHolder {
        
        TextView nameTextView;
        TextView bookTextView;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        /** Get the convertView **/
        View view = convertView;

        /** Holder instance **/
        FindBookInFriendAdapterHolder messagesNamesAdapterHolder;


        /** if convertView is null, its first inflation for list item view **/
        if(view == null){
            view = layoutInflater.inflate(inflateLayoutId, parent, false);
            messagesNamesAdapterHolder = new FindBookInFriendAdapterHolder();
            messagesNamesAdapterHolder.bookTextView = (TextView) view.findViewById(R.id.has_book_text_view);
            messagesNamesAdapterHolder.nameTextView = (TextView) view.findViewById(R.id.contact_name_text_view);

            view.setTag(messagesNamesAdapterHolder);
        } else {
            /** else, get the holder from the view's tag **/
            messagesNamesAdapterHolder = (FindBookInFriendAdapterHolder) view.getTag();
        }

        String name = findBookInFriendArrayList.get(position).getName();
        if (name == null
                || name.length() == 0) {

            messagesNamesAdapterHolder.nameTextView.setText(findBookInFriendArrayList.get(position).getPhoneNumber());
        } else {
            messagesNamesAdapterHolder.nameTextView.setText(name);
        }


        messagesNamesAdapterHolder.bookTextView.setText(String.format(context.getString(R.string.has_book), lookupBook));

        view.setOnClickListener(new FindBookInFriendItemClickListener(position));

        /** return the inflated view **/
        return view;
    }

    public void updateAll(ArrayList<FindBookMetaData> findBookMetaDataArrayList){
        this.findBookInFriendArrayList.clear();
        this.findBookInFriendArrayList = new ArrayList<>(findBookMetaDataArrayList);
        notifyDataSetChanged();
    }

    class FindBookInFriendItemClickListener implements View.OnClickListener{

        private int position;

        public FindBookInFriendItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            /** Ask user to send message to friend who has the book **/
            FibokuAlertMessageDialog fibokuAlertMessageDialog = new FibokuAlertMessageDialog(
                    context,
                    context.getString(R.string.lend_book),
                    context.getString(R.string.ask_your_friend_to_lend),
                    context.getString(R.string.no),
                    context.getString(R.string.yes)
            );
            fibokuAlertMessageDialog.show();
            fibokuAlertMessageDialog.setOnAlertButtonClicked(new FibokuAlertMessageDialog.OnAlertButtonClicked() {
                @Override
                public void onLeftButtonClicked(View v) {}

                @Override
                public void onRightButtonClicked(View v) {
                    MessagesFirebaseHelper messagesFirebaseHelper = new MessagesFirebaseHelper();
                    messagesFirebaseHelper.sendMessage(findBookInFriendArrayList.get(position).getPhoneNumber(), context.getString(R.string.hey_i_need) + lookupBook + context.getString(R.string.book_fiboku_tells_me));
                }
            });
        }
    }
}
