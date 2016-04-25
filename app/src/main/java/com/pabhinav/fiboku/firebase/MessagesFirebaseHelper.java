package com.pabhinav.fiboku.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.application.FibokuApplication;
import com.pabhinav.fiboku.models.Message;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;
import com.pabhinav.fiboku.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class MessagesFirebaseHelper {

    @Setter
    MessageFirebaseEvents messageFirebaseEvents;

    /**
     * Fetches all the messages send to or came from a given phone number
     * present in the user directory. This phone number has to be of a
     * friend in this user's directory, else, security exception is
     * thrown by firebase.
     *
     * @param phoneNumber of the friend.
     */
    public void getAllMessages(String phoneNumber){

        final Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.MESSAGES + "/" + FibokuApplication.getInstance().getPhoneNumber() + "/" + phoneNumber);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Message> messageArrayList = new ArrayList<Message>();

                HashMap<String,?> numberMessageDataMap = (HashMap<String, ?>) dataSnapshot.getValue();
                if (numberMessageDataMap == null
                        || numberMessageDataMap.size() == 0) {

                    if(messageFirebaseEvents != null){
                        messageFirebaseEvents.onAllMessagesFetched(messageArrayList, null);
                    }
                    return;
                }
                for (Map.Entry<String, ?> entry : numberMessageDataMap.entrySet()) {

                    if(entry.getKey() != null && entry.getValue() != null && !entry.getKey().equals("Unread Count")){
                        HashMap<String, String> messageDataMap = (HashMap < String, String >)entry.getValue();
                        Message message = new Message.MessageBuilder()
                                .addMessage(messageDataMap.get("Message Body"))
                                .addTimeForMessage(String.valueOf(entry.getKey()))
                                .addPhoneNumber(String.valueOf(messageDataMap.get("From")))
                                .addMessageRead(true)
                                .build();

                        messageArrayList.add(message);
                    }
                }

                MessageUtil.sortMessages(messageArrayList);

                if(messageFirebaseEvents != null){
                    messageFirebaseEvents.onAllMessagesFetched(messageArrayList, null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(messageFirebaseEvents != null){
                    messageFirebaseEvents.onAllMessagesFetched(null, firebaseError);
                }
            }
        });
    }

    public void markAllMessagesAsRead(String phoneNumber){

        final Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.MESSAGES + "/" + FibokuApplication.getInstance().getPhoneNumber() + "/" + phoneNumber);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, ?> numberMessageDataMap = (HashMap<String, ?>) dataSnapshot.getValue();
                if(numberMessageDataMap == null
                        || numberMessageDataMap.size() == 0){
                    return;
                }
                for (Map.Entry<String, ?> entry : numberMessageDataMap.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        /** Set message read true **/
                        firebase.child(String.valueOf(entry.getKey())).child("Message Read").setValue("true");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }


    public void sendMessage(String phoneNumber, String message){

        final Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.MESSAGES + "/");
        HashMap<String, String> messageDataMap = new HashMap<>();
        messageDataMap.put("From", FibokuApplication.getInstance().getPhoneNumber());
        messageDataMap.put("Message Body", message);
        messageDataMap.put("Message Read", "true");

        firebase.child(FibokuApplication.getInstance().getPhoneNumber()).child(phoneNumber).child(String.valueOf(System.currentTimeMillis())).setValue(messageDataMap);

        messageDataMap.put("Message Read", "false");
        firebase.child(phoneNumber).child(FibokuApplication.getInstance().getPhoneNumber()).child(String.valueOf(System.currentTimeMillis())).setValue(messageDataMap);
    }

    /**
     * Fetches all unread message counts.
     */
    public void getAllUnreadMessageCount(){

        Firebase firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.MESSAGES + "/" + FibokuApplication.getInstance().getPhoneNumber());
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Integer> numberUnreadMessageMap = new HashMap<>();
                int totalCount = 0;
                HashMap<String, ?> numberTimeMap = (HashMap < String, ?>)dataSnapshot.getValue();
                if(numberTimeMap == null || numberTimeMap.size() == 0){
                    if(messageFirebaseEvents != null){
                        messageFirebaseEvents.onAllUnReadMessageCountFetched(new HashMap<String, Integer>(), 0, null);
                    }
                    return;
                }
                for (Map.Entry<String, ?> numberTimeEntry : numberTimeMap.entrySet()) {

                    int counter = 0;
                    HashMap<String, ?> timeMessageMap = (HashMap < String, ?>)numberTimeEntry.getValue();
                    for(Map.Entry<String, ?> timeMessageEntry : timeMessageMap.entrySet()){
                        HashMap<String, String> messageMap = (HashMap < String, String >)timeMessageEntry.getValue();
                        if(String.valueOf(messageMap.get("Message Read")).equals("false")){
                            counter++;
                            totalCount++;
                        }
                    }
                    numberUnreadMessageMap.put(numberTimeEntry.getKey(), counter);
                }

                if(messageFirebaseEvents != null){
                    messageFirebaseEvents.onAllUnReadMessageCountFetched(numberUnreadMessageMap, totalCount, null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(messageFirebaseEvents != null){
                    messageFirebaseEvents.onAllUnReadMessageCountFetched(null, 0, firebaseError);
                }
            }
        });
    }

    public interface MessageFirebaseEvents{
        void onAllMessagesFetched(ArrayList<Message> messageArrayList, FirebaseError firebaseError);
        void onAllUnReadMessageCountFetched(HashMap<String, Integer> numberUnreadMessageMap, int totalCount, FirebaseError firebaseError);
    }


}
