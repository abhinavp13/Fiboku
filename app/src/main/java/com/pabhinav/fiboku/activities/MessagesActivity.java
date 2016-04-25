package com.pabhinav.fiboku.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.adapter.MessagesAdapter;
import com.pabhinav.fiboku.firebase.MessagesFirebaseHelper;
import com.pabhinav.fiboku.models.Message;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Messages displaying activity.
 *
 * @author pabhinav
 */
public class MessagesActivity extends AppCompatActivity {

    /**
     * Phone number of user.
     */
    String phoneNumber;

    /**
     * {@link MessagesFirebaseHelper} instance.
     */
    MessagesFirebaseHelper messagesFirebaseHelper;

    /**
     * Recycler view for displaying messages
     */
    RecyclerView messageRecyclerView;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        phoneNumber = BundleUtil.getStringFromBundle(savedInstanceState, getIntent().getExtras(), Constants.MESSSAGE_PHONE_NUMBER, "");

        ArrayList<Message> messageArrayList = new ArrayList<>();
        final MessagesAdapter messagesAdapter = new MessagesAdapter(R.layout.messages_recycler_view_item, messageArrayList, this);
        messageRecyclerView = (RecyclerView)findViewById(R.id.messages_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);// Scroll to bottom
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setAdapter(messagesAdapter);


        messagesFirebaseHelper = new MessagesFirebaseHelper();
        messagesFirebaseHelper.getAllMessages(phoneNumber);
        messagesFirebaseHelper.setMessageFirebaseEvents(new MessagesFirebaseHelper.MessageFirebaseEvents() {
            @Override
            public void onAllMessagesFetched(ArrayList<Message> messageArrayList, FirebaseError firebaseError) {
                if (firebaseError == null) {
                    messagesAdapter.changeDataCompletely(messageArrayList);
                }
            }

            @Override
            public void onAllUnReadMessageCountFetched(HashMap<String, Integer> numberUnreadMessageMap, int totalCount, FirebaseError firebaseError) {
            }
        });
        messagesFirebaseHelper.markAllMessagesAsRead(phoneNumber);
    }

    /** Back icon clicked by user **/
    public void backClicked(View v){
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(Constants.MESSSAGE_PHONE_NUMBER, phoneNumber);
    }

    /** Message send clicked **/
    public void messageSendClicked(View v){

        hideKeyboard();

        EditText messageEditText = (EditText) findViewById(R.id.message_edit_text);
        String message = messageEditText.getText().toString();
        if(message.length() == 0){
            return;
        }
        messageEditText.setText("");
        messageEditText.clearFocus();

        messagesFirebaseHelper.sendMessage(phoneNumber, message);
    }

    /** Hides keyboard **/
    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
